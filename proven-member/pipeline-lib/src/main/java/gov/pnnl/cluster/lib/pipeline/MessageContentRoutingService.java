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
package gov.pnnl.cluster.lib.pipeline;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

//import org.apache.jena.rdf.model.Model;
//import org.openrdf.repository.RepositoryException;

import javax.json.JsonObject;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.jet.pipeline.ContextFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.cluster.lib.pipeline.MessageContentRoutingService.MessageContentRoutingResponse;
import gov.pnnl.proven.cluster.lib.disclosure.message.DisclosureMessage;
import gov.pnnl.proven.cluster.lib.disclosure.message.KnowledgeMessage;
import gov.pnnl.proven.cluster.lib.disclosure.message.MessageContent;
import gov.pnnl.proven.cluster.lib.disclosure.message.MessageContentGroup;
import gov.pnnl.proven.cluster.lib.disclosure.message.MessageUtils;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessage;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessageIDSFactory;
import gov.pnnl.proven.cluster.lib.disclosure.message.ResponseMessage;
import gov.pnnl.proven.cluster.lib.member.MemberProperties;
import gov.pnnl.proven.cluster.lib.disclosure.message.MessageContent.MessageContentName;


/**
 * Provides services to triple store (T3).
 * 
 * @author d3j766
 *
 */
public class MessageContentRoutingService {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(MessageContentRoutingService.class);


	private Jsonb jsonb = JsonbBuilder.create();

	/**
	 * Summary response information for a Message Content Routing request. This is included
	 * in a {@code ResponseMessage} as it's message content.
	 * 
	 * @author d3j766
	 *
	 */
	public class MessageContentRoutingResponse implements Serializable {

		private static final long serialVersionUID = 1L;

		int statusCode;
		String statusReason;
		long count;
		String message;

		MessageContentRoutingResponse() {
		}

		public MessageContentRoutingResponse(Response.Status status, long count) {
			this.statusCode = status.getStatusCode();
			this.statusReason = status.getReasonPhrase();
			this.count = count;
			this.message = "";
		}
		
		public MessageContentRoutingResponse(int statusCode, String reason, String message) {
			this.statusCode = statusCode;
			this.statusReason = reason;	
			this.count = 0;
			this.message = message;
		}
	}


	/**
	 * Returns {@code ContextFactory} for Jet processing pipelines that require T3 services.
	 * 
	 * @return {@link ContextFactory}
	 */
	public static ContextFactory<MessageContentRoutingService> mcrService() {
	
		return ContextFactory.withCreateFn(x -> MessageContentRoutingService.newMessageContentRoutingService()).toNonCooperative().withLocalSharing();

	}

		
	/**
	 * Creates a new T3Service with default settings.
	 * 
	 * @param serviceUrl
	 *            identifies SPARQL endpoint
	 * 
	 * @throws RepositoryException
	 */
	public static MessageContentRoutingService newMessageContentRoutingService() {
		MessageContentRoutingService mcrs = new MessageContentRoutingService();
		return mcrs;
	}

	private MessageContentRoutingService() {

	}


//EGS 9/12/2020	private MessageContentRoutingResponse writeMeasurementMessage(JsonObject val) {
	private MessageContentRoutingResponse writeMeasurementMessage(ProvenMessage val) {
		MessageContentRoutingResponse ret = null;
		long now = Instant.now().toEpochMilli();
		String nowStr = ((Long)now).toString();
        ClientConfig config = new ClientConfig();
        config.getSerializationConfig().addDataSerializableFactoryClass(ProvenMessageIDSFactory.FACTORY_ID, ProvenMessageIDSFactory.class);
        config.setProperty("hazelcast.client.statistics.enabled", "true");
        GroupConfig groupConfig = new GroupConfig();
        groupConfig.setName("proven");
        config.setGroupConfig(groupConfig);
        config.getNetworkConfig().addAddress("127.0.0.1:5701");
		HazelcastInstance client = HazelcastClient.newHazelcastClient(config);
		IMap<String, ProvenMessage> provenMessages = client.getMap("gov.pnnl.proven.common.knowledge.message");
		
		KnowledgeMessage km = new KnowledgeMessage(val);
		provenMessages.put(nowStr, (ProvenMessage)km);
		return new MessageContentRoutingResponse(Response.Status.ACCEPTED,1);
	}


	public ResponseMessage add(ProvenMessage sourceMessage) {


		ResponseMessage ret = null;		
		MessageContentRoutingResponse loadResponse = writeMeasurementMessage(sourceMessage);
		JsonReader reader = Json.createReader(new StringReader(jsonb.toJson(loadResponse)));
		JsonObject loadResponseObject = reader.readObject();
//		//JsonReader reader = Json.createReader(new StringReader(""));
//09142020		ret = new ResponseMessage(Response.Status.fromStatusCode(loadResponse.statusCode), sourceMessage, loadResponseObject);
//>>>>>>> Stashed changes
		return ret;
	}


	

	private ResponseMessage createResponseMessage(MessageContentRoutingResponse mcrResponse, ProvenMessage sourceMessage) {
	JsonReader reader = Json.createReader(new StringReader(jsonb.toJson(mcrResponse)));
	JsonObject loadResponseObject = reader.readObject();
	return new ResponseMessage(Response.Status.fromStatusCode(mcrResponse.statusCode), loadResponseObject,
			sourceMessage);
}	
	
	

}
