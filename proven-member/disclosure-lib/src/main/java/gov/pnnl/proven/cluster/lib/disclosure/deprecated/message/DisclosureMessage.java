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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureIDSFactory;
import gov.pnnl.proven.cluster.lib.disclosure.MessageContent;
import gov.pnnl.proven.cluster.lib.disclosure.MessageContentGroup;
import gov.pnnl.proven.cluster.lib.disclosure.item.DisclosureItem;

/**
 * Disclosure messages represent the original input message disclosed to the
 * platform. It is responsible for transforming its message content into either
 * a {@code KnowledgeMessage} or {@code RequestMessage} for downstream message
 * processing.
 * 
 * @author d3j766
 *
 */
public class DisclosureMessage extends ProvenMessage implements IdentifiedDataSerializable, Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(DisclosureMessage.class);

	/**
	 * True, if the disclosed content is in the Request message group.
	 */
	protected boolean isRequest;

	/**
	 * True, if the disclosed content is in the Knowledge message group.
	 */
	protected boolean isKnowledge;

	/**
	 * True, if the disclosed content is a {@code MessageContent#Measurement}.
	 */
	protected boolean isMeasurements;

	public DisclosureMessage() {
	}

	public DisclosureMessage(DisclosureItem di) {
		super(di);
		MessageContent disclosedContent = di.getContext().getContent();
		MessageContentGroup disclosedContentGroup = MessageContentGroup.getType(di.getContext().getContent());
		isRequest = MessageContentGroup.Request == disclosedContentGroup;
		isKnowledge = MessageContentGroup.Knowledge == disclosedContentGroup;
		isMeasurements = MessageContent.Measurement == disclosedContent;
	}
	
	public boolean isRequest() {
		return isRequest;
	}

	public boolean isKnowledge() {
		return isKnowledge;
	}

	public boolean isMeasurements() {
		return isMeasurements;
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		super.readData(in);
		this.isRequest = in.readBoolean();
		this.isKnowledge = in.readBoolean();
		this.isMeasurements = in.readBoolean();
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		super.writeData(out);
		out.writeBoolean(this.isRequest);
		out.writeBoolean(this.isKnowledge);
		out.writeBoolean(this.isMeasurements);
	}

	@Override
	public int getFactoryId() {
		return DisclosureIDSFactory.FACTORY_ID;
	}

	@Override
	public int getId() {
		//return DisclosureIDSFactory.DISCLOSURE_MESSAGE_TYPE;
		return 0;
	}

}
