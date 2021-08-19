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
import java.util.Optional;

import javax.json.JsonValue;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

import org.leadpony.justify.api.InstanceType;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidatingException;
import org.leadpony.justify.api.Problem;

import gov.pnnl.proven.cluster.lib.disclosure.exception.ValidatableBuildException;

/**
 * Immutable class representing the context of an artifact item. The context
 * helps to identify an artifact in terms of its identifier and version for use
 * in model messages.
 * 
 * @author d3j766
 * 
 * @see ModelArtifactItem, ModelItem
 *
 */
public class ArtifactContext implements Validatable {

	public static final String ID_PROP = "id";
	public static final String VERSION_PROP = "version";

	private String id;
	private String version;
	private Boolean latest;

	public ArtifactContext() {
	}

	@JsonbCreator
	public static ArtifactContext createArtifactContext(@JsonbProperty(ID_PROP) String id,
			@JsonbProperty(VERSION_PROP) String version) {
		return ArtifactContext.newBuilder().withId(id).withVersion(version).build(true);
	}

	private ArtifactContext(Builder b) {
		this.id = b.id;
		this.version = b.version;
	}

	@JsonbProperty(ID_PROP)
	public String getId() {
		return id;
	}

	@JsonbProperty(VERSION_PROP)
	public Optional<String> getVersion() {
		return Optional.ofNullable(version);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {

		private String id;
		private String version;

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public Builder withVersion(String version) {
			this.version = version;
			return this;
		}

		/**
		 * Builds new instance. Instance is validated post construction.
		 * 
		 * @return new instance
		 * 
		 * @throws JsonValidatingException if created instance fails JSON-SCHEMA
		 *                                 validation.
		 * 
		 */
		public ArtifactContext build() {
			return build(false);
		}

		private ArtifactContext build(boolean trustedBuilder) {

			ArtifactContext ret = new ArtifactContext(this);

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

		//@formatter:off
		ret = sbf.createBuilder()

				.withId(Validatable.schemaId(this.getClass()))
				
				.withSchema(Validatable.schemaDialect())
				
				.withTitle("Artifact context")
				
				.withDescription("The context helps to identify an artifact in terms of its "
						+ "identifier and version for use in model item messages.")

				.withType(InstanceType.OBJECT)
				
				.withProperty(ID_PROP, sbf.createBuilder()
					.withDescription("Artifact identifier, represented as an URI.  If the URI is designated as a locator"
							+ "(see 'locator' property) then contents will be retrieved from this location")
					.withType(InstanceType.STRING)
					.withPattern(Validatable.URI_PATTERN)
					.build())

				.withProperty(VERSION_PROP, sbf.createBuilder()
					.withDescription("Artifact's version.")
					.withType(InstanceType.STRING, InstanceType.NULL)
					.withDefault(JsonValue.NULL)
					.build())
																
				.withRequired(ID_PROP)
				
				.build();
		
		//@formatter:on

		return ret;
	}
}
