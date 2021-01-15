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
package gov.pnnl.proven.cluster.lib.module.service.module;

import java.io.Serializable;

import gov.pnnl.proven.cluster.lib.module.service.exception.UnsupportedServiceException;

/**
 * Represents a request that may be serviced by a {@link ProvenModuleSave}
 * 
 * @author d3j766
 *
 */
public abstract class ModuleService<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Request input
	 */
	private T t;

	/**
	 * Represents the name of request. Used by request registry
	 */
	private String requestName;

	/**
	 * Maximum number of request retries before being sent to error stream
	 */
	private int retries;

	/**
	 * Time to live (in seconds) before being removed from a request buffer.
	 */
	private int ttl;

	/**
	 * Priority of request as defined in {@link RequestPriority}. Higher
	 * priority requests are services before lower priority requests.
	 */
	private RequestPriority priority;

	/**
	 * Scope of the reuest's service execution as defined in
	 * {@link RequestScope}
	 */
	private RequestScope scope;

	/**
	 * Request constructor. Input of request is required at time of
	 * construction.
	 * 
	 * @param t
	 *            the type of input for the request
	 */
	protected ModuleService(T t) {
		this.t = t;
	}

	/**
	 * Provides the {@link ModuleService} that will service the request.
	 * 
	 * @return the ModuleService for this request
	 * @throws UnsupportedServiceException
	 *             if the request does not have an associated ModuleService
	 */
	public ModuleService<T> getServiceProvider() throws UnsupportedServiceException {
		throw new UnsupportedServiceException();
	}

	public T getInput() {
		return t;
	}

	public String getRequestName() {
		return requestName;
	}

	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public RequestPriority getPriority() {
		return priority;
	}

	public void setPriority(RequestPriority priority) {
		this.priority = priority;
	}

	public RequestScope getScope() {
		return scope;
	}

	public void setScope(RequestScope scope) {
		this.scope = scope;
	}

}
