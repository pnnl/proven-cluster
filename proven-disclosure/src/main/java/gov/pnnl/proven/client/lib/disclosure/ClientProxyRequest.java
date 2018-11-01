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
package gov.pnnl.proven.client.lib.disclosure;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.pnnl.proven.client.lib.disclosure.exception.InvalidRequestRegistrationException;
import gov.pnnl.proven.client.lib.disclosure.exception.MaximumClientSessionsException;
import gov.pnnl.proven.client.lib.disclosure.exception.UnknownSessionException;
import gov.pnnl.proven.client.lib.disclosure.request.ProxyRegisteredRequest;

/**
 * Represents a client request that can be serviced by a Proven Cluster. A
 * Client, in this context, represents an application external to a Proven
 * Cluster.
 * 
 * @author d3j766
 *
 * @param <T>
 *            the input data type of the request
 * @param <V>
 *            the result data type of the request
 */
public class ClientProxyRequest<T, V> extends ProxyRequest<T, V> implements Serializable {

	private static final long serialVersionUID = 1L;
	static Logger log = LoggerFactory.getLogger(ClientProxyRequest.class);

	public static final int MAX_SESSIONS = 100;
	private static final String DEFAULT_SESSION;
	private static Set<String> SESSIONS = new HashSet<String>();

	static {
		DEFAULT_SESSION = getNewSession();
		SESSIONS.add(DEFAULT_SESSION);
	}

	/**
	 * Identifies the session identifier for this request.
	 */
	private String requestSession;

	/**
	 * Creates a new session identifier.
	 * 
	 * @return new session id
	 */
	private static String getNewSession() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Static factory method to create a new ClientProxyRequest.
	 * 
	 * @param registeredRequest
	 *            provides the request's cluster registration information.
	 * @return the new ClientProxyReuest. The new request is assigned the
	 *         default session identifier.
	 * @throws InvalidRequestRegistrationException
	 *             if the registration is not complete.
	 */
	public static <T, V> ClientProxyRequest<T, V> createClientProxyRequest(ProxyRegisteredRequest<T, V> registeredRequest)
			throws InvalidRequestRegistrationException {
		ClientProxyRequest<T, V> cpr = new ClientProxyRequest<>(registeredRequest);
		return cpr;
	}

	/**
	 * Static factory method to create a new ClientProxyRequest and create a new
	 * session.
	 * 
	 * @param registeredRequest
	 *            provides the request's cluster registration information.
	 * @return the new ClientProxyReuest. The new request is assigned a new
	 *         session identifier.
	 * @throws InvalidRequestRegistrationException
	 *             if the registration is not complete.
	 * @throws MaximumClientSessionsException
	 *             if number of client sessions has exceeded number allowed, see
	 *             {@code ClientProxyRequest#MAX_SESSIONS}
	 */
	public static <T, V> ClientProxyRequest<T, V> createClientProxyRequestNewSesssion(
			ProxyRegisteredRequest<T, V> registeredRequest, boolean newSession)
			throws InvalidRequestRegistrationException, MaximumClientSessionsException {
		ClientProxyRequest<T, V> cpr = new ClientProxyRequest<>(registeredRequest, true);
		return cpr;
	}

	/**
	 * Static factory method to create a new ClientProxyRequest and use a
	 * previously created session.
	 * 
	 * @param registeredRequest
	 *            provides the request's cluster registration information.
	 * @return the new ClientProxyReuest. The new request is assigned to a
	 *         previous client's request session.
	 * @throws InvalidRequestRegistrationException
	 *             if the registration is not complete.
	 * @throws UnknownSessionException
	 *             if provided session does not exist
	 */
	public static <T, V> ClientProxyRequest<T, V> createClientProxyRequestPreviousSession(
			ProxyRegisteredRequest<T, V> registeredRequest, String session)
			throws InvalidRequestRegistrationException, UnknownSessionException {
		ClientProxyRequest<T, V> cpr = new ClientProxyRequest<>(registeredRequest, session);
		return cpr;
	}

	protected ClientProxyRequest(ProxyRegisteredRequest<T, V> registeredRequest) throws InvalidRequestRegistrationException {
		super(registeredRequest);
		requestSession = DEFAULT_SESSION;
	}

	protected ClientProxyRequest(ProxyRegisteredRequest<T, V> registeredRequest, boolean newSession)
			throws InvalidRequestRegistrationException, MaximumClientSessionsException {
		this(registeredRequest);
		if (newSession) {
			if (SESSIONS.size() >= MAX_SESSIONS) {
				throw new MaximumClientSessionsException();
			} else {
				requestSession = getNewSession();
				SESSIONS.add(requestSession);
			}
		}
	}

	protected ClientProxyRequest(ProxyRegisteredRequest<T, V> registeredRequest, String session)
			throws InvalidRequestRegistrationException, UnknownSessionException {
		this(registeredRequest);
		if (!SESSIONS.contains(session)) {
			throw new UnknownSessionException();
		} else {
			requestSession = getNewSession();
			SESSIONS.add(requestSession);
		}
	}

	public String getRequestSession() {
		return requestSession;
	}

	public String getDefaultSession() {
		return DEFAULT_SESSION;
	}

	public void removeSession(String session) {
		if (session != DEFAULT_SESSION) {
			SESSIONS.remove(session);
		}
	}

}
