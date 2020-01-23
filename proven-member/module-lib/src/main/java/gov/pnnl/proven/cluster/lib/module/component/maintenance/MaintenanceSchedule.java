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
package gov.pnnl.proven.cluster.lib.module.component.maintenance;

import java.util.Optional;
import java.util.SortedSet;

import javax.inject.Inject;

import org.slf4j.Logger;

import gov.pnnl.proven.cluster.lib.module.component.ManagedComponent;
import gov.pnnl.proven.cluster.lib.module.component.TaskSchedule;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Eager;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperation;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationResult;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationSeverity;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationStatus;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.ScheduleCheck;
import gov.pnnl.proven.cluster.lib.module.messenger.event.MaintenanceEvent;
import gov.pnnl.proven.cluster.lib.module.messenger.event.MessageEvent;
import gov.pnnl.proven.cluster.lib.module.registry.MemberMaintenanceRegistry;

/**
 * Performs maintenance checks provided by the registered supplier.
 * 
 * @see ManagedMaintenance, MaintenanceCheck, TaskSchedule
 * 
 * @author d3j766
 *
 */
public class MaintenanceSchedule extends TaskSchedule<ComponentMaintenance> {

	private static final long serialVersionUID = 1L;

	@Inject
	Logger log;

	@Inject
	@Eager
	MemberMaintenanceRegistry mr;

	/**
	 * If true, indicates a components default maintenance has been registered.
	 * False, otherwise.
	 */
	boolean defaultRegistered = false;

	public MaintenanceSchedule() {
	}

	/**
	 * Registers default scheduled maintenance identified by a managed
	 * component. The default maintenance is registered only once.
	 */
	@Override
	synchronized public void register(ManagedComponent operator) {

		this.operatorOpt = Optional.of(operator);

		if (!defaultRegistered) {
			ComponentMaintenance cm = operator.scheduledMaintenance();
			mr.register(operator, cm);
			defaultRegistered = true;
		}
	}

	/**
	 * Performs provided maintenance checks, if any.
	 */
	@Override
	protected void apply() {

		if (operatorOpt.isPresent()) {

			log.debug("Maintenance schedule APPLY for: " + operatorOpt.get().getDoId());

			ManagedComponent operator = operatorOpt.get();

			// Get Scheduler maintenance information from operator
			SortedSet<ScheduleCheck> sOps = mr.getScheduleOps(operator);

			// Get component maintenance information from operator
			SortedSet<MaintenanceOperation> cOps = mr.getOps(operator);

			// First perform scheduler checks. Report only if FAILED
			MaintenanceOperationResult result = operator.schedulerCheck(sOps);
			if (result.getStatus() == MaintenanceOperationStatus.FAILED) {
				MaintenanceEvent event = new MaintenanceEvent(operator, result, sOps, registryOverdueMillis);
				notifyRegistry(event, false);
			}

			// Perform component checks only if scheduler checks PASSED and
			// report.
			if (result.getStatus() == MaintenanceOperationStatus.PASSED) {
				result = operator.check(cOps);
				MaintenanceEvent event = new MaintenanceEvent(operator, result, sOps, registryOverdueMillis);
				notifyRegistry(event, false);
			}
		}

	}

	@Override
	public boolean isReportable(MessageEvent event) {

		MaintenanceOperationSeverity reportedSeverity = (null == reported()) ? (MaintenanceOperationSeverity.Noop)
				: (((MaintenanceEvent) reported()).getResult().getSeverity());
		MaintenanceOperationSeverity reportingSeverity = ((MaintenanceEvent) event).getResult().getSeverity();

		return ((reportingSeverity != reportedSeverity));
	}

}
