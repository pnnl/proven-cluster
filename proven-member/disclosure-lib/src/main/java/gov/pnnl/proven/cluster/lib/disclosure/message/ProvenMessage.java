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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonSerializable;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.disclosure.DomainProvider;

/**
 * General messaging construct used by Proven to communicate data between Proven
 * application components. Proven messages have global identification and a
 * defined processing life-cycle within a Proven cluster member.
 * 
 * @author raju332
 * @author d3j766
 *
 */
public abstract class ProvenMessage implements IdentifiedDataSerializable, Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ProvenMessage.class);

	/**
	 * Message KEY
	 * 
	 * Message keys are used to identify proven messages in the memory grid of
	 * Proven's hybrid store.
	 */
	public static final String MESSAGE_KEY_DELIMETER = "^||^";

	public enum MessageKeyPartOrder {
		MessageId,
		Domain,
		Name,
		Source,
		Created;
	};

	/**
	 * Provides the KEY for a ProvenMessage being stored in a message stream.
	 * 
	 * @return a String representing the KEY
	 */
	public String getMessageKey() {

		String ret = "";
		ret += this.messageId + MESSAGE_KEY_DELIMETER;
		ret += ((this.domain == null) ? "" : this.domain.getDomain()) + MESSAGE_KEY_DELIMETER;
		ret += this.created;

		return ret;
	}

	/**
	 * Epoch time of message creation.
	 */
	Long created;

	/**
	 * Messages are stored internally as JSON
	 */
	JsonObject message;

	/**
	 * JSON schema, can be disclosed with message.
	 */
	JsonObject messageSchema;

	/**
	 * Messages are assigned an identifier , making it unique across disclosure
	 * sources.
	 */
	UUID messageId;

	/**
	 * Messages may be assigned a disclosure identifier provided by the
	 * discloser as part of the message. This identifier is not managed by the
	 * platform, but is included in response messages as a convenience to the
	 * discloser. Null by default.
	 */
	String disclosureId;

	/**
	 * Identifies messages domain, all messages must be associated with a
	 * domain. If one is not provided, Proven provides a default domain for
	 * which the message will be associated. In Proven, a domain represents a
	 * discrete sphere or model of activity or knowledge. That is, it identifies
	 * a grouping of knowledge that is managed separately from other domain
	 * knowledge models. In proven's hybrid store, domain knowledge is isolated
	 * by sub-graphs in its semantic store, by databases in time-series store,
	 * and by key values in distributed Map structures in the memory grid.
	 */
	DisclosureDomain domain;

	/**
	 * If true, message content will not be persisted in the semantic or
	 * time-series components of the hybrid store. Default is false.
	 */
	boolean isTransient;

	/**
	 * If true, message content will remain in memory grid facet of hybrid store
	 * unless explicitly removed, @see {@link MessageContent#Static}. Default is
	 * false.
	 */
	boolean isStatic;

	/**
	 * Identifies client requester responsible for disclosure of this message.
	 * 
	 * TODO - for now this is a simple string, but this value should be
	 * formalized on client side in JSON disclosure schema.
	 * 
	 */
	String requester;

	public ProvenMessage() {

	}

	public ProvenMessage(JsonObject message) {
		this(message, null);
	}

	public ProvenMessage(JsonObject message, JsonObject schema) {

		// Epoch creation time
		this.created = new Date().getTime();

		// This must not be null - serialization will throw an NPE
		this.message = message;

		// Optional - may be null
		this.messageSchema = schema;

		// Generated for each message - global identifier
		this.messageId = UUID.randomUUID();

		// TODO - determine field value from the message -or- transfer when
		// creating a non-disclosure message.
		this.disclosureId = "Not Provided";

		// messageContent must be provided in a getter by the concrete class

		// TODO - determine field value from the message. Using Proven's domain
		// by default.
		this.domain = DomainProvider.getProvenDisclosureDomain();

		// TODO - determine field value from the message
		this.isTransient = false;

		// TODO - determine field value from the message
		this.isStatic = false;

		// TODO - determine field value from the message
		this.requester = "UNKNOWN";

	}

	public ProvenMessage(ProvenMessage sourceMessage, JsonObject message) {
		this(sourceMessage, message, null);
	}

	public ProvenMessage(ProvenMessage sourceMessage, JsonObject message, JsonObject schema) {

		// Epoch creation time
		this.created = new Date().getTime();

		// This must not be null - serialization will throw an NPE
		this.message = message;

		// Optional - may be null
		this.messageSchema = schema;

		// Generated for each message - global identifier
		this.messageId = UUID.randomUUID();

		this.disclosureId = sourceMessage.getDisclosureId();

		// MessageContent must be provided in a getter by the concrete class

		this.domain = sourceMessage.getDomain();
		this.isTransient = sourceMessage.isTransient;
		this.isStatic = sourceMessage.isStatic;
		this.requester = sourceMessage.getRequester();
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		this.created = in.readLong();
		this.message = MessageJsonUtils.jsonIn(in.readByteArray());

		boolean nullMessageSchema = in.readBoolean();
		if (!nullMessageSchema)
			this.messageSchema = MessageJsonUtils.jsonIn(in.readByteArray());

		this.messageId = UUID.fromString(in.readUTF());
		this.disclosureId = in.readUTF();
		this.domain = in.readObject();
		this.isTransient = in.readBoolean();
		this.isStatic = in.readBoolean();
		this.requester = in.readUTF();
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		out.writeLong(this.created);
		out.writeByteArray(MessageJsonUtils.jsonOut(this.message));

		boolean nullMessageSchema = (null == this.messageSchema);
		out.writeBoolean(nullMessageSchema);
		if (!nullMessageSchema)
			out.writeByteArray(MessageJsonUtils.jsonOut(this.messageSchema));

		out.writeUTF(this.messageId.toString());
		out.writeUTF(this.disclosureId);
		out.writeObject(this.domain);
		out.writeBoolean(this.isTransient);
		out.writeBoolean(this.isStatic);
		out.writeUTF(this.requester);
	}

	public JsonObject getMessage() {
		return message;
	}
	
	public String getMessageStr(boolean pretty) {
		
		String ret;
		
		if (pretty) {
			ret = MessageJsonUtils.prettyPrint(message);
		}
		else {
			ret = message.toString();
		}
		
		return ret;
	}
	
	public UUID getMessageId() {
		return messageId;
	}

	public String getDisclosureId() {
		return disclosureId;
	}

	public abstract MessageContent getMessageContent();

	public DisclosureDomain getDomain() {
		return domain;
	}

	public boolean isTransient() {
		return isTransient;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public String getRequester() {
		return requester;
	}

}
