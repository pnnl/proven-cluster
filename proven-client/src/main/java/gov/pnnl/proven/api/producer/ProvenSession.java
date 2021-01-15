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

package gov.pnnl.proven.api.producer;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.api.exchange.ExchangeInfo;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessageOriginal;
import gov.pnnl.proven.api.exception.NullExchangeInfoException;
import gov.pnnl.proven.api.exchange.Exchange;


/**
 *
 * Represents a provenance exchange session. Interacts directly with the exchange to add provenance
 * messages.
 * 
 */
class ProvenSession {
	private final Logger log = LoggerFactory.getLogger(ProvenSession.class);

	//private ExchangeInfo exchangeInfo;
	private SessionInfo sessionInfo;
	private List<ExchangeInfo> exchanges = new ArrayList<ExchangeInfo>();

	ProvenSession() {

		//this.exchangeInfo = list;
		this.sessionInfo = new SessionInfo();
		//this.provenInfo = provenanceContext.getProvenInfo();

		//exchanges = provenanceContext.getExchanges();
		
		
		// auto connect to server for exchange information
		/*exchanges = serverInfo.getExchangeServer().getExchanges(serverInfo);

		if (exchanges.isEmpty()) {
			log.error("Provenance session initialization failed.");
		}*/

	}

	SessionInfo getSessionInfo() {
		return sessionInfo;
	}


	
	/*List<ExchangeInfo> getExchangeInfo() {
		return exchanges;
	}*/

	ProvenResponse sendMessage(ProvenMessageOriginal message, ExchangeInfo exchangeInfo, String requestId) throws NullExchangeInfoException {
	
		Exchange exchange;
		try {
			exchange = exchangeInfo.getExchange();
		} catch (Exception e) {
			log.error("ExchangeInfo not set");
		throw new NullExchangeInfoException();
		}

		return exchange.addProvenData(exchangeInfo, message, sessionInfo, requestId);
		
		//use Round Robin for load balancing
		//Round robin Implementation
		/*if (message.setNodeIdentifiers()) {
			//use Round Robin for load balancing
			ExchangeInfo exchangeInfo = exchanges.get(roundRobinState);
			Exchange exchange = exchangeInfo.getExchange();
			exchange.addProvenance(exchangeInfo, message, sessionInfo, provenanceInfo);
			if (roundRobinState != exchanges.size()-1)
				roundRobinState++;
			else
				roundRobinState = 0;
			*/
			/*for (ExchangeInfo exchangeInfo : exchanges) {
				Exchange exchange = exchangeInfo.getExchange();
				exchange.addProvenance(exchangeInfo, message, sessionInfo, provenanceInfo);
			}*/


			/*if (exchange.addProvenance(exchangeInfo, message)) {
					ret = true;
					sessionInfo.setMessageCount(sessionInfo.getMessageCount() + 1L);
					break;
				} else {
					sessionInfo.setErrorCount(sessionInfo.getErrorCount() + 1L);
				}*/



		//}

	}
	
	ProvenResponse sendMessage(String message, ExchangeInfo exchangeInfo, String measurementName, String instanceId) throws Exception {
		
		Exchange exchange;
		try {
			exchange = exchangeInfo.getExchange();
		} catch (Exception e) {
			log.error("Problem sending message. Exchange not set");
		throw new NullExchangeInfoException();
		}

		return exchange.addProvenData(exchangeInfo, message, sessionInfo, measurementName, instanceId);
		
		//use Round Robin for load balancing
		//Round robin Implementation
		/*if (message.setNodeIdentifiers()) {
			//use Round Robin for load balancing
			ExchangeInfo exchangeInfo = exchanges.get(roundRobinState);
			Exchange exchange = exchangeInfo.getExchange();
			exchange.addProvenance(exchangeInfo, message, sessionInfo, provenanceInfo);
			if (roundRobinState != exchanges.size()-1)
				roundRobinState++;
			else
				roundRobinState = 0;
			*/
			/*for (ExchangeInfo exchangeInfo : exchanges) {
				Exchange exchange = exchangeInfo.getExchange();
				exchange.addProvenance(exchangeInfo, message, sessionInfo, provenanceInfo);
			}*/


			/*if (exchange.addProvenance(exchangeInfo, message)) {
					ret = true;
					sessionInfo.setMessageCount(sessionInfo.getMessageCount() + 1L);
					break;
				} else {
					sessionInfo.setErrorCount(sessionInfo.getErrorCount() + 1L);
				}*/



		//}

	}
	
}
