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

import static gov.pnnl.proven.cluster.lib.disclosure.item.Validatable.readNullable;
import static gov.pnnl.proven.cluster.lib.disclosure.item.Validatable.rw;
import static gov.pnnl.proven.cluster.lib.disclosure.item.Validatable.writeNullable;
import static gov.pnnl.proven.cluster.lib.disclosure.item.Validatable.ww;

import java.io.IOException;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

import org.leadpony.justify.api.InstanceType;
import org.leadpony.justify.api.JsonSchema;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.disclosure.DisclosureIDSFactory;
import gov.pnnl.proven.cluster.lib.disclosure.DomainProvider;
import gov.pnnl.proven.cluster.lib.disclosure.MessageContent;

/**
 * Represents the context for a disclosure item. The context helps to identify
 * both exchange processing and hybrid-store message storage locations. This is
 * an immutable class.
 * 
 * @author d3j766
 * 
 * @see DisclosureItem
 *
 */
public class MessageContext implements Validatable, IdentifiedDataSerializable {

	// Jsonb property names and rules
	private static final String CONTENT_PROP = "content";
	private static final String ITEM_PROP = "item";
	private static final String DOMAIN_PROP = "domain";
	private static final String REQUESTOR_PROP = "requestor";
	private static final String NAME_PROP = "name";
	private static final String TAGS_PROP = "tags";
	private static final int MAX_TAGS = 10;

	// Defaults
	private static final MessageContent DEFAULT_CONTENT = MessageContent.Explicit;
	private static final Class<? extends MessageItem> DEFAULT_ITEM = ExplicitItem.class;
	private static final DisclosureDomain DEFAULT_DOMAIN = DomainProvider.getProvenDisclosureDomain();

	// Properties
	private MessageContent content;
	private Class<? extends MessageItem> item;
	private DisclosureDomain domain;
	private String requestor;
	private String name;
	private String[] tags;

	// HZ serialization
	public MessageContext() {
	}

	@JsonbCreator
	public static MessageContext createMessageContext(@JsonbProperty(CONTENT_PROP) String content,
			@JsonbProperty(ITEM_PROP) String item, @JsonbProperty(DOMAIN_PROP) String domain,
			@JsonbProperty(REQUESTOR_PROP) String requestor, @JsonbProperty(NAME_PROP) String name,
			@JsonbProperty(TAGS_PROP) String[] tags) {
		return MessageContext.newBuilder().withContent(content).withItem(item).withDomain(domain)
				.withRequestor(requestor).withName(name).withTags(tags).build();
	}

	private MessageContext(Builder b) {
		this.content = (null != b.content) ? (b.content) : (DEFAULT_CONTENT);
		this.item = (null != b.item) ? (b.item) : (DEFAULT_ITEM);
		this.domain = (null != b.domain) ? (b.domain) : (DEFAULT_DOMAIN);
		this.requestor = b.requestor;
		this.name = b.name;
		this.tags = (null != b.tags) ? (b.tags) : (new String[] {});
	}

	@JsonbProperty(CONTENT_PROP)
	public MessageContent getContent() {
		return content;
	}

	@JsonbProperty(ITEM_PROP)
	public Class<? extends MessageItem> getItem() {
		return item;
	}

	@JsonbProperty(DOMAIN_PROP)
	public DisclosureDomain getDomain() {
		return domain;
	}

	@JsonbProperty(value = REQUESTOR_PROP)
	public String getRequestor() {
		return requestor;
	}

	@JsonbProperty(NAME_PROP)
	public String getName() {
		return name;
	}

	@JsonbProperty(TAGS_PROP)
	public String[] getTags() {
		return tags;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {

		private MessageContent content;
		private Class<? extends MessageItem> item;
		private DisclosureDomain domain;
		private String requestor;
		private String name;
		private String[] tags;

		private Builder() {
		}

		public Builder withContent(MessageContent content) {
			this.content = content;
			return this;
		}

		public Builder withContent(String content) {
			this.content = MessageContent.getMessageContent(content);
			return this;
		}

		public Builder withItem(Class<? extends MessageItem> item) {
			this.item = item;
			return this;
		}

		public Builder withItem(String item) {
			this.item = MessageItem.messageType(item);
			return this;
		}

		public Builder withDomain(DisclosureDomain domain) {
			this.domain = domain;
			return this;
		}

		public Builder withDomain(String domain) {
			this.domain = new DisclosureDomain(domain);
			return this;
		}

		public Builder withRequestor(String requestor) {
			this.requestor = requestor;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withTags(String[] tags) {
			this.tags = tags;
			return this;
		}

		public MessageContext build() {
			return new MessageContext(this);
		}
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		out.writeUTF(getContent().toString());
		out.writeObject(item);
		getDomain().writeData(out);
		writeNullable(getRequestor(), out, ww((v, o) -> out.writeUTF(v)));
		writeNullable(getName(), out, ww((v, o) -> out.writeUTF(v)));
		writeNullable(getTags(), out, ww((v, o) -> out.writeUTFArray(v)));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readData(ObjectDataInput in) throws IOException {
		this.content = MessageContent.getMessageContent(in.readUTF());
		this.item = (Class<? extends MessageItem>) in.readObject();
		this.domain = new DisclosureDomain();
		this.domain.readData(in);
		this.requestor = readNullable(in, rw((i) -> i.readUTF()));
		this.name = readNullable(in, rw((i) -> i.readUTF()));
		this.tags = readNullable(in, rw((i) -> i.readUTFArray()));
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

		// JSON-B defaults and enumerations
		JsonString defaultContent = Json.createValue(DEFAULT_CONTENT.getName());
		Set<JsonValue> contents = Validatable.toJsonValues(MessageContent.getNames(true));
		JsonString defaultItem;
		try {
			defaultItem = Json.createValue(DEFAULT_ITEM.newInstance().getMessageName());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(
					"Could not instantiate class with no-arg constructor: " + DEFAULT_ITEM.getSimpleName(), e);
		}
		Set<JsonValue> items = Validatable.toJsonValues(MessageItem.messageNames());
		JsonString defaultDomain = Json.createValue(DEFAULT_DOMAIN.getDomain());

		//@formatter:off

		ret = sbf.createBuilder()

				.withId(Validatable.schemaId(this.getClass()))
					
				.withSchema(Validatable.schemaDialect())
					
				.withTitle("Message context schema")

				.withDescription(
						"Defines the context for Proven disclosure items.  The context identifies a disclosure item's "
						+ "processing and storage requirements within the platform.")

				.withType(InstanceType.OBJECT)

				.withProperty(CONTENT_PROP, sbf.createBuilder()
					.withType(InstanceType.STRING, InstanceType.NULL)
					.withEnum(contents)
					.withDefault(defaultContent).build())

				.withProperty(ITEM_PROP, sbf.createBuilder()
						.withType(InstanceType.STRING, InstanceType.NULL)
						.withEnum(items)
						.withDefault(defaultItem).build())

				.withProperty(DOMAIN_PROP,
						sbf.createBuilder()
						.withType(InstanceType.STRING, InstanceType.NULL)
						.withDefault(defaultDomain).build())

				.withProperty(REQUESTOR_PROP, sbf.createBuilder()
						.withType(InstanceType.STRING, InstanceType.NULL)
						.withDefault(JsonValue.NULL).build())

				.withProperty(NAME_PROP, sbf.createBuilder()
						.withType(InstanceType.STRING, InstanceType.NULL)
						.withDefault(JsonValue.NULL).build())

				.withProperty(TAGS_PROP, sbf.createBuilder()
						.withType(InstanceType.ARRAY, InstanceType.NULL)
						.withDefault(JsonValue.EMPTY_JSON_ARRAY)
						.withItems(sbf.createBuilder()
								.withType(InstanceType.STRING).
								withMaxItems(MAX_TAGS).build())
						.build())
		
				.build();
			
			//@formatter:on

		return ret;
	}

}
