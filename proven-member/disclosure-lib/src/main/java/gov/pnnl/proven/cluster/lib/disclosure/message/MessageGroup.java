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

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.disclosure.DomainProvider;

/**
 * 
 * Message groups represent a collection of {@code MessageContent}. A
 * {@code MessageContent} must be a member of one message group.   
 *  
 * @see MessageContent
 * 
 * @author d3j766
 *
 */
public enum MessageGroup {

	Disclosure(GroupLabel.DISCLOSURE_GROUP, MessageContent.Disclosure),

	Knowledge(
			GroupLabel.KNOWLEDGE_GROUP,
			MessageContent.Explicit,
			MessageContent.Implicit,
			MessageContent.Measurement,
			MessageContent.Static,
			MessageContent.Structure),

	Request(
			GroupLabel.REQUEST_GROUP,
			MessageContent.Administrative,
			MessageContent.ContinuousQuery,
			MessageContent.Query),

	Response(GroupLabel.RESPONSE_GROUP, MessageContent.Response);

	private class GroupLabel {
		private static final String DISCLOSURE_GROUP = "disclosed";
		private static final String KNOWLEDGE_GROUP = "knowledge";
		private static final String REQUEST_GROUP = "request";
		private static final String RESPONSE_GROUP = "response";
	}

	static Logger log = LoggerFactory.getLogger(MessageGroup.class);

	private String groupLabel;
	private List<MessageContent> messageContents;

	MessageGroup(String groupLabel, MessageContent... contents) {
		this.groupLabel = groupLabel;
		messageContents = Arrays.asList(contents);
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

	public static MessageGroup getType(MessageContent mcToCheckFor) {

		MessageGroup ret = null;
		for (MessageGroup mst : values()) {
			for (MessageContent mc : mst.messageContents) {
				if (mc.equals(mcToCheckFor))
					ret = mst;
				break;
			}
		}
		return ret;
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
	public String getStreamName(DisclosureDomain dd) {
		return buildStreamName(dd, groupLabel);
	}

	private String buildStreamName(DisclosureDomain dd, String sLabel) {
		String domainPart = dd.getReverseDomain();
		String streamPart = sLabel;
		return domainPart + "." + streamPart;
	}

}
