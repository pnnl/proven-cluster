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
package gov.pnnl.proven.cluster.module.disclosure.sse;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.OutboundSseEvent.Builder;
import javax.ws.rs.sse.SseEventSink;

import org.slf4j.Logger;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.disclosure.message.MessageContent;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessage;
import gov.pnnl.proven.cluster.lib.disclosure.message.ResponseMessage;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Managed;
import gov.pnnl.proven.cluster.lib.module.exchange.RequestExchange;
import gov.pnnl.proven.cluster.lib.module.manager.StreamManager;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.Manager;
import gov.pnnl.proven.cluster.lib.module.stream.MessageStreamProxy;
import gov.pnnl.proven.cluster.lib.module.stream.MessageStreamType;
import gov.pnnl.proven.cluster.module.disclosure.dto.SseRegisterEventDto;
import gov.pnnl.proven.cluster.module.disclosure.dto.SseResponseEventDto;

/**
 * Manages SSE sessions created by the resource class
 * {@code SseSessionResource}. This manager supports session registration and
 * deregistration. Registered sessions listen for entry messages added to their
 * associated stream according to its {@code SseEvent}, and pushes event data
 * built from the entry message to the session's client.
 * 
 * The initial event message for a registered session provides the session
 * identifier; in this way the client may deregister the session when finished.
 * 
 * Sessions that have been closed are periodically removed from the registry.
 * These are sessions that have been closed by means other than the DELETE
 * service provided by the SSE resource class.
 * 
 * @author d3j766
 *
 */
@ApplicationScoped
public class SseSessionManager implements EntryAddedListener<String, ProvenMessage> {

	public static final String SSE_EXECUTOR_SERVICE = "concurrent/SSE";
	public static final int SSE_RECONNECT_DELAY = 4000;
	public static final int REGISTRATIONS_PER_CLEAN = 25;

	@Resource(lookup = RequestExchange.RE_EXECUTOR_SERVICE)
	ManagedExecutorService mes;

	@Inject 
	HazelcastInstance hzi;
	
	@Inject
	@Manager
	StreamManager sm;

	@Inject
	Logger logger;

	/**
	 * There is a single listener per a domain stream. Each listener has a UUID
	 * which was provided at create time.
	 */
	Map<SimpleEntry<DisclosureDomain, MessageStreamType>, UUID> listenerRegistry;

	/**
	 * Maps Storing all registered sessions. The first session added for a
	 * domain stream will cause creation of it's domain stream listener. The
	 * last session to be removed from a domain stream will cause the removal
	 * it's domain stream listener. Some data is duplicated between Maps for the
	 * benefit of retrievals.
	 */
	Map<SimpleEntry<DisclosureDomain, MessageStreamType>, Set<SseSession>> sessionRegistry;
	Map<UUID, SimpleEntry<DisclosureDomain, MessageStreamType>> sessionsById;

	/**
	 * A shared resource for SSE event identifiers.
	 */
	AtomicInteger eventId;

	/**
	 * A running count of the number of successfully performed registrations.
	 */
	int registrationCount = 0;

	@PostConstruct
	public void initialize() {
		listenerRegistry = new HashMap<>();
		sessionRegistry = new HashMap<>();
		sessionsById = new HashMap<>();
		eventId = new AtomicInteger(1);
	}

	@PreDestroy
	public void destroy() {

		// Remove listeners
		for (SimpleEntry<DisclosureDomain, MessageStreamType> se : sessionRegistry.keySet()) {
			removeListener(se.getKey(), se.getValue());
		}

		// Close sessions
		for (Set<SseSession> sessions : sessionRegistry.values()) {
			for (SseSession session : sessions) {
				closeSession(session);
			}
		}

	}

	public synchronized void register(SseSession session) {

		// Clean closed event sinks periodically
		if ((registrationCount > 0) && (0 == (registrationCount % REGISTRATIONS_PER_CLEAN))) {
			cleanSessions();
		}

		// Get register event data to push to client
		SseRegisterEventDto sre = new SseRegisterEventDto(session);

		// Determine if it is the first session
		boolean isFirstSession = isFirstSessionForDomainStream(session);

		// Add session to registry
		addSession(session);

		// Send register event
		String comment = "SSE session registration";
		CompletableFuture.runAsync(() -> {
			try {
				sendEventData(session, sre, comment);
			} catch (Throwable t) {
				t.printStackTrace();
				throw t;
			}
		}, mes).exceptionally(this::entryException);

		// Add listener, if it's first session for the domain stream
		if (isFirstSession) {
			addListener(session.getDomain(), session.getEvent().getStreamType());
		}

		registrationCount++;
		logger.debug("Resistration count :: " + registrationCount);
		logger.debug("Session registered, session ID :: " + session.getSessionId());
	}

	public synchronized void deregister(UUID sessionId) {

		// Get the registered session by ID
		SseSession session = retrieveSession(sessionId);

		// NOP if session does not exist
		if (null != session) {

			boolean isLastSession = isLastSessionForDomainStream(session);
			if (isLastSession) {
				
				// Turn off the entry listener for domain stream
				removeListener(session.getDomain(), session.getEvent().getStreamType());
			}

			// Update registry and close session
			removeSession(sessionId);
			closeSession(session);

			logger.debug("Session deregistered, session ID :: " + sessionId);

		} else {
			logger.debug("Session deregistration for session ID :: " + sessionId + " , NOT FOUND");
		}

	}

	private SseSession retrieveSession(UUID sessionId) {

		SseSession ret = null;
		SimpleEntry<DisclosureDomain, MessageStreamType> domainStream = sessionsById.get(sessionId);
		if (null != domainStream) {
			for (SseSession s : sessionRegistry.get(domainStream)) {
				if (s.getSessionId().equals(sessionId)) {
					ret = s;
					break;
				}
			}
		}
		return ret;
	}

	private void addSession(SseSession session) {

		DisclosureDomain dd = session.getDomain();
		MessageStreamType mst = session.getEvent().getStreamType();
		UUID sessionId = session.getSessionId();
		SimpleEntry<DisclosureDomain, MessageStreamType> se = new SimpleEntry<>(dd, mst);

		// Add to registry
		sessionsById.put(sessionId, se);
		if (sessionRegistry.containsKey(se)) {
			sessionRegistry.get(se).add(session);

		} else {
			Set<SseSession> sessions = new HashSet<>();
			sessions.add(session);
			sessionRegistry.put(se, sessions);
		}
		logger.debug("After adding session - Domain stream " + "[" + dd.getDomain() + "::" + mst.toString()
				+ "] COUNT :: " + sessionRegistry.get(se).size());
	}

	private void removeSession(UUID sessionId) {

		SimpleEntry<DisclosureDomain, MessageStreamType> domainStream = sessionsById.get(sessionId);
		if (null != domainStream) {
			SseSession sessionToRemove = null;
			for (SseSession s : sessionRegistry.get(domainStream)) {
				if (s.getSessionId().equals(sessionId)) {
					sessionToRemove = s;
					break;
				}
			}
			if (null != sessionToRemove) {
				removeSession(sessionToRemove);
			}
		}
	}

	private void removeSession(SseSession session) {

		if (null != session) {

			SimpleEntry<DisclosureDomain, MessageStreamType> domainStream = new SimpleEntry<>(session.getDomain(),
					session.getEvent().getStreamType());
			sessionRegistry.get(domainStream).remove(session);
			sessionsById.remove(session.getSessionId());

			logger.debug("After removing sesson - Domain stream " + "[" + session.getDomain() + "::"
					+ session.getEvent().getStreamType().toString() + "] COUNT :: "
					+ sessionRegistry.get(domainStream).size());
		}
	}

	private void closeSession(SseSession session) {

		if (!session.getEventSink().isClosed()) {
			try {
				session.getEventSink().close();
			} catch (Exception e) {
				logger.info("Closure failed for SSE event sink in session :: " + session.getSessionId());
			}
		}
	}

	private void cleanSessions() {

		// For each session if it's event sink is closed then remove it from the
		// registry.
		for (Set<SseSession> sessions : sessionRegistry.values()) {
			for (SseSession session : sessions) {
				if (session.getEventSink().isClosed()) {
					removeSession(session);
				}
			}
		}

	}

	private void addListener(DisclosureDomain dd, MessageStreamType mst) {

		SimpleEntry<DisclosureDomain, MessageStreamType> se = new SimpleEntry<>(dd, mst);
		MessageStreamProxy msp = sm.getMessageStreamProxy(dd, mst);
		UUID listenerId = UUID.fromString(msp.getMessageStream().getStream().addEntryListener(this, true));
		listenerRegistry.put(se, listenerId);
	}

	private void removeListener(DisclosureDomain dd, MessageStreamType mst) {

		SimpleEntry<DisclosureDomain, MessageStreamType> se = new SimpleEntry<>(dd, mst);
		MessageStreamProxy msp = sm.getMessageStreamProxy(dd, mst);
		UUID listenerId = listenerRegistry.get(se);
		if (null != listenerId) {
			String listenerIdStr = listenerId.toString();
			msp.getMessageStream().getStream().removeEntryListener(listenerIdStr);
		}
		listenerRegistry.remove(se);
	}

	private boolean isFirstSessionForDomainStream(SseSession session) {

		boolean ret = false;
		DisclosureDomain dd = session.getDomain();
		MessageStreamType mst = session.getEvent().getStreamType();
		SimpleEntry<DisclosureDomain, MessageStreamType> se = new SimpleEntry<>(dd, mst);
		if ((!sessionRegistry.containsKey(se)) || (sessionRegistry.get(se).isEmpty())) {
			ret = true;
		}
		return ret;
	}

	private boolean isLastSessionForDomainStream(SseSession session) {

		boolean ret = false;
		DisclosureDomain dd = session.getDomain();
		MessageStreamType mst = session.getEvent().getStreamType();
		SimpleEntry<DisclosureDomain, MessageStreamType> se = new SimpleEntry<>(dd, mst);
		if ((sessionRegistry.containsKey(se)) && (sessionRegistry.get(se).size() == 1)
				&& (sessionRegistry.get(se).contains(session))) {
			ret = true;
		}
		return ret;
	}

	private void sendEventData(SseSession session, SseEventData data, String comment) {

		try {

			Builder sseBuilder = session.getSse().newEventBuilder();

			//@formatter:off
			OutboundSseEvent sseEvent = sseBuilder
					.name(session.getEvent().getEvent())
					.id(String.valueOf(eventId.getAndIncrement()))
					.mediaType(MediaType.APPLICATION_JSON_TYPE )
					.data(data.getClass(), data)
					.reconnectDelay(SSE_RECONNECT_DELAY)
					.comment(comment)
					.build();
			//@formatter:on

			session.getEventSink().send(sseEvent);

		} catch (IllegalStateException e) {
			// Connection has been closed - remove session from registry
			logger.debug("Stale session encountered - Session ID :: " + session.getSessionId());
			deregister(session.getSessionId());
		} catch (Throwable t) {
			logger.error("SSE send event data failure");
			t.printStackTrace();
		}

	}

	private SseEventData extractEventData(SseSession session, ProvenMessage message) {

		SseEventData eventData = null;

		MessageContent mc = message.getMessageContent();
		MessageStreamType mst = MessageStreamType.getType(mc);

		switch (mst) {

		case Response:

			ResponseMessage rm = (ResponseMessage) message;
			eventData = new SseResponseEventDto(session, rm);
			break;

		default:
			logger.error("Unsupported stream type for SSE event data processing encountered.");
			break;
		}

		return eventData;
	}

	@Override
	public void entryAdded(EntryEvent<String, ProvenMessage> event) {

		// Send events for added message
		ProvenMessage message = event.getValue();
		CompletableFuture.runAsync(() -> {

			try {
				MessageContent mc = message.getMessageContent();
				MessageStreamType mst = MessageStreamType.getType(mc);
				DisclosureDomain dd = message.getDisclosureItem().getDisclosureDomain();
				SimpleEntry<DisclosureDomain, MessageStreamType> se = new SimpleEntry<>(dd, mst);
				Set<SseSession> sessions = sessionRegistry.get(se);
				boolean hasSessions = ((null != sessions) && (!sessions.isEmpty()));

				if (hasSessions) {

					for (SseSession session : sessions) {

						// Check if event data should be sent to session
						boolean hasDomain = session.hasDomain(dd);
						boolean hasContent = session.hasContent(mc);						
						boolean hasRequester = session.hasRequestor(message.getDisclosureItem().getRequestorId());
						boolean sendEvent = ((hasDomain) && (hasContent) && (hasRequester));

						if (sendEvent) {
							SseEventData eventData = extractEventData(session, message);
							if (null != eventData) {
								String comment = "sse event data for a " + message.getClass().getSimpleName();
								sendEventData(session, eventData, comment);
							}
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				throw ex;
			}

		}, mes).exceptionally(this::entryException);

	}

	/**
	 * Callback for entry message processing, if completed exceptionally.
	 * 
	 * TODO - how to recover from exception? Include message in the exception
	 * that is re-thrown and save for retry?
	 * 
	 * @param readerException
	 *            the exception thrown from the entry processor.
	 */
	protected Void entryException(Throwable readerException) {

		Void ret = null;

		// Simple log message for now...
		logger.error("Sending SSE event data for a Proven message has failed.");

		return ret;
	}

}
