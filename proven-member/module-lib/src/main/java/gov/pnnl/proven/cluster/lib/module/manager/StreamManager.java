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

package gov.pnnl.proven.cluster.lib.module.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.disclosure.DomainProvider;
import gov.pnnl.proven.cluster.lib.module.component.annotation.ManagedComponentType;
import gov.pnnl.proven.cluster.lib.module.stream.MessageStream;
import gov.pnnl.proven.cluster.lib.module.stream.MessageStreamProxy;
import gov.pnnl.proven.cluster.lib.module.stream.MessageStreamType;
import gov.pnnl.proven.cluster.lib.module.stream.annotation.StreamConfig;

/**
 * A component manager responsible for managing the platform's message streams.
 * 
 * @author d3j766
 * 
 * @see ComponentManager, MessageStream
 *
 */
@ApplicationScoped
public class StreamManager extends ManagerComponent implements ComponentManager {

	static Logger log = LoggerFactory.getLogger(StreamManager.class);

	@Inject
	@ManagedComponentType
	Provider<MessageStream> msProvider;

	/**
	 * Set of managed message stream instances that provide access to the
	 * underlying IMDG distributed data structures. Message streams are
	 * organized by disclosure domain.
	 */
	private Map<DisclosureDomain, Set<MessageStream>> domainStreams;

	@PostConstruct
	public void initialize() {
		// Initialize managed streams with Proven's default domain streams
		log.debug("Creating default Proven managed streams");
		domainStreams = new HashMap<DisclosureDomain, Set<MessageStream>>();
		createStreams(DomainProvider.getProvenDisclosureDomain());
	}

	@Inject
	public StreamManager() {
		super();
	}

	/**
	 * Force bean activation.
	 */
	public void ping() {
		log.debug("Activating StreamManager");
	}

	public MessageStreamProxy getMessageStreamProxy(DisclosureDomain domain, MessageStreamType streamType) {

		// Create all message streams for domain
		createStreams(domain);

		// Acquire the message stream for proxy instance
		Optional<MessageStream> ms = domainStreams.get(domain).stream()
				.filter(obj -> obj.getStreamName().equals(streamType.getStreamName(domain))).findAny();

		return new MessageStreamProxy(ms.get());

	}

	public Set<MessageStreamProxy> getMessageStreamProxyies(DisclosureDomain domain) {

		Set<MessageStreamProxy> msps = new HashSet<>();

		// Create all message streams for domain
		createStreams(domain);

		// Acquire the message streams for new proxy instances
		domainStreams.get(domain).stream().forEach((ms) -> {
			msps.add(new MessageStreamProxy(ms));
		});

		return msps;
	}

	public Set<MessageStream> getManagedStreams(DisclosureDomain dd) {

		Set<MessageStream> ret = new HashSet<>();

		if ((null != dd) && (domainStreams.containsKey(dd))) {
			ret = domainStreams.get(dd);
		}

		return ret;
	}

	/**
	 * Produces a new proxy instance for a {@code MessageStream} managed
	 * component.
	 * 
	 * @param ip
	 *            the injection point. This is required to be from a
	 *            {@code ComponentManager}.
	 * 
	 * @return a new {@code MessageStreamProxy}
	 */
	@Produces
	private MessageStreamProxy messageStreamProxyProducer(InjectionPoint ip) {

		StreamConfig sc = null;
		if (null != ip) {
			// Get Stream Configuration
			Annotated annotated = ip.getAnnotated();
			if (annotated.isAnnotationPresent(StreamConfig.class)) {
				sc = (StreamConfig) annotated.getAnnotation(StreamConfig.class);
			}
		}

		// Missing Stream configuration, use default provided by class.
		if (null == sc) {
			sc = MessageStreamProxy.class.getAnnotation(StreamConfig.class);
		}

		// Get disclosure domain and stream type
		String domainStr = sc.domain();
		DisclosureDomain domain = new DisclosureDomain(domainStr);
		MessageStreamType streamType = sc.streamType();

		return getMessageStreamProxy(domain, streamType);
	}

	/**
	 * Creates and adds the message stream components to the manager for the
	 * provided domain, only if the domain has not already been added. This
	 * method synchronizes on {@code #domainStreams}.
	 * 
	 * @param dd
	 *            the disclosure domain
	 */
	private void createStreams(DisclosureDomain dd) {

		synchronized (domainStreams) {
			if (!isManagedDomain(dd)) {
				Set<MessageStream> messageStreams = new HashSet<MessageStream>();
				for (MessageStreamType mst : MessageStreamType.values()) {
					MessageStream ms = msProvider.get();
					ms.configure(dd, mst);
					messageStreams.add(ms);
				}
				domainStreams.put(dd, messageStreams);
			}
		}
	}

	private boolean isManagedDomain(DisclosureDomain dd) {
		return (domainStreams.containsKey(dd));
	}
	
}
