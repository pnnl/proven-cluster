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
package gov.pnnl.proven.cluster.lib.disclosure.message;

import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;
import javax.json.JsonObject;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.everit.json.schema.ValidationException;

/**
 * Abstract class for disclosure messages. Disclosure messages represent the
 * original input message disclosed to the platform. It is responsible for
 * transforming its message content into either a {@code KnowledgeMessage} or
 * {@code RequestMessage} in order to support downstream message processing.
 * 
 * @author d3j766
 *
 */
public class DisclosureMessage extends ProvenMessage implements IdentifiedDataSerializable, Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * This represents the message content type of the disclosed content itself.
	 */
	MessageContent disclosedContent;

	/**
	 * True, if the disclosed content is in the Request message group.
	 */
	boolean isRequest;

	/**
	 * True, if the disclosed content is in the Knowledge message group.
	 */
	boolean isKnowledge;

	/**
	 * True, if the disclosed content is {@code MessageContent#Measurement}.
	 */
	boolean hasMeasurements;
	
	/**
	 * mimeType can be application/json or application/jsonld.
	 */
	String mimeType;

	public DisclosureMessage() {
	}

	public DisclosureMessage(JsonObject message) throws Exception {
		this(message, null);
	}

	public DisclosureMessage(JsonObject message, JsonObject schema) throws Exception {
		super(message, schema);

		// TODO these are assumed default characteristics of the disclosed
		// message for an initial implementation (i.e. explicit disclosure of
		// measurement data). Need to add internal methods to examine the JSON
		// to set the member properties based on the actual content of the
		// message.
		
		MessageModel mm = MessageModel.getInstance();
		try {
			String jsonApi = mm.getModelFile("proven-schema.json");
			//System.out.println(jsonApi);
			JSONObject jsonApiSchema = new JSONObject(new JSONTokener(jsonApi));
			Schema jsonSchema = SchemaLoader.load(jsonApiSchema);
			//System.out.println(message.toString());
			jsonSchema.validate(new JSONObject(message.toString()));	
			System.out.println("Valid JSON Data");
			} catch (ValidationException e) {
				e.printStackTrace();
				System.out.println(e.getCausingExceptions());
				System.out.println(e.getAllMessages());
				System.out.println("Invalid JSON Data");

			}
		//this.domain = new DisclosureDomain(message.get("domain").toString());
		this.name = message.getJsonString("name").getString();
		this.authToken = message.getJsonString("authToken").getString();
		this.requester =  message.getJsonString("requestorId").getString();
		this.isStatic = message.get("isStatic") != null;
		this.isTransient = message.get("isTransient") != null;
		this.disclosureId = message.getJsonString("disclosureId").getString();
		String content_type = message.getJsonString("content").getString();
		if(content_type.equalsIgnoreCase("explicit")) {
			this.disclosedContent = MessageContent.Explicit;
			this.isRequest = false;
			this.isKnowledge = true;
			this.hasMeasurements = true;
			this.mimeType = message.getJsonString("mimeType").getString();
		}

		else if (content_type.equalsIgnoreCase("query")) {
			this.disclosedContent = MessageContent.Query;
			this.isRequest = true;
			this.isKnowledge = false;
			this.hasMeasurements = false;
		}
		this.message = (JsonObject) message.get("message");
		/*this.disclosedContent = MessageContent.Explicit;
		this.isRequest = false;
		this.isKnowledge = true;
		this.hasMeasurements = true;*/
	}

	/**
	 * TODO Implement.  Provides a new KnowledgeMessage based on the the disclosed
	 * message; only if the disclosed content type is in
	 * {@code MessageGroup#Knowledge}.  
	 */
	public Optional<KnowledgeMessage> getKnowledgeMessage() {
		Optional<KnowledgeMessage> ret = Optional.empty();
		return ret;
	}

	/**
	 * TODO Implement.  Provides a new RequestMessage based on the the disclosed
	 * message; only if the disclosed content type is in
	 * {@code MessageGroup#Request}.
	 */
	public Optional<RequestMessage> getRequestMessage() {
		Optional<RequestMessage> ret = Optional.empty();
		return ret;
	}

	@Override
	public MessageContent getMessageContent() {
		return MessageContent.Disclosure;
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		super.readData(in);
		this.disclosedContent = MessageContent.valueOf(in.readUTF());
		this.isRequest = in.readBoolean();
		this.isKnowledge = in.readBoolean();
		this.hasMeasurements = in.readBoolean();
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		super.writeData(out);
		out.writeUTF(this.disclosedContent.toString());
		out.writeBoolean(this.isRequest);
		out.writeBoolean(this.isKnowledge);
		out.writeBoolean(this.hasMeasurements);
	}
	
	@Override
	public int getFactoryId() {
		return ProvenMessageIDSFactory.FACTORY_ID;
	}

	@Override
	public int getId() {
		return ProvenMessageIDSFactory.DISCLOSURE_MESSAGE_TYPE;
	}	

}
