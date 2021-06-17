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
package gov.pnnl.proven.cluster.lib.disclosure.item;

import static gov.pnnl.proven.cluster.lib.disclosure.DisclosureIDSFactory.jsonValueIn;
import static gov.pnnl.proven.cluster.lib.disclosure.DisclosureIDSFactory.jsonValueOut;
import static gov.pnnl.proven.cluster.lib.disclosure.item.MessageItem.messageName;
import static gov.pnnl.proven.cluster.lib.disclosure.item.Validatable.readNullable;
import static gov.pnnl.proven.cluster.lib.disclosure.item.Validatable.rw;
import static gov.pnnl.proven.cluster.lib.disclosure.item.Validatable.writeNullable;
import static gov.pnnl.proven.cluster.lib.disclosure.item.Validatable.ww;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

import org.leadpony.justify.api.InstanceType;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidatingException;
import org.leadpony.justify.api.Problem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureIDSFactory;
import gov.pnnl.proven.cluster.lib.disclosure.exception.ValidatableBuildException;

/**
 * Immutable class representing data disclosed to a Proven platform.
 * 
 * @author d3j766
 * 
 */
public class DisclosureItem implements Validatable, IdentifiedDataSerializable {

	static Logger log = LoggerFactory.getLogger(DisclosureItem.class);

	// Jsonb property names
	public static final String MESSAGE_ID_PROP = "messageId";
	public static final String SYSTEM_SENT_TIME_PROP = "systemSentTime";
	public static final String SOURCE_MESSAGE_ID_PROP = "sourceMessageId";
	public static final String AUTH_TOKEN_PROP = "authToken";
	public static final String CONTEXT_PROP = "context";
	public static final String MESSAGE_PROP = "message";
	public static final String MESSAGE_SCHEMA_PROP = "messageSchema";
	public static final String IS_TRANSIENT_PROP = "isTransient";
	public static final String IS_LINKED_DATA_PROP = "isLinkedData";
	public static final String APPLICATION_SENT_TIME_PROP = "applicationSentTime";

	private UUID messageId;
	private UUID sourceMessageId;
	private Long systemSentTime;
	private Long applicationSentTime;
	private String authToken;
	private MessageContext context;
	private JsonStructure message;
	private JsonObject messageSchema;
	private boolean isTransient;
	private boolean isLinkedData;

	// Necessary for HZ Serialization
	public DisclosureItem() {
	}

	// TODO - REMOVE
	public DisclosureItem(JsonObject obj) {
	}

	public DisclosureItem(String str) {
	}
	// TODO - REMOVE

	/**
	 * Creation based on a previous DisclosureItem. Provided message item will
	 * be assigned as the new payload.
	 * 
	 * @param di
	 *            original disclosure item
	 * @param mi
	 *            new payload
	 */
	public static DisclosureItem createFromDisclosureItem(DisclosureItem di, MessageItem mi) {
		return DisclosureItem.newBuilder().withDisclosureItem(di).withMessage(mi.toJson()).build(true);
	}

	/**
	 * JSON-B constructor.
	 * 
	 * Should only be called for JSON-B deserialization via
	 * {@link Validatable#toValidatable}
	 * 
	 */

	@JsonbCreator
	public static DisclosureItem createDisclosureItem(
			@JsonbProperty(APPLICATION_SENT_TIME_PROP) long applicationSentTime,
			@JsonbProperty(AUTH_TOKEN_PROP) String authToken, @JsonbProperty(CONTEXT_PROP) MessageContext context,
			@JsonbProperty(MESSAGE_PROP) JsonStructure message,
			@JsonbProperty(MESSAGE_SCHEMA_PROP) JsonObject messageSchema,
			@JsonbProperty(IS_TRANSIENT_PROP) boolean isTransient,
			@JsonbProperty(IS_LINKED_DATA_PROP) boolean isLinkedData) {
		return DisclosureItem.newBuilder().withApplicationSentTime(applicationSentTime).withAuthToken(authToken)
				.withContext(context).withMessage(message).withMessageSchema(messageSchema).withIsTransient(isTransient)
				.withIsLinkedData(isLinkedData).build(true);
	}

	private DisclosureItem(Builder b) {

		this.messageId = UUID.randomUUID();
		this.sourceMessageId = b.sourceMessageId;
		this.systemSentTime = new Date().getTime();

		// Schema props
		this.applicationSentTime = b.applicationSentTime;
		this.authToken = b.authToken;
		this.context = b.context;
		this.message = b.message;
		this.messageSchema = b.messageSchema;
		this.isTransient = b.isTransient;
		this.isLinkedData = b.isLinkedData;

	}

	@JsonbProperty(MESSAGE_ID_PROP)
	public UUID getMessageId() {
		return messageId;
	}

	@JsonbProperty(SOURCE_MESSAGE_ID_PROP)
	public UUID getSourceMessageId() {
		return sourceMessageId;
	}

	@JsonbProperty(SYSTEM_SENT_TIME_PROP)
	public Long getSystemSentTime() {
		return systemSentTime;
	}

	@JsonbProperty(APPLICATION_SENT_TIME_PROP)
	public Long getApplicationSentTime() {
		return applicationSentTime;
	}

	@JsonbProperty(AUTH_TOKEN_PROP)
	public String getAuthToken() {
		return authToken;
	}

	@JsonbProperty(CONTEXT_PROP)
	public MessageContext getContext() {
		return context;
	}

	@JsonbProperty(MESSAGE_PROP)
	public JsonStructure getMessage() {
		return message;
	}

	@JsonbProperty(value = MESSAGE_SCHEMA_PROP)
	public JsonObject getMessageSchema() {
		return messageSchema;
	}

	@JsonbProperty(IS_TRANSIENT_PROP)
	public boolean getIsTransient() {
		return isTransient;
	}

	@JsonbProperty(IS_LINKED_DATA_PROP)
	public boolean getIsLinkedData() {
		return isLinkedData;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {

		private boolean trustedMessageItem = false;
		private UUID sourceMessageId;
		private Long applicationSentTime;
		private String authToken;
		private MessageContext context;
		private JsonStructure message;
		private JsonObject messageSchema;
		private boolean isTransient;
		private boolean isLinkedData;

		private Builder() {
		}

		public Builder withSourceMessageId(UUID sourceMessageId) {
			this.sourceMessageId = sourceMessageId;
			return this;
		}

		public Builder withApplicationSentTime(Long applicationSentTime) {
			this.applicationSentTime = applicationSentTime;
			return this;
		}

		public Builder withAuthToken(String authToken) {
			this.authToken = authToken;
			return this;
		}

		public Builder withContext(JsonObject context) {
			this.context = Validatable.toValidatable(MessageContext.class, context.toString(), true);
			return this;
		}

		public Builder withContext(MessageContext context) {
			this.context = context;
			return this;
		}

		public Builder withMessage(JsonStructure message) {
			this.trustedMessageItem = false;
			this.message = message;
			return this;
		}

		public Builder withMessage(MessageItem message) {
			this.trustedMessageItem = true;
			this.message = message.toJson();
			return this;
		}

		public Builder withMessageSchema(JsonObject messageSchema) {
			this.messageSchema = messageSchema;
			return this;
		}

		public Builder withIsTransient(boolean isTransient) {
			this.isTransient = isTransient;
			return this;
		}

		public Builder withIsLinkedData(boolean isLinkedData) {
			this.isLinkedData = isLinkedData;
			return this;
		}

		public Builder withDisclosureItem(DisclosureItem di) {
			return withSourceMessageId(di.getSourceMessageId()).withApplicationSentTime(di.getApplicationSentTime())
					.withAuthToken(di.getAuthToken()).withContext(di.getContext()).withIsTransient(di.getIsTransient())
					.withIsLinkedData(di.getIsLinkedData());
		}

		/**
		 * Builds new instance. Instance is validated post construction.
		 * 
		 * @return new instance
		 * 
		 * @throws JsonValidatingException
		 *             if created instance fails JSON-SCHEMA validation.
		 * 
		 */
		public DisclosureItem build() {
			return build(false);
		}

		private DisclosureItem build(boolean trustedBuilder) {

			DisclosureItem ret = new DisclosureItem(this);
			

			if (!trustedBuilder) {
				List<Problem> problems = ret.validate();
				if (!problems.isEmpty()) {
					throw new ValidatableBuildException("Builder failure", new JsonValidatingException(problems));
				}
			}
			
			if (!trustedMessageItem) {
				List<Problem> problems = Validatable.validate(context.getItem(), message.toString());
				if (!problems.isEmpty()) {
					throw new ValidatableBuildException("Builder failure", new JsonValidatingException(problems));
				}				
			}

			if (ret.getIsLinkedData()) {
				if (!Validatable.isValidJsonLD(ret.getMessage())) {
					throw new ValidatableBuildException("Builder failure: Invalid JSON-LD included in payload");
				}
			}

			return ret;
		}
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		out.writeUTF(getMessageId().toString());
		writeNullable(getSourceMessageId().toString(), out, ww((v, o) -> out.writeUTF(v)));
		out.writeLong(getSystemSentTime());
		writeNullable(getApplicationSentTime(), out, ww((v, o) -> out.writeLong(v)));
		writeNullable(getAuthToken(), out, ww((v, o) -> out.writeUTF(v)));
		this.context.writeData(out);
		out.writeByteArray(jsonValueOut(getMessage()));
		writeNullable(getMessageSchema(), out, ww((v, o) -> out.writeByteArray(jsonValueOut(getMessageSchema()))));
		out.writeBoolean(isTransient);
		out.writeBoolean(isLinkedData);
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		this.messageId = UUID.fromString(in.readUTF());
		this.sourceMessageId = readNullable(in, rw((i) -> UUID.fromString(in.readUTF())));
		this.systemSentTime = in.readLong();
		this.applicationSentTime = readNullable(in, rw((i) -> i.readLong()));
		this.authToken = readNullable(in, rw((i) -> i.readUTF()));
		this.context = new MessageContext();
		this.context.readData(in);
		this.message = (JsonStructure) jsonValueIn(in.readByteArray());
		this.messageSchema = readNullable(in, rw((i) -> (JsonObject) jsonValueIn(i.readByteArray())));
		this.isTransient = in.readBoolean();
		this.isLinkedData = in.readBoolean();
	}

	@JsonbTransient
	@Override
	public int getFactoryId() {
		return DisclosureIDSFactory.FACTORY_ID;
	}

	@JsonbTransient
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return DisclosureIDSFactory.MESSAGE_CONTEXT_TYPE;
	}

	@Override
	public JsonSchema toSchema() {

		JsonSchema ret;

		//@formatter:off
		
		ret = sbf.createBuilder()

				.withId(Validatable.schemaId(this.getClass()))
				
				.withSchema(Validatable.schemaDialect())
				
				.withTitle("Disclosure item message schema")

				.withDescription(
					"Proven's message container for information disclosure to the platform.  It contains "
				  + "general information pertaining to the contained message as well as the messsage itself, "
				  + "which is provided in the 'message' object")

				.withType(InstanceType.OBJECT)

				.withProperty(APPLICATION_SENT_TIME_PROP, sbf.createBuilder()
					.withTitle("Application message sent time")
					.withDescription("Sent time defined by message's sender.  "
					 + "This is an epoch timestamp formatted in milliseconds.")
					.withType(InstanceType.INTEGER, InstanceType.NULL)
					.withDefault(JsonValue.NULL)
					.withMinimum(0)
					.withMaximum(Long.MAX_VALUE)
					.build())					
				
				.withProperty(AUTH_TOKEN_PROP, sbf.createBuilder()							
					.withType(InstanceType.STRING, InstanceType.NULL)
					.withDefault(JsonValue.NULL)
					.withPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$")
					.build())
				
				.withProperty(CONTEXT_PROP, Validatable.retrieveSchema(MessageContext.class))
				
				.withProperty(MESSAGE_PROP, sbf.createBuilder()
					.withTitle("Message payload")
					.withDescription("Contains a message item of type selected in the 'context' property.")
					.withType(InstanceType.OBJECT, InstanceType.ARRAY)
					.withComment("Json will be validated for the item type selected.")
					.build())

				.withProperty(MESSAGE_SCHEMA_PROP, sbf.createBuilder()							
					.withType(InstanceType.OBJECT, InstanceType.NULL)
					.withDefault(JsonValue.NULL)
					.withComment("Unsupported")
					.build())

				.withProperty(IS_TRANSIENT_PROP, sbf.createBuilder()
					.withTitle("Transient message")
					.withDescription("If true, message will not be archived.  "
					 + "The content will be distributed to the Hybrid Store's IMDG and will be removed "
					 + "based on its domain's expiration policy and will not be added to a registered archive.")
					.withType(InstanceType.BOOLEAN)
					.withDefault(JsonValue.FALSE)
					.build())
				
				.withProperty(IS_LINKED_DATA_PROP, sbf.createBuilder()
					.withTitle("Linked Data message")
					.withDescription("if true, indicates message is in JSON-LD format.  "
					 + "The selecetd Message item type must be " + MessageItem.messageName(ExplicitItem.class)
					 + " for this property to be true.")
					.withType(InstanceType.BOOLEAN)
					.withDefault(JsonValue.FALSE)
					.build())
			
				.withIf(sbf.createBuilder()
					.withProperty(IS_LINKED_DATA_PROP, sbf.createBuilder()
							.withConst(JsonValue.TRUE)
							.build())
					.build())
				.withThen(sbf.createBuilder()
					.withProperty(CONTEXT_PROP, sbf.createBuilder()
							.withProperty(MessageContext.ITEM_PROP, sbf.createBuilder()
									.withAnyOf(
									  sbf.createBuilder().withConst(Json.createValue(messageName(ExplicitItem.class))).build(),
									  sbf.createBuilder().withConst(Json.createValue(messageName(ImplicitItem.class))).build())
									.build())
							.build())
					.build())
				
				.withRequired(MESSAGE_PROP, CONTEXT_PROP)
				
				.build();
			
			//@formatter:on

		return ret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((messageId == null) ? 0 : messageId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DisclosureItem)) {
			return false;
		}
		DisclosureItem other = (DisclosureItem) obj;
		if (messageId == null) {
			if (other.messageId != null) {
				return false;
			}
		} else if (!messageId.equals(other.messageId)) {
			return false;
		}
		return true;
	}

}
