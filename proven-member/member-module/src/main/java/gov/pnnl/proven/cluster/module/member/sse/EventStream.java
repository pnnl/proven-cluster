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
package gov.pnnl.proven.cluster.module.member.sse;

import static gov.pnnl.proven.cluster.lib.disclosure.item.sse.EventType.OPERATION;
import static gov.pnnl.proven.cluster.lib.module.stream.MessageStreamType.RESPONSE;
import static gov.pnnl.proven.cluster.module.member.sse.EventStream.EventLabel.OPERATION_RESPONSE_LABEL;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.naming.ConfigurationException;

import gov.pnnl.proven.cluster.lib.disclosure.item.sse.EventType;
import gov.pnnl.proven.cluster.lib.module.stream.MessageStreamType;

/**
 * Associates the different EventType's with their message stream. Each event
 * type is associated with a single stream type as defined by
 * {@code MessageStreamType}. The event data for the event type is based on
 * entries from its associated stream.
 *
 * @author d3j766
 * 
 * @see EventType, MessageStreamType
 *
 */
public enum EventStream {

	OPERATION_RESPONSE(OPERATION, RESPONSE, OPERATION_RESPONSE_LABEL);

	public class EventLabel {
		public static final String OPERATION_RESPONSE_LABEL = "operation-response-event";
	}

	private EventType eventType;
	private String label;
	private MessageStreamType streamType;

	EventStream(EventType eventType, MessageStreamType streamType, String label) {
		this.eventType = eventType;
		this.label = label;
		this.streamType = streamType;
	}

	/**
	 * Provides the SSE's type.
	 * 
	 * @return the type of SSE.
	 */
	public EventType getEventType() {
		return eventType;
	}

	/**
	 * Provides the SSE's associated stream type.
	 * 
	 * @return name for the SSE event type
	 */
	public MessageStreamType getStreamType() {
		return streamType;
	}

	/**
	 * Provides the name of for the SSE event type.
	 * 
	 * @return name for the SSE event type
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Provides a list of all SSE event types.
	 */
	public static List<String> getLabels() {

		List<String> ret = new ArrayList<>();
		for (EventStream event : values()) {
			ret.add(event.getLabel());
		}
		return ret;
	}

	/**
	 * Retrieves the EventStream for a given event type. There is a single
	 * EventStream for an event type.
	 * 
	 * @param eventType
	 *            the provided event type.
	 * @return the associated EventStream.
	 */
	public static EventStream getForEvent(EventType eventType) {

		EventStream ret = null;

		for (EventStream es : EventStream.values()) {
			if (es.getEventType() == eventType) {
				ret = es;
				break;
			}
		}
		
		// Ensure EventStream is configured correctly
		if (null ==  ret) {
			throw new IllegalArgumentException("Event type missing in EventStream");
		}
		
		return ret;
	}

	/**
	 * Retrieves the EventStream(s) for a given stream. There can be 0 or more
	 * events associated with a stream.
	 * 
	 * @param streamType
	 *            the provided stream type.
	 *            
	 * @return list of EventStream
	 */
	public static List<EventStream> getEventsForStream(MessageStreamType streamType) {

		List<EventStream> ret = new ArrayList<>();

		for (EventStream es : EventStream.values()) {
			if (es.getStreamType() == streamType) {
				ret.add(es);
			}
		}		
		return ret;
	}

}
