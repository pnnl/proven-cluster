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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.json.JsonObject;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.disclosure.message.exception.InvalidProvenMessageException;

/**
 * General messaging construct used by Proven to communicate data between Proven
 * application components. Proven messages have global identification and a
 * defined processing life-cycle within a Proven cluster member.
 * 
 * @author raju332
 * @author d3j766
 *
 */
public abstract class ProvenMessage implements IdentifiedDataSerializable, Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ProvenMessage.class);

	/**
	 * Message KEY
	 * 
	 * Message keys are used to identify proven messages in the memory grid of
	 * Proven's hybrid store.
	 */
	public static final String MESSAGE_KEY_DELIMETER = "^||^";

	public enum MessageKeyPartOrder {
		MessageId,
		Domain,
		Name,
		Source,
		Created;
	};

	/**
	 * Provides the KEY for a ProvenMessage being stored in a message stream.
	 * 
	 * @return a String representing the KEY
	 */
	public String getMessageKey() {

		String ret = "";
		ret += this.messageId + MESSAGE_KEY_DELIMETER;
		ret += ((this.domain == null) ? "" : this.domain.getDomain()) + MESSAGE_KEY_DELIMETER;
		ret += this.messageProperties.getCreated();

		return ret;
	}

	/**
	 * Messages are stored internally as JSON
	 */
	JsonObject message;
	
	
	/**
	 * JSON schema, can be disclosed with message.
	 */
	JsonObject messageSchema;


	/**
	 * Messages are assigned an identifier , making it unique across disclosure
	 * sources.
	 */
	UUID messageId;

	/**
	 * Identifies {@link MessageContent}
	 */
	MessageContent messageContent;

	/**
	 * Identifies messages domain, all messages must be associated with a
	 * domain. If one is not provided, Proven provides a default domain for
	 * which the message will be associated. In Proven, a domain represents a
	 * discrete sphere or model of activity or knowledge. That is, it identifies
	 * a grouping of knowledge that is managed separately from other domain
	 * knowledge models. In proven's hybrid store, domain knowledge is isolated
	 * by sub-graphs in its semantic store, by databases in time-series store,
	 * and by key values in distributed Map structures in the memory grid.
	 */
	DisclosureDomain domain;

	/**
	 * If true, message content will not be persisted in the semantic or
	 * time-series components of the hybrid store. Default is false.
	 */
	boolean isTransient;

	/**
	 * If true, message content will remain in memory grid facet of hybrid store
	 * unless explicitly removed, @see {@link MessageContent#Static}. Default is
	 * false.
	 */
	boolean isStatic;

	/**
	 * Identifies source of the message (e.g. proven-client).
	 */
	String source;

	/**
	 * Properties associated with message. 
	 */
	MessageProperties messageProperties = new MessageProperties();

	public ProvenMessage() {		
	}
	
	public ProvenMessage(JsonObject message, DisclosureDomain domain) {
		this.message = message;
		this.domain = domain;
		this.source = "UNKNOWN";
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		this.message = in.readObject();
		this.messageId = UUID.fromString(in.readUTF());
		this.messageContent = MessageContent.valueOf(in.readUTF());
		this.domain = in.readObject();
		this.isTransient = in.readBoolean();
		this.isStatic = in.readBoolean();
		this.source = in.readUTF();
		this.messageProperties = in.readObject();	
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		out.writeObject(this.message);
		out.writeUTF(this.messageId.toString());
		out.writeUTF(this.messageContent.toString());
		out.writeObject(this.domain);
		out.writeBoolean(this.isTransient);
		out.writeBoolean(this.isStatic);
		out.writeUTF(this.source);
		out.writeObject(this.messageProperties);
	}
	
	public JsonObject getMessage() {
		return message;
	}

	public UUID getMessageId() {
		return messageId;
	}

	public MessageContent getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(MessageContent messageContent) {
		this.messageContent = messageContent;
	}

	public DisclosureDomain getDomain() {
		return domain;
	}

	public boolean isTransient() {
		return isTransient;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public String getSource() {
		return source;
	}

	public MessageProperties getMessageProperties() {
		return messageProperties;
	}
	
}
