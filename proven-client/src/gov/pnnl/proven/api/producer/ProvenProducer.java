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

/**
 * 
 */
package gov.pnnl.proven.api.producer;

import java.io.Serializable;
import java.util.List;

import javax.jms.JMSException;

import gov.pnnl.proven.api.exception.NullExchangeInfoException;
import gov.pnnl.proven.api.exception.SendMessageException;
import gov.pnnl.proven.api.exchange.ExchangeInfo;
import gov.pnnl.proven.api.exchange.ExchangeType;
import gov.pnnl.proven.api.exchange.MqConsumer;
import gov.pnnl.proven.message.ProvenMessage;
import gov.pnnl.proven.message.exception.InvalidProvenMessageException;

import org.apache.activemq.command.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author raju332
 *
 */
public class ProvenProducer extends Producer{
	private final Logger log = LoggerFactory.getLogger(ProvenProducer.class);


	
	public ProvenProducer() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	ExchangeInfo exchangeInfo;
	private MessageInfo messageInfo;
	public void mqProducer(String servicesUri, String userName, String password) {
		
		exchangeInfo = new ExchangeInfo(ExchangeType.MQ, servicesUri, userName, password);
		
	}
	public void restProducer(String servicesUri,String userName, String password){
		exchangeInfo = new ExchangeInfo(ExchangeType.REST, servicesUri, userName, password);
	}
	
	public void setMessageInfo(String domain, String name, String source, List<String> Keywords){
		messageInfo = new MessageInfo(domain, name,source, Keywords);
	}

	public ProvenResponse sendMessage(String message, String requestId) throws InvalidProvenMessageException, SendMessageException, NullExchangeInfoException {

		ProvenMessage pm;
		if(messageInfo != null) {			
			pm = ProvenMessage.message(message).keywords(messageInfo.getKeywords()).domain(messageInfo.getDomain()).name(messageInfo.getName()).source(messageInfo.getSource()).build();
		}
		else
			pm = ProvenMessage.message(message).build();
		
		//System.out.println("PM Message: " + pm.getMessage() + " requestId: " + requestId);
		
		//log.info(pm.getMessage()+ " requestId: " + requestId);
		return sendMessage(pm, exchangeInfo, requestId);
		
		//MqConsumer consumer = new MqConsumer(exchangeInfo, requestId);
		//return consumer.receive();
		
		//ProvenResponse response = new ProvenResponse();
		//response.data
		
			
		
	}
	
//	public void sendHWMessage(String message, String requestId) throws InvalidProvenMessageException, SendMessageException, NullExchangeInfoException {
//		
//		ProvenMessage pm;
//		if(messageInfo != null) {			
//			pm = ProvenMessage.message(message).keywords(messageInfo.getKeywords()).domain(messageInfo.getDomain()).name(messageInfo.getName()).source(messageInfo.getSource()).buildHW();
//		}
//		else
//			pm = ProvenMessage.message(message).buildHW();
//		//System.out.println("PM Message: " + pm.getMessage() + " requestId: " + requestId);
//		//log.info(pm.getMessage()+ " requestId: " + requestId);
//		sendMessage(pm, exchangeInfo, requestId);
//	}

		

}
