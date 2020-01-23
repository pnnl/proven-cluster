/*******************************************************************************
 * Copyright (c) 2017, Battelle Memorial Institute All rights reserved.
 * Battelle Memorial Institute (hereinafter Battelle) hereby grants permission to any person or entity 
 * lawfully obtaining a copy of this software and associated documentation files (hereinafter the 
 * Software) to redistribute and use the Software in source and binary forms, with or without modification. 
 * Such person or entity may use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of 
 * the Software, and may permit others to do so, subject to the following conditions:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the 
 * following disclaimers.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Other than as used herein, neither the name Battelle Memorial Institute or Battelle may be used in any 
 * form whatsoever without the express written consent of Battelle.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * BATTELLE OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * General disclaimer for use with OSS licenses
 * 
 * This material was prepared as an account of work sponsored by an agency of the United States Government. 
 * Neither the United States Government nor the United States Department of Energy, nor Battelle, nor any 
 * of their employees, nor any jurisdiction or organization that has cooperated in the development of these 
 * materials, makes any warranty, express or implied, or assumes any legal liability or responsibility for 
 * the accuracy, completeness, or usefulness or any information, apparatus, product, software, or process 
 * disclosed, or represents that its use would not infringe privately owned rights.
 * 
 * Reference herein to any specific commercial product, process, or service by trade name, trademark, manufacturer, 
 * or otherwise does not necessarily constitute or imply its endorsement, recommendation, or favoring by the United 
 * States Government or any agency thereof, or Battelle Memorial Institute. The views and opinions of authors expressed 
 * herein do not necessarily state or reflect those of the United States Government or any agency thereof.
 * 
 * PACIFIC NORTHWEST NATIONAL LABORATORY operated by BATTELLE for the 
 * UNITED STATES DEPARTMENT OF ENERGY under Contract DE-AC05-76RL01830
 ******************************************************************************/
package gov.pnnl.proven.cluster.lib.module.component.interceptor;

import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Activating;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.CheckingScheduler;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.CheckingStatus;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Deactivating;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Failed;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.FailedActivateRetry;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.FailedDeactivateRetry;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.FailedOnlineRetry;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.FailedSchedulerRetry;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Failing;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Offline;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Online;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.OutOfService;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Removing;
import static gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationStatus.FAILED;
import static gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationStatus.PASSED;
import static gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation.Activate;
import static gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation.Check;
import static gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation.Deactivate;
import static gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation.Fail;
import static gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation.Remove;
import static gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation.SchedulerCheck;
import static gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation.Shutdown;
import static gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation.Suspend;
import static gov.pnnl.proven.cluster.lib.module.util.LoggerResource.currentThreadLog;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

import org.slf4j.Logger;

import gov.pnnl.proven.cluster.lib.module.component.ManagedComponent;
import gov.pnnl.proven.cluster.lib.module.component.ManagedStatus;
import gov.pnnl.proven.cluster.lib.module.component.ManagedStatusOperation;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Eager;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Managed;
import gov.pnnl.proven.cluster.lib.module.component.exception.StatusOperationException;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.ComponentMaintenance;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperation;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationResult;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationSeverity;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.ScheduleCheck;
import gov.pnnl.proven.cluster.lib.module.messenger.observer.ManagedObserver;

@Decorator
@Priority(value = Interceptor.Priority.APPLICATION)
public abstract class StatusDecorator implements ManagedStatusOperation {

	@Inject
	Logger log;

	@Inject
	@Eager
	ManagedObserver mo;

	@Inject
	@Delegate
	@Managed
	ManagedComponent mc;

	/**
	 * Maximum retry attempts for a status operation.
	 */
	private static final int MAX_RETRY_ATTEMPTS = 5;

	/**
	 * Retry counts.
	 */
	private static Map<UUID, Map<ManagedStatus, Integer>> retries = new HashMap<>();

	@PostConstruct
	public void statusDecoratorPostConstruct() {
		log.debug("Inside status decorator post construct");
	}

	/**
	 * Verifies if there are remaining retries for the delegate's provided retry
	 * status value. If there are remaining retries, the recorded retry attempts
	 * will be incremented by 1 and the provided retry status returned.
	 * 
	 * @param status
	 *            the retry status to verify
	 * 
	 * @return the provided status value if it's either not a retry status (i.e.
	 *         nothing to verify) or there are remaining retry attempts.
	 *         Otherwise a {@code ManagedStatus#Failed} status is returned,
	 *         representing no managed attempts remain and the operation should
	 *         be considered as failed.
	 */
	private ManagedStatus verifyRetries(ManagedStatus status) {

		ManagedStatus ret = status;

		UUID id = mc.getId();

		if (ManagedStatus.isRetry(status)) {
			if (retries.containsKey(id)) {
				int count = retries.get(id).get(status);
				if (count >= MAX_RETRY_ATTEMPTS) {
					ret = Failed;
				} else {
					retries.get(id).put(status, count + 1);
				}
			} else {
				addInitialRetry(id, status);
			}
		}

		return ret;
	}

	/**
	 * Resets retry count to zero for the delegate's provided status value.
	 * 
	 * @param status
	 */
	private void resetRetries(ManagedStatus status) {

		UUID id = mc.getId();

		if (ManagedStatus.isRetry(status)) {
			if (retries.containsKey(id)) {
				retries.get(id).put(status, 0);
			}
		} else {
			addInitialRetry(id, status);
		}

	}

	/**
	 * Indicates if a delegate's status operation, identified by the provide
	 * retry status, being performed is an initial attempt (i.e. not a retry
	 * attempt).
	 * 
	 * @return true if is an initial attempt. False otherwise. If the provided
	 *         status value is not a retry status, true is returned.
	 */
	private boolean isInitialOperation(ManagedStatus status) {

		boolean ret = true;
		UUID id = mc.getId();

		if (ManagedStatus.isRetry(status)) {
			if (retries.containsKey(id)) {
				if (retries.get(id).get(status) > 0) {
					ret = false;
				}
			}
		}

		return ret;
	}

	private void addInitialRetry(UUID id, ManagedStatus status) {
		Map<ManagedStatus, Integer> opRetries = new HashMap<>();
		opRetries.put(status, 0);
		retries.put(id, opRetries);
	}

	/**
	 * @see ManagedStatusOperation#activate()
	 */
	@Override
	public boolean activate() {

		log.debug(currentThreadLog("START ACTIVATE DECORATOR"));

		boolean opSuccess = false;
		log.debug("Decorator activate started");
		ManagedStatus inStatus = mc.getStatus();

		if (Activate.verifyOperation(inStatus)) {
			mc.setStatus(Activating);

			try {
				opSuccess = mc.activate();
			} catch (Exception e) {
				mc.setStatus(inStatus);
				throw new StatusOperationException(Activate, e);
			}

			if (opSuccess) {
				mc.setStatus(Online);
				resetRetries(FailedActivateRetry);
			} else {
				mc.setStatus(verifyRetries(FailedActivateRetry));
			}

		} else {
			log.warn("Activate operation not performed.  Incompatible input status: " + mc.getStatus()
					+ "\n For component: " + mc.getDoId());

		}
		log.debug("Decorator activate completed");
		log.debug(currentThreadLog("END ACTIVATE DECORATOR"));

		return opSuccess;
	}

	/**
	 * @see ManagedStatusOperation#deactivate()
	 */
	@Override
	public void scale(UUID scaled) {
		// TODO
	}

	/**
	 * @see ManagedStatusOperation#deactivate()
	 */
	@Override
	public boolean deactivate() {

		log.debug(currentThreadLog("START DEACTIVATE DECORATOR"));

		boolean opSuccess = true;
		log.debug("Decorator deactivate started");
		ManagedStatus inStatus = mc.getStatus();

		if (Deactivate.verifyOperation(mc.getStatus())) {

			// Only perform if not already Offline
			if (mc.getStatus() != Offline) {

				mc.setStatus(Deactivating);

				try {
					opSuccess = mc.deactivate();
				} catch (Exception e) {
					mc.setStatus(inStatus);
					throw new StatusOperationException(Deactivate, e);
				}

				if (opSuccess) {
					mc.setStatus(Offline);
					resetRetries(FailedDeactivateRetry);
				} else {
					mc.setStatus(verifyRetries(FailedDeactivateRetry));
				}
			}
		} else {
			log.warn("Deactivate operation not performed.  Incompatible input status: " + mc.getStatus()
					+ "\n For component: " + mc.getDoId());
		}
		log.debug("Decorator deactivate completed");
		log.debug(currentThreadLog("END DEACTIVATE DECORATOR"));

		return opSuccess;
	}

	/**
	 * @see ManagedStatusOperation#fail()
	 */
	@Override
	public void fail() {

		log.debug(currentThreadLog("START FAIL DECORATOR"));
		log.debug("Decorator fail started");
		if (Fail.verifyOperation(mc.getStatus())) {
			mc.setStatus(Failing);

			try {
				mc.fail(); // Component specific
			} catch (Exception e) {
				mc.setStatus(Failed);
				throw new StatusOperationException(Fail, e);
			}
			mc.setStatus(Failed);

		} else {
			log.warn("Fail operation not performed.  Incompatible input status: " + mc.getStatus()
					+ "\n For component: " + mc.getDoId());
		}
		log.debug("Decorator fail completed");
		log.debug(currentThreadLog("END FAIL DECORATOR"));
	}

	/**
	 * @see ManagedStatusOperation#remove()
	 */
	@Override
	public void remove() {

		log.debug(currentThreadLog("START REMOVE DECORATOR"));

		log.debug("Decorator remove started");
		if (Remove.verifyOperation(mc.getStatus())) {
			mc.setStatus(Removing);
			try {
				mc.remove(); // Component specific
			} catch (Exception e) {
				mc.setStatus(OutOfService);
				throw new StatusOperationException(Remove, e);
			}
			mc.setStatus(OutOfService);

		} else {
			log.warn("Remove operation not performed.  Incompatible input status: " + mc.getStatus()
					+ "\n For component: " + mc.getDoId());
		}
		log.debug("Decorator remove completed");
		log.debug(currentThreadLog("END REMOVE DECORATOR"));

	}

	/**
	 * @see ManagedStatusOperation#suspend()
	 */
	@Override
	public void suspend() {

		log.debug(currentThreadLog("START SUSPEND DECORATOR"));

		log.debug("Decorator suspend started");
		if (Suspend.verifyOperation(mc.getStatus())) {

			if (mc.getStatus() != Offline) {

				mc.setStatus(Deactivating);
				try {
					mc.deactivate(); // Component specific
				} catch (Exception e) {
					mc.setStatus(Offline);
					throw new StatusOperationException(Suspend, e);
				}
				mc.setStatus(Offline);
			}

		} else {
			log.warn("Shutdown operation not performed. Incompatible input status: " + mc.getStatus()
					+ "\n For component: " + mc.getDoId());
		}
		log.debug("Decorator shutdown completed");
		log.debug(currentThreadLog("END SHUTDOWN DECORATOR"));
	}

	/**
	 * @see ManagedStatusOperation#shutdown()
	 */
	@Override
	public void shutdown() {

		log.debug(currentThreadLog("START SHUTDOWN DECORATOR"));

		log.debug("Decorator shutdown started");
		if (Shutdown.verifyOperation(mc.getStatus())) {
			mc.setStatus(Removing);
			try {
				mc.remove(); // Component specific
			} catch (Exception e) {
				mc.setStatus(OutOfService);
				throw new StatusOperationException(Shutdown, e);
			}
			mc.setStatus(OutOfService);

		} else {
			log.warn("Shutdown operation not performed. Incompatible input status: " + mc.getStatus()
					+ "\n For component: " + mc.getDoId());
		}
		log.debug("Decorator shutdown completed");
		log.debug(currentThreadLog("END SHUTDOWN DECORATOR"));
	}

	/**
	 * @see ManagedStatusOperation#check(ComponentMaintenance)
	 */
	@Override
	public <T extends MaintenanceOperation> MaintenanceOperationResult check(SortedSet<T> ops) {

		log.debug(currentThreadLog("START CHECK DECORATOR"));
		log.debug("Decorator check started");
		MaintenanceOperationResult opResult = new MaintenanceOperationResult();

		if (!ops.isEmpty()) {

			ManagedStatus inStatus = mc.getStatus();

			// Reset all operation results for a new check. This will not cause
			// a
			// re-sort (add causes a re-sort), thus order of operation execution
			// is
			// maintained as set by previous check.
			ops.forEach((op) -> {
				op.getResult().resetDefault();
			});

			if (Check.verifyOperation(inStatus)) {
				mc.setStatus(CheckingStatus);
				try {

					opResult = mc.check(ops);

				} catch (Exception e) {
					mc.setStatus(inStatus);
					throw new StatusOperationException(Check, e);
				}

				ManagedStatus status = opResult.getSeverity().getStatus();
				if (status != FailedOnlineRetry) {
					resetRetries(FailedOnlineRetry);
				} else {
					status = verifyRetries(FailedOnlineRetry);
				}
				mc.setStatus(status);
			}

			else {
				log.warn("Check operation not performed. Incompatible input status: " + mc.getStatus()
						+ "\n For component: " + mc.getDoId());
			}

		}
		log.debug("Decorator check completed");
		log.debug(currentThreadLog("END CHECK DECORATOR"));
		return opResult;
	}

	/**
	 * @see ManagedStatusOperation#schedulerCheck(SchedulerCheck)
	 */
	@Override
	public MaintenanceOperationResult schedulerCheck(SortedSet<ScheduleCheck> ops) {

		log.debug(currentThreadLog("START SCHEDULER CHECK DECORATOR"));
		log.debug("Decorator SchedulerCheck started");
		MaintenanceOperationResult opResult = new MaintenanceOperationResult();

		if (!ops.isEmpty()) {

			/**
			 * Reset all operation results for a new check. This will not cause
			 * a re-sort (add causes a re-sort), thus order of operation
			 * execution is maintained as set by previous check.
			 * 
			 * If this is an initial scheduler check then record the component's
			 * current status as it's "pre-check" status. If the initial
			 * operation or a remaining retry passes, then the components's
			 * status will be reset back to this "pre-check" status. This is
			 * necessary for scheduler checks because these checks are performed
			 * separately from component maintenance, and will block component
			 * maintenance until a scheduler issue is resolved. If there are no
			 * issues or a issue is resolved then this "pre-check status can be
			 * used by component maintenance which follows the scheduler checks.
			 */

			ManagedStatus inStatus = mc.getStatus();
			ops.forEach((op) -> {
				op.getResult().resetDefault();
				if (isInitialOperation(FailedSchedulerRetry)) {
					op.setPreCheckStatus(inStatus);
				}
			});

			if (SchedulerCheck.verifyOperation(inStatus)) {

				mc.setStatus(CheckingScheduler);

				try {

					opResult = mc.schedulerCheck(ops);

				} catch (Exception e) {
					mc.setStatus(inStatus);
					throw new StatusOperationException(SchedulerCheck, e);
				}

				ManagedStatus status = opResult.getSeverity().getStatus();

				if (status != FailedSchedulerRetry) {
					resetRetries(FailedSchedulerRetry);
				} else {
					status = verifyRetries(FailedSchedulerRetry);
				}

				// At this point the status can only be another retry,
				// a non-recoverable, or online (i.e. all checks PASSED). Online
				// indicates component should be set back to its "pre-check"
				// status,
				// which can be retrieved from one of it's operations (they
				// should
				// all have the same "pre-check" value).
				if (opResult.getStatus() == PASSED) {
					mc.setStatus(ops.first().getPreCheckStatus());
				} else {
					mc.setStatus(status);
				}

				// This will trigger a registry notification to unregister
				// component
				// from the maintenance registry as it is no longer being
				// maintained.
			} else if (!ManagedStatus.isRecoverable(inStatus)) {
				opResult = new MaintenanceOperationResult(FAILED,
						MaintenanceOperationSeverity.getSeverityByStatus(inStatus), Optional.empty());
			} else {
				log.warn("SchedulerCheck operation not performed.  Input status: " + mc.getStatus());
			}

		}

		log.debug("Decorator SchedulerCheck completed");
		log.debug(currentThreadLog("END SCHEDULER CHECK DECORATOR"));

		return opResult;
	}

}
