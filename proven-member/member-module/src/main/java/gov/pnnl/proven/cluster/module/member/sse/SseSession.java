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

import static gov.pnnl.proven.cluster.lib.disclosure.MessageContent.ANY;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.disclosure.MessageContent;
import gov.pnnl.proven.cluster.lib.disclosure.MessageContentGroup;
import gov.pnnl.proven.cluster.lib.disclosure.item.sse.EventSubscription;
import gov.pnnl.proven.cluster.lib.module.stream.MessageStreamType;

/**
 * Represents an SSE session. SSE Sessions are created by the resource class
 * {@code SseSessionResource} and registered with a {@code SseSessionManager}.
 * Each session is for a specific {@code SseEvent} type. The SSE event data is
 * filtered based on configuration information before being pushed to client.
 * 
 * {@code SseEvent} types are coupled with a Proven message stream as defined in
 * {@code MessageStreamType}. When entries are added to the associated stream,
 * event data is created based on that entry and sent to the client assuming it
 * meets the filter criteria.
 * 
 * @see SseSessionResource, SseSessionManager, SseEvent, MessageStreamType
 * 
 * @author d3j766
 *
 */
public class SseSession {

    static Logger logger = LoggerFactory.getLogger(SseSession.class);

    private UUID sessionId;
    private EventSubscription eventSubscription;
    private Sse sse;
    private SseEventSink eventSink;
    private MessageStreamType eventStream;

    public SseSession(UUID sessionId, EventSubscription eventSubscription, Sse sse, SseEventSink eventSink) {

	this.eventSubscription = eventSubscription;
	this.sessionId = sessionId;
	this.sse = sse;
	this.eventSink = eventSink;

	logger.debug("Created new SSE Session for event type: " + eventSubscription.getEventType());
	logger.debug("");
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof SseSession)) {
	    return false;
	}
	SseSession other = (SseSession) obj;
	if (sessionId == null) {
	    if (other.sessionId != null) {
		return false;
	    }
	} else if (!sessionId.equals(other.sessionId)) {
	    return false;
	}
	return true;
    }

    public MessageStreamType getEventStream() {

	MessageStreamType ret = eventStream;
	if (null == ret) {
	    MessageContent mc = eventSubscription.getEventType().getMessageContent();
	    ret = MessageStreamType.getType(mc);
	}
	return eventStream;
    }

    public UUID getSessionId() {
	return sessionId;
    }

    public void setSessionId(UUID sessionId) {
	this.sessionId = sessionId;
    }

    public EventSubscription getEventSubscription() {
	return eventSubscription;
    }

    public void setEventSubscription(EventSubscription eventSubscription) {
	this.eventSubscription = eventSubscription;
    }

    public Sse getSse() {
	return sse;
    }

    public void setSse(Sse sse) {
	this.sse = sse;
    }

    public SseEventSink getEventSink() {
	return eventSink;
    }

    public void setEventSink(SseEventSink eventSink) {
	this.eventSink = eventSink;
    }

}
