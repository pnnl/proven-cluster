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

package gov.pnnl.proven.cluster.module.member.resource;

import static gov.pnnl.proven.cluster.module.member.resource.MemberResourceConsts.RR_SSE;
import static gov.pnnl.proven.cluster.module.member.resource.MemberResourceConsts.R_SUBSCRIPTION;

import java.util.UUID;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.stream.JsonParsingException;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import org.slf4j.Logger;

import gov.pnnl.proven.cluster.lib.disclosure.exception.ValidatableBuildException;
import gov.pnnl.proven.cluster.lib.disclosure.item.Validatable;
import gov.pnnl.proven.cluster.lib.disclosure.item.sse.EventData;
import gov.pnnl.proven.cluster.lib.disclosure.item.sse.EventSubscription;
import gov.pnnl.proven.cluster.lib.disclosure.item.sse.EventType;
import gov.pnnl.proven.cluster.lib.disclosure.item.sse.OperationSubscription;
import gov.pnnl.proven.cluster.module.member.sse.SseSession;
import gov.pnnl.proven.cluster.module.member.sse.SseSessionManager;

/**
 * 
 * A resource class supporting creation of SSE Session subscriptions. Each
 * subscribed session represents a connection to a client where SSE data will be
 * pushed based on the session's configuration determined by an
 * EventSubscription.
 * 
 * @author d3j766
 *
 * @see SssSession, EventSubscription
 * 
 */
@Path(RR_SSE)
public class SseSessionResource {

	@Inject
	Logger logger;

	@Inject
	SseSessionManager sm;

	/**
	 * SSE Subscription. Connects an SSE session with the client requestor.
	 * Event messages selected for push to client are determined by the posted
	 * EventSubscription.
	 * 
	 * @param sse
	 *            server-side entry point for creating {@link OutboundSseEvent}
	 *            and {@link SseBroadcaster}. Provided by the application
	 *            container.
	 * 
	 * @param eventSink
	 *            represents HTTP client connection where event data will be
	 *            pushed. Provided by the application container.
	 * 
	 * @param postedSubscription
	 *            defines the subscription being requested by the client.
	 * 
	 */
	@POST
	@Path(R_SUBSCRIPTION + "/{eventType}")
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public void getResponseEvents(@Context Sse sse, @Context SseEventSink eventSink,
			@PathParam("eventType") EventType et, JsonObject postedSubscription) {
		
		boolean closeConnection = false;
		try {
			EventSubscription es = getEventSubscription(postedSubscription, et);
			SseSession session = new SseSession(UUID.randomUUID(), es, sse, eventSink);
			sm.register(session);
			String statusMessage = "Successful subscription creation";
			EventData data = sm.createSubscriptionEventData((JsonObject) es.toJson(), Response.Status.OK,
					session.getSessionId(), statusMessage);
			sm.sendEventData(session, data, null);
		} catch (ValidatableBuildException ex) {
			if (!eventSink.isClosed()) {
				String statusMessage = "Unsuccessful subscription creation.  Invalid posted Event Subscription";
				EventData data = sm.createSubscriptionEventData(postedSubscription, Response.Status.BAD_REQUEST, null,
						statusMessage);
				sm.sendEventData(sse, eventSink, data, null, null);
			}
			closeConnection = true;
		} catch (Exception e) {
			if (!eventSink.isClosed()) {
				String statusMessage = "Unsuccessful subscription creation.  Internal error.";
				EventData data = sm.createSubscriptionEventData(postedSubscription,
						Response.Status.INTERNAL_SERVER_ERROR, null, statusMessage);
				sm.sendEventData(sse, eventSink, data, null, null);
			}
			closeConnection = true;
		} finally {
			if (closeConnection) {
				eventSink.close();
			}
		}
	}

	/**
	 * Unsubscribe SSE. Allows for explicit removal of an SSE session per a
	 * client request. This will close the client connection and remove the SSE
	 * session from {@code SseSessionManager}'s registry.
	 * 
	 * TODO - improve response to account for errors in unsubscribing.
	 * 
	 * @param sessionId
	 *            identifies the session to unsubscribe. This value is pushed to
	 *            the client at time of subscription creation.
	 * 
	 * @return a response indicating success of session removal.
	 */
	@Path(R_SUBSCRIPTION + "/{sesionId}")
	@DELETE
	public Response deregister(@PathParam("sesionId") String sessionId) {

		Response ret = Response.ok().build();
		UUID id = null;

		try {
			id = UUID.fromString(sessionId);
			sm.deregister(id);
		} catch (IllegalArgumentException e) {
			ret = Response.status(Status.BAD_REQUEST.getStatusCode(), "Invalid UUID value provided.").build();
		}

		return ret;
	}

	private EventSubscription getEventSubscription(JsonObject subscription, EventType et) {

		EventSubscription es;

		switch (et) {
		case OPERATION:
			es = Validatable.toValidatable(OperationSubscription.class, subscription.toString());
			break;

		default:
			throw new IllegalArgumentException("Unkown or missing event type");
		}

		return es;
	}

}