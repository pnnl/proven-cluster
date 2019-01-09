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
/**
 * 
 */
package gov.pnnl.proven.cluster.lib.disclosure;

import java.io.Serializable;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.cluster.lib.disclosure.exception.InvalidRequestRegistrationException;
import gov.pnnl.proven.cluster.lib.disclosure.request.ProxyRegisteredRequest;

/**
 * An abstract class representing a request that can be serviced by a Proven
 * Cluster. The ProxyRequest provides information for locating and servicing the
 * request that it represents inside a Proven Cluster.
 * 
 * @author d3j766
 *
 * @param <T>
 *            the request input type. Void class can be used as a placeholder to
 *            represent no input data.
 * @param <V>
 *            the request result type. Void class can be used as a placeholder
 *            to represent no result.
 * 
 * @see Void
 * 
 * @since
 * 
 */
public abstract class ProxyRequest<T, V> implements RequestLocator<T, V>, DomainProvider, Serializable {

	private static final long serialVersionUID = 1L;
	static Logger log = LoggerFactory.getLogger(ProxyRequest.class);

	public static final int DEFAULT_REQUEST_RETRIES = 4;

	/**
	 * Request input data.
	 */
	T inputData;

	/**
	 * Identifies this request. Assigned at construction, cannot be modified.
	 */
	final String requestId;

	/**
	 * A reference to a registered request inside a Proven Cluster.
	 */
	ProxyRegisteredRequest<T, V> registeredRequest;

	/**
	 * Provides domain for the request's source. A Proven Cluster groups
	 * together requests and other associated information for storage under a
	 * {@code DisclosureDomain}
	 */
	DomainProvider sourceDomain;

	/**
	 * Timestamp when request was request was created.
	 */
	Long created;

	/**
	 * Scope of the request. Default is {@code RequestScope#ModuleAny}
	 */
	RequestScope scope = RequestScope.ModuleAny;

	/**
	 * Maximum number of request retries before being sent to error stream
	 */
	private int retries;

	/**
	 * Constructs a new ProxyRequest. 
	 * 
	 * @param registeredRequest
	 *            provides the request's registration information.
	 * @return the new ProxyReuest
	 * @throws InvalidRequestRegistrationException
	 *             if the registration is not complete
	 */
	protected ProxyRequest(ProxyRegisteredRequest<T, V> registeredRequest) throws InvalidRequestRegistrationException {

		if (null == registeredRequest) {
			throw new InvalidRequestRegistrationException("Registered request not provded");
		}
		if (null == registeredRequest.getRequestName()) {
			throw new InvalidRequestRegistrationException("Registered request's name not provded");
		}
		if (null == registeredRequest.getInputType()) {
			throw new InvalidRequestRegistrationException("Registered request's input type not provded");
		}
		if (null == registeredRequest.getResultType()) {
			throw new InvalidRequestRegistrationException("Registered request's result type not provded");
		}

		// Add registration and set common defaults
		this.requestId = registeredRequest.getRequestName() + "-" + UUID.randomUUID().toString();
		this.registeredRequest = registeredRequest;
		this.created = new java.util.Date().getTime();
		this.scope = RequestScope.ModuleAny;
		setRetries(DEFAULT_REQUEST_RETRIES);
	}

	@Override
	public ProxyRegisteredRequest<T, V> getLocator() {
		return registeredRequest;
	}

	/**
	 * Adds input data to the request. Default value is null.
	 * 
	 * Note: Null values are allowed and it is assumed that the cluster side
	 * service implementation will account for this situation.
	 * 
	 * @param input
	 *            the input data
	 */
	public void addInput(T input) {
		inputData = input;
	}

	/**
	 * Returns {@code DisclosureDomain} for the request. If sourceDomain has not
	 * been set, then the common domain, as defined in {@code DomainProvider} is
	 * returned.
	 * 
	 * @see DomainProvider
	 * 
	 */
	@Override
	public DisclosureDomain getDomain() {

		DisclosureDomain ret;

		if (null != sourceDomain) {
			ret = sourceDomain.getDomain();
		} else {
			ret = DomainProvider.getCommonDomain();
		}
		return ret;
	}

	public void setSourceDomain(DomainProvider sourceDomain) {
		this.sourceDomain = sourceDomain;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public RequestScope getScope() {
		return scope;
	}

	public void setScope(RequestScope scope) {
		this.scope = scope;
	}

}
