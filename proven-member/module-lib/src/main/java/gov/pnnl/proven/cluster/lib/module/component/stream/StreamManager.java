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

package gov.pnnl.proven.cluster.lib.module.component.stream;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.disclosure.DomainProvider;
import gov.pnnl.proven.cluster.lib.module.component.ComponentManager;
import gov.pnnl.proven.cluster.lib.module.component.ModuleComponent;


/**
 * A component manager responsible for managing the platform's message streams.
 *  
 * @author d3j766
 * 
 * @see ComponentManager
 *
 */
@ApplicationScoped
public class StreamManager implements ComponentManager, ModuleComponent, MessageStreamProvider {

	static Logger log = LoggerFactory.getLogger(StreamManager.class);

	/**
	 * Set of managed proxy instances that provide access to the underlying
	 * message streams, organized by the domain of disclosure.
	 */
	private Map<DisclosureDomain, Set<MessageStreamProxy>> domainStreams;

	@PostConstruct
	public void initialize() {
		domainStreams = new HashMap<DisclosureDomain, Set<MessageStreamProxy>>();
		createStreams(DomainProvider.getProvenDisclosureDomain());
	}
	
	/**
	 * Force bean activation.
	 */
	public void ping() {
		log.debug("Activating StreamModule");
	}

	private void createStreams(DisclosureDomain dd) {

		synchronized (domainStreams) {

			if (!isManagedDomain(dd)) {
				Set<MessageStreamProxy> msps = new HashSet<MessageStreamProxy>();
				for (MessageStream ms : MessageStream.values()) {
					msps.add(createStream(dd, ms));
				}
				domainStreams.put(dd, msps);
			}
		}
	}

	private MessageStreamProxy createStream(DisclosureDomain dd, MessageStream ms) {	
		return new MessageStreamProxy(dd, ms);
	}

	private boolean isManagedDomain(DisclosureDomain dd) {
		return (domainStreams.containsKey(dd));
	}

	@Override
	public MessageStreamProxy getMessageStream(DisclosureDomain dd, MessageStream ms) {

		if (!isManagedDomain(dd)) {
			createStreams(dd);
		}

		Optional<MessageStreamProxy> msp = domainStreams.get(dd)
				.stream()
				.filter(obj -> obj.getStreamName().equals(ms.getStreamName(dd)))
				.findAny();
		
		return msp.get();
	}

}
