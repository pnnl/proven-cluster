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
package gov.pnnl.proven.cluster.lib.disclosure.deprecated.exchange;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.annotation.JsonbTransient;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureIDSFactory;
import gov.pnnl.proven.cluster.lib.disclosure.MessageContent;
import gov.pnnl.proven.cluster.lib.disclosure.item.DisclosureItem;

/**
 * Base class for internal response messaging.
 * 
 * @author d3j766
 * 
 */
public class ResponseItem implements BufferedItem, IdentifiedDataSerializable {

	static Logger log = LoggerFactory.getLogger(ResponseItem.class);

	@JsonbTransient
	private Jsonb jsonb = JsonbBuilder.create();

	@JsonbTransient
	private DisclosureItemState bufferedState;

	@JsonbTransient
	protected Optional<DisclosureItem> disclosureItem;

	@JsonbTransient
	private Status status;

	private int statusCode;
	private String statusReason;
	private String message;

	public ResponseItem() {
	}

	public ResponseItem(Status status, Optional<String> message) {
		this(status, message, Optional.empty());
	}

	public ResponseItem(Status status, Optional<String> message, Optional<DisclosureItem> disclosureItem) {
		bufferedState = DisclosureItemState.New;
		this.disclosureItem = (disclosureItem.isPresent() ? disclosureItem : Optional.empty());
		this.status = status;
		this.statusCode = status.getStatusCode();
		this.statusReason = status.getReasonPhrase();
		this.message = (message.isPresent() ? message.get() : "");
	}

	public JsonObject toJson() {

		JsonObject ret;
		try (JsonReader reader = Json.createReader(new StringReader(jsonb.toJson(this)))) {
			ret = reader.readObject();
		}

		return ret;
	}

	public Status getStatus() {
		return status;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatusReason() {
		return statusReason;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public MessageContent getMessageContent() {
		return MessageContent.OPERATION_EVENT;
	}

	@Override
	public DisclosureItemState getItemState() {
		return bufferedState;
	}

	@Override
	public void setItemState(DisclosureItemState bufferedState) {
		this.bufferedState = bufferedState;
	}

	public boolean hasDisclosureItem() {
		return this.disclosureItem.isPresent();
	}

	public Optional<DisclosureItem> getDisclosureItem() {
		return disclosureItem;
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		out.writeString(this.bufferedState.toString());
		if (disclosureItem.isPresent()) {
			out.writeBoolean(true);
			disclosureItem.get().writeData(out);
		} else {
			out.writeBoolean(false);
		}
		out.writeInt(status.getStatusCode());
		out.writeInt(getStatusCode());
		out.writeString(getStatusReason());
		out.writeString(getMessage());
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		this.bufferedState = DisclosureItemState.valueOf(in.readString());
		boolean hasDisclosureItem = in.readBoolean();
		if (hasDisclosureItem) {
			DisclosureItem di = new DisclosureItem();
			di.readData(in);
			this.disclosureItem = Optional.of(di);
		}
		this.status = Status.fromStatusCode(in.readInt());
		this.statusCode = in.readInt();
		this.statusReason = in.readString();
		this.message = in.readString();
	}

	@Override
	public int getFactoryId() {
		return DisclosureIDSFactory.FACTORY_ID;
	}

	@Override
	public int getClassId() {
		//return DisclosureIDSFactory.RESPONSE_ITEM_TYPE;
		return 0;
	}
}
