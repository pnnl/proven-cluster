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
package gov.pnnl.proven.cluster.lib.disclosure.exchange;

import java.io.IOException;

import javax.json.JsonObject;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.disclosure.DomainProvider;
import gov.pnnl.proven.cluster.lib.disclosure.exception.JSONDataValidationException;
import gov.pnnl.proven.cluster.lib.disclosure.message.MessageJsonUtils;
import gov.pnnl.proven.cluster.lib.disclosure.message.MessageModel;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessageIDSFactory;

/**
 * Represents a disclosed data item originating from an external Proven client.
 * This class wraps the data item and validates it against the Proven schema at
 * construction time ensuring it adheres to message formating required by
 * Proven; validation failure will throw an exception.
 * 
 * TODO - determine how domain specific message schema data should be used. This
 * would include Proven's messaging for different content types, for example,
 * queries, measurements, administrative, etc., as well as other domain provided
 * JSON schemas specific to the disclosed content. Should these message specific
 * schemas be used here for validation or internally inside Proven post
 * disclosure. Currently only proven-schema.json is being used in the constructor,
 * validating only the non-message/schema fields.
 * 
 * @see DisclosureType, ItemProperties, MessageContent
 * 
 * @author d3j766
 *
 */
public class ClientItem implements IdentifiedDataSerializable {

	static Logger log = LoggerFactory.getLogger(ClientItem.class);

	private JsonObject provenMessage;

	public ClientItem() {
	}

	/**
	 * Creates the client item after a successful JSON schema validation of the
	 * provided JSON string.
	 * 
	 * @param item
	 *            the disclosed JSON string
	 * 
	 * @throws JSONDataValidationException
	 *             if ClientItem could not be created due to malformed message
	 * 
	 * @see DisclosureType
	 * 
	 */
	public ClientItem(String item) throws JSONDataValidationException {

		try {

			// Disclosure type and schema validation
			this.provenMessage = DisclosureType.getJsonItem(item);
			MessageModel mm = MessageModel.getInstance(new DisclosureDomain(DomainProvider.PROVEN_DISCLOSURE_DOMAIN));
			String jsonApi = mm.getApiSchema();
			JSONObject jsonApiSchema = new JSONObject(new JSONTokener(jsonApi));
			Schema jsonSchema = SchemaLoader.load(jsonApiSchema);
			jsonSchema.validate(provenMessage);
			log.debug("Valid JSON Data item");

		} catch (ValidationException e) {
			throw new JSONDataValidationException(
					"ClientItem construction failed, invalid message Data: " + e.getAllMessages(), e);
		} catch (Exception e) {
			throw new JSONDataValidationException(
					"ClientItem construction failed, invalid message Data: " + e.getMessage(), e);
		}
	}

	public JsonObject getProvenMessage() {
		return provenMessage;
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		boolean nullJsonEntry = (null == this.provenMessage);
		out.writeBoolean(nullJsonEntry);
		if (!nullJsonEntry)
			out.writeByteArray(MessageJsonUtils.jsonOut(this.provenMessage));
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		boolean nullJsonEntry = in.readBoolean();
		if (!nullJsonEntry)
			this.provenMessage = MessageJsonUtils.jsonIn(in.readByteArray());
	}

	@Override
	public int getFactoryId() {
		return ProvenMessageIDSFactory.FACTORY_ID;
	}

	@Override
	public int getId() {
		return ProvenMessageIDSFactory.CLIENT_ITEM_TYPE;
	}
}
