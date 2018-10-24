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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import gov.pnnl.proven.client.lib.disclosure.exception.InvalidRequestRegistrationException;
import gov.pnnl.proven.client.lib.disclosure.exception.UnknownClientSessionIdException;

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
public class ClientProxyRequest<T, V> extends ProxyRequest<T, V> {

	private static final long serialVersionUID = 1L;

	private static final int MAX_SESIONS = 100;
	private static final String INITIAL_SESSION_ID;
	private static Set<String> SESSION_IDS = new HashSet<String>();

	static {
		INITIAL_SESSION_ID = UUID.randomUUID().toString();
		SESSION_IDS.add(INITIAL_SESSION_ID);
	}

	/**
	 * Identifies the session identifier for this request.
	 */
	private String sessionId;

	/**
	 * Static factory method to create a new ClientProxyRequest.
	 * 
	 * @param registeredRequest
	 *            provides the request's cluster registration information.
	 * @return the new ClientProxyReuest
	 * @throws InvalidRequestRegistrationException
	 *             if the registration is not complete.
	 */
	public synchronized static <T, V> ClientProxyRequest<T, V> createClientProxyRequest(
			RequestRegistration<T, V> registeredRequest, boolean newSession)
			throws InvalidRequestRegistrationException, MaximumSessions {

		ClientProxyRequest<T, V> cpr = new ClientProxyRequest<>(registeredRequest);

		// Generate new session id for this instance, if requested
		if (newSession) {
			
			cpr.setS SESSION_ID = UUID.randomUUID().toString();
		}

		return cpr;
	}

	// The parent class ensures the registered request information provided is
	// complete and will
	// set its default values.
	protected ClientProxyRequest(RequestRegistration<T, V> registeredRequest)
			throws InvalidRequestRegistrationException {
		super(registeredRequest);
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) throws UnknownClientSessionIdException {

		if (!SESSION_IDS.contains(sessionId)) {
			throw new UnknownClientSessionIdException("Could not find session identifier for Client");
		}

		this.sessionId = sessionId;
	}

}
