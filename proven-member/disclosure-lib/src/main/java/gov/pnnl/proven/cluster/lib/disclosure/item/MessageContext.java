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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

import org.leadpony.justify.api.InstanceType;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidatingException;
import org.leadpony.justify.api.Problem;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureIDSFactory;
import gov.pnnl.proven.cluster.lib.disclosure.MessageContent;
import gov.pnnl.proven.cluster.lib.disclosure.exception.ValidatableBuildException;

/**
 * Immutable class representing the context of a disclosure item. The context
 * helps to identify both exchange processing and distribution to hybrid-store.
 * 
 * @author d3j766
 * 
 * @see DisclosureItem
 *
 */
public class MessageContext implements Validatable, IdentifiedDataSerializable {

    public static final int MAX_TAGS = 10;

    public static final String CONTENT_PROP = "content";
    public static final String ITEM_PROP = "item";
    public static final String REQUESTOR_PROP = "requestor";
    public static final String NAME_PROP = "name";
    public static final String TAGS_PROP = "tags";

    private MessageContent content;
    private Class<? extends MessageItem> item;
    private String requestor;
    private String name;
    private String[] tags;

    public MessageContext() {
    }

    @JsonbCreator
    public static MessageContext createMessageContext(@JsonbProperty(ITEM_PROP) String item,
	    @JsonbProperty(REQUESTOR_PROP) String requestor, @JsonbProperty(NAME_PROP) String name,
	    @JsonbProperty(TAGS_PROP) String[] tags) {
	return MessageContext.newBuilder().withItem(item).withRequestor(requestor).withName(name).withTags(tags)
		.build(true);
    }

    private MessageContext(Builder b) {
	this.item = b.item;
	this.content = MessageItem.messageContent(b.item);
	this.requestor = b.requestor;
	this.name = b.name;
	this.tags = b.tags;
    }

    @JsonbProperty(CONTENT_PROP)
    public MessageContent getContent() {
	return content;
    }

    @JsonbProperty(ITEM_PROP)
    public Class<? extends MessageItem> getItem() {
	return item;
    }

    @JsonbProperty(value = REQUESTOR_PROP)
    public Optional<String> getRequestor() {
	return Optional.ofNullable(requestor);
    }

    @JsonbProperty(NAME_PROP)
    public Optional<String> getName() {
	return Optional.ofNullable(name);
    }

    @JsonbProperty(TAGS_PROP)
    public String[] getTags() {
	return tags;
    }

    public static Builder newBuilder() {
	return new Builder();
    }

    public static final class Builder {

	private Class<? extends MessageItem> item;
	private String requestor;
	private String name;
	private String[] tags;

	private Builder() {
	}

	public Builder withItem(Class<? extends MessageItem> item) {
	    this.item = item;
	    return this;
	}

	public Builder withItem(String item) {
	    this.item = MessageItem.messageType(item);
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

	public Builder withTags(String... tags) {
	    this.tags = tags;
	    return this;
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
	public MessageContext build() {
	    return build(false);
	}

	private MessageContext build(boolean trustedBuilder) {

	    MessageContext ret = new MessageContext(this);

	    if (!trustedBuilder) {
		List<Problem> problems = ret.validate();
		if (!problems.isEmpty()) {
		    throw new ValidatableBuildException("Builder failure", new JsonValidatingException(problems));
		}
	    }

	    return ret;
	}
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
	out.writeUTF(getContent().toString());
	out.writeObject(item);
	writeNullable(requestor, out, ww((v, o) -> out.writeUTF(v)));
	writeNullable(name, out, ww((v, o) -> out.writeUTF(v)));
	writeNullable(getTags(), out, ww((v, o) -> out.writeUTFArray(v)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readData(ObjectDataInput in) throws IOException {
	this.content = MessageContent.getMessageContent(in.readUTF());
	this.item = (Class<? extends MessageItem>) in.readObject();
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

	// Schema defaults
	final JsonString defaultItem = Json.createValue(MessageItem.messageName(ExplicitItem.class));
	final Set<JsonValue> items = Validatable.toJsonValues(MessageItem.messageNames());
	final int maxTags = 10;

	//@formatter:off

		ret = sbf.createBuilder()

				.withId(Validatable.schemaId(this.getClass()))
					
				.withSchema(Validatable.schemaDialect())
					
				.withTitle("Message context schema")

				.withDescription("Defines the context for a disclosure item.  The context "
							+ "identifies its processing and storage "
							+ "requirements within the platform.")

				.withType(InstanceType.OBJECT)

				.withProperty(ITEM_PROP, sbf.createBuilder()
					.withDescription("Identifies the type of message item contained in a disclosure item.  "
							+ "The provided selections are the message names for each item type.")
					.withType(InstanceType.STRING)
					.withEnum(items)
					.withDefault(defaultItem)
					.build())

				.withProperty(REQUESTOR_PROP, sbf.createBuilder()
					.withType(InstanceType.STRING, InstanceType.NULL)
					.withDefault(JsonValue.NULL)
					.build())

				.withProperty(NAME_PROP, sbf.createBuilder()
					.withType(InstanceType.STRING, InstanceType.NULL)
					.withDefault(JsonValue.NULL)
					.build())

				.withProperty(TAGS_PROP, sbf.createBuilder()
					.withType(InstanceType.ARRAY)
					.withDefault(JsonValue.EMPTY_JSON_ARRAY)
					.withItems(sbf.createBuilder()
						.withType(InstanceType.STRING)
						.withMaxItems(MAX_TAGS)
						.build())
					.build())
		
				.build();
			
			//@formatter:on

	return ret;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((content == null) ? 0 : content.hashCode());
	result = prime * result + ((item == null) ? 0 : item.hashCode());
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + ((requestor == null) ? 0 : requestor.hashCode());
	result = prime * result + Arrays.hashCode(tags);
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
	if (!(obj instanceof MessageContext)) {
	    return false;
	}
	MessageContext other = (MessageContext) obj;
	if (content != other.content) {
	    return false;
	}
	if (item == null) {
	    if (other.item != null) {
		return false;
	    }
	} else if (!item.getName().equals(other.item.getName())) {
	    return false;
	}
	if (name == null) {
	    if (other.name != null) {
		return false;
	    }
	} else if (!name.equals(other.name)) {
	    return false;
	}
	if (requestor == null) {
	    if (other.requestor != null) {
		return false;
	    }
	} else if (!requestor.equals(other.requestor)) {
	    return false;
	}
	if (!Arrays.equals(tags, other.tags)) {
	    return false;
	}
	return true;
    }
}
