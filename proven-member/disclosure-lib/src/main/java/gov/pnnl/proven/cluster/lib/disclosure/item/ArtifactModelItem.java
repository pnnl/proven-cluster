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
import java.util.Optional;

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
 * Immutable class representing a Artifact model item message. This message
 * defines a single semantic model that may be used to define query and/or
 * message models.
 * 
 * @author d3j766
 *
 * @see MessageModelItem
 */
public class ArtifactModelItem implements MessageItem {

	public static final String MODEL_ARTIFACT_MESSAGE_NAME = "model-artifact-message";
	public static final String DEFAULT_MODEL_NAME = "DEFAULT";
	public static final String CONTENT_ENCODING = "base64";

	public enum Syntax {

		JSONLD("JSON-LD"),
		RDFXML("RDF/XM"),
		TURTLE("Turtle"),
		NTRIPLES("N-Triple"),
		N3("N3"),
		NQUADS("N-Quads");

		private String syntaxName;

		private Syntax(String syntaxName) {
			this.syntaxName = syntaxName;
		}

		public String getSyntaxName() {
			return syntaxName;
		}

		public static List<String> getSyntaxNames() {

			List<String> ret = new ArrayList<String>();
			for (Syntax syntax : Syntax.values()) {
				ret.add(syntax.getSyntaxName());
			}

			return ret;
		}

		public static Syntax getDefaultSyntax() {
			return JSONLD;
		}

	}

	static final String ARTIFACT_PROP = "artifact";
	static final String LOCATOR_PROP = "locator";
	static final String NAMED_QUERY_MODELS_PROP = "namedModels";
	static final String DEFAULT_QUERY_MODEL_PROP = "defaultQueryModel";
	static final String CONTENT_PROP = "content";
	static final String SYNTAX_PROP = "syntax";

	private ArtifactContext artifact;
	private Boolean locator;
	private String[] namedQueryModels;
	private Boolean defaultQueryModel;
	private String content;
	private Syntax syntax;

	public ArtifactModelItem() {
	}

	@JsonbCreator
	public static ArtifactModelItem createMessageContext(@JsonbProperty(ARTIFACT_PROP) ArtifactContext artifact,
			@JsonbProperty(LOCATOR_PROP) Boolean locator, @JsonbProperty(NAMED_QUERY_MODELS_PROP) String[] namedModels,
			@JsonbProperty(DEFAULT_QUERY_MODEL_PROP) Boolean defaultQueryModel,
			@JsonbProperty(CONTENT_PROP) String content, @JsonbProperty(SYNTAX_PROP) Syntax syntax) {
		return ArtifactModelItem.newBuilder().withArtifact(artifact).withLocator(locator)
				.withNamedQueryModels(namedModels).withDefaultQueryModel(defaultQueryModel).withContent(content)
				.withSyntax(syntax).build(true);
	}

	private ArtifactModelItem(Builder b) {
		this.artifact = b.artifact;
		this.locator = b.locator;
		this.namedQueryModels = b.namedQueryModels;
		this.defaultQueryModel = b.defaultQueryModel;
		this.content = b.content;
		this.syntax = b.syntax;
	}

	@JsonbProperty(ARTIFACT_PROP)
	public ArtifactContext getArtifact() {
		return artifact;
	}

	@JsonbProperty(LOCATOR_PROP)
	public Boolean isLocator() {
		return locator;
	}

	@JsonbProperty(NAMED_QUERY_MODELS_PROP)
	public String[] getNamedQueryModels() {
		return namedQueryModels;
	}

	@JsonbProperty(DEFAULT_QUERY_MODEL_PROP)
	public Boolean isDefaultQueryModel() {
		return defaultQueryModel;
	}

	@JsonbProperty(CONTENT_PROP)
	public Optional<String> getContent() {
		return Optional.ofNullable(content);
	}

	@JsonbProperty(SYNTAX_PROP)
	public Syntax getSyntax() {
		return syntax;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {

		private ArtifactContext artifact;
		private Boolean locator;
		private String[] namedQueryModels;
		private Boolean defaultQueryModel;
		private String content;
		private Syntax syntax;

		private Builder() {
		}

		public Builder withArtifact(ArtifactContext artifact) {
			this.artifact = artifact;
			return this;
		}

		public Builder withLocator(Boolean locator) {
			this.locator = locator;
			return this;
		}

		public Builder withNamedQueryModels(String[] namedModels) {
			this.namedQueryModels = namedModels;
			return this;
		}

		public Builder withDefaultQueryModel(Boolean defaultQueryModel) {
			this.defaultQueryModel = defaultQueryModel;
			return this;
		}

		public Builder withContent(String content) {
			this.content = content;
			return this;
		}

		public Builder withSyntax(Syntax syntax) {
			this.syntax = syntax;
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
		public ArtifactModelItem build() {
			return build(false);
		}

		private ArtifactModelItem build(boolean trustedBuilder) {

			ArtifactModelItem ret = new ArtifactModelItem(this);

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
		return MessageContent.REFERENCE;
	}

	@Override
	public String messageName() {
		return MODEL_ARTIFACT_MESSAGE_NAME;
	}

	@Override
	public JsonSchema toSchema() {

		JsonSchema ret;

		//@formatter:off
		ret = sbf.createBuilder()

			.withId(Validatable.schemaId(this.getClass()))
			
			.withSchema(Validatable.schemaDialect())
			
			.withTitle("Model Artifact message schema")
			
			.withDescription("Model artifacts represent a semantic model, or graph, that "
						+ "contains knowledge used to support domain operations and query "
					    + "requests within a Proven platform.")

			.withType(InstanceType.OBJECT)
			
			.withProperty(MESSAGE_NAME_PROP, messageNamePropertySchema())
		
			.withProperty(ARTIFACT_PROP, Validatable.retrieveSchema(ArtifactContext.class))

			.withProperty(LOCATOR_PROP, sbf.createBuilder()
				.withDescription("If true, indicates artifact's identifier is a locator and will be used to "
						+ "retrieve artifact's contents.")
				.withType(InstanceType.BOOLEAN)
				.build())
			
			.withProperty(NAMED_QUERY_MODELS_PROP, sbf.createBuilder()
					.withDescription("Identifies the query models this artifaact will be part of."
							+ "Names may be represented as a file path, in this way, "
							+ "query models can be organized similar to a file system.")
					.withType(InstanceType.ARRAY)
					.withDefault(JsonValue.EMPTY_JSON_ARRAY)
					.withItems(sbf.createBuilder()
							.withType(InstanceType.STRING)
							.withPattern("([/a-zA-Z0-9_-]+)+$")
							.withDescription("Cannot include the deafult query model name in array of named models. "
									+ "Set property " + DEFAULT_QUERY_MODEL_PROP + " to true to indicate the artifact "
									+ "should be included in the default query model")
							.withNot(sbf.createBuilder()
									.withPattern("^\\/*" + DEFAULT_MODEL_NAME + "\\/*$")
									.build())	
							.build())
					.build())
			
			.withProperty(DEFAULT_QUERY_MODEL_PROP, sbf.createBuilder()
					.withDescription("If true, indicates this Model will be asssigned to the default "
							+ "domain query model.")
					.withType(InstanceType.BOOLEAN)
					.withDefault(JsonValue.FALSE)
					.build())
			
			.withProperty(CONTENT_PROP, sbf.createBuilder()
				.withDescription("BASE64 encoding of the artifact's content; required if locator property is false.")
				.withType(InstanceType.STRING, InstanceType.NULL)
				.withDefault(JsonValue.NULL)
				.withContentEncoding(CONTENT_ENCODING)
				.build())
			
			.withProperty(SYNTAX_PROP, sbf.createBuilder()
				.withDescription("Identifies graph syntax.")
				.withType(InstanceType.STRING)
				.withDefault(Json.createValue(Syntax.getDefaultSyntax().getSyntaxName()))
				.withEnum(Validatable.toJsonValues(Syntax.getSyntaxNames()))
				.withDefault(Json.createValue(Syntax.JSONLD.getSyntaxName()))
				.build())
			
			.withAllOf(
				sbf.createBuilder()
				.withDescription("If artifact's identifier is not designated as a locator then " 
						+ "artifact content must be provided in message.")
				.withIf(sbf.createBuilder()
					.withProperty(LOCATOR_PROP, sbf.createBuilder()
						.withConst(JsonValue.FALSE)
						.build())
					.build())
				.withThen(sbf.createBuilder()
					.withRequired(CONTENT_PROP)				
					.build())
				.build())							
			
			.withRequired(ARTIFACT_PROP, LOCATOR_PROP)
			
			.build();
		
		//@formatter:on

		return ret;
	}

}
