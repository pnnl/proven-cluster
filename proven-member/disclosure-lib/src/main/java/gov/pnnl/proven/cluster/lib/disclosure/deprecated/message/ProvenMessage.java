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

package gov.pnnl.proven.cluster.lib.disclosure.deprecated.message;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.disclosure.MessageContent;
import gov.pnnl.proven.cluster.lib.disclosure.item.DisclosureItem;

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
		ret += this.disclosureItem.getContext().getDomain().getDomain() + MESSAGE_KEY_DELIMETER;
		ret += this.created;

		return ret;
	}

	/**
	 * Epoch time of message creation.
	 */
	private Long created;

	/**
	 * Messages are assigned an identifier , making it unique across disclosure
	 * sources.
	 */
	private UUID messageId;

	/**
	 * Identifies the source/parent message identifier, if any.
	 */
	private UUID sourceMessageId;

	/**
	 * Contains disclosed message contents
	 */
	private DisclosureItem disclosureItem;

	public ProvenMessage() {
		this.disclosureItem = new DisclosureItem();
	}

	/**
	 * Constructor for an initial disclosure.
	 * 
	 * @param di
	 *            the disclosure contents
	 */
	public ProvenMessage(DisclosureItem di) {
		this.created = new Date().getTime();
		this.messageId = UUID.randomUUID();
		this.sourceMessageId = null;
		this.disclosureItem = di;
	}

	/**
	 * Constructor for a downstream message. That is, after an initial
	 * disclosure. The new message is linked to the parent message via
	 * {@link #sourceMessageId}. The original disclosure item is maintained
	 * under {@link #disclosureItem}.
	 * 
	 * @param sourceMessage
	 *            the parent/source message for this message.
	 */
	public ProvenMessage(ProvenMessage source) {
		this.created = new Date().getTime();
		this.messageId = UUID.randomUUID();
		this.sourceMessageId = source.getMessageId();
		this.disclosureItem = source.getDisclosureItem();
	}

	public Long getCreated() {
		return this.created;
	}

	public UUID getMessageId() {
		return messageId;
	}

	public UUID getSourceMessageId() {
		return this.sourceMessageId;
	}

	public DisclosureItem getDisclosureItem() {
		return this.disclosureItem;
	}

	public String getMessageStr(boolean pretty) {
		String ret;
		JsonObject message = disclosureItem.getMessage();
		if (pretty) {
			ret = MessageJsonUtils.prettyPrint(message);
		} else {
			ret = message.toString();
		}
		return ret;
	}

	public MessageContent getMessageContent() {
		return getDisclosureItem().getMessageContent();
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		this.created = in.readLong();
		this.messageId = UUID.fromString(in.readUTF());
		boolean nullSourceMessageId = in.readBoolean();
		if (!nullSourceMessageId) {
			this.sourceMessageId = UUID.fromString(in.readUTF());
		}
		this.disclosureItem.readData(in);
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		out.writeLong(this.created);
		out.writeUTF(this.messageId.toString());
		boolean nullSourceMessageId = (null == this.sourceMessageId);
		out.writeBoolean(nullSourceMessageId);
		if (!nullSourceMessageId) {
			out.writeUTF(this.messageId.toString());
		}
		this.disclosureItem.writeData(out);
	}

}
