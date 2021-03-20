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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.disclosure.MessageContent;

/**
 * Message items represent the message payload for a DisclosureItem. Convenience
 * methods also included to provide the message item names and their
 * types.
 * 
 * @see DisclosureItem, MessageContent
 * 
 * @author d3j766
 *
 */
public interface MessageItem extends Validatable, IdentifiedDataSerializable {

	static Map<String, Class<? extends MessageItem>> messagesByName = MessageInitializer.messagesByName();
	static Map<Class<? extends MessageItem>, String> messagesByType = MessageInitializer.messagesByType();
	static Map<Class<? extends MessageItem>, MessageContent> messageContentByType = MessageInitializer.messageContentByType();
	

	static List<String> messageNames() {
		return new ArrayList<String>(messagesByName.keySet());
	}

	static List<Class<? extends MessageItem>> messageTypes() {
		return new ArrayList<Class<? extends MessageItem>>(messagesByType.keySet());
	}	

	static Class<? extends MessageItem> messageType(String name) {
		return messagesByName.get(name);
	}

	static String messageName(Class<? extends MessageItem> type) {
		return messagesByType.get(type);
	}
	
	static MessageContent messageContent(Class<? extends MessageItem> type) {
		return messageContentByType.get(type);
	}
	
	String messageName();

	MessageContent messageContent();
}

class MessageInitializer {

	static Map<String, Class<? extends MessageItem>> messagesByName() {

		Map<String, Class<? extends MessageItem>> ret = null;
		try {
			ret = new HashMap<String, Class<? extends MessageItem>>();
			List<Class<MessageItem>> cList = Validatable.getValidatables(true);
			for (Class<MessageItem> c : cList) {
				ret.put(c.newInstance().messageName(), c);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Failed to load message information", e);
		}
		return ret;
	}

	static Map<Class<? extends MessageItem>, String> messagesByType() {

		Map<Class<? extends MessageItem>, String> ret = null;
		try {
			ret = new HashMap<Class<? extends MessageItem>, String>();
			List<Class<MessageItem>> cList = Validatable.getValidatables(true);
			for (Class<MessageItem> c : cList) {
				ret.put(c, c.newInstance().messageName());
			}
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Failed to load message information", e);
		}
		return ret;
	}
	
	static Map<Class<? extends MessageItem>, MessageContent> messageContentByType() {

		Map<Class<? extends MessageItem>, MessageContent> ret = null;
		try {
			ret = new HashMap<Class<? extends MessageItem>, MessageContent>();
			List<Class<MessageItem>> cList = Validatable.getValidatables(true);
			for (Class<MessageItem> c : cList) {
				ret.put(c, c.newInstance().messageContent());
			}
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Failed to load message information", e);
		}
		return ret;
	}
		
}
