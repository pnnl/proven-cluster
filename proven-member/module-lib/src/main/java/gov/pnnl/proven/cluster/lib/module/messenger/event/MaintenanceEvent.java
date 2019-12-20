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
package gov.pnnl.proven.cluster.lib.module.messenger.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;

import gov.pnnl.proven.cluster.lib.module.component.ManagedComponent;
import gov.pnnl.proven.cluster.lib.module.component.ManagedStatus;
import gov.pnnl.proven.cluster.lib.module.component.interceptor.StatusDecorator;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperation;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationResult;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationSeverity;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationStatus;

/**
 * Provides {@code ManagedComponent} status information.
 * 
 * @author d3j766
 *
 */
public class MaintenanceEvent extends ComponentEvent {

	MaintenanceOperationResult result;
	boolean isUnmaintainedSeverity;
	Optional<String> failedOpOpt = Optional.empty();
	List<String> allOps = new ArrayList<>();
	List<String> passedOps = new ArrayList<>();
	List<String> notInvokedOps = new ArrayList<>();
	long registryOverdueMillis;

	public MaintenanceEvent(ManagedComponent mc, MaintenanceOperationResult result, SortedSet<MaintenanceOperation> ops,
			long registryOverdueMillis) {

		super(mc);
		this.result = result;
		this.registryOverdueMillis = registryOverdueMillis;
		ops.forEach((op) -> {
			allOps.add(op.opName());
			MaintenanceOperationStatus mos = op.getResult().getStatus();
			switch (mos) {
			case FAILED:
				failedOpOpt = Optional.of(op.opName());
				break;
			case PASSED:
				passedOps.add(op.opName());
				break;
			case NOT_INVOKED:
				notInvokedOps.add(op.opName());
				break;
			}
		});
		isUnmaintainedSeverity = unmaintainedSeverity();
	}

	/**
	 * Determines if the severity was caused by a non-maintained managed
	 * component. Meaning, the severity result was not caused by a failed
	 * maintenance operation. It was the result of a maintenance request for a
	 * component whose status indicates maintenance is no longer required (i.e.
	 * unmaintained)
	 *
	 * @see ManagedStatus, {@link StatusDecorator#check(SortedSet)}
	 * 
	 */
	public boolean unmaintainedSeverity() {
		return ((!ManagedStatus.isMaintained(result.getSeverity().getStatus())) && (!failedOpOpt.isPresent()));
	}

	/**
	 * @return the severity
	 */
	public MaintenanceOperationResult getResult() {
		return result;
	}

	/**
	 * @return the registryOverdueMillis
	 */
	public long getRegistryOverdueMillis() {
		return registryOverdueMillis;
	}

	/**
	 * @param registryOverdueMillis
	 *            the registryOverdueMillis to set
	 */
	public void setRegistryOverdueMillis(long registryOverdueMillis) {
		this.registryOverdueMillis = registryOverdueMillis;
	}

	/**
	 * @return the failedOpOpt
	 */
	public Optional<String> getFailedOpOpt() {
		return failedOpOpt;
	}

	/**
	 * @return the allOps
	 */
	public List<String> getAllOps() {
		return allOps;
	}

	/**
	 * @return the passedOps
	 */
	public List<String> getPassedOps() {
		return passedOps;
	}

	/**
	 * @return the notInvokedOps
	 */
	public List<String> getNotInvokedOps() {
		return notInvokedOps;
	}

}
