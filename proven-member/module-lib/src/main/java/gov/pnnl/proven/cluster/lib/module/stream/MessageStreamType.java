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
package gov.pnnl.proven.cluster.lib.module.stream;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.disclosure.DomainProvider;
import gov.pnnl.proven.cluster.lib.disclosure.message.MessageContent;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessage;

/**
 * Message streams store {@code ProvenMessage} instances, that have been
 * disclosed to the platform. These stream types represent the built-in streams
 * provided by Proven. Every disclosure domain will have its own set of message
 * streams as defined here, this include the Proven disclosure domain as well.
 * 
 * Each stream type supports one or more {@code MessageContent} types. A
 * specific content type is only supported by a single stream type. Attempts to
 * add message content to a stream where it is not supported will not be
 * allowed.
 * 
 * @see ProvenMessage, MessageContent, DisclosureDomain,
 *      {@link DomainProvider#getProvenDisclosureDomain()}
 * 
 * @author d3j766
 *
 */
public enum MessageStreamType {

	Disclosure(StreamLabel.DISCLOSURE_STREAM, MessageContent.Disclosure),

	Knowledge(
			StreamLabel.KNOWLEDGE_STREAM,
			MessageContent.Explicit,
			MessageContent.Implicit,
			MessageContent.Measurement,
			MessageContent.Static,
			MessageContent.Structure),

	Request(
			StreamLabel.REQUEST_STREAM,
			MessageContent.Administrative,
			MessageContent.ContinuousQuery,
			MessageContent.Query),

	Response(StreamLabel.RESPONSE_STREAM, MessageContent.Response);

	private class StreamLabel {
		private static final String DISCLOSURE_STREAM = "disclosed";
		private static final String KNOWLEDGE_STREAM = "knowledge";
		private static final String REQUEST_STREAM = "request";
		private static final String RESPONSE_STREAM = "response";
	}

	static Logger log = LoggerFactory.getLogger(MessageStreamType.class);

	private String streamLabel;
	private List<MessageContent> messageContents;

	MessageStreamType(String streamLabel, MessageContent... contents) {
		this.streamLabel = streamLabel;
		messageContents = Arrays.asList(contents);
	}

	/**
	 * Provides the name of the message stream using provided domain.
	 * 
	 * @param dd
	 *            the disclosure domain. If null, the default proven domain is
	 *            used.
	 * 
	 * @return the name of the associated disclosure stream
	 * 
	 */
	protected String getStreamName(DisclosureDomain dd) {
		return buildStreamName(dd, streamLabel);
	}

	/**
	 * Provides the {@code MessageContent} supported by the stream.
	 * 
	 * @return a list of supported MessageContent
	 * 
	 */
	public List<MessageContent> getMessageContents() {
		return messageContents;
	}
	
	public static MessageStreamType getType(MessageContent mcToCheckFor) {
		
		MessageStreamType ret = null;
		for (MessageStreamType mst : values()) {
			for (MessageContent mc : mst.messageContents ) {
				if (mc.equals(mcToCheckFor))
					ret = mst;
					break;
			}
		}
		return ret;
	}

	private String buildStreamName(DisclosureDomain dd, String sLabel) {
		String domainPart = dd.getReverseDomain();
		String streamPart = sLabel;
		return domainPart + "." + streamPart;
	}

}
