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
package gov.pnnl.proven.cluster.lib.disclosure.item.sse;

import java.util.Arrays;
import java.util.List;

import javax.json.JsonValue;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.ws.rs.core.Response;

import org.leadpony.justify.api.InstanceType;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidatingException;
import org.leadpony.justify.api.Problem;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.disclosure.exception.ValidatableBuildException;
import gov.pnnl.proven.cluster.lib.disclosure.item.DisclosureItem;
import gov.pnnl.proven.cluster.lib.disclosure.item.MessageItem;
import gov.pnnl.proven.cluster.lib.disclosure.item.Validatable;
import gov.pnnl.proven.cluster.lib.disclosure.item.operation.ItemOperation;

/**
 * Represents a SSE subscription for operation events.
 * 
 * @author d3j766
 *
 * @see OperationEvent
 * 
 */
public class OperationSubscription implements EventSubscription {

	public static final String DOMAIN_PROP = "domain";
	public static final String STATUSES_PROP = "statuses";
	public static final String OPERATIONS_PROP = "operations";
	public static final String ITEMS_PROP = "items";
	public static final String NAMES_PROP = "names";
	public static final String REQUESTORS_PROP = "requestors";
	public static final String TAGS_PROP = "tags";

	private DisclosureDomain domain;
	private List<String> statuses;
	private List<String> operations;
	private List<String> items;
	private List<String> names;
	private List<String> requestors;
	private List<String> tags;

	public OperationSubscription() {
	}

	@JsonbCreator
	public static OperationSubscription createOperationSubscription(@JsonbProperty(DOMAIN_PROP) DisclosureDomain domain,
			@JsonbProperty(STATUSES_PROP) List<String> statuses,
			@JsonbProperty(OPERATIONS_PROP) List<String> operations, @JsonbProperty(ITEMS_PROP) List<String> items,
			@JsonbProperty(NAMES_PROP) List<String> names, @JsonbProperty(REQUESTORS_PROP) List<String> requestors,
			@JsonbProperty(TAGS_PROP) List<String> tags) {
		return OperationSubscription.newBuilder().withDomain(domain).withStatuses(statuses).withOperations(operations)
				.withItems(items).withRequestors(requestors).withNames(names).withTags(tags).build(true);
	}

	private OperationSubscription(Builder b) {
		this.domain = b.domain;
		this.statuses = b.statuses;
		this.operations = b.operations;
		this.items = b.items;
		this.requestors = b.requestors;
		this.names = b.names;
		this.tags = b.tags;
	}

	@JsonbProperty(STATUSES_PROP)
	public List<String> getStatuses() {
		return statuses;
	}

	@JsonbProperty(OPERATIONS_PROP)
	public List<String> getOperations() {
		return operations;
	}

	@JsonbProperty(ITEMS_PROP)
	public List<String> getItems() {
		return items;
	}

	@JsonbProperty(NAMES_PROP)
	public List<String> getNames() {
		return names;
	}

	@JsonbProperty(REQUESTORS_PROP)
	public List<String> getRequestors() {
		return requestors;
	}

	@JsonbProperty(TAGS_PROP)
	public List<String> getTags() {
		return tags;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {

		private DisclosureDomain domain;
		private List<String> statuses;
		private List<String> operations;
		private List<String> items;
		private List<String> names;
		private List<String> requestors;
		private List<String> tags;

		private Builder() {
		}

		public Builder withDomain(DisclosureDomain domain) {
			this.domain = domain;
			return this;
		}

		public Builder withStatuses(List<String> statuses) {
			this.statuses = statuses;
			return this;
		}

		public Builder withOperations(List<String> operations) {
			this.operations = operations;
			return this;
		}

		public Builder withItems(List<String> items) {
			this.items = items;
			return this;
		}

		public Builder withNames(List<String> names) {
			this.names = names;
			return this;
		}

		public Builder withRequestors(List<String> requestors) {
			this.requestors = requestors;
			return this;
		}

		public Builder withTags(List<String> tags) {
			this.tags = tags;
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
		public OperationSubscription build() {
			return build(false);
		}

		private OperationSubscription build(boolean trustedBuilder) {

			OperationSubscription ret = new OperationSubscription(this);

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
	public EventType getEventType() {
		// TODO Auto-generated method stub
		return EventType.OPERATION;
	}

	@Override
	public DisclosureDomain getDomain() {
		return domain;
	}

	@Override
	public boolean subscribed(DisclosureItem disclosureItem) {
		boolean ret = false;
		return false;
	}

	@Override
	public JsonSchema toSchema() {

		JsonSchema ret;
		
		 items = MessageItem.messageNames();

		//@formatter:off
		ret = sbf.createBuilder()

			.withId(Validatable.schemaId(this.getClass()))
			
			.withSchema(Validatable.schemaDialect())
			
			.withTitle("Operation subscription.")

			.withDescription("A SSE subscription for operation results.") 

			.withType(InstanceType.OBJECT)
			
			.withProperty(DOMAIN_PROP,
					sbf.createBuilder()
					.withDescription("Identifies domain value.")
					.withType(InstanceType.STRING)
					.withPattern(Validatable.DOMAIN_PATTERN)
					.build())
			.withProperty(STATUSES_PROP, sbf.createBuilder()
					.withType(InstanceType.ARRAY)
					.withDefault(JsonValue.EMPTY_JSON_ARRAY)
					.withItems(sbf.createBuilder()
						.withType(InstanceType.STRING)
						.withEnum(Validatable.toJsonValues(Arrays.asList(
							Response.Status.Family.SUCCESSFUL.toString(),
							Response.Status.Family.CLIENT_ERROR.toString(),
							Response.Status.Family.SERVER_ERROR.toString(),
							Response.Status.Family.OTHER.toString())))
						.withUniqueItems(true)
						.build())
					.build())			
			.withProperty(OPERATIONS_PROP, sbf.createBuilder()
					.withType(InstanceType.ARRAY)
					.withDefault(JsonValue.EMPTY_JSON_ARRAY)
					.withItems(sbf.createBuilder()
						.withType(InstanceType.STRING)
						.withEnum(Validatable.toJsonValues(ItemOperation.getOperationNames()))
						.withUniqueItems(true)
						.build())
					.build())			
			.withProperty(ITEMS_PROP, sbf.createBuilder()
					.withType(InstanceType.ARRAY)
					.withDefault(JsonValue.EMPTY_JSON_ARRAY)
					.withItems(sbf.createBuilder()
						.withType(InstanceType.STRING)
						.withEnum(Validatable.toJsonValues(MessageItem.messageNames()))
						.withUniqueItems(true)
						.build())
					.build())
			.withProperty(NAMES_PROP, sbf.createBuilder()
					.withType(InstanceType.ARRAY)
					.withDefault(JsonValue.EMPTY_JSON_ARRAY)
					.withItems(sbf.createBuilder()
						.withType(InstanceType.STRING)
						.build())
					.build())
			.withProperty(REQUESTORS_PROP, sbf.createBuilder()
					.withType(InstanceType.ARRAY)
					.withDefault(JsonValue.EMPTY_JSON_ARRAY)
					.withItems(sbf.createBuilder()
						.withType(InstanceType.STRING)
						.build())
					.build())						
			.withProperty(TAGS_PROP, sbf.createBuilder()
					.withType(InstanceType.ARRAY)
					.withDefault(JsonValue.EMPTY_JSON_ARRAY)
					.withItems(sbf.createBuilder()
						.withType(InstanceType.STRING)
						.build())
					.build())

			.withRequired(DOMAIN_PROP)
			
			.build();
						
		//@formatter:on

		return ret;
	}

}
