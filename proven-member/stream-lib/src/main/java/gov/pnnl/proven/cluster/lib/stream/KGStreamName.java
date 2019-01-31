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
package gov.pnnl.proven.cluster.lib.stream;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.disclosure.exception.UnmanagedMessageContentStream;
import gov.pnnl.proven.cluster.lib.disclosure.message.MessageContent;

/**
 * Knowledge Graph streams store {@code ProvenMessage} instances in the memory
 * component of Proven's hybrid store. The combination of the message's
 * {@code DisclosureDomain} and the {@code MessageContent} identifies the
 * knowledge graph's stream for a ProvenMessage.
 * 
 * @author d3j766
 *
 */
public enum KGStreamName {

		Disclosed(StreamType.DISCLOSED_STREAM, MessageContent.Disclosure),

		Knowledge(
				StreamType.KNOWLEDGE_STREAM,
				MessageContent.Administrative,
				MessageContent.ContinuousQuery,
				MessageContent.Explicit,
				MessageContent.Implicit,
				MessageContent.Measurement,
				MessageContent.Query,
				MessageContent.Static,
				MessageContent.Structure),

		Response(StreamType.RESPONSE_STREAM, MessageContent.Response);

		private class StreamType {
			private static final String DISCLOSED_STREAM = "disclosed";
			private static final String KNOWLEDGE_STREAM = "knowledge";
			private static final String RESPONSE_STREAM = "response";
		}

		static Logger log = LoggerFactory.getLogger(KGStreamName.class);
		
		String streamType;
		List<MessageContent> messageContents;

		KGStreamName(String streamType, MessageContent... contents) {
			this.streamType = streamType;
			messageContents = Arrays.asList(contents);
		}

		/**
		 * Provides the name of the disclosure stream by domain. The domain name
		 * and stream type are used to name the disclosure stream.
		 * 
		 * @param dd
		 *            the disclosure domain
		 * 
		 * @return the name of the associated disclosure stream
		 * 
		 */
		public String getName(DisclosureDomain dd) {
			return buildStreamName(dd, streamType);
		}

		/**
		 * Provides the name of the disclosure stream associated with a
		 * {@code MessageContent}. It is assumed that there is a single stream
		 * associated with each message content type. A runtime exception is
		 * thrown if a stream cannot be found for the provided message content
		 * type. The domain name and stream type are used to name the disclosure
		 * stream.
		 * 
		 * @param mc
		 *            the MessageContent to search on
		 * @param dd
		 *            the disclosure domain
		 * 
		 * @return the name of the associated disclosure stream
		 * 
		 */
		public String getName(MessageContent mc, DisclosureDomain dd) {

			String ret;

			if (Disclosed.messageContents.contains(mc)) {
				ret = buildStreamName(dd, StreamType.DISCLOSED_STREAM);
			} else if (Knowledge.messageContents.contains(mc)) {
				ret = buildStreamName(dd, StreamType.KNOWLEDGE_STREAM);
			} else if (Response.messageContents.contains(mc)) {
				ret = buildStreamName(dd, StreamType.RESPONSE_STREAM);
			} else {
				throw new UnmanagedMessageContentStream();
			}

			return ret;
		}

		private String buildStreamName(DisclosureDomain dd, String sType) {
			String domainPart = dd.getReverseDomain();
			String streamPart = sType;
			return domainPart + "." + streamPart;
		}

}
