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
package gov.pnnl.proven.cluster.lib.disclosure.deprecated.message;

import java.io.IOException;

import javax.json.JsonObject;
import javax.ws.rs.core.Response;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureIDSFactory;
import gov.pnnl.proven.cluster.lib.disclosure.MessageContent;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.exchange.ResponseItem;
import gov.pnnl.proven.cluster.lib.disclosure.item.DisclosureItem;

/**
 * Messages created in response to events (e.g. disclosure, request processing,
 * knowledge processing, etc.) occurring within the Proven platform. They
 * provide a record of the event's status and other related data. These messages
 * are published as Server-Sent Events (SSE) making them available for client
 * consumption.
 * 
 * @see ProvenMessage
 * 
 * @author d3j766
 *
 */
public class ResponseMessage extends ProvenMessage {

	private static final long serialVersionUID = 1L;

	/**
	 * JSON response message
	 */
	protected JsonObject responseMessage;

	/**
	 * Response status using commonly used HTTP status codes
	 * 
	 * @see Response.Status
	 */
	protected Response.Status status;

	/**
	 * Content type of the source message for this response
	 */
	protected MessageContent sourceContentType;

	public ResponseMessage() {
	}
	
	public ResponseMessage(ResponseItem response, DisclosureItem di) {
		super(di);
		this.responseMessage = response.toJson();
		this.status = response.getStatus();
		this.sourceContentType = di.getContext().getContent();
	}
	
	public ResponseMessage(ResponseItem response, ProvenMessage sourceMessage) {
		super(sourceMessage);
		this.responseMessage = response.toJson();
		this.status = response.getStatus();
		this.sourceContentType = sourceMessage.getMessageContent();
	}

	public JsonObject getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(JsonObject responseMessage) {
		this.responseMessage = responseMessage;
	}

	public Response.Status getStatus() {
		return status;
	}

	public void setStatus(Response.Status status) {
		this.status = status;
	}

	public MessageContent getSourceContentType() {
		return sourceContentType;
	}

	public void setSourceContentType(MessageContent sourceContentType) {
		this.sourceContentType = sourceContentType;
	}

	@Override
	public MessageContent getMessageContent() {
		return MessageContent.OPERATION_EVENT;
	}

	@Override
	public int getFactoryId() {
		return DisclosureIDSFactory.FACTORY_ID;
	}

	@Override
	public int getClassId() {
		//return DisclosureIDSFactory.RESPONSE_MESSAGE_TYPE;
		return 0;
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		super.readData(in);
		this.status = Response.Status.valueOf(in.readString());
		this.sourceContentType = MessageContent.valueOf(in.readString());
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		super.writeData(out);
		out.writeString(status.name());
		out.writeString(sourceContentType.name());
	}

}
