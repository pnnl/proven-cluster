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
package gov.pnnl.proven.cluster.lib.module.messenger.annotation;

import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Busy;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Failed;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.FailedActivateRetry;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.FailedOnlineRetry;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.FailedRemoveRetry;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.NonRecoverable;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Offline;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Online;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Ready;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Recoverable;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Qualifier;

import gov.pnnl.proven.cluster.lib.module.component.ManagedStatus;

/**
 * Used to qualify status event messages, indicating the message is for a
 * particular status operation.
 * 
 * @see StatusOperation
 * 
 * @author d3j766
 *
 */
@Documented
@Qualifier
@Retention(RUNTIME)
@Target({ PARAMETER, METHOD, TYPE, FIELD })
public @interface StatusOperation {

	/**
	 * Enumerates status operations and their valid {@code ManagedStatus}
	 * inputs. A {@code ManagedComponent}'s status operation will not be
	 * performed unless its status is one of the indicated valid status input
	 * values.
	 * 
	 * @author d3j766
	 *
	 */
	enum Operation {

		/**
		 * A component may activate if it is in one of the enumerated states.
		 */
		Activate(Ready, Offline, FailedActivateRetry),

		/**
		 * A parent's scale operation to create or recycle is triggered by a
		 * child component that is in one of the enumerated states. The new or
		 * recycled component will be of the same type that triggered the scale
		 * operation.
		 */
		Scale(Busy, FailedOnlineRetry, NonRecoverable),

		/**
		 * A component may deactivate if it is in one of the enumerated states.
		 */
		Deactivate(Recoverable),

		/**
		 * A component may fail if it is in one of the enumerated states.
		 */
		Fail(Recoverable),

		/**
		 * A parent may remove a child component from service if the child is in
		 * one of the enumerated states.
		 */
		Remove(Failed, FailedRemoveRetry),

		/**
		 * Maintenance will be performed on a component if it is in one of the
		 * enumerated states.
		 */
		CheckAndRepair(Offline, Online, Busy, FailedOnlineRetry);

		private final Set<ManagedStatus> validStatus;

		Operation(ManagedStatus... validStatus) {
			this.validStatus = new HashSet<>(Arrays.asList(validStatus));
		}

		/**
		 * Verify the operation is suitable for the provided status input.
		 * 
		 * @return true if the status is suitable for the operation, false
		 *         otherwise.
		 * 
		 */
		public boolean verifyOperation(ManagedStatus status) {
			//@formatter:off
			return (validStatus.contains(status) 
				|| (!status.isTransition() && !ManagedStatus.isRecoverable(status) && validStatus.contains(NonRecoverable))
				|| (!status.isTransition() && ManagedStatus.isRecoverable(status) && validStatus.contains(Recoverable)));
			//@formatter:on
		}

	}

	/**
	 * (Required) The {@link StatusOperation.Operation} type.
	 * 
	 */
	StatusOperation.Operation operation();

}
