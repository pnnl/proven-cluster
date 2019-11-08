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

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import gov.pnnl.proven.cluster.lib.module.messenger.ScheduledMessage;
import gov.pnnl.proven.cluster.lib.module.messenger.event.FailureEvent;
import gov.pnnl.proven.cluster.lib.module.messenger.event.MessageEvent;
import gov.pnnl.proven.cluster.lib.module.messenger.event.StatusEvent;

/**
 * Identifies {@code ManagedComponent} status operations. Each component
 * operation will be modify its {@code ManagedStatus} value based on processing
 * result.
 * 
 * @see ManagedComponent, ManagedStatus
 * 
 * @author d3j766
 *
 */
public interface ManagedStatusOperation {

	<T extends ManagedComponent> T createComponent(Class<T> subtype, Annotation... qualifiers);

	<T extends ManagedComponent> List<T> createComponents(Class<T> subtype, Annotation... qualifiers);

	void activateCreated(UUID created);

	void activate();

	void failedCreated(UUID created);

	void failed();

	void retry();

	void deactivateCreated(UUID created);

	void deactivate();

	void remove();

	/**
	 * Changes status to {@code ManagedStatus#FailedOnlineRetry} representing a
	 * runtime failure event has occurred. If {@code noRetry} is false then
	 * component's status will be set to {@code ManagedStatus#Failed} resulting
	 * in no reactivation attempts to be made. If {@code noRetry} is true then
	 * reactivation attempts will be made.
	 * 
	 * @param noRetry
	 *            true indicates retries should be supported, false otherwise.
	 */
	void failure(FailureEvent event, boolean noRetry);

	/**
	 * Performs a check and update of the component's current status and returns the current status report.
	 */
	List<ScheduledMessage> checkAndUpdate();

}
