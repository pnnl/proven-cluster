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
package gov.pnnl.proven.cluster.lib.module.exchange;

import static gov.pnnl.proven.cluster.lib.disclosure.MessageContent.Administrative;
import static gov.pnnl.proven.cluster.lib.disclosure.MessageContent.ModuleService;
import static gov.pnnl.proven.cluster.lib.disclosure.MessageContent.PipelineService;
import static gov.pnnl.proven.cluster.lib.disclosure.MessageContent.Query;
import static gov.pnnl.proven.cluster.lib.disclosure.MessageContent.Response;
//import static gov.pnnl.proven.cluster.lib.disclosure.MessageContent.getSchemaValues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.cluster.lib.disclosure.MessageContent;

/**
 * Represents the type of exchange components used to process
 * {@code DisclosureItem}s. Each exchange type is mapped to zero or more content
 * types. Where the content type for a disclosed item determines the type of
 * exchange processing component it will be routed to by a ModuleExchange.
 * 
 * @see MessageContent, ModuleExchange
 * 
 * @author d3j766
 *
 */
public enum ExchangeType {

	/**
	 * Entry point for disclosed items created outside the exchange environment,
	 * either from an external or internal source with respect to the Proven
	 * platform.
	 */
	DisclosureQueue(),

	/**
	 * Manages disclosed items for entry into the Hybrid store. All schema based
	 * message content is routed to a disclosure buffer.
	 */
	DisclosureBuffer(),

	/**
	 * Processes request content.
	 * 
	 * @see MessageContent
	 */
	RequestBuffer(Query, Administrative),

	/**
	 * Processes service content.
	 */
	ServiceBuffer(PipelineService, ModuleService),

	/**
	 * Processes response content for storage in Hybrid store.
	 */
	RespnseBuffer(Response);

	static Logger log = LoggerFactory.getLogger(ExchangeType.class);

	private List<MessageContent> contentTypes;

	ExchangeType(MessageContent... contentTypes) {
		this.contentTypes = Arrays.asList(contentTypes);
	}

	/**
	 * Represents the content types that will be routed to exchange components
	 * of this type by a ModuleExchange.
	 * 
	 * @see ModuleExchange
	 */
	public List<MessageContent> getContentTypes() {
		return contentTypes;
	}

	/**
	 * Represents the exchange types supporting the provided message content
	 * type.
	 * 
	 * @param mc
	 *            the message content type
	 * 
	 * @return ExchangeTypes supporting the provided message content type.
	 */
	public List<ExchangeType> supportedExchangeTypes(MessageContent mc) {

		List<ExchangeType> ret = new ArrayList<>();
		for (ExchangeType et : values()) {
			if (et.getContentTypes().contains(mc)) {
				ret.add(et);
			}
		}

		return ret;
	}

}
