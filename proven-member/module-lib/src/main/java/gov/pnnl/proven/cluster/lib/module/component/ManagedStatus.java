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
 * Represents the possible states for managed components. A component's status
 * value may be a transition state, indicating it will be changed to a
 * non-transition state based on current processing. New status processing must
 * not be initiated while a component is in a transition state.
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
	 * 
	 * @see Reporter
	 * 
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
	 * Transition status, indicating the component has been requested to check
	 * and update its status.
	 */
	CheckingStatus(true),

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
	 * Component has been suspended and is considered as offline. Existing tasks
	 * will be completed, new tasks will not be accepted. The component may be
	 * reactivated from this state.
	 */
	Offline(false),

	/**
	 * Transition status, indicating a new component is being created in order
	 * to add additional resources due to {@link #Busy} or {@link #Failed}
	 * resources of the same type. Once successfully created the new component
	 * will be put in a {@link #Ready} status.
	 */
	Scaling(true),

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
	 * either during deactivation. Deactivation retries may be attempted from
	 * this state.
	 */
	FailedDeactivateRetry(false),

	/**
	 * A failed state, indicating an error condition was encountered during a
	 * scaling attempt. Scaling retries may be attempted from this state.
	 */
	FailedScalingRetry(false),

	/**
	 * A failed state, indicating an error condition was encountered while
	 * {@link #Online}. Reactivation retries may be attempted from this state.
	 */
	FailedOnlineRetry(false),

	/**
	 * Indicates component has failed and will no longer accept retry attempts
	 * and will be removed.
	 */
	Failed(false),

	/**
	 * Transition status, indicating the component has failed and is being
	 * removed from service.
	 */
	Removing(true),

	/**
	 * Indicates the component has been removed and is considered to be out of
	 * service. Any cleanup activities will be done as part of the removal
	 * process. This is the terminal state for a managed component.
	 */
	OutOfService(false),

	/**
	 * Indicates status of managed component is not known to a monitoring
	 * component. Status information is used by monitoring components (e.g.
	 * registry) to perform their tasks. If the status cannot be discovered it
	 * is marked as unknown, from their perspective, and treated as such.
	 */
	Unknown(false);

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

}
