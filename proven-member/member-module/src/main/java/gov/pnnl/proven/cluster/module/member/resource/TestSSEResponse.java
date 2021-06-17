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

import java.util.Calendar;
import java.util.Optional;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;

import gov.pnnl.proven.cluster.lib.disclosure.DomainProvider;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.exchange.ResponseItem;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.DisclosureMessage;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.MessageModel;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.ResponseMessage;
import gov.pnnl.proven.cluster.lib.disclosure.item.DisclosureItem;
import gov.pnnl.proven.cluster.lib.module.stream.MessageStreamProxy;
import gov.pnnl.proven.cluster.lib.module.stream.MessageStreamType;
import gov.pnnl.proven.cluster.lib.module.stream.annotation.StreamConfig;

@Path(RR_SSE)
public class TestSSEResponse {

	@Inject
	Logger logger;
	
	private final static String SCHEMA_TEST_FILE = "schema-test.json";

	@StreamConfig(domain = DomainProvider.PROVEN_DISCLOSURE_DOMAIN, streamType = MessageStreamType.RESPONSE)
	@Inject
	MessageStreamProxy msp;

	@PUT
	@Path("/test/response/{count}")
	public Response addResponses(@PathParam("count") int count) throws Exception {

		Response ret = Response.ok().build();

		logger.debug("START :: " + Calendar.getInstance().getTime().toString());
		
		MessageModel mm = MessageModel.getInstance(DomainProvider.getProvenDisclosureDomain());

		int i = 0;
		int iterations = count;
		System.out.println("START :: " + Calendar.getInstance().getTime().toString());
		while (i <= (iterations - 1)) {

			JsonObject testMessage = Json.createObjectBuilder().add("response", "TESTNG EVENT DATA HOLDER")
					.add("count", String.valueOf(i)).build();
			logger.debug("Test Message :: " + testMessage.toString());

			// Create source message - just needed to be able to create test response message
			DisclosureMessage dm = new DisclosureMessage(new DisclosureItem(mm.getModelFile(SCHEMA_TEST_FILE)));

			// Create new response message and add - this should cause a send
			// event on server
			ResponseMessage rm = new ResponseMessage(new ResponseItem(Status.OK, Optional.empty()), dm);

			// Add message to queue
			msp.getMessageStream().getStream().put(rm.getMessageKey(), rm);
			logger.debug("Added response message :: " + i);

			Thread.sleep(1000);

			i++;
		}
		logger.debug("END :: " + Calendar.getInstance().getTime().toString());

		return ret;
	}

}
