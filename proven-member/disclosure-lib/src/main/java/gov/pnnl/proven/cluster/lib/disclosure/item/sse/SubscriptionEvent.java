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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonValue;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

import org.leadpony.justify.api.InstanceType;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidatingException;
import org.leadpony.justify.api.Problem;

import gov.pnnl.proven.cluster.lib.disclosure.exception.ValidatableBuildException;
import gov.pnnl.proven.cluster.lib.disclosure.item.Validatable;
import gov.pnnl.proven.cluster.lib.disclosure.item.response.ResponseItem;

/**
 * Immutable class representing an SSE subscription.
 * 
 * @author d3j766
 *
 * @see EventData, EventSubscriptionItem
 *
 */
public class SubscriptionEvent implements EventData {

	public static final String SUBSCRIPTION_EVENT_NAME = "subscription-event";

	static final String SESSION_ID_PROP = "sessionId";
	static final String EVENT_NAME_PROP = "eventName";
	static final String SUBSCRIPTION_PROP = "subscription";

	private UUID sessionId;
	private EventSubscription subscription;

	public SubscriptionEvent() {
	}

	@JsonbCreator
	public static SubscriptionEvent createOperationEvent(@JsonbProperty(SESSION_ID_PROP) UUID sessionId,
			@JsonbProperty(SUBSCRIPTION_PROP) EventSubscription subscription) {
		return SubscriptionEvent.newBuilder().withSessionId(sessionId)
				.withSubscription(subscription).build(true);
	}

	private SubscriptionEvent(Builder b) {
		this.sessionId = b.sessionId;
		this.subscription = b.subscription;
	}

	@JsonbProperty(SESSION_ID_PROP)
	@Override
	public UUID getSessionId() {
		return sessionId;
	}

	@JsonbProperty(EVENT_NAME_PROP)
	@Override
	public String getEventName() {
		return SUBSCRIPTION_EVENT_NAME;
	}

	@JsonbProperty(SUBSCRIPTION_PROP)
	public EventSubscription getSubscription() {
		return subscription;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {

		private UUID sessionId;
		private EventSubscription subscription;

		private Builder() {
		}

		public Builder withSessionId(UUID sessionId) {
			this.sessionId = sessionId;
			return this;
		}

		public Builder withSubscription(EventSubscription subscription) {
			this.subscription = subscription;
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
		public SubscriptionEvent build() {
			return build(false);
		}

		private SubscriptionEvent build(boolean trustedBuilder) {

			SubscriptionEvent ret = new SubscriptionEvent(this);

			if (!trustedBuilder) {
				List<Problem> problems = ret.validate();
				if (!problems.isEmpty()) {
					throw new ValidatableBuildException("Builder failure", new JsonValidatingException(problems));
				}
			}

			return ret;
		}
	}

	public JsonSchema toSchema() {

		JsonSchema ret;
		
		// Create EventuSbscription schema definition listing
		List<Class<? extends EventSubscription>> subscriptions = EventSubscription.subscriptions();
		List<JsonSchema> subscriptionSchemas = new ArrayList<>();
		for (Class<? extends EventSubscription> subscription : subscriptions) {
			subscriptionSchemas.add(Validatable.retrieveSchema(subscription));
		}
		
		//@formatter:off
		ret = sbf.createBuilder()

			.withId(Validatable.schemaId(this.getClass()))
			
			.withSchema(Validatable.schemaDialect())
			
			.withTitle("SSE subscription.")

			.withDescription("This SSE message provides the subscription information, including the session identifier "
						   + "reference for the HTTP connection.") 

			.withType(InstanceType.OBJECT)

			.withProperty(SESSION_ID_PROP, sbf.createBuilder()
				.withDescription("SSE session identifier")
				.withType(InstanceType.STRING)
				.withPattern(UUID_PATTERN)
				.build())
							
			.withProperty(EVENT_NAME_PROP, sbf.createBuilder()
				.withDescription("This message name.")
				.withType(InstanceType.STRING)
				.withConst(Json.createValue(getEventName()))
				.build())
			
			.withProperty(SUBSCRIPTION_PROP, sbf.createBuilder()
					.withOneOf(subscriptionSchemas)
					.withType(InstanceType.OBJECT)
					.build())
						
			.withRequired(SESSION_ID_PROP, EVENT_NAME_PROP, SUBSCRIPTION_PROP)
			
			.build();
						
		//@formatter:on

		return ret;
	}
}
