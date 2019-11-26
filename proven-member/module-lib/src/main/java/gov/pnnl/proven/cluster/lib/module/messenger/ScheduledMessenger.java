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
package gov.pnnl.proven.cluster.lib.module.messenger;

import java.lang.annotation.Annotation;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;

import gov.pnnl.proven.cluster.lib.module.component.ScheduledTask;
import gov.pnnl.proven.cluster.lib.module.messenger.event.MessageEvent;

/**
 * Sends {@code ScheduledMessages} containing {@MessageEvent}s on a fixed delay
 * schedule.
 * 
 * Default {@code TaskSchedule} is provided here, and used if
 * {@code TaskSchedule} is not annotated at injection point.
 * 
 * @see ScheduledTask, ScheduledMessages, MessageEvent, TaskSchedule
 * 
 * @author d3j766
 *
 */
public class ScheduledMessenger extends ScheduledTask<ScheduledMessages> {

	private static final long serialVersionUID = 1L;

	@Inject
	Logger log;

	@Inject
	Event<MessageEvent> eventInstance;

	public ScheduledMessenger() {
	}

	@PostConstruct
	public void initMessenger() {
	}

	@PreDestroy
	public void destroyMessenger() {
	}

	/**
	 * Sends the {@code ScheduledMessage}.
	 * 
	 * @param messages
	 *            optional reported message content
	 */
	protected void apply(Optional<ScheduledMessages> messages) {

		Annotation[] qualifiers = {};

		if (messages.isPresent()) {

			ScheduledMessages sms = messages.get();

			for (ScheduledMessage sm : sms.getMessages()) {

				if (sm.getQualifiers().isPresent()) {
					qualifiers = sm.getQualifiers().get().toArray(qualifiers);
				}

				if (sm.isAsync()) {
					log.debug("ASYNC FIRE : " + sm.getEvent().getClass().getSimpleName());
					eventInstance.select(qualifiers).fireAsync(sm.getEvent());
				} else {
					log.debug("SYNC FIRE : " + sm.getEvent().getClass().getSimpleName());
					eventInstance.select(qualifiers).fire(sm.getEvent());
				}
			}
		}
	}

}
