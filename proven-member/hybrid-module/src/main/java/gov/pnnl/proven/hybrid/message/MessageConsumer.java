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

package gov.pnnl.proven.hybrid.message;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.resource.AdministeredObjectDefinition;
import javax.resource.ConnectionFactoryDefinition;
import org.openrdf.model.Statement;

import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMeasurement;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessage;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessageOriginal;
import gov.pnnl.proven.cluster.lib.disclosure.message.DisclosureResponse;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenStatement;

import static gov.pnnl.proven.cluster.lib.disclosure.message.MessageTopic.TopicConfig.*;
import static gov.pnnl.proven.hybrid.util.Consts.*;

//@formatter:off
//
//@ConnectionFactoryDefinition(
//		name = JNDI_CONNECTION, 
//		interfaceName = "javax.jms.ConnectionFactory", 
////		interfaceName = "org.apache.activemq.ActiveMQConnectionFactory",		
//		resourceAdapter = JMS_MQ_ADAPTER,
//		properties = {"UserName=" + JMS_MQ_USER_NAME, 
//				      "Password=" + JMS_MQ_PASSWORD, 
//				      "ServerUrl=" + JMS_MQ_CONNECTION_URL })
////				      "brokerURL=" + JMS_MQ_CONNECTION_URL })
//
//@formatter:on
abstract class MessageConsumer {

	protected static String REQUEST_ID_MESSAGE_PROPERTY = "requestId";

//	@Resource(lookup = JNDI_RESPONSE)
//	Topic topic;
//
//	@Resource(lookup = JNDI_CONNECTION)
//	ConnectionFactory factory;

	abstract DisclosureResponse processMessage(ProvenMessageOriginal pm);

	protected void sendResponse(DisclosureResponse pr, Message message) {

		pr.setRequestId(getRequestId(message));

//		try (Connection conn = factory.createConnection()) {
//			Session sess = conn.createSession(true, Session.AUTO_ACKNOWLEDGE);
//			ObjectMessage om = sess.createObjectMessage();
//			om.setObject(pr);
//			sess.createProducer(topic).send(om);
//		} catch (JMSException ex) {
//			Logger.getLogger(MessageConsumer.class.getName()).log(Level.SEVERE, "Sending response failed", ex);
//		}

	}

	protected String getRequestId(Message message) {
		String ret = null;
		try {
			message.getStringProperty(REQUEST_ID_MESSAGE_PROPERTY);
		} catch (JMSException e) {
			ret = null;
		}
		return ret;
	}

	protected void testOutput(ProvenMessageOriginal pm) {

		if (null != pm.getStatements()) {
			int stmts = pm.getStatements().size();
			System.out.println("Nunmber of statements:" + stmts);
			Consumer<ProvenStatement> consumerNames = name -> {
				System.out.println(name);
			};
			pm.getStatements().forEach(consumerNames);
		}

		if (null != pm.getMeasurements()) {
			int measurements = pm.getMeasurements().size();
			System.out.println("Nunmber of measurements:" + measurements);
			Consumer<ProvenMeasurement> consumerMeasurements = measurement -> {
				System.out.println(measurement);
			};
			pm.getMeasurements().forEach(consumerMeasurements);
		}

	}
}
