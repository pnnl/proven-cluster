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
package gov.pnnl.proven.module.disclosure.sse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import org.slf4j.Logger;
import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.disclosure.DomainProvider;
import gov.pnnl.proven.cluster.lib.disclosure.message.MessageContent;
import gov.pnnl.proven.cluster.lib.module.stream.MessageStreamType;

import static gov.pnnl.proven.cluster.lib.disclosure.message.MessageContent.*;

/**
 * Represents an SSE session. SSS Sessions are created by the resource class
 * {@code SseSessionResource} and registered with a {@code SseSessionManager}.
 * Each session is for a specific {@code SseEvent} type. The SSE event data is
 * filtered based on configuration information provided at construction, before
 * being pushed to client.
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

	@Inject
	Logger logger;

	private SseEvent event;
	private UUID sessionId;
	private Sse sse;
	private SseEventSink eventSink;
	private DisclosureDomain domain;
	private List<MessageContent> contents;
	private String requester;

	public SseSession(SseEvent event, UUID sessionId, Sse sse, SseEventSink eventSink, Optional<String> domainOpt,
			Optional<String[]> contentsOpt, Optional<String> requesterOpt) {

		this.event = event;
		this.sessionId = sessionId;
		this.sse = sse;
		this.eventSink = eventSink;

		// domain
		this.domain = DomainProvider.getProvenDisclosureDomain();
		if ((domainOpt.isPresent()) && (DisclosureDomain.isValidDomain(domainOpt.get()))) {
			this.domain = new DisclosureDomain(domainOpt.get());
		}

		// contents
		this.contents = new ArrayList<MessageContent>();
		this.contents.add(Any);
		if (contentsOpt.isPresent()) {
			String[] source = contentsOpt.get();
			if (source.length > 0) {
				List<MessageContent> valid = MessageContent.getValues();
				ArrayList<MessageContent> dest = new ArrayList<>();
				for (String mcStr : source) {
					MessageContent mc = MessageContent.getValue(mcStr);
					if ((null != mc) && (valid.contains(mc))) {
						if (mc.equals(Any)) {
							dest.clear();
							dest.add(mc);
							break;
						} else {
							dest.add(mc);
						}
					}
				}
				if (!dest.isEmpty()) {
					this.contents = dest;
				}
			}
		}

		// requester
		this.requester = null;
		if ((requesterOpt.isPresent()) && (!requesterOpt.get().isEmpty())) {
			this.requester = requesterOpt.get();
		}

	}

	public boolean hasEvent(SseEvent eventToCheck) {
		return event.equals(eventToCheck);
	}

	public boolean hasDomain(DisclosureDomain domainToCheck) {
		return domain.equals(domainToCheck);
	}

	public boolean hasContent(MessageContent mcToCheck) {

		boolean ret = true;

		if (!contents.contains(Any)) {
			if (!contents.contains(mcToCheck)) {
				ret = false;
			}
		}

		return ret;
	}

	public boolean hasRequester(String requesterToCheck) {

		boolean ret = true;
		if (null != requester) {
			ret = requester.equals(requesterToCheck);
		}

		return ret;
	}

	public SseEvent getEvent() {
		return event;
	}

	public void setEvent(SseEvent event) {
		this.event = event;
	}

	public UUID getSessionId() {
		return sessionId;
	}

	public void setSessionId(UUID sessionId) {
		this.sessionId = sessionId;
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

	public DisclosureDomain getDomain() {
		return domain;
	}

	public void setDomain(DisclosureDomain domain) {
		this.domain = domain;
	}

	public List<MessageContent> getContents() {
		return contents;
	}

	public void setContents(List<MessageContent> contents) {
		this.contents = contents;
	}

	public String getRequester() {
		return requester;
	}

	public void setRequester(String requester) {
		this.requester = requester;
	}

}
