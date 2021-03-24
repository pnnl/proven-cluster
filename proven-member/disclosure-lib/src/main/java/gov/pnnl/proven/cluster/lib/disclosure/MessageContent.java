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

package gov.pnnl.proven.cluster.lib.disclosure;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.andrewoma.dexx.collection.HashSet;

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
	 * The Default content type. Explicit message content is domain instance
	 * data that can be represented in the semantic and memory grid facets of
	 * the hybrid store.
	 */
	Explicit(MessageContentName.EXPLICIT),

	/**
	 * Static or reference message content. This is data that changes
	 * infrequently (i.e. lookup data) and is a form of {@link #Explicit}.
	 * Static content will not age-off of the memory grid facet of the hybrid
	 * store, but instead remain available in-memory for query and analysis
	 * purposes.
	 */
	Static(MessageContentName.STATIC),

	/**
	 * Implicit content is content that is created from existing explicit
	 * content. This content is considered as transient, in that it can be
	 * regenerated on demand.
	 */
	Implicit(MessageContentName.IMPLICIT),

	/**
	 * Semantic concept structure definitions and associated instance data (e.g.
	 * an ontology or vocabulary sources). Structure message content is stored
	 * in its own graph contributing to a single domain's knowledge model or all
	 * domain knowledge models (e.g. an upper or foundation ontology).
	 */
	Structure(MessageContentName.STRUCTURE),

	/**
	 * Model content represents a semantic data model supporting domain based
	 * operations within the Proven platform.
	 */
	Model(MessageContentName.MODEL),

	/**
	 * Query message content. Represents requests to query the Hybrid Store.
	 */
	Query(MessageContentName.QUERY),

	/**
	 * Replay message content. Represents a request to replay a specified set of
	 * messages starting at either disclosure (i.e. exchange) or distribution
	 * (i.e. stream).
	 */
	Replay(MessageContentName.REPLAY),

	/**
	 * Represents a pipeline processing service request. Contains the service
	 * object or a reference to an already registered service object.
	 * Information regarding the service and action to perform is contained in
	 * the message.
	 */
	PipelineService(MessageContentName.PIPELINE_SERVICE),

	/**
	 * Represents a module processing service request. Contains the service
	 * object or a reference to an already registered service object.
	 * Information regarding the service and action to perform is contained in
	 * the message.
	 */
	ModuleService(MessageContentName.MODULE_SERVICE),

	/**
	 * Cluster administrative message content. Contains directives to configure
	 * and manage cluster members.
	 */
	Administrative(MessageContentName.ADMINISTRATIVE),

	/**
	 * Represents Proven measurement data (i.e. metrics) that will be processed
	 * and stored into a time-series store registered with the Proven data
	 * platform.
	 */
	Measurement(MessageContentName.MEASUREMENT),

	/**
	 * Represents response message data for an internal processing component.
	 * Not schema validated.
	 */
	Response(MessageContentName.RESPONSE),

	/**
	 * Represents any of the JSON Schema related message content types. It
	 * itself is not a schema based content type. This is a convenience type,
	 * use {@link #getSchemaValues()} to return all schema based types.
	 */
	Any(MessageContentName.ANY);

	public class MessageContentName {
		public static final String EXPLICIT = "explicit";
		public static final String STATIC = "static";
		public static final String IMPLICIT = "implicit";
		public static final String STRUCTURE = "structure";
		public static final String MODEL = "messageModel";
		public static final String QUERY = "query";
		public static final String REPLAY = "replay";
		public static final String CONTINUOUS_QUERY = "continuous";
		public static final String PIPELINE_SERVICE = "pipeline";
		public static final String MODULE_SERVICE = "module";
		public static final String ADMINISTRATIVE = "administrative";
		public static final String MEASUREMENT = "measurement";
		public static final String RESPONSE = "response";
		public static final String SERVICE_REGISTRATION = "serviceRegistration";
		public static final String ANY = "any";
	}

	private static Logger log = LoggerFactory.getLogger(MessageContent.class);

	private String name;

	MessageContent() {
	}

	MessageContent(String name) {
		this.name = name;
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

	public static MessageContent getMessageContent(String name) {

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
		boolean excludeAny = false;
		return getNames(excludeAny);
	}

	/**
	 * Provides a list of message content names.
	 * 
	 * @param excludeAny
	 *            {@link #Any} will be excluded from the returned list if true.
	 */
	public static List<String> getNames(boolean excludeAny) {

		List<String> ret = new ArrayList<>();
		for (MessageContent mc : values()) {
			if (!(mc.equals(MessageContent.Any) && (excludeAny))) {
				ret.add(mc.getName());
			}
		}

		return ret;
	}
}
