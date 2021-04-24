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
package gov.pnnl.proven.cluster.lib.pipeline.service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.jet.pipeline.ContextFactory;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureIDSFactory;
import gov.pnnl.proven.cluster.lib.disclosure.MessageContentGroup;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.KnowledgeMessage;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.ProvenMessage;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.ResponseMessage;
import gov.pnnl.proven.cluster.lib.pipeline.response.MessageContentRoutingResponse;

/**
 * Provides services to triple store (T3).
 * 
 * @author d3j766
 *
 */
public class MessageContentRoutingService {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(MessageContentRoutingService.class);

	/**
	 * Returns {@code ContextFactory} for Jet processing pipelines that require
	 * T3 services.
	 * 
	 * @return {@link ContextFactory}
	 */
	public static ContextFactory<MessageContentRoutingService> mcrService() {

		return ContextFactory.withCreateFn(x -> MessageContentRoutingService.newMessageContentRoutingService())
				.toNonCooperative().withLocalSharing();

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

	private JsonObjectBuilder processNestedObject(JsonObject iObject) {
		JsonObjectBuilder ret = Json.createObjectBuilder();
		Set<String> iokeys = iObject.keySet();
		Iterator<String> ioit = iokeys.iterator();

		while (ioit.hasNext()) {
			String iokey = ioit.next();

			if (iObject.get(iokey).getValueType() == ValueType.STRING) {
				ret.add(iokey, iObject.get(iokey));
			} else if (iObject.get(iokey).getValueType() != ValueType.NUMBER) {
				ret.add(iokey, iObject.get(iokey));
			} else if (iObject.get(iokey) instanceof JsonObject) {
				JsonObject i2Object = (JsonObject) iObject.get(iokey);
				ret = processNestedObject(i2Object);
			}
		}

		return ret;

	}

	//
	// How do we take responses coming deep inside pipeline to add to the
	// response?
	// Prototype idea of adding a JSON message to the content of the JSON being
	// built.
	//
	private JsonObject transformationResponse(String reason, int code, String response) {
		JsonObjectBuilder responseObj = Json.createObjectBuilder();
		responseObj.add("reason", reason);
		responseObj.add("code", reason);
		responseObj.add("response", response);
		return responseObj.build();

	}

	private JsonArrayBuilder processDifferenceArray(String direction, String simulationId, JsonArray differenceArray,
			JsonArrayBuilder values) {
		@SuppressWarnings("unchecked")
		Iterator<JsonValue> diterator = differenceArray.iterator();
		JsonArrayBuilder record = Json.createArrayBuilder();
		JsonObjectBuilder valueObject = Json.createObjectBuilder();
		while (diterator.hasNext()) {
			JsonObject dobject = (JsonObject) diterator.next();
			Set<String> dkeys = dobject.keySet();
			Iterator<String> dit = dkeys.iterator();

			record.add("TRUE,hasSimulationMessageType,INPUT,STRING");
			record.add("TRUE,hasMeasurementDifference," + direction + ",STRING");
			record.add("TRUE,simulation_id," + simulationId + ",STRING");
			while (dit.hasNext()) {
				String rdkey = dit.next();
				if (dobject.get(rdkey).getValueType() == ValueType.STRING) {
					if (rdkey.equalsIgnoreCase("object")) {
						record.add("TRUE," + rdkey + "," + dobject.getJsonString(rdkey) + ",STRING");
					} else {
						record.add("FALSE," + rdkey + "," + dobject.getJsonString(rdkey) + ",STRING");
					}
				} else if (dobject.get(rdkey).getValueType() == ValueType.NUMBER) {
					record.add("FALSE," + rdkey + "," + dobject.getJsonNumber(rdkey) + ",NUMBER");
				} else if (dobject.get(rdkey) instanceof JsonObject) {
					JsonObject iObject = dobject.getJsonObject(rdkey);
					record.add(processNestedObject(iObject).build());

				}

			}

			valueObject.add("record", record.build());
			values.add(valueObject);
			// values.add(valueObject.build());

		}
		return values;
		// return values;
	}

	@SuppressWarnings("static-access")
	JsonObject simulationOutput2ProvenMeasurement(JsonObject simulationOutput) {

		JsonObject ret = null;
		long timestamp = (long) -1;
		boolean hasMeasurementObject = false;
		JsonObjectBuilder message = Json.createObjectBuilder();
		JsonObjectBuilder metrics = Json.createObjectBuilder();
		JsonArrayBuilder header = Json.createArrayBuilder();
		JsonObjectBuilder errorResponse = Json.createObjectBuilder();
		String measurementArrayKey = null;
		String simulationId = "";

		JsonObject messageContentObject = (JsonObject) simulationOutput.get("message");
		Set<String> messageContent_keys = messageContentObject.keySet();
		//
		if (messageContentObject.get("timestamp") != null) {
			if (messageContentObject.get("timestamp").getValueType() == ValueType.NUMBER) {
				timestamp = messageContentObject.getJsonNumber("timestamp").longValue();
			} else if (messageContentObject.get("timestamp").getValueType() == ValueType.STRING) {
				String timestamp_str = messageContentObject.getJsonString("timestamp").getString();
				if (timestamp_str.matches("-?\\d+(.\\d+)?")) {
					Long timestampL = Long.parseLong(timestamp_str);
					timestamp = timestampL.longValue();
				}
				// String timestamp_str =
				// innerMessageObject.getJsonString("timestamp").getString();
				// Long timestamp_L = Long.parseLong(timestamp_str);
				// long timestamp = timestamp_L.longValue();

			}
		}
		if (simulationOutput.get("simulation_id") != null) {
			simulationId = simulationOutput.get("simulation_id").toString();
		}

		message.add("timestamp", timestamp);
		if (simulationOutput.get("measurement") != null)
			message.add("measurement", simulationOutput.getJsonString("measurement"));
		else {
			message.add("measurement", "simulation");
		}

		// {
		// "timestamp": 1357048800,
		// "measurement": "simulation",

		header.add("is_metadata");
		header.add("label");
		header.add("value");
		header.add("value_type");
		message.add("header", header.build());

		//
		// "header": ["is_metadata", "label", "value", "value_type"],
		//

		JsonObject measurementsObject = messageContentObject.getJsonObject("measurements");
		Set<String> measurementKeys = new HashSet<String>();
		measurementKeys = measurementsObject.keySet();
		JsonArrayBuilder record = Json.createArrayBuilder();
		JsonArrayBuilder values = Json.createArrayBuilder();
		for (String key : measurementKeys) {
			JsonObjectBuilder valueObject = Json.createObjectBuilder();
			record.add("True," + "simulation_id" + "," + simulationId + ",STRING");
			record.add("True,hasSimulationMessageType,OUTPUT,STRING");
			record.add("True," + "object" + "," + key + ",STRING");
			System.out.println("record: " + key);
			JsonObject measurementRecord = (JsonObject) measurementsObject.get(key);
			Set<String> fieldKeys = new HashSet<String>();
			fieldKeys = measurementRecord.keySet();
			for (String fieldKey : fieldKeys) {
				if (measurementRecord.get(fieldKey).getValueType() == ValueType.NUMBER) {
					System.out.print("key= " + fieldKey + " ,NUMBER, " + measurementRecord.getJsonNumber(fieldKey));
					record.add("FALSE," + fieldKey + "," + measurementRecord.getJsonNumber(fieldKey) + ",NUMBER");
				} else if (measurementRecord.get(fieldKey).getValueType() == ValueType.STRING) {
					System.out.print("key= " + fieldKey + " ,STRING, " + measurementRecord.getString(fieldKey));
					record.add("FALSE," + fieldKey + "," + measurementRecord.getString(fieldKey) + ",STRING");
				}
			}

			valueObject.add("record", record.build());
			//
			// [{
			// "record": ["True,hasSimulationMessageType,INPUT,STRING",
			// "True,hasMeasurementDifference,FORWARD,STRING",
			// "FALSE,object,\"61A547FB-9F68-5635-BB4C-F7F537FD824C\",STRING",
			// "FALSE,attribute,\"ShuntCompensator.sections\",STRING",
			// "FALSE,value,\"0\",STRING"]
			// },
			//
			values.add(valueObject.build());

		}
		metrics.add("metrics", values.build());
		message.add("message", metrics.build());
		ret = message.build();
		System.out.println(ret.toString());
		return ret;
	}

	JsonObject simulationInput2ProvenMeasurement(JsonObject messageObject) {
		// private ProvenMessageResponse influxWriteSimulationInput(JsonObject
		// commandObject, InfluxDB influxDB,
		// String measurementName, String instanceId) {
		// ProvenMessageResponse ret = null;

		JsonObject ret = null;
		long timestamp = (long) -1;
		String differenceMrid = null;
		JsonArray forwardDifferenceArray = null;
		JsonArray reverseDifferenceArray = null;
		JsonObject inputObject = (JsonObject) messageObject.get("input");
		String simulationId = "";
		//
		// Check for simulation_id
		//
		if (inputObject.get("simulation_id") != null) {
			simulationId = inputObject.get("simulation_id").toString();
		}

		if ((simulationId.equalsIgnoreCase(""))) {
			JsonObjectBuilder measurement = Json.createObjectBuilder();
			transformationResponse("Invalid or missing message content type.  Simulation id missing.",
					Status.BAD_REQUEST.getStatusCode(), "{ \"ERROR\": \"Bad request made to time-series database.\" }");
			return ret;
		}

		JsonObject object = (JsonObject) inputObject.get("message");
		@SuppressWarnings("unchecked")
		Set<String> keys = object.keySet();
		Iterator<String> it = keys.iterator();

		//
		// Extract the timestamp if possible
		//

		if ((object.get("timestamp").getValueType() == ValueType.NUMBER)
				|| (object.get("timestamp").getValueType() == ValueType.STRING)) {
			timestamp = object.getJsonNumber("timestamp").longValue();
		}

		if (timestamp == -1) {
			transformationResponse(
					"Invalid or missing message content type.  Invalid (non-numeric epoch value) or missing measurement timestamp.",
					Status.BAD_REQUEST.getStatusCode(), "{ \"ERROR\": \"Bad request made to time-series database.\" }");

			return ret;
		}

		//
		// Initialize outer object and array builders
		//
		// {message :
		// metrics : {
		// header:[],
		// values:[
		// {record:[]},
		// {record:[]}
		// ]
		// }
		// }
		JsonObjectBuilder message = Json.createObjectBuilder();
		JsonObjectBuilder metrics = Json.createObjectBuilder();
		JsonArrayBuilder header = Json.createArrayBuilder();
		JsonArrayBuilder values = Json.createArrayBuilder();
		JsonObjectBuilder errorResponse = Json.createObjectBuilder();

		message.add("timestamp", timestamp);
		if (messageObject.get("measaurement") != null)
			message.add("measurement", messageObject.getJsonString("measurement"));
		else {
			message.add("measurement", "simulation");
		}

		header.add("is_metadata");
		header.add("label");
		header.add("value");
		header.add("value_type");
		message.add("header", header.build());
		//
		// Collect reverse and difference metrics
		//
		while (it.hasNext()) {
			String key = it.next();
			if (key.equalsIgnoreCase("difference_mrid")) {
				differenceMrid = (String) object.getString(key);
			}
			if ((object.get(key).getValueType() == ValueType.ARRAY) && (key.equalsIgnoreCase("forward_differences"))) {
				forwardDifferenceArray = (JsonArray) object.get(key);
			}
			if ((object.get(key).getValueType() == ValueType.ARRAY) && (key.equalsIgnoreCase("reverse_differences"))) {
				reverseDifferenceArray = (JsonArray) object.get(key);
			}

		}
		//
		// [{
		// "record": ["True,hasSimulationMessageType,INPUT,STRING",
		// "True,hasMeasurementDifference,FORWARD,STRING",
		// "FALSE,object,\"61A547FB-9F68-5635-BB4C-F7F537FD824C\",STRING",
		// "FALSE,attribute,\"ShuntCompensator.sections\",STRING",
		// "FALSE,value,\"0\",STRING"]
		// },
		//

		if ((forwardDifferenceArray == null) && (reverseDifferenceArray == null)) {
			transformationResponse(
					"Invalid or missing message content type. The forwarDifferenceArray or reverseDifferenceArray measurements are missing.",
					Status.BAD_REQUEST.getStatusCode(), "{ \"ERROR\": \"Bad request made to time-series database.\" }");

			return ret;
		}
		JsonObjectBuilder valueObject = Json.createObjectBuilder();
		if (forwardDifferenceArray != null) {
			values = processDifferenceArray("FORWARD", simulationId, forwardDifferenceArray, values);
		}
		if (reverseDifferenceArray != null) {
			values = processDifferenceArray("REVERSE", simulationId, reverseDifferenceArray, values);
		}
		metrics.add("metrics", values.build());
		message.add("message", metrics.build());
		ret = message.build();
		System.out.println(ret.toString());
		return ret;

	}

	//
	// Detect the type of measurement contained in disclosure message
	//

	@SuppressWarnings("unchecked")
	public String detectObjectType(Object messageObject) {
		String objectType = "";
		if (messageObject instanceof JsonObject) {

			JsonObject mObject = (JsonObject) messageObject;
			JsonObject object = (JsonObject) (mObject.get("message"));
			if (object != null) {
				objectType = "O";
				return objectType;

			}

			object = (JsonObject) mObject.get("input");
			if (object != null) {
				objectType = "I";
				return objectType;
			}

			object = (JsonObject) mObject.get("filter");
			if (object != null) {
				objectType = "Q";
				return objectType;
			}

			//
			// If it isn't a JsonObject, assume that it is a JsonArray
			// representing Alarms
			//
		} else {
			objectType = "A";
		}

		return objectType;

	}

	private MessageContentRoutingResponse writeKnowledgeMessage(ProvenMessage val) {
		MessageContentRoutingResponse ret = null;
		long now = Instant.now().toEpochMilli();
		String nowStr = ((Long) now).toString();
		ClientConfig config = new ClientConfig();
		config.getSerializationConfig().addDataSerializableFactoryClass(DisclosureIDSFactory.FACTORY_ID,
				DisclosureIDSFactory.class);
		config.setProperty("hazelcast.client.statistics.enabled", "true");
		GroupConfig groupConfig = new GroupConfig();
		groupConfig.setName("proven");
		config.setGroupConfig(groupConfig);
		config.getNetworkConfig().addAddress("127.0.0.1:5701");
		HazelcastInstance client = HazelcastClient.newHazelcastClient(config);
		IMap<String, ProvenMessage> provenMessages = client.getMap("gov.pnnl.proven.common.knowledge.message");

		System.out.println(val.toString());
		KnowledgeMessage km = new KnowledgeMessage(val);

		provenMessages.put(nowStr, (ProvenMessage) km);
		return new MessageContentRoutingResponse(Response.Status.ACCEPTED, Optional.empty(), 1);
	}

	public ResponseMessage add(ProvenMessage sourceMessage) {

		ResponseMessage ret = null;
		MessageContentRoutingResponse loadResponse = null;
		System.out.println("!!!!!!!!ADD!!!!!!!!!!!!!!!!!");
		try {

			MessageContentGroup mcg = MessageContentGroup.Knowledge;
			System.out.println("!!!!!!!!ADD!!!!!!!!!!!!!!!!!     "
					+ detectObjectType(sourceMessage.getDisclosureItem().getMessage()));
			if (detectObjectType(sourceMessage.getDisclosureItem().getMessage()).equalsIgnoreCase("I")) {
				JsonObject x = simulationInput2ProvenMeasurement((JsonObject) sourceMessage.getDisclosureItem().getMessage());
				loadResponse = writeKnowledgeMessage(sourceMessage);
			} else if (detectObjectType(sourceMessage.getDisclosureItem().getMessage()).equalsIgnoreCase("O")) {
				JsonObject x = simulationOutput2ProvenMeasurement((JsonObject) sourceMessage.getDisclosureItem().getMessage());
				loadResponse = writeKnowledgeMessage(sourceMessage);
			}
			ret = new ResponseMessage(loadResponse, sourceMessage);

		} catch (Exception ex) {

			ex.printStackTrace();

			if (null != loadResponse) {
				ret = new ResponseMessage(loadResponse, sourceMessage);

			}
			// Create a general error response
			else {
				String message = "MessageContentRouting error : " + ex.getMessage();
				MessageContentRoutingResponse errorResponse = new MessageContentRoutingResponse(
						Response.Status.INTERNAL_SERVER_ERROR, Optional.of(message), 0);
				ret = new ResponseMessage(errorResponse, sourceMessage);
			}
		}
		return ret;
	}

}
