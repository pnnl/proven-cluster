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

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

import org.leadpony.justify.api.InstanceType;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonSchemaBuilder;
import org.leadpony.justify.api.JsonValidatingException;
import org.leadpony.justify.api.Problem;

import gov.pnnl.proven.cluster.lib.disclosure.MessageContent;
import gov.pnnl.proven.cluster.lib.disclosure.exception.ValidatableBuildException;
import gov.pnnl.proven.cluster.lib.disclosure.item.operation.ItemOperation;
import gov.pnnl.proven.cluster.lib.disclosure.item.operation.OperationContext;

/**
 * Immutable class representing a Model item message. This message defines a
 * model, or graph, containing one or more model artifacts used to support
 * exchange operation processing and/or query requests.
 * 
 * @author d3j766
 *
 * @see ModelArtifactItem, ArtifacContext
 *
 */
public class ModelItem implements MessageItem {

	private static final int MIN_ARTIFACTS = 1;

	static final String ARTIFACTS_PROP = "artifacts";
	static final String NAMED_QUERY_MODEL_PROP = "namedModel";
	static final String DEFAULT_QUERY_MODEL_PROP = "defaultQueryModel";
	static final String MESSAGE_CONTEXT_PROP = "messageContext";
	static final String OPERATION_CONTEXT_PROP = "operationContext";

	private List<ArtifactContext> artifacts;
	private String namedQueryModel;
	private Boolean defaultQueryModel;
	private MessageContext messageContext;
	private JsonObject operationContext;

	public ModelItem() {
	}

	@JsonbCreator
	public static ModelItem createModelItem(@JsonbProperty(ARTIFACTS_PROP) List<ArtifactContext> artifacts,
			@JsonbProperty(NAMED_QUERY_MODEL_PROP) String namedModel,
			@JsonbProperty(DEFAULT_QUERY_MODEL_PROP) Boolean defaultQueryModel,
			@JsonbProperty(MESSAGE_CONTEXT_PROP) MessageContext messageContext,
			@JsonbProperty(OPERATION_CONTEXT_PROP) JsonObject operationContext) {
		return ModelItem.newBuilder().withArtifacts(artifacts).withNamedQueryModel(namedModel)
				.withDefaultQueryModel(defaultQueryModel).withMessageContext(messageContext)
				.withOperationContext(operationContext).build(true);
	}

	private ModelItem(Builder b) {
		this.artifacts = b.artifacts;
		this.namedQueryModel = b.namedQueryModel;
		this.defaultQueryModel = b.defaultQueryModel;
		this.messageContext = b.messageContext;
		this.operationContext = b.operationContext;
	}

	@JsonbProperty(ARTIFACTS_PROP)
	public List<ArtifactContext> getArtifacts() {
		return artifacts;
	}

	@JsonbProperty(NAMED_QUERY_MODEL_PROP)
	public String getNamedQueryModel() {
		return namedQueryModel;
	}

	@JsonbProperty(DEFAULT_QUERY_MODEL_PROP)
	public Boolean isDefaultQueryModel() {
		return defaultQueryModel;
	}

	@JsonbProperty(MESSAGE_CONTEXT_PROP)
	public MessageContext getMessageContext() {
		return messageContext;
	}

	@JsonbProperty(OPERATION_CONTEXT_PROP)
	public JsonObject getOperationContext() {
		return operationContext;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {

		private List<ArtifactContext> artifacts;
		private String namedQueryModel;
		private Boolean defaultQueryModel;
		private MessageContext messageContext;
		private JsonObject operationContext;

		private Builder() {
		}

		public Builder withArtifacts(List<ArtifactContext> artifacts) {
			this.artifacts = artifacts;
			return this;
		}

		public Builder withNamedQueryModel(String namedModel) {
			this.namedQueryModel = namedModel;
			return this;
		}

		public Builder withDefaultQueryModel(Boolean defaultQueryModel) {
			this.defaultQueryModel = defaultQueryModel;
			return this;
		}

		public Builder withMessageContext(MessageContext messageContext) {
			this.messageContext = messageContext;
			return this;
		}

		public Builder withOperationContext(JsonObject operationContext) {
			this.operationContext = operationContext;
			return this;
		}		
		
		public Builder withOperationContext(OperationContext operationContext) {
			this.operationContext = (JsonObject) operationContext.toJson();
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
		public ModelItem build() {
			return build(false);
		}

		private ModelItem build(boolean trustedBuilder) {

			ModelItem ret = new ModelItem(this);

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
		return MessageContent.Model;
	}

	@Override
	public String messageName() {
		return "Model message";
	}

	public JsonSchema toSchema() {

		JsonSchema ret;

		// Create allowed OperationContext schema definitions
		List<JsonSchema> allowedOps = new ArrayList<>();
		for (ItemOperation op : ItemOperation.getModelOperations()) {
			allowedOps.add(Validatable.retrieveSchema(op.getOpContext()));
		}
		
		//@formatter:off
		ret = sbf.createBuilder()

				.withId(Validatable.schemaId(this.getClass()))
				
				.withSchema(Validatable.schemaDialect())
				
				.withTitle("Model item message schema")

				.withDescription("This message defines a model (i.e., graph) containing one or "
						+ "more model artifacts used to support exchange operation processing "
						+ "and/or query requests.  The provided model artifacts are combined into a single "
						+ "graph")

				.withType(InstanceType.OBJECT)

				.withProperty(ARTIFACTS_PROP, sbf.createBuilder()
						.withDescription("Array of artifiacts for which the model will be composed of.")
						.withType(InstanceType.ARRAY)
						.withItems(Validatable.retrieveSchema(ArtifactContext.class))
						.withMinItems(MIN_ARTIFACTS)
						.build())
								
				.withProperty(NAMED_QUERY_MODEL_PROP, sbf.createBuilder()
						.withDescription("Model will be used by query requests that identify this model "
								+ "by the provided name.")
						.withType(InstanceType.STRING, InstanceType.NULL)
						.build())
				
				.withProperty(DEFAULT_QUERY_MODEL_PROP, sbf.createBuilder()
						.withDescription("If true, indicates this Model will be asssigned to the default "
								+ "domain query model.")
						.withType(InstanceType.BOOLEAN)
						.withDefault(JsonValue.FALSE)
						.build())
								
				.withProperty(MESSAGE_CONTEXT_PROP, Validatable.retrieveSchema(MessageContext.class))
				
				.withProperty(OPERATION_CONTEXT_PROP, sbf.createBuilder()
						.withOneOf(allowedOps)
						.withType(InstanceType.OBJECT, InstanceType.NULL)
						.build())
				
				.withNot(sbf.createBuilder()
					.withDescription("Model must apply to an exchange operation and/or a query default or named model.")
					.withProperty(OPERATION_CONTEXT_PROP, sbf.createBuilder() 
						.withConst(JsonValue.NULL)
						.build())
					.withProperty(NAMED_QUERY_MODEL_PROP, sbf.createBuilder()
						.withConst(JsonValue.NULL)
						.build())
					.withProperty(DEFAULT_QUERY_MODEL_PROP, sbf.createBuilder()
						.withConst(JsonValue.FALSE)
						.build())
					.build())

				.withRequired(ARTIFACTS_PROP, NAMED_QUERY_MODEL_PROP, DEFAULT_QUERY_MODEL_PROP, 
						      MESSAGE_CONTEXT_PROP, OPERATION_CONTEXT_PROP)	
						
			.build();
		
		//@formatter:on

		return ret;
	}
}
