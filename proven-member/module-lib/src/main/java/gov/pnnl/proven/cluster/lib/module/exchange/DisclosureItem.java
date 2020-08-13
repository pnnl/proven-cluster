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
package gov.pnnl.proven.cluster.lib.module.exchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.json.stream.JsonParsingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.disclosure.DomainProvider;
import gov.pnnl.proven.cluster.lib.disclosure.exception.InvalidDisclosureDomainException;
import gov.pnnl.proven.cluster.lib.disclosure.exception.JSONDataValidationException;
import gov.pnnl.proven.cluster.lib.disclosure.exception.UnsupportedDisclosureType;
import gov.pnnl.proven.cluster.lib.disclosure.exchange.ClientItem;
import gov.pnnl.proven.cluster.lib.disclosure.exchange.DisclosureType;
import gov.pnnl.proven.cluster.lib.disclosure.exchange.ItemProperty;
import gov.pnnl.proven.cluster.lib.disclosure.message.DisclosureMessage;
import gov.pnnl.proven.cluster.lib.disclosure.message.MessageJsonUtils;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessageIDSFactory;
import gov.pnnl.proven.cluster.lib.module.util.ModuleIDSFactory;

/**
 * Internal representation of a disclosed data item. Property values are
 * extracted and made available for internal processing.
 * 
 * @author d3j766
 * 
 */
public class DisclosureItem implements BufferedItem, IdentifiedDataSerializable {

	static Logger log = LoggerFactory.getLogger(DisclosureItem.class);

	private BufferedItemState bufferedState;

	private Map<ItemProperty, JsonValue> itemProperties = new HashMap<>();

	public DisclosureItem() {
	}

	/**
	 * Construction based on an externally disclosed ClientItem. Properties from
	 * the ClientItem are extracted and saved in a local Map.
	 * 
	 * ItemProperty is used to extract fields from ClientItem's JSON Proven
	 * Message. ItemProperty's field name's and value types must match the
	 * Proven schema or a validation exception will be thrown during construction.
	 * 
	 * Note: As long as ItemProperty is aligned with Proven's message schema,
	 * schema validation in ClientItem will ensure that the DisclosureItem is
	 * correct.  
	 * 
	 * @param clientItem
	 * 
	 * @throws JSONValidationException
	 * 
	 * @see ItemProperty
	 */
	public DisclosureItem(ClientItem clientItem) throws JSONDataValidationException {

		bufferedState = BufferedItemState.New;

		JsonObject pm = clientItem.getProvenMessage();
		for (ItemProperty prop : ItemProperty.values()) {
			String propName = prop.getName();
			boolean hasValue = (pm.containsKey(propName));
			if (hasValue) {

				JsonValue value = pm.get(propName);

				// Verify type
				boolean typeVerified = false;
				for (ValueType vt : prop.getValueType()) {
					if (vt.equals(value.getValueType())) {
						typeVerified = true;
					}
				}

				if (typeVerified) {
					itemProperties.put(prop, value);
				} else {
					throw new JSONDataValidationException(
							"DisclosureItem construction failed, invalid JSON type for:: " + propName);
				}
			} else {
				itemProperties.put(prop, JsonValue.NULL);
			}
		}
	}

	public boolean hasValue(ItemProperty prop) {
		return (!(itemProperties.get(prop).equals(JsonValue.NULL)));
	}

	/**
	 * Retrieves the DisclosureDomain for this disclosure item. If no domain
	 * was provided, Proven's disclosure domain is returned by default.
	 * 
	 * @return disclosure domain
	 */
	@Override
	public DisclosureDomain disclosureDomain() {
		JsonValue value getItemPropertyValue(ItemProperty.DOMAIN);	
	}

	public DisclosureMessage getDisclosureMessage() throws UnsupportedDisclosureType, JsonParsingException, Exception {
		return new DisclosureMessage(jsonEntry);
	}

	@Override
	public BufferedItemState getItemState() {
		return bufferedState;
	}

	@Override
	public void setItemState(BufferedItemState bufferedState) {
		this.bufferedState = bufferedState;
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		out.writeUTF(this.bufferedState.toString());
		boolean nullJsonEntry = (null == this.jsonEntry);
		out.writeBoolean(nullJsonEntry);
		if (!nullJsonEntry)
			out.writeByteArray(MessageJsonUtils.jsonOut(this.jsonEntry));
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		this.bufferedState = BufferedItemState.valueOf(in.readUTF());
		boolean nullJsonEntry = in.readBoolean();
		if (!nullJsonEntry)
			this.jsonEntry = MessageJsonUtils.jsonIn(in.readByteArray());
	}

	@Override
	public int getFactoryId() {
		return ModuleIDSFactory.FACTORY_ID;
	}

	@Override
	public int getId() {
		return ModuleIDSFactory.DISCLOSURE_ITEM_TYPE;
	}
}
