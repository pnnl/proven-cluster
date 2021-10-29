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
package gov.pnnl.proven.cluster.lib.disclosure.item.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.ws.rs.core.Response;

import org.leadpony.justify.api.InstanceType;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidatingException;
import org.leadpony.justify.api.Problem;

import gov.pnnl.proven.cluster.lib.disclosure.exception.ValidatableBuildException;
import gov.pnnl.proven.cluster.lib.disclosure.item.MessageContext;
import gov.pnnl.proven.cluster.lib.disclosure.item.Validatable;
import gov.pnnl.proven.cluster.lib.disclosure.item.operation.ItemOperation;
import gov.pnnl.proven.cluster.lib.disclosure.item.operation.OperationContext;

/**
 * Immutable class representing the context of a response item, containing
 * baseline information describing the result of a processing operation within
 * the platform.
 * 
 * @author d3j766
 * 
 * @see ResponseItem, ItemOperation
 *
 */
public class ResponseContext implements Validatable {

	public static final String STATUS_CODE_PROP = "statusCode";
	public static final String STATUS_MESSAGE_PROP = "statusMessage";
	public static final String OPERATION_START_TIME_PROP = "operationStartTime";
	public static final String OPERATION_END_TIME_PROP = "opertionEndTime";
	public static final String MESSAGE_CONTEXT_PROP = "messageContext";
	public static final String OPERATION_CONTEXT_PROP = "operationContext";

	private Response.Status statusCode;
	private String statusMessage;
	private Long operationStartTime;
	private Long operationEndTime;
	private MessageContext messageContext;
	private OperationContext operationContext;

	public ResponseContext() {
	}

	@JsonbCreator
	public static ResponseContext createResponseContext(@JsonbProperty(STATUS_CODE_PROP) Response.Status statusCode,
			@JsonbProperty(STATUS_MESSAGE_PROP) String statusMessage,
			@JsonbProperty(OPERATION_START_TIME_PROP) Long operationStartTime,
			@JsonbProperty(OPERATION_END_TIME_PROP) Long operationEndTime,
			@JsonbProperty(MESSAGE_CONTEXT_PROP) MessageContext messageContext,
			@JsonbProperty(OPERATION_CONTEXT_PROP) JsonObject operationContext) {
		return ResponseContext.newBuilder().withStatusCode(statusCode).withStatusMessage(statusMessage)
				.withOperationStartTime(operationStartTime).withOperationEndTime(operationEndTime)
				.withMessageContext(messageContext).withOperationContext(operationContext).build(true);
	}

	private ResponseContext(Builder b) {
		this.statusCode = b.statusCode;
		this.statusMessage = b.statusMessage;
		this.operationStartTime = b.operationStartTime;
		this.operationEndTime = b.operationEndTime;
		this.messageContext = b.messageContext;
		this.operationContext = b.operationContext;
	}

	@JsonbProperty(STATUS_CODE_PROP)
	public Response.Status getStatusCode() {
		return statusCode;
	}

	@JsonbProperty(STATUS_MESSAGE_PROP)
	public Optional<String> getStatusMessage() {
		return Optional.ofNullable(statusMessage);
	}

	@JsonbProperty(OPERATION_START_TIME_PROP)
	public Long getOperationStartTime() {
		return operationStartTime;
	}

	@JsonbProperty(OPERATION_END_TIME_PROP)
	public Long getOperationEndTime() {
		return operationEndTime;
	}

	@JsonbProperty(MESSAGE_CONTEXT_PROP)
	public MessageContext getMessageContext() {
		return messageContext;
	}

	@JsonbProperty(OPERATION_CONTEXT_PROP)
	public OperationContext getOperationContext() {
		return operationContext;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {

		private Response.Status statusCode;
		private String statusMessage;
		private long operationStartTime;
		private long operationEndTime;
		private MessageContext messageContext;
		private OperationContext operationContext;

		private Builder() {
		}

		public Builder withStatusCode(Response.Status statusCode) {
			this.statusCode = statusCode;
			return this;
		}

		public Builder withStatusMessage(String statusMessage) {
			this.statusMessage = statusMessage;
			return this;
		}

		public Builder withOperationStartTime(long operationStartTime) {
			this.operationStartTime = operationStartTime;
			return this;
		}

		public Builder withOperationEndTime(long operationEndTime) {
			this.operationEndTime = operationEndTime;
			return this;
		}

		public Builder withMessageContext(MessageContext messageContext) {
			this.messageContext = messageContext;
			return this;
		}

		public Builder withOperationContext(JsonObject json) {
			this.operationContext = OperationContext.fromJson(json);
			return this;
		}

		public Builder withOperationContext(OperationContext oc) {
			this.operationContext = oc;
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
		public ResponseContext build() {
			return build(false);
		}

		private ResponseContext build(boolean trustedBuilder) {

			ResponseContext ret = new ResponseContext(this);

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
	public JsonSchema toSchema() {

		JsonSchema ret;

		// Create allowed OperationContext schema definitions
		List<JsonSchema> allowedOps = new ArrayList<>();
		for (ItemOperation op : ItemOperation.values()) {
			allowedOps.add(Validatable.retrieveSchema(op.getOpContext()));
		}

		//@formatter:off

		ret = sbf.createBuilder()

			.withId(Validatable.schemaId(this.getClass()))
				
			.withSchema(Validatable.schemaDialect())
				
			.withTitle("Response context schema")
	
			.withDescription("Defines the context for a response item providing baseline information"
					+ " for a response.  Response represents the result of a processing opertion "
					+ " within the platform")
	
			.withType(InstanceType.OBJECT)
	
			.withProperty(STATUS_CODE_PROP,
					sbf.createBuilder()
					.withDescription("Uses common HTTP status codes to describe the response.")
					.withType(InstanceType.STRING)
					.withPattern("^[1-5][0-9][0-9]$")
					.build())
			
			.withProperty(STATUS_MESSAGE_PROP,
					sbf.createBuilder()
					.withDescription("Response description.")
					.withType(InstanceType.STRING, InstanceType.NULL)
					.withDefault(JsonValue.NULL)
					.build())
						
			.withProperty(OPERATION_START_TIME_PROP, sbf.createBuilder()
					.withTitle("Operation start time")
					.withDescription("Time operation was started. This is an epoch timestamp formatted "
							+ "in milliseconds.")
					.withType(InstanceType.INTEGER)
					.withMinimum(0)
					.withMaximum(Long.MAX_VALUE)
					.build())	
			
			.withProperty(OPERATION_END_TIME_PROP, sbf.createBuilder()
					.withTitle("Operation end time")
					.withDescription("Time operation was ended. This is an epoch timestamp formatted "
							+ "in milliseconds.")
					.withType(InstanceType.INTEGER)
					.withMinimum(0)
					.withMaximum(Long.MAX_VALUE)
					.build())	
	
			.withProperty(MESSAGE_CONTEXT_PROP, Validatable.retrieveSchema(MessageContext.class))
			
			.withProperty(OPERATION_CONTEXT_PROP, sbf.createBuilder()
					.withOneOf(allowedOps)
					.withType(InstanceType.OBJECT)
					.build())
			
			.withRequired(STATUS_CODE_PROP, OPERATION_START_TIME_PROP, OPERATION_END_TIME_PROP, 
					      MESSAGE_CONTEXT_PROP, OPERATION_CONTEXT_PROP)
							
			.build();
			
			//@formatter:on

		return ret;
	}

}
