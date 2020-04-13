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
package gov.pnnl.proven.cluster.lib.module.component;

/**
 * Represents possible states for managed components. A component's status value
 * may be a transition state, indicating it is in process of being changed to a
 * non-transition state. New status processing must not be initiated while a
 * component is in a transition state.
 * 
 * @see ManagedComponent
 * 
 * @author d3j766
 *
 */
public enum ManagedStatus {

	/**
	 * Transition status, indicating the component is in the process of being
	 * created.
	 */
	Creating(true),

	/**
	 * Indicates the component has successfully been created and is ready to be
	 * activated for status reporting. In this state the component may perform
	 * local services, however no reporting will be performed until it has been
	 * activated making it known to other members of the cluster.
	 */
	Ready(false),

	/**
	 * Transition status, indicating the component is in process of being
	 * activated.
	 */
	Activating(true),

	/**
	 * Component has been activated and is reporting its status.
	 */
	Online(false),

	/**
	 * Transition status, indicating the component has been requested to perform
	 * maintenance checks and update its status accordingly.
	 */
	CheckingStatus(true),

	/**
	 * Component has been deactivated due to a maintenance check operation and
	 * is considered as offline. Existing tasks will be completed, new
	 * tasks/data will not be assigned. The component may be reactivated from
	 * this state by another check operation, and only if the cause for
	 * deactivation has been repaired by a subsequent maintenance check.
	 */
	CheckedOffline(false),

	/**
	 * A failed state, indicating an error condition was encountered while
	 * {@link #Online}. Reactivation retries may be attempted from this state.
	 */
	FailedOnlineRetry(false),

	/**
	 * A failed state, indicating an error condition was encountered with a
	 * component's TaskSchedule. Maintenance check/repairs may be performed from
	 * this state. Specifically, a SchedulerCheck operation may be performed.
	 */
	FailedSchedulerRetry(false),

	/**
	 * Transition status, indicating the component has been requested to perform
	 * scheduler maintenance check and update its status accordingly.
	 */
	CheckingScheduler(true),

	/**
	 * Component can no longer accept new tasks. It will continue to perform its
	 * existing tasks.
	 */
	Busy(false),

	/**
	 * Transition status, indicating the component is in process of being
	 * deactivated.
	 */
	Deactivating(true),

	/**
	 * Component has been suspended due to a deactivate operation and is
	 * considered as offline. Existing tasks will be completed, new tasks will
	 * not be assigned. The component may be reactivated from this state by its
	 * creator.
	 */
	Offline(false),

	/**
	 * Transition status, indicating the component has encountered an error
	 * condition and is in process of being moved to a failed state.
	 */
	Failing(true),

	/**
	 * A failed state, indicating component has encountered an error condition
	 * either during activation attempt. Reactivation retries may be attempted
	 * from this state.
	 */
	FailedActivateRetry(false),

	/**
	 * A failed state, indicating component has encountered an error condition
	 * during deactivation. Deactivation retries may be attempted from this
	 * state.
	 */
	FailedDeactivateRetry(false),

	/**
	 * Indicates component has failed and will no longer accept retry attempts
	 * and will be removed from service.
	 */
	Failed(false),

	/**
	 * Transition status, indicating the component has failed and/or is being
	 * shutdown and is being removed from service. Any cleanup activities will
	 * be done as part of the removal process.
	 */
	Removing(true),

	/**
	 * Indicates the component has been removed and is considered to be out of
	 * service. This is the terminal state for a managed component.
	 */
	OutOfService(false),

	/**
	 * Indicates status of managed component is not known to its
	 * {@code MemberRegistry}. This status is only used by a
	 * {@code MemberRegistry} to mark a component as
	 * {@code ManagedStatus#Unknown} if expected status messages have not been
	 * received.
	 */
	Unknown(false),

	/**
	 * Indicates any transition status value.
	 */
	Transition(true),

	/**
	 * Indicates any non-transition status value.
	 */
	NonTransition(false),

	/**
	 * Indicates any non-transition status, where the component can be recovered
	 * for service.
	 */
	Recoverable(false),

	/**
	 * Indicates any status, where the component can not be
	 * recovered for service.
	 */
	NonRecoverable(false),

	/**
	 * Indicates any status that is a terminating state.
	 */
	Terminal(false),

	/**
	 * Indicates any non-transition status that is not a terminating state.
	 */
	NonTerminal(false);

	private final boolean isTransition;

	ManagedStatus(boolean isTransition) {
		this.isTransition = isTransition;
	}

	/**
	 * Returns true if a transition status.
	 */
	public boolean isTransition() {
		return isTransition;
	}

	/**
	 * Returns true if provided status value is a {@code #Recoverable} status.
	 */
	public static boolean isRecoverable(ManagedStatus status) {

		boolean ret = true;

		final ManagedStatus[] nonRecoverable = { Failing, Failed, Removing, OutOfService, NonRecoverable };

		for (ManagedStatus failedStatus : nonRecoverable) {
			if (failedStatus == status) {
				ret = false;
				break;
			}
		}

		return ret;
	}

	/**
	 * Returns true if provided status value is a "retry" status value. Meaning,
	 * the status value is a failed state allowing for retry attempt to rectify
	 * failure.
	 */
	public static boolean isRetry(ManagedStatus status) {

		boolean ret = false;

		final ManagedStatus[] retries = { FailedOnlineRetry, FailedActivateRetry, FailedSchedulerRetry,
				FailedDeactivateRetry };
		if (!status.isTransition()) {
			for (ManagedStatus retry : retries) {
				if (retry == status) {
					ret = true;
					break;
				}
			}
		}

		return ret;
	}

	/**
	 * Returns true if provided status value is a {@code #Terminal} status.
	 */
	public static boolean isTerminal(ManagedStatus status) {

		boolean ret = false;

		final ManagedStatus[] terminals = { OutOfService, Terminal };
		if (!status.isTransition()) {
			for (ManagedStatus terminal : terminals) {
				if (terminal == status) {
					ret = true;
					break;
				}
			}
		}

		return ret;
	}

}
