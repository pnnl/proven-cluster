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

package gov.pnnl.proven.cluster.lib.disclosure.message;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the different message content types in support of message
 * disclosure. A distinction is made for types that can be validated by the
 * Proven message JSON Schema and those that are internal to the Proven
 * platform.
 * 
 * @author d3j766
 * 
 * @see DisclosureItem, ProvenMessage
 *
 */
public enum MessageContent {

	/**
	 * Internal type only used by DisclosureMessage. It's not a valid content
	 * type for disclosed data to the platform. A DisclosureMessage declares its
	 * content type as {@link #Disclosure} in order to allow for its storage in
	 * a disclosure stream.
	 * 
	 * Post any exchange processing, all disclosed data is routed to a domain's
	 * disclosure stream as a DisclosureMessage. Domain disclosure streams are
	 * the entry points for disclosed data into Proven's hybrid store's memory
	 * grid facet (i.e. streaming environment).
	 * 
	 * @see DisclosureItem, DisclosureMessage
	 * 
	 */
	Disclosure(MessageContentName.DISCLOSURE, false),

	/**
	 * The Default content type. Explicit message content is domain instance
	 * data that can be represented in the semantic and memory grid facets of
	 * the hybrid store.
	 */
	Explicit(MessageContentName.EXPLICIT, true),

	/**
	 * Static or reference message content. This is data that changes
	 * infrequently (i.e. lookup data) and is a form of {@link #Explicit}.
	 * Static content will not age-off of the memory grid facet of the hybrid
	 * store, but instead remain available in-memory for query and analysis
	 * purposes.
	 */
	Static(MessageContentName.STATIC, true),

	/**
	 * Implicit content is content that is created from existing explicit
	 * content. This content is considered as transient, in that it can be
	 * regenerated on demand.
	 */
	Implicit(MessageContentName.IMPLICIT, true),

	/**
	 * Semantic concept structure definitions and associated instance data (e.g.
	 * an ontology or vocabulary sources). Structure message content is stored
	 * in its own graph contributing to a single domain's knowledge model or all
	 * domain knowledge models (e.g. an upper or foundation ontology).
	 */
	Structure(MessageContentName.STRUCTURE, true),

	/**
	 * Query message content. Knowledge describes a query for execution over
	 * Proven's hybrid store. These are "select" only query types run against
	 * hybrid store, and results may be stored back in hybrid store and/or
	 * returned to caller if requested.
	 */
	Query(MessageContentName.QUERY, true),

	/**
	 * Continuous Query message content. Queries to run over a cluster member's
	 * ProvenMessage data name for a configured frequency, in order to detect
	 * and/or create newly purposed message content. This is a type of
	 * {@link MessageContent#Query}; query results are not returned to
	 * requester, but instead are saved in platform for future processing.
	 */
	ContinuousQuery(MessageContentName.CONTINUOUS_QUERY, true),

	/**
	 * Represents a pipeline processing service. Contains reference information
	 * regarding a pipeline request, used for management purposes.
	 */
	PipelineService(MessageContentName.PIPELINE_REQUEST, true),

	/**
	 * Represents a module processing service. Contains reference information
	 * regarding a module request, used for management purposes.
	 */
	ModuleService(MessageContentName.MODULE_REQUEST, true),

	/**
	 * Cluster administrative message content. Contains directives to configure
	 * and manage cluster members.
	 */
	Administrative(MessageContentName.ADMINISTRATIVE, true),

	/**
	 * Represents Proven measurement data (i.e. metrics) that will be processed
	 * and stored into a time-series store registered with the Proven data
	 * platform.
	 */
	Measurement(MessageContentName.MEASUREMENT, true),

	/**
	 * Represents response message data for an internal processing component.
	 * Not schema validated.
	 */
	Response(MessageContentName.RESPONSE, false),

	/**
	 * Represents a request for the registration of a service object.
	 */
	ServiceRegistration(MessageContentName.SERVICE_REGISTRATION, true),

	/**
	 * Represents any of the JSON Schema related message content types. It
	 * itself is not a schema based content type. This is a convenience type,
	 * use {@link #getSchemaValues()} to return all schema based types.
	 */
	Any(MessageContentName.ANY, false);

	public class MessageContentName {
		public static final String DISCLOSURE = "disclosure";
		public static final String EXPLICIT = "explicit";
		public static final String STATIC = "static";
		public static final String IMPLICIT = "implicit";
		public static final String STRUCTURE = "structure";
		public static final String QUERY = "query";
		public static final String CONTINUOUS_QUERY = "continuous";
		public static final String PIPELINE_REQUEST = "pipeline";
		public static final String MODULE_REQUEST = "module";
		public static final String ADMINISTRATIVE = "administrative";
		public static final String MEASUREMENT = "measurement";
		public static final String RESPONSE = "response";
		public static final String SERVICE_REGISTRATION = "serviceRegistration";
		public static final String ANY = "any";
	}

	private static Logger log = LoggerFactory.getLogger(MessageContent.class);

	private String name;
	private boolean schema;

	MessageContent() {
	}

	MessageContent(String name, boolean schema) {
		this.name = name;
		this.schema = schema;
	}

	/**
	 * Name of the message content type. For schema content types (@see
	 * {@link #isSchema()}), this name must match the Proven message's public
	 * JSON Schema for a "content" value.
	 * 
	 * @return name for the content type
	 */
	public String getName() {
		return name;
	}

	/**
	 * Indicates if the message content type is included in the Proven Message's
	 * JSON Schema. This schema is used for syntactic verification of a Proven
	 * message. Syntactic verification is only necessary for content originating
	 * from an external source.
	 * 
	 * @return true if it's part of Proven message's JSON Schema, false
	 *         otherwise.
	 */
	public boolean isSchema() {
		return schema;
	}

	public static MessageContent getValue(String name) {

		MessageContent ret = null;
		for (MessageContent mc : values()) {
			if (name.equals(mc.getName())) {
				ret = mc;
				break;
			}
		}

		return ret;
	}

	/**
	 * Provides a list of all message content names.
	 */
	public static List<String> getNames() {

		List<String> ret = new ArrayList<>();
		for (MessageContent mc : values()) {
			ret.add(mc.getName());
		}

		return ret;
	}

	/**
	 * Provides a list of all schema based message content types.
	 * 
	 * @see #Any
	 */
	public static MessageContent[] getSchemaValues() {

		List<MessageContent> mcList = new ArrayList<>();
		for (MessageContent mc : values()) {
			if (mc.isSchema()) {
				mcList.add(mc);
			}
		}
		MessageContent ret[] = new MessageContent[mcList.size()];
		return mcList.toArray(ret);
	}

	/**
	 * Provides a list of all non-schema based message content types.
	 */
	public static MessageContent[] getNonSchemaValues() {

		List<MessageContent> mcList = new ArrayList<>();
		for (MessageContent mc : values()) {
			if (!mc.isSchema()) {
				mcList.add(mc);
			}
		}
		MessageContent ret[] = new MessageContent[mcList.size()];
		return mcList.toArray(ret);
	}

}
