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

import java.util.List;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

import org.leadpony.justify.api.InstanceType;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidatingException;
import org.leadpony.justify.api.Problem;

import gov.pnnl.proven.cluster.lib.disclosure.MessageContent;
import gov.pnnl.proven.cluster.lib.disclosure.exception.ValidatableBuildException;
import gov.pnnl.proven.cluster.lib.disclosure.item.Validatable;

/**
 * Immutable class representing an operation's response. This response item
 * provides baseline information to accommodate a standard response.
 * 
 * and can be extended if the operation require it.
 * 
 * @author d3j766
 *
 * @see ResponseContext, ItemOperation, ResponseItem
 *
 */
public class BaseResponseItem implements ResponseItem {

	public static final String BASE_RESPONSE_MESSAGE_NAME = "base-response-message";

	private ResponseContext responseContext;

	public BaseResponseItem() {
	}

	@JsonbCreator
	public static BaseResponseItem createBaseResponseItem(
			@JsonbProperty(RESPONSE_CONTEXT_PROP) ResponseContext responseContext) {
		return BaseResponseItem.newBuilder().withResponseContext(responseContext).build(true);
	}

	private BaseResponseItem(Builder b) {
		this.responseContext = b.responseContext;
	}

	@JsonbProperty(RESPONSE_CONTEXT_PROP)
	@Override
	public ResponseContext getResponseContext() {
		return responseContext;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {

		private ResponseContext responseContext;

		private Builder() {
		}

		public Builder withResponseContext(ResponseContext responseContext) {
			this.responseContext = responseContext;
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
		public BaseResponseItem build() {
			return build(false);
		}

		private BaseResponseItem build(boolean trustedBuilder) {

			BaseResponseItem ret = new BaseResponseItem(this);

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
	public MessageContent messageContent() {
		return MessageContent.Response;
	}

	@Override
	public String messageName() {
		return BASE_RESPONSE_MESSAGE_NAME;
	}

	public JsonSchema toSchema() {

		JsonSchema ret;

		//@formatter:off
		ret = sbf.createBuilder()

				.withId(Validatable.schemaId(this.getClass()))
				
				.withSchema(Validatable.schemaDialect())
				
				.withTitle("Base response item message schema")

				.withDescription("This message provides baseline operation response information.  "
						+ "This can be extended if an operation's response requires it.")

				.withType(InstanceType.OBJECT)
				
				.withProperty(MESSAGE_NAME_PROP, messageNamePropertySchema())
				
				.withProperty(RESPONSE_CONTEXT_PROP, Validatable.retrieveSchema(ResponseContext.class)) 
				
				.withRequired(RESPONSE_CONTEXT_PROP)	
						
			.build();
		
		//@formatter:on

		return ret;
	}
}
