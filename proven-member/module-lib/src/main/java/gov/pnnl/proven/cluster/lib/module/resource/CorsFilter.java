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
package gov.pnnl.proven.cluster.lib.module.resource;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerResponseFilter {

	// Original CORS Support
	// @Override
	// public void filter(ContainerRequestContext requestContext,
	// ContainerResponseContext responseContext) throws IOException {
	// responseContext.getHeaders().add(
	// "Access-Control-Allow-Origin", "*");
	// responseContext.getHeaders().add(
	// "Access-Control-Allow-Credentials", "true");
	// responseContext.getHeaders().add(
	// "Access-Control-Allow-Headers",
	// "Origin, Content-Type, Accept, Authorization");
	// responseContext.getHeaders().add(
	// "Access-Control-Allow-Methods",
	// "GET, POST, PUT, DELETE, OPTIONS, HEAD");
	// }

	public static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
	public final static int MAX_AGE = 42 * 60 * 60;
	public final static String DEFAULT_ALLOWED_HEADERS = "origin,accept,content-type";
	public final static String DEFAULT_EXPOSED_HEADERS = "location,info";

	// TODO Fix multiple CorsFilter's in filter chain? This is reason for
	// checking if header was already added into response.
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		final MultivaluedMap<String, Object> headers = responseContext.getHeaders();

		if (!headers.containsKey("Access-Control-Allow-Origin"))
			headers.add("Access-Control-Allow-Origin", "*");
		if (!headers.containsKey("Access-Control-Allow-Headers"))
			headers.add("Access-Control-Allow-Headers", getRequestedAllowedHeaders(requestContext));
		if (!headers.containsKey("Access-Control-Expose-Headers"))
			headers.add("Access-Control-Expose-Headers", getRequestedExposedHeaders(requestContext));
		if (!headers.containsKey("Access-Control-Allow-Credentials"))
			headers.add("Access-Control-Allow-Credentials", "true");
		if (!headers.containsKey("Access-Control-Allow-Methods"))
			headers.add("Access-Control-Allow-Methods", ALLOWED_METHODS);
		if (!headers.containsKey("Access-Control-Max-Age"))
			headers.add("Access-Control-Max-Age", MAX_AGE);
		if (!headers.containsKey("x-responded-by"))
			headers.add("x-responded-by", "cors-response-filter");
	}

	String getRequestedAllowedHeaders(ContainerRequestContext responseContext) {
		List<String> headers = responseContext.getHeaders().get("Access-Control-Allow-Headers");
		return createHeaderList(headers, DEFAULT_ALLOWED_HEADERS);
	}

	String getRequestedExposedHeaders(ContainerRequestContext responseContext) {
		List<String> headers = responseContext.getHeaders().get("Access-Control-Expose-Headers");
		return createHeaderList(headers, DEFAULT_EXPOSED_HEADERS);
	}

	String createHeaderList(List<String> headers, String defaultHeaders) {
		if (headers == null || headers.isEmpty()) {
			return defaultHeaders;
		}
		StringBuilder retVal = new StringBuilder();
		for (int i = 0; i < headers.size(); i++) {
			String header = (String) headers.get(i);
			retVal.append(header);
			retVal.append(',');
		}
		retVal.append(defaultHeaders);
		return retVal.toString();
	}

}
