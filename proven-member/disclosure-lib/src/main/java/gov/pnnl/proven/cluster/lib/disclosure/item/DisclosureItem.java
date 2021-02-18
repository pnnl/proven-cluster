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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import javax.activation.MimeType;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.bind.Jsonb;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.ws.rs.core.MediaType;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.disclosure.DisclosureIDSFactory;
import gov.pnnl.proven.cluster.lib.disclosure.DomainProvider;
import gov.pnnl.proven.cluster.lib.disclosure.MessageContent;
import gov.pnnl.proven.cluster.lib.disclosure.MessageJsonUtils;
import gov.pnnl.proven.cluster.lib.disclosure.UUIDAdapter;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.exchange.BufferedItem;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.exchange.DisclosureProperty;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.MessageModel;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.MessageQuery;
import gov.pnnl.proven.cluster.lib.disclosure.exception.JSONDataValidationException;
import gov.pnnl.proven.cluster.lib.disclosure.exception.UnsupportedDisclosureType;

/**
 * Represents data disclosed to the Proven platform.
 * 
 * @author d3j766
 * 
 */
public class DisclosureItem implements BufferedItem, IdentifiedDataSerializable {

	static Logger log = LoggerFactory.getLogger(DisclosureItem.class);

	private DisclosureItemState bufferedState;

	private UUID messageId;
	private UUID sourceMessageId;
	private String authToken;
	private String domain;
	private MessageContent content;
	private String messageName;
	private String disclosureId;
	private String requestorName;
	private JsonObject message;
	private JsonObject messageSchema;
	private boolean isTransient;
	@JsonbTypeAdapter(UUIDAdapter.class)
	private boolean isLinkedData;

	public DisclosureItem() {
	}

	/**
	 * Constructs a new DisclosureItem from a JSON string. 
	 * 
	 * @param entry
	 *            the disclosed JSON string
	 * 
	 * @throws UnsupportedDisclosureType
	 *             if string does not represent a supported
	 *             {@link DisclosureType}
	 * 
	 * @throws JSONDataValidationException
	 *             if message can not be validated as a Proven disclosure
	 *             message
	 * 
	 */
	public DisclosureItem(String item) throws JSONDataValidationException, UnsupportedDisclosureType {
		this(DisclosureType.getJsonItem(item));
	}

	/**
	 * Schema validation is performed for provided JSON object representing the DisclosureItem.  
	 * Validation error will throw an exception.
	 * 
	 * NOTE:
	 * 
	 * @param entry
	 *            the disclosed JSON object
	 * 
	 * @throws UnsupportedDisclosureType
	 *             if string is not a supported {@link DisclosureType}
	 * 
	 * @throws JSONDataValidationException
	 *             if message can not be validated as a Proven Message
	 * 
	 */
	public DisclosureItem(JsonObject item) throws JSONDataValidationException {

		this.bufferedState = DisclosureItemState.New;

		// Validate item against the Proven message schema
		try {
			// Disclosure type and schema validation
			MessageModel mm = MessageModel.getInstance(new DisclosureDomain(DomainProvider.PROVEN_DISCLOSURE_DOMAIN));
			String jsonApi = mm.getApiSchema();
			org.json.JSONObject jsonApiSchema = new org.json.JSONObject(new JSONTokener(jsonApi));
			Schema jsonSchema = SchemaLoader.load(jsonApiSchema);
			jsonSchema.validate(new org.json.JSONObject(item.toString()));
			log.debug("Valid JSON Data item");

		} catch (ValidationException e) {
			throw new JSONDataValidationException(
					"DisclosureItem construction failed, invalid message Data: " + e.getAllMessages(), e);
		} catch (Exception e) {
			throw new JSONDataValidationException(
					"DisclosureItem construction failed, invalid message Data: " + e.getMessage(), e);
		}

		for (DisclosureProperty prop : DisclosureProperty.values()) {
			String propName = prop.getProperty();
			boolean hasValue = (item.containsKey(propName));
			if (hasValue && (item.get(propName) != JsonValue.NULL)) {
				JsonValue value = item.get(propName);
				itemProperties.put(prop, value);
			} else {
				if (prop.hasDefault()) {
					itemProperties.put(prop, prop.getDefaultValue());
				} else {
					itemProperties.put(prop, JsonValue.NULL);
				}
			}
		}
	}

	/**
	 * Copy constructor
	 */
	public DisclosureItem(DisclosureItem di) {
		this.bufferedState = DisclosureItemState.New;
		this.itemProperties = new HashMap<>(di.itemProperties);
	}

	@Override
	public MessageContent getMessageContent() {
		return MessageContent.getValue(getStringProp(DisclosureProperty.CONTENT).get());
	}

	public Optional<String> getAuthToken() {
		return getStringProp(DisclosureProperty.AUTH_TOKEN);
	}

	public DisclosureDomain getDisclosureDomain() {
		return new DisclosureDomain(getStringProp(DisclosureProperty.DOMAIN).get());
	}

	public Optional<String> getName() {
		return getStringProp(DisclosureProperty.NAME);
	}

	public Optional<String> getQueryType() {
		return getStringProp(DisclosureProperty.QUERY_TYPE);
	}

	public Optional<String> getQueryLanguage() {
		return getStringProp(DisclosureProperty.QUERY_LANGUAGE);
	}

	public Optional<String> getDisclosureId() {
		return getStringProp(DisclosureProperty.DISCLOSURE_ID);
	}

	public Optional<String> getRequestorId() {
		return getStringProp(DisclosureProperty.REQUESTOR_ID);
	}

	public boolean isStatic() {
		return getBooleanProp(DisclosureProperty.IS_STATIC).get();
	}

	public boolean isTransient() {
		return getBooleanProp(DisclosureProperty.IS_TRANSIENT).get();
	}

	public JsonObject getMessage() {
		return getObjectProp(DisclosureProperty.MESSAGE).get();
	}

	public Optional<JsonObject> getMessageSchema() {
		return getObjectProp(DisclosureProperty.MESSAGE);
	}

	private Optional<String> getStringProp(DisclosureProperty prop) {
		Optional<String> ret = Optional.empty();
		JsonValue val = itemProperties.get(prop);
		if (val != JsonValue.NULL) {
			ret = Optional.of(((JsonString) val).getString());
		}
		return ret;
	}

	private Optional<Boolean> getBooleanProp(DisclosureProperty prop) {
		Optional<Boolean> ret = Optional.empty();
		JsonValue val = itemProperties.get(prop);
		if (val != JsonValue.NULL) {
			if (val == JsonValue.TRUE) {
				ret = Optional.of(true);
			} else {
				ret = Optional.of(false);
			}
		}
		return ret;
	}

	private Optional<JsonObject> getObjectProp(DisclosureProperty prop) {
		Optional<JsonObject> ret = Optional.empty();
		JsonValue val = itemProperties.get(prop);
		if (val != JsonValue.NULL) {
			ret = Optional.of((JsonObject) val);
		}
		return ret;
	}

	@Override
	public DisclosureItemState getItemState() {
		return bufferedState;
	}

	@Override
	public void setItemState(DisclosureItemState bufferedState) {
		this.bufferedState = bufferedState;
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		out.writeUTF(this.bufferedState.toString());
		out.writeInt(((null == itemProperties) ? 0 : itemProperties.size()));
		for (Entry<DisclosureProperty, JsonValue> entry : itemProperties.entrySet()) {
			out.writeUTF(entry.getKey().toString());
			out.writeByteArray(MessageJsonUtils.jsonValueOut(entry.getValue()));
		}
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		this.bufferedState = DisclosureItemState.valueOf(in.readUTF());
		int count = in.readInt();
		for (int i = 0; i < count; i++) {
			DisclosureProperty dp = DisclosureProperty.valueOf(in.readUTF());
			JsonValue jv = MessageJsonUtils.jsonValueIn(in.readByteArray());
			itemProperties.put(dp, jv);
		}
	}

	@Override
	public int getFactoryId() {
		return DisclosureIDSFactory.FACTORY_ID;
	}

	@Override
	public int getId() {
		return DisclosureIDSFactory.DISCLOSURE_ITEM_TYPE;
	}
}
