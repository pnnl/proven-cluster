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

package gov.pnnl.proven.api.exchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ReplicatedMap;

import gov.pnnl.proven.api.producer.ProvenResponse;
import gov.pnnl.proven.api.producer.SessionInfo;
import gov.pnnl.proven.cluster.lib.disclosure.DisclosureRegister;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessage;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessageOriginal;

/**
 * Provides implementation for Hazelcast based Exchange.
 * 
 * @see Exchange
 * 
 * @see ExchangeServer
 * 
 * @author raju332
 *
 */
class HazelcastExchange implements Exchange {
	private final Logger log = LoggerFactory.getLogger(HazelcastExchange.class);

	HazelcastInstance client;
	ReplicatedMap<Object, Object> registry;
	DisclosureRegister disclosureRegister = new DisclosureRegister();

	HazelcastClusterInfo hazelcastClusterInfo;

	public HazelcastExchange(HazelcastClusterInfo hazelcastClusterInfo) {
		this.hazelcastClusterInfo = hazelcastClusterInfo;
		client = hazelcastClusterInfo.getClient();

	}

	@Override
	public ProvenResponse addProvenData(ExchangeInfo exchangeInfo, ProvenMessageOriginal message,
			SessionInfo sessionInfo, String requestId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addProvenData(ExchangeInfo exchangeInfo, List<ProvenMessage> messages) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ProvenResponse addProvenData(ExchangeInfo exchangeInfo, String message, SessionInfo sessionInfo,
			String measurementName, String instanceId) throws Exception {

		// Get Registry and registry items
		ReplicatedMap<Object, Object> registry = client
				.getReplicatedMap(disclosureRegister.getDisclosureRegistryName());

		if (registry.isEmpty()) {
			log.error("Disclosure Registry is Empty!");
		}
		Set<Entry<Object, Object>> registrySet = registry.entrySet();
		log.debug("Registry Size:" + registry.size());

		IQueue<Object> queueStream;
		List<Entry<Object, Object>> availableQueues = new ArrayList<Entry<Object, Object>>();
		for (Entry<Object, Object> registryItem : registrySet) {
			log.debug(registryItem.getKey() + ":");
			log.debug(" " + registryItem.getValue());
			// create a list with the available queues by checking the boolean value of the
			// item
			if ((boolean) registryItem.getValue()) {
				availableQueues.add(registryItem);

			}
		}

		if (availableQueues.isEmpty()) {
			log.error("No queues available to disclose messages");
		} else {
			Entry<Object, Object> availableQueue = availableQueues.get(hazelcastClusterInfo.roundRobinState);
			queueStream = client.getQueue((String) availableQueue.getKey());
			queueStream.put(message);
			if (hazelcastClusterInfo.roundRobinState != availableQueues.size() - 1)
				hazelcastClusterInfo.roundRobinState++;
			else
				hazelcastClusterInfo.roundRobinState = 0;
		}

		return null;
	}

}
