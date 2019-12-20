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
package gov.pnnl.proven.cluster.lib.module.component.maintenance.operation;

import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Unknown;
import static gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationSeverity.Available;
import static gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationStatus.PASSED;

import java.util.Optional;

import javax.annotation.PostConstruct;

import gov.pnnl.proven.cluster.lib.module.component.ManagedStatus;
import gov.pnnl.proven.cluster.lib.module.component.interceptor.StatusDecorator;

/**
 * Checks a ManagedComponent's scheduler.
 * 
 * Returns {@code MaintenanceOperationStatus#PASSED} if the schedule is running
 * or has been cancelled. Schedule cancellation is a managed event.
 * 
 * Returns {@code MaintenanceOperationStatus#PASSED} if the schedule has failed
 * to an unmanaged exception
 * 
 * Returns {@code MaintenanceOperationStatus#FAILED} if the schedule has been
 * stopped due to an error.
 * 
 * @author d3j766
 *
 */
public class SchedulerCheck extends MaintenanceOperation {

	/**
	 * Contains ManagedStatus value of the operator before the initial
	 * SchedulerCheck operation has been performed. Meaning, a status value of
	 * {@code ManagedStatus#Recoverable}, NOT including
	 * {@code ManagedStatus#FailedSchedulerRetry}. This value is managed by
	 * {@link StatusDecorator#schedulerCheck(SchedulerCheck)}.
	 * 
	 * @see #validPreCheckStatus(ManagedStatus)
	 */
	private ManagedStatus preCheckStatus = Unknown;

	public SchedulerCheck() {
		super();
	}

	@PostConstruct
	public void init() {
		log.debug("Inside SchedulerCheck contructor");
	}

	@Override
	public MaintenanceOperationResult checkAndRepair() {
		log.debug("Performing maintenance operation: " + opName());
		return new MaintenanceOperationResult(PASSED, Available, Optional.empty());
	}

	@Override
	public MaintenanceOperationSeverity maximumSeverity() {
		return MaintenanceOperationSeverity.Severe;
	}

	@Override
	public int priority() {
		return MaintenanceCheck.Priority.HIGH;
	}

	public boolean validPreCheckStatus(ManagedStatus status) {
		return ((status.isRecoverable(status)) && (status != ManagedStatus.FailedSchedulerRetry));
	}

	/**
	 * @return the preCheckStatus
	 */
	public ManagedStatus getPreCheckStatus() {
		return preCheckStatus;
	}

	/**
	 * @param preCheckStatus the preCheckStatus to set
	 */
	public void setPreCheckStatus(ManagedStatus preCheckStatus) {
		this.preCheckStatus = preCheckStatus;
	}
	
}
