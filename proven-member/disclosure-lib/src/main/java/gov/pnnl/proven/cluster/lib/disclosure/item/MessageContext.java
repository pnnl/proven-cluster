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
package gov.pnnl.proven.cluster.lib.disclosure.item;

import java.util.Objects;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.disclosure.DomainProvider;
import gov.pnnl.proven.cluster.lib.disclosure.MessageContent;

/**
 * Represents the context for a Proven message disclosure.
 * 
 * @author d3j766
 * 
 * @see DisclosureItem
 *
 */
public class MessageContext {

	// Required
	private MessageContent content;
	private Class<MessageItem> item;

	// Optional w/ default
	private DisclosureDomain domain;

	// Optional w/o default
	private String requestor;
	private String name;
	private String[] tags;

	// Necessary for HZ serialization
	public MessageContext() {
	}

	private MessageContext(Builder b) {
		Objects.requireNonNull(b.content);
		this.content = b.content;
		Objects.requireNonNull(b.item);
		this.item = b.item;
		this.domain = (null != b.domain) ? b.domain : new DisclosureDomain(DomainProvider.PROVEN_DISCLOSURE_DOMAIN);
		this.requestor = b.requestor;
		this.name = b.name;
		this.tags = (null != b.tags) ? (b.tags) : (new String[] {});
	}

	public MessageContent getContent() {
		return content;
	}

	public Class<MessageItem> getItem() {
		return item;
	}

	public DisclosureDomain getDomain() {
		return domain;
	}

	public String getRequestor() {
		return requestor;
	}

	public String getName() {
		return name;
	}

	public String[] getTags() {
		return tags;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	static final class Builder {

		private MessageContent content;
		private Class<MessageItem> item;
		private DisclosureDomain domain;
		private String requestor;
		private String name;
		private String[] tags;

		private Builder() {
		}

		public Builder withContent(MessageContent content) {
			this.content = content;
			return this;
		}

		public Builder withItem(Class<MessageItem> item) {
			this.item = item;
			return this;
		}

		public Builder withDomain(DisclosureDomain domain) {
			this.domain = domain;
			return this;
		}

		public Builder withRequestor(String requestor) {
			this.requestor = requestor;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withTags(String[] tags) {
			this.tags = tags;
			return this;
		}

		public MessageContext build() {
			return new MessageContext(this);
		}
	}

}
