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
import static gov.pnnl.proven.cluster.lib.disclosure.item.DisclosureItemState.New;
import static gov.pnnl.proven.cluster.lib.disclosure.item.Validatable.readNullable;
import static gov.pnnl.proven.cluster.lib.disclosure.item.Validatable.rw;
import static gov.pnnl.proven.cluster.lib.disclosure.item.Validatable.writeNullable;
import static gov.pnnl.proven.cluster.lib.disclosure.item.Validatable.ww;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import javax.json.JsonObject;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

import org.leadpony.justify.api.InstanceType;
import org.leadpony.justify.api.JsonSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureIDSFactory;
import gov.pnnl.proven.cluster.lib.disclosure.MessageContent;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.exchange.BufferedItem;

/**
 * Immutable class representing data disclosed to the Proven platform.
 * 
 * @author d3j766
 * 
 */
public class DisclosureItem implements BufferedItem, Validatable, IdentifiedDataSerializable {

	static Logger log = LoggerFactory.getLogger(DisclosureItem.class);

	// Jsonb property names
	private static final String MESSAGE_ID_PROP = "messageId";
	private static final String SYSTEM_SENT_TIME_PROP = "systemSentTime";
	private static final String SOURCE_MESSAGE_ID_PROP = "sourceMessageId";
	private static final String BUFFERED_STATE_PROP = "bufferedState";
	private static final String AUTH_TOKEN_PROP = "content";
	private static final String CONTEXT_PROP = "item";
	private static final String MESSAGE_PROP = "domain";
	private static final String MESSAGE_SCHEMA_PROP = "requestor";
	private static final String IS_TRANSIENT_PROP = "name";
	private static final String IS_LINKED_DATA_PROP = "tags";
	private static final String APPLICATION_SENT_TIME_PROP = "applicationSentTime";

	// Defaults
	private static final MessageContext DEFAULT_CONTEXT = MessageContext.newBuilder().build();

	// Exchange processing state
	private DisclosureItemState bufferedState;

	// Identification
	private UUID messageId;
	private UUID sourceMessageId;

	// Sent times
	Long systemSentTime;
	Long applicationSentTime;

	// Jsonb Properties
	private String authToken;
	private MessageContext context;
	private JsonObject message;
	private JsonObject messageSchema;
	private boolean isTransient;
	private boolean isLinkedData;

	// HZ Serialization
	public DisclosureItem() {
	}

	// TODO - REMOVE
	public DisclosureItem(JsonObject obj) {
	}
	public DisclosureItem(String str) {
	}	
	
	/**
	 * Creation based on a previous DisclosureItem. Provided message item will
	 * be the new payload.
	 * 
	 * @param di
	 *            original message
	 * @param mi
	 *            new payload
	 */
	public static DisclosureItem createFromDisclosureItem(DisclosureItem di, MessageItem mi) {
		return DisclosureItem.newBuilder().withDisclosureItem(di).withMessage(mi.toJson()).build();
	}

	@JsonbCreator
	public static DisclosureItem createDisclosureItem(@JsonbProperty(AUTH_TOKEN_PROP) String authToken,
			@JsonbProperty(CONTEXT_PROP) JsonObject context, @JsonbProperty(MESSAGE_PROP) JsonObject message,
			@JsonbProperty(MESSAGE_SCHEMA_PROP) JsonObject messageSchema,
			@JsonbProperty(IS_TRANSIENT_PROP) boolean isTransient,
			@JsonbProperty(IS_LINKED_DATA_PROP) boolean isLinkedData)
			throws InstantiationException, IllegalAccessException, IOException {
		return DisclosureItem.newBuilder().withAuthToken(authToken).withContext(context).withMessage(message)
				.withMessageSchema(messageSchema).withIsTransient(isTransient).withIsLinkedData(isLinkedData).build();
	}

	private DisclosureItem(Builder b) {
		this.messageId = UUID.randomUUID();
		this.systemSentTime = new Date().getTime();
		this.applicationSentTime = b.applicationSentTime;
		this.bufferedState = New;
		this.sourceMessageId = b.sourceMessageId;
		this.authToken = b.authToken;
		this.context = (null != b.context) ? (b.context) : (DEFAULT_CONTEXT);
		this.message = b.message;
		this.messageSchema = b.messageSchema;
		this.isTransient = b.isTransient;
		this.isLinkedData = b.isLinkedData;
	}

	@JsonbProperty(MESSAGE_ID_PROP)
	public UUID getMessageId() {
		return messageId;
	}

	@JsonbProperty(SYSTEM_SENT_TIME_PROP)
	public Long getSystemSentTime() {
		return systemSentTime;
	}

	@JsonbProperty(APPLICATION_SENT_TIME_PROP)
	public Long getApplicationSentTime() {
		return applicationSentTime;
	}

	@JsonbProperty(SOURCE_MESSAGE_ID_PROP)
	public UUID getSourceMessageId() {
		return sourceMessageId;
	}

	@JsonbProperty(BUFFERED_STATE_PROP)
	public DisclosureItemState getBufferedState() {
		return bufferedState;
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
	public JsonObject getMessage() {
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

		private UUID sourceMessageId;
		private Long applicationSentTime;
		private String authToken;
		private MessageContext context;
		private JsonObject message;
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

		public Builder withContext(JsonObject context)
				throws InstantiationException, IllegalAccessException, IOException {
			this.context = Validatable.toValidatable(MessageContext.class, context.toString(), true);
			return this;
		}

		public Builder withContext(MessageContext context) {
			this.context = context;
			return this;
		}

		public Builder withMessage(JsonObject message) {
			this.message = message;
			return this;
		}

		public Builder withMessage(MessageItem message) {
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
					.withAuthToken(di.getAuthToken()).withContext(di.getContext()).withMessage(di.getMessage())
					.withMessageSchema(di.getMessageSchema()).withIsTransient(di.getIsTransient())
					.withIsLinkedData(di.getIsLinkedData());
		}

		public DisclosureItem build() {
			return new DisclosureItem(this);
		}
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		writeNullable(getAuthToken(), out, ww((v, o) -> out.writeUTF(v)));
		this.context.writeData(out);
		out.writeByteArray(jsonValueOut(getMessage()));
		writeNullable(getMessageSchema(), out, ww((v, o) -> out.writeByteArray(jsonValueOut(getMessageSchema()))));
		out.writeBoolean(isTransient);
		out.writeBoolean(isLinkedData);
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		this.authToken = readNullable(in, rw((i) -> i.readUTF()));
		this.context = new MessageContext();
		this.context.readData(in);
		this.message = (JsonObject) jsonValueIn(in.readByteArray());
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
	public DisclosureItemState getItemState() {
		return this.bufferedState;
	}

	@Override
	public void setItemState(DisclosureItemState bufferedState) {
		this.bufferedState = bufferedState;
	}

	@Override
	public MessageContent getMessageContent() {
		return this.context.getContent();
	}

	@Override
	public JsonSchema toSchema() {

		JsonSchema ret;

		//@formatter:off
		
			ret = sbf.createBuilder()

					.withId(Validatable.schemaId(this.getClass()))
					
					.withSchema(Validatable.schemaDialect())
					
					.withTitle("Message context schema")

					.withDescription(
							"Defines the context of a proven disclosure, which identifies its "
						  + "processing and storage requirements within the platform.")

					.withType(InstanceType.OBJECT)

					.withProperty(CONTEXT_PROP, Validatable.retrieveSchema(MessageContext.class))
					
					.withProperty(MESSAGE_PROP, sbf.createBuilder()							
							.withType(InstanceType.OBJECT)
							.build())
					
					.build();
			
			//@formatter:on

		return ret;
	}

}
