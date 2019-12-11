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
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.CheckingStatus;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Deactivating;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Failed;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.FailedActivateRetry;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.FailedDeactivateRetry;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Failing;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Offline;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Online;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.OutOfService;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Removing;
import static gov.pnnl.proven.cluster.lib.module.component.maintenance.MaintenanceSeverity.Undetermined;
import static gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation.Activate;
import static gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation.Check;
import static gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation.Deactivate;
import static gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation.Fail;
import static gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation.Remove;
import static gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation.Shutdown;
import static gov.pnnl.proven.cluster.lib.module.util.LoggerResource.currentThreadLog;

import java.util.SortedSet;
import java.util.UUID;

import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

import org.slf4j.Logger;

import gov.pnnl.proven.cluster.lib.module.component.ManagedComponent;
import gov.pnnl.proven.cluster.lib.module.component.ManagedStatus;
import gov.pnnl.proven.cluster.lib.module.component.ManagedStatusOperation;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Managed;
import gov.pnnl.proven.cluster.lib.module.component.exception.StatusOperationException;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.ComponentMaintenance;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.MaintenanceOperation;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.MaintenanceSeverity;
import gov.pnnl.proven.cluster.lib.module.messenger.observer.ManagedObserver;

@Decorator
@Priority(value = Interceptor.Priority.APPLICATION)
public abstract class StatusDecorator implements ManagedStatusOperation {

	@Inject
	Logger log;

	@Inject
	ManagedObserver mo;

	@Inject
	@Delegate
	@Managed
	ManagedComponent mc;

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
			} else {
				mc.setStatus(FailedActivateRetry);
			}

		} else {
			log.warn("Activate operation not performed.  Incompatible input status: " + mc.getStatus());

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

		boolean opSuccess = false;
		log.debug("Decorator deactivate started");
		ManagedStatus inStatus = mc.getStatus();
		if (Deactivate.verifyOperation(mc.getStatus())) {
			mc.setStatus(Deactivating);

			try {
				opSuccess = mc.deactivate();
			} catch (Exception e) {
				mc.setStatus(inStatus);
				throw new StatusOperationException(Deactivate, e);
			}

			if (opSuccess) {
				mc.setStatus(Offline);
			} else {
				mc.setStatus(FailedDeactivateRetry);
			}
		} else {
			log.warn("Deactivate operation not performed.  Incompatible input status: " + mc.getStatus());
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
				failTasks();
			} catch (Exception e) {
				mc.setStatus(Failed);
				throw new StatusOperationException(Fail, e);
			}
			mc.setStatus(Failed);

		} else {
			log.warn("Fail operation not performed.  Incompatible input status: " + mc.getStatus());
		}
		log.debug("Decorator fail completed");
		log.debug(currentThreadLog("END FAIL DECORATOR"));
	}

	private void failTasks() {
		mc.getMaintenanceSchedule().stop();
		mc.fail(); // Component specific
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
				removeTasks();
			} catch (Exception e) {
				mc.setStatus(OutOfService);
				throw new StatusOperationException(Remove, e);
			}
			mc.setStatus(OutOfService);

		} else {
			log.warn("Remove operation not performed.  Incompatible input status: " + mc.getStatus());
		}
		log.debug("Decorator remove completed");
		log.debug(currentThreadLog("END REMOVE DECORATOR"));

	}

	private void removeTasks() {
		mc.getMaintenanceSchedule().stop();
		mc.getMessengerSchedule().stop();
		mo.unregister(mc);
		mc.remove(); // Component specific
	}

	/**
	 * @see ManagedStatusOperation#remove()
	 */
	@Override
	public void shutdown() {

		log.debug(currentThreadLog("START SHUTDOWN DECORATOR"));

		log.debug("Decorator shutdown started");
		if (Shutdown.verifyOperation(mc.getStatus())) {
			mc.setStatus(Removing);
			try {
				removeTasks();
			} catch (Exception e) {
				mc.setStatus(OutOfService);
				throw new StatusOperationException(Shutdown, e);
			}
			mc.setStatus(OutOfService);

		} else {
			log.warn("Shutdown operation not performed.  Incompatible input status: " + mc.getStatus());
		}
		log.debug("Decorator shutdown completed");
		log.debug(currentThreadLog("END SHUTDOWN DECORATOR"));

	}

	/**
	 * @see ManagedStatusOperation#check(ComponentMaintenance)
	 */
	@Override
	public MaintenanceSeverity check(SortedSet<MaintenanceOperation> ops) {

		log.debug(currentThreadLog("START CHECK DECORATOR"));

		log.debug("Decorator check started");
		MaintenanceSeverity opResult = Undetermined;
		ManagedStatus inStatus = mc.getStatus();
		if (Check.verifyOperation(mc.getStatus())) {
			mc.setStatus(CheckingStatus);

			try {
				opResult = mc.check(ops);
			} catch (Exception e) {
				mc.setStatus(inStatus);
				throw new StatusOperationException(Check, e);
			}
			mc.setStatus(opResult.getStatus());

		} else {
			log.warn("Check operation not performed.  Incompatible input status: " + mc.getStatus());
		}
		log.debug("Decorator check completed");
		log.debug(currentThreadLog("END CHECK DECORATOR"));

		return opResult;
	}

}
