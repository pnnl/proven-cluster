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

import java.util.List;

import javax.json.Json;
import javax.json.JsonValue;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

import org.leadpony.justify.api.InstanceType;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidatingException;
import org.leadpony.justify.api.Problem;

import gov.pnnl.proven.cluster.lib.disclosure.MessageContent;
import gov.pnnl.proven.cluster.lib.disclosure.exception.ValidatableBuildException;

/**
 * Immutable class representing a JSON-LD @context.
 * 
 * @author d3j766
 *
 */
public class LdContext implements MessageItem {

	public static final String LD_CONTEXT_MESSAGE_NAME = "ld-context-message";

	public static final String LD_CONTEXT_PROP = "@context";
	public static final String MESSAGE_CONTEXT_PROP = "messageContext";

	private JsonValue ldContext;
	private MessageContext messageContext;

	public LdContext() {
	}

	@JsonbCreator
	public static LdContext createLdContext(@JsonbProperty(LD_CONTEXT_PROP) JsonValue ldContext,
			@JsonbProperty(MESSAGE_CONTEXT_PROP) MessageContext messageContext) {
		return LdContext.newBuilder().withLdContext(ldContext).withMessageContext(messageContext).build(true);
	}

	private LdContext(Builder b) {
		this.ldContext = b.ldContext;
	}

	@JsonbProperty(LD_CONTEXT_PROP)
	public JsonValue getldContext() {
		return ldContext;
	}

	@JsonbProperty(MESSAGE_CONTEXT_PROP)
	public MessageContext getMessageContext() {
		return messageContext;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {

		private JsonValue ldContext;
		private MessageContext messageContext;

		private Builder() {
		}

		public Builder withLdContext(JsonValue ldContext) {
			this.ldContext = ldContext;
			return this;
		}

		public Builder withMessageContext(MessageContext messageContext) {
			this.messageContext = messageContext;
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
		public LdContext build() {
			return build(false);
		}

		private LdContext build(boolean trustedBuilder) {

			LdContext ret = new LdContext(this);

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
	public String messageName() {
		// TODO Auto-generated method stub
		return LD_CONTEXT_MESSAGE_NAME;
	}

	@Override
	public MessageContent messageContent() {
		return MessageContent.REFERENCE;
	}

	@Override
	public JsonSchema toSchema() {

		JsonSchema ret;

		//@formatter:off

		ret = sbf.createBuilder()

				.withId(Validatable.schemaId(this.getClass()))
					
				.withSchema(Validatable.schemaDialect())
					
				.withTitle("JSON-LD Context disclosure schema")

				.withDescription("Defines a JSON-LD context for a messsage item type.")

				.withType(InstanceType.OBJECT)

				.withProperty(LD_CONTEXT_PROP, sbf.createBuilder()
					.withDescription("The LD Context definition.")
					.withType(InstanceType.OBJECT, InstanceType.ARRAY, InstanceType.STRING)
					.build())
				
				.withProperty(MESSAGE_CONTEXT_PROP, Validatable.retrieveSchema(MessageContext.class))
		
				.withIf(sbf.createBuilder()
					.withDescription("If the message item type selected is anything other then explicit or implicit, "
							+ "meaning the item type's LD Context is managed by Proven, only the message item type "
							+ "is used to determine the LD Context to contextualize a disclosure message and other properties "
							+ "in the message context are ignored/set to null.  For explicit and/or implicit message "
							+ "item types, they may be configured using the remaining properties to, if necessary, support "
							+ "multiple LD Contexts per message type.")
					.withProperty(MESSAGE_CONTEXT_PROP, sbf.createBuilder()
						.withProperty(MessageContext.ITEM_PROP, sbf.createBuilder()
							.withNot(sbf.createBuilder()
								.withAnyOf(
									sbf.createBuilder()	
									.withConst(Json.createValue(MessageItem.messageName(ExplicitItem.class)))
									.build(),				
									sbf.createBuilder()	
									.withConst(Json.createValue(MessageItem.messageName(ImplicitItem.class)))
									.build())
								.build())
						.build())
					.build())
				.build())
				.withThen(sbf.createBuilder()
					.withProperty(MESSAGE_CONTEXT_PROP, sbf.createBuilder()
						.withProperty(MessageContext.REQUESTOR_PROP, sbf.createBuilder()
							.withConst(JsonValue.NULL)
							.build())
						.withProperty(MessageContext.NAME_PROP, sbf.createBuilder()
							.withConst(JsonValue.NULL)
							.build())
						.withProperty(MessageContext.TAGS_PROP, sbf.createBuilder()
							.withConst(JsonValue.NULL)
							.build())
						.build())
					.build())
				
				.withRequired(LD_CONTEXT_PROP, MESSAGE_CONTEXT_PROP)
				
				.build();
			
		//@formatter:on

		return ret;
	}
}
