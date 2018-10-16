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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.api.exception.CreateMessageException;
import gov.pnnl.proven.api.exception.NullExchangeInfoException;
import gov.pnnl.proven.api.exception.SendMessageException;
import gov.pnnl.proven.api.exchange.ExchangeInfo;
import gov.pnnl.proven.message.ProvenMessage;


/**
 * Provides services for management of a provenance production session. A ProvenanceProducer can
 * create and send provenance messages to their registered provenance server.
 * 
 * @author raju332
 *
 */
public abstract class Producer implements ProvenStatus {
	 final Logger log = LoggerFactory.getLogger(Producer.class);
	/**
	 * Provides access to the provenance context as well as the current set of ProvenanceProducers.
	 * This instance is shared among all ProvenanceProducers.
	 */
	Registry registry = Registry.getInstance();

	/**
	 * Provides a ProvenanceProducer with its current registration information.
	 */
	private Registration registration;

	/**
	 * Provides information about this ProvenanceProducer. Required information for the producer is
	 * captured at construction.
	 */
	private ProducerInfo producerInfo;

	/**
	 * Provides access to current set of registered ProvenanceProducers.
	 * 
	 */
	private static class Registry {

		/**
		 * Single Registry instance. Shared by all ProvenanceProducers.
		 */
		static final Registry registryInstance = new Registry();

		/**
		 * Current set of registered ProvenanceProducers.
		 */
		private Set<Registration> registrations = Collections
				.synchronizedSet(new HashSet<Registration>());

		/**
		 * Must use getInstance(), to get the Registry instance
		 */
		private Registry() {
		}

		public static Registry getInstance() {
			return registryInstance;
		}

		/**
		 * Adds registration to the Registry.
		 * 
		 * @param registration
		 *            the registration to add.
		 * 
		 * @throws
		 */
		public void addRegistration(Registration registration) {
			registrations.add(registration);
		}

	}

	/**
	 * Stores a ProvenanceProducer's provenance context and provenance exchange session. Each
	 * ProvenanceProducer will have it's own registration.
	 */
	private class Registration {

		/**
		 * Registration identifier.
		 */
		private UUID registrationId;

		/**
		 * Represents a registered provenance exchange session.
		 */
		private ProvenSession provenSession;

		/**
		 * Registers the provenance context for a ProvenanceProducer. A provenance exchange session
		 * will be created as part of the registration. If for any reason the provenance context
		 * cannot be registered with this provenance producer an I
		 * 
		 * @throws IOException
		 *             if context file fails to load.
		 * 
		 */
		Registration() {

			// Set registration id (UUID) and add it to registry
			registrationId = UUID.randomUUID();

			// Initialize provenance context.
			//provenContext = ProvenContext.getInstance();

			// Initialize exchange session, this may fail if provenance server is unavailable at
			// producer registration time. Provenance messages can still be sent to the exchange,
			// but they will not be consumed until an exchange session is established with the
			// provenance server.
			provenSession = new ProvenSession();
		}

		public ProvenSession getProvenSession() {
			return provenSession;
		}


		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Registration)) {
				return false;
			}
			Registration other = (Registration) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (registrationId == null) {
				if (other.registrationId != null) {
					return false;
				}
			} else if (!registrationId.equals(other.registrationId)) {
				return false;
			}
			return true;
		}
		private Producer getOuterType() {
			return Producer.this;
		}


	}
	/**
	 * Constructs a new ProvenanceProducer using the provided application name and application
	 * version. A connection to the registered provenance server will be attempted in order to
	 * establish a provenance exchange session. If the connection fails provenance messages can
	 * still be produced/sent, however, they will not be consumed by a provenance server until the
	 * connection is established.
	 * 
	 * @param applicationName
	 *            name of the application this provenance producer is sending provenance messages on
	 *            behalf of.
	 * @param applicationVersion
	 *            version of the application this provenance producer is sending provenance messages
	 *            on behalf of.
	 */
	public Producer() {

		// Initialize ProducerInfo
		//this.producerInfo = new ProducerInfo(applicationName, applicationVersion);

		// Register and add to registry
		registration = new Registration();
		registry.addRegistration(registration);

	};


	/**
	 * Creates a new ProvenanceMessage identified by the provided message name. Message term values
	 * will be provided if the term's origin type is <i>API</i> (meaning it's value is provided by
	 * the API itself). Caller is responsible for setting values for remaining terms where term
	 * origin type is <i>USER</i>. Messages cannot be sent unless all term value's are provided,
	 * that is, no null values.
	 * 
	 * @param messageName
	 *            the name of the message, case insensitive.
	 * @return 
	 * 
	 * @return the new message
	 * 
	 * @throws CreateMessageException
	 *             if the message could not be created for the provided message name.
	 */
	public ProvenMessage createMessage(String messageName) throws CreateMessageException {
		return null;


	}

	
	/**
	 * Sets the provided exchange info to this producer's provenance exchange.
	 * 
	 * @param exchangeinfo
	 *            exchangeinfo used to co
	 *            
	 *            mmunicate with server
	 * 
	 */
		
	

	/**
	 * Sends the provided provenance message to this producer's provenance exchange. Before sending
	 * the message a check is made to ensure all message terms have been set, that is, they all have
	 * non null values.
	 * 
	 * @param message
	 *            the provenance message to send
	 * 
	 * @return true if the provenance message was sent
	 * @throws NullExchangeInfoException 
	 * 
	 * @throws NullTermValueException
	 *             if a message term has a null value
	 */
	public ProvenResponse sendMessage(ProvenMessage message, ExchangeInfo exchangeInfo, String requestId) throws SendMessageException, NullExchangeInfoException {

		/*if(registration.provenContext.getProvenInfo().isSaveMessagesInFile())
		{

			//Files.write(Paths.get("./messages.txt"), message.generateJsonLd().getBytes());
			PrintWriter out;
			try {
				out = new PrintWriter(new BufferedWriter(new FileWriter("messages.txt", true)));
				out.println(message.getMessage());
				out.close();	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

		}
		else*/
		return registration.provenSession.sendMessage(message, exchangeInfo, requestId);
		
	}
	
	
		
	@Override
	public Date createdDtm() {
		return registration.getProvenSession().getSessionInfo().getCreatedDtm();
	}

	@Override
	public Long messageFailCount() {
		return registration.getProvenSession().getSessionInfo().getErrorCount();
	}

	@Override
	public Long messageCount() {
		return registration.getProvenSession().getSessionInfo().getMessageCount();
	}
	


}
