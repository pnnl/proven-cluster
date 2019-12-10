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
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.FailedRemoveRetry;
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
import static gov.pnnl.proven.cluster.lib.module.util.LoggerResource.currentThreadLog;

import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;

import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

import org.slf4j.Logger;

import gov.pnnl.proven.cluster.lib.module.component.ManagedComponent;
import gov.pnnl.proven.cluster.lib.module.component.ManagedStatusOperation;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Managed;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.ComponentMaintenance;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.MaintenanceOperation;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.MaintenanceSeverity;

@Decorator
@Priority(value = Interceptor.Priority.APPLICATION)
public abstract class StatusDecorator implements ManagedStatusOperation {

	@Inject
	Logger log;

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
		if (Activate.verifyOperation(mc.getStatus())) {
			mc.setStatus(Activating);

			opSuccess = mc.activate();

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

	}

	/**
	 * @see ManagedStatusOperation#deactivate()
	 */
	@Override
	public boolean deactivate() {

		log.debug(currentThreadLog("START DEACTIVATE DECORATOR"));

		boolean opSuccess = false;
		log.debug("Decorator deactivate started");
		if (Deactivate.verifyOperation(mc.getStatus())) {
			mc.setStatus(Deactivating);
			opSuccess = mc.deactivate();
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
	 * @see ManagedStatusOperation#fail()()
	 */
	@Override
	public void fail() {

		log.debug("Decorator fail started");
		if (Fail.verifyOperation(mc.getStatus())) {

			// LOCAL deactivation called here to disable component

			mc.setStatus(Failing);
			mc.setStatus(Failed);
		} else {
			log.warn("Fail operation not performed.  Incompatible input status: " + mc.getStatus());
		}
		log.debug("Decorator fail completed");
	}

	/**
	 * @see ManagedStatusOperation#remove()
	 */
	@Override
	public boolean remove() {

		boolean opSuccess = false;
		log.debug("Decorator remove started");
		if (Remove.verifyOperation(mc.getStatus())) {
			mc.setStatus(Removing);
			opSuccess = mc.remove();
			if (opSuccess) {
				mc.setStatus(OutOfService);
			} else {
				mc.setStatus(FailedRemoveRetry);
			}
		} else {
			log.warn("Remove operation not performed.  Incompatible input status: " + mc.getStatus());
		}
		log.debug("Decorator remove completed");

		return opSuccess;
	}

	/**
	 * @see ManagedStatusOperation#check(ComponentMaintenance)
	 */
	@Override
	public MaintenanceSeverity check(SortedSet<MaintenanceOperation> ops) {

		log.debug("Decorator check started");
		MaintenanceSeverity opResult = Undetermined;
		if (Check.verifyOperation(mc.getStatus())) {
			mc.setStatus(CheckingStatus);
			MaintenanceSeverity severity = mc.check(ops);
			mc.setStatus(severity.getStatus());
		} else {
			log.warn("Check operation not performed.  Incompatible input status: " + mc.getStatus());
		}
		log.debug("Decorator check completed");

		return opResult;
	}

}
