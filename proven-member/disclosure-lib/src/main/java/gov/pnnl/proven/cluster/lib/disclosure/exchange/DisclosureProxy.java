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
import java.io.Serializable;

import javax.json.JsonObject;
import javax.json.stream.JsonParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.disclosure.exception.UnsupportedDisclosureEntryType;
import gov.pnnl.proven.cluster.lib.disclosure.message.DisclosureMessage;
import gov.pnnl.proven.cluster.lib.disclosure.message.JsonDisclosure;
import gov.pnnl.proven.cluster.lib.disclosure.message.MessageContent;
import gov.pnnl.proven.cluster.lib.disclosure.message.MessageJsonUtils;
import gov.pnnl.proven.cluster.lib.disclosure.message.MessageUtils;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessageIDSFactory;

/**
 * Wrapper class for a externally disclosed entry/message request. A
 * {@code DisclosureProxy} may be added to a {@code DisclosureBuffer} as a
 * buffered item.
 * 
 * @author d3j766
 *
 */
public class DisclosureProxy implements BufferedItem, IdentifiedDataSerializable {

	private static final long serialVersionUID = 1L;

	static Logger log = LoggerFactory.getLogger(DisclosureProxy.class);

	private BufferedItemState bufferedState;
	private JsonObject jsonEntry;

	/**
	 * No-arg constructor for serialization
	 */
	public DisclosureProxy() {
	}

	/**
	 * Based on an internally provided json object disclosure entry.
	 * 
	 * @param entry
	 */
	public DisclosureProxy(JsonObject jsonEntry) {
		this.jsonEntry = jsonEntry;
		bufferedState = BufferedItemState.New;
	}

	/**
	 * DisclosureProxy is created using a String value. Disclosure type is
	 * verified. Conversion to Json is made, if applicable.
	 * 
	 * @param entry
	 *            the disclosed string value
	 * 
	 * @throws UnsupportedDisclosureEntryType
	 *             if string is not a supported disclosure type.
	 */
	public DisclosureProxy(String entry) throws UnsupportedDisclosureEntryType {
		this.jsonEntry = DisclosureEntryType.getJsonEntry(entry);
		bufferedState = BufferedItemState.New;
	}

	public JsonObject getJsonEntry() {
		return jsonEntry;
	}

	public DisclosureMessage getDisclosureMessage() throws UnsupportedDisclosureEntryType, JsonParsingException, Exception {
		return new DisclosureMessage(jsonEntry);
	}

	@Override
	public BufferedItemState getItemState() {
		return bufferedState;
	}

	public DisclosureEntryType getEntryType() {
		return null;
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
		return ProvenMessageIDSFactory.FACTORY_ID;
	}

	@Override
	public int getId() {
		return ProvenMessageIDSFactory.DISCLOSURE_PROXY_TYPE;
	}
}
