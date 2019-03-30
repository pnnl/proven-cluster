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

package gov.pnnl.proven.cluster.module.disclosure.resource;

import static gov.pnnl.proven.cluster.module.disclosure.resource.ResourceConsts.RR_SSE;
import static gov.pnnl.proven.cluster.module.disclosure.resource.ResourceConsts.R_RESPONSE_EVENTS;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import org.slf4j.Logger;

import gov.pnnl.proven.cluster.module.disclosure.sse.SseEvent;
import gov.pnnl.proven.cluster.module.disclosure.sse.SseSession;
import gov.pnnl.proven.cluster.module.disclosure.sse.SseSessionManager;

/**
 * 
 * A resource class used for the registration and deregistration of
 * {@code SseSession}s. Each registered session represents a connection to a
 * client where SSE data will be pushed based on the session's configuration.
 * 
 * TODO - create a SseSession configuration class to support POSTing
 * configuration.
 * 
 * @author d3j766
 *
 */
@Path(RR_SSE)
public class SseSessionResource {

	@Inject
	Logger logger;

	@Inject
	SseSessionManager sessions;

	/**
	 * Registers an SSE session with the client requester. Response event data
	 * is pushed to client as it is created/added on server side. Response event
	 * data is based on {@code ResponseMessage}s. The event data sent to the
	 * client is filtered using query parameters provided in the service call.
	 * 
	 * TODO - this is initial implementation. Are there other query parameters
	 * to add?
	 * 
	 * @param eventSink
	 *            represents HTTP client connection where event data will be
	 *            pushed. Provided by the application container.
	 * @param domain
	 *            (optional) identifies disclosure domain that the response
	 *            event is based on. Only events matching this value will be
	 *            sent. If not provided, the Proven domain is used.
	 * @param content
	 *            (optional) identifies the type of message contents that the
	 *            response event is based on. The types are listed at
	 *            {@code MessageContent#getNames()}. Only events matching these
	 *            value will be sent. If not provided all contentTypes are
	 *            included.
	 * @param requestor
	 *            (optional) identifies disclosure source (i.e. requester) that
	 *            the response event is based on. This must be provided at
	 *            disclosure time for a match to be made. Only events matching
	 *            this value will be sent. If not provided all disclosure
	 *            sources are included.
	 */
	@GET
	@Path(R_RESPONSE_EVENTS)
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public void getResponseEvents(@Context Sse sse, @Context SseEventSink eventSink,
			@QueryParam("domain") String domain, @QueryParam("content") String content,
			@QueryParam("requester") String requester) {

		logger.debug("Registering SSE for domain :: " + domain);

		// domain
		Optional<String> domainOpt = Optional.ofNullable(domain);

		// content list
		List<String> contentList = null;
		if (null != content) {
			contentList = Stream.of(content.split(",")).map(String::trim).map(String::toLowerCase)
					.collect(Collectors.toList());
		}
		Optional<List<String>> contentsOpt = Optional.ofNullable(contentList);

		// requester
		Optional<String> requesterOpt = Optional.ofNullable(requester);

		SseSession session = new SseSession(SseEvent.Response, UUID.randomUUID(), sse, eventSink, domainOpt,
				contentsOpt, requesterOpt);

		sessions.register(session);
	}

	/**
	 * Allows for explicit removal of an SSE session. This will close the
	 * connection and remove from {@code SseSessionManager}'s registry.
	 * 
	 * TODO - improve response to account for errors in deregister.
	 * 
	 * @param sessionId
	 *            the session id, the value is provided in the first even push
	 *            after registration.
	 * @return a response indicating success of session removal.
	 */
	@Path("/session/{sesionId}")
	@DELETE
	public Response deregister(@PathParam("sesionId") String sessionId) {

		Response ret = Response.ok().build();
		UUID id = null;

		try {
			id = UUID.fromString(sessionId);
			sessions.deregister(id);
		} catch (IllegalArgumentException e) {
			ret = Response.status(Status.BAD_REQUEST.getStatusCode(), "Invalid UUID value provided.").build();
		}

		return ret;
	}

}