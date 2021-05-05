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
 * Immutable class representing a Model Artifact item message. This message
 * defines a single model, or graph, that can be used in a ModelItem's
 * definition.
 * 
 * @author d3j766
 *
 * @see ModelItem
 */
public class ModelArtifactItem implements MessageItem {

	public enum Syntax {

		JSONLD("JSON-LD"),
		RDFXML("RDF/XM"),
		TURTLE("Turtle"),
		NTRIPLES("N-Triple"),
		N3("N3"),
		NQUADS("N-Quads");

		private String schemaName;

		private Syntax(String name) {
			this.schemaName = name;
		}

		public String getSchemaName() {
			return schemaName;
		}

		public static List<String> getNames() {

			List<String> ret = new ArrayList<String>();
			for (Syntax syntax : Syntax.values()) {
				ret.add(syntax.getSchemaName());
			}

			return ret;
		}

		public static Syntax getDefaultSyntax() {
			return JSONLD;
		}

	}

	static final String ARTIFACT_PROP = "artifact";
	static final String LOCATOR_PROP = "locator";
	static final String LD_CONTEXT_PROP = "ldContext";
	static final String CONTENT_PROP = "content";
	static final String SYNTAX_PROP = "syntax";

	private ArtifactContext artifact;
	private Boolean locator;
	private Boolean ldContext;
	private String content;
	private Syntax syntax;

	public ModelArtifactItem() {
	}

	@JsonbCreator
	public static ModelArtifactItem createMessageContext(@JsonbProperty(ARTIFACT_PROP) ArtifactContext artifact,
			@JsonbProperty(LOCATOR_PROP) Boolean locator, @JsonbProperty(LD_CONTEXT_PROP) Boolean ldContext,
			@JsonbProperty(CONTENT_PROP) String content, @JsonbProperty(SYNTAX_PROP) Syntax syntax) {
		return ModelArtifactItem.newBuilder().withArtifact(artifact).withLocator(locator).withLdContext(ldContext)
				.withContent(content).withSyntax(syntax).build(true);
	}

	private ModelArtifactItem(Builder b) {
		this.artifact = b.artifact;
		this.locator = b.locator;
		this.ldContext = b.ldContext;
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

	@JsonbProperty(LD_CONTEXT_PROP)
	public Boolean isLdContext() {
		return ldContext;
	}

	@JsonbProperty(CONTENT_PROP)
	public String getContent() {
		return content;
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
		private Boolean ldContext;
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

		public Builder withLdContext(Boolean ldContext) {
			this.ldContext = ldContext;
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
		public ModelArtifactItem build() {
			return build(false);
		}

		private ModelArtifactItem build(boolean trustedBuilder) {

			ModelArtifactItem ret = new ModelArtifactItem(this);

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
		return "Model Artifact message";
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
		
			.withProperty(ARTIFACT_PROP, Validatable.retrieveSchema(ArtifactContext.class))

			.withProperty(LOCATOR_PROP, sbf.createBuilder()
				.withDescription("If true, indicates artifact's identifier is a locator and will be used to "
						+ "retrieve artifact's contents.")
				.withType(InstanceType.BOOLEAN, InstanceType.NULL)
				.withDefault(JsonValue.FALSE)
				.build())
						
			.withProperty(LD_CONTEXT_PROP, sbf.createBuilder()
				.withDescription("If true, indicates the artifact is a JSON-LD @context object.")
				.withType(InstanceType.BOOLEAN, InstanceType.NULL)
				.withDefault(JsonValue.FALSE)
				.build())
			
			.withProperty(CONTENT_PROP, sbf.createBuilder()
				.withDescription("BASE64 encoding of the artifact's content; required if locator property is false.")
				.withType(InstanceType.STRING, InstanceType.NULL)
				.withContentEncoding("base64")
				.build())
			
			.withProperty(SYNTAX_PROP, sbf.createBuilder()
				.withDescription("Identifies graph syntax.")
				.withType(InstanceType.STRING, InstanceType.NULL)
				.withDefault(Json.createValue(Syntax.getDefaultSyntax().getSchemaName()))
				.withEnum(Validatable.toJsonValues(Syntax.getNames()))
				.withDefault(Json.createValue(Syntax.JSONLD.getSchemaName()))
				.build())
			
			.withAllOf(
				sbf.createBuilder()
				.withDescription("If JSON-LD context then syntax must be JSON-LD")
				.withIf(sbf.createBuilder()
					.withProperty(LD_CONTEXT_PROP, sbf.createBuilder()
						.withConst(JsonValue.TRUE)
						.build())
					.build())
				.withThen(sbf.createBuilder()
					.withProperty(SYNTAX_PROP, sbf.createBuilder()
						.withConst(Json.createValue(Syntax.JSONLD.getSchemaName()))
						.build())
					.build())
				.build(),								

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
			
			.withRequired(ARTIFACT_PROP)
			
			.build();
		
		//@formatter:on

		return ret;
	}

}
