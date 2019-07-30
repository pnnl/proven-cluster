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
package gov.pnnl.proven.cluster.module.hybrid.pipeline;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JetConfig;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.datamodel.Tuple2;
import com.hazelcast.jet.pipeline.ContextFactory;
import static com.hazelcast.jet.pipeline.JournalInitialPosition.START_FROM_OLDEST;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.nio.Address;

import gov.pnnl.cluster.lib.pipeline.T3Service;
import gov.pnnl.proven.cluster.lib.disclosure.DomainProvider;
import gov.pnnl.proven.cluster.lib.disclosure.message.KnowledgeMessage;
import gov.pnnl.proven.cluster.lib.disclosure.message.MessageContent;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessage;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessageIDSFactory;
import gov.pnnl.proven.cluster.lib.disclosure.message.ResponseMessage;
import gov.pnnl.proven.cluster.lib.module.stream.MessageStreamProxy;
import gov.pnnl.proven.cluster.lib.module.stream.MessageStreamType;
import gov.pnnl.proven.cluster.lib.module.stream.annotation.StreamConfig;
import gov.pnnl.proven.cluster.module.hybrid.util.Consts;

import static com.hazelcast.jet.Traversers.traverseArray;
import static com.hazelcast.jet.aggregate.AggregateOperations.counting;
import static com.hazelcast.jet.datamodel.Tuple2.tuple2;
import static java.util.Arrays.asList;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

public class T3Pipeline implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	protected HazelcastInstance hzi;

	@PostConstruct
	public void init() {
		System.out.println("T3Pipeline post construct");
	}

	public T3Pipeline() {
	}

	public void submit(MessageStreamProxy sourceMsp, MessageStreamProxy sinkMsp) {

		// System.setProperty("hazelcast.logging.type", "log4j");
		System.out.println("START CLIENT:: " + Calendar.getInstance().getTime().toString());

		// Jet client config
		ClientConfig jetConfig = new ClientConfig();
		jetConfig.getNetworkConfig().addAddress("127.0.0.1:4701", "127.0.0.1:4702", "127.0.0.1:4703");
		jetConfig.getGroupConfig().setName("jet");

		// Remote HZ config
		Address address = hzi.getCluster().getLocalMember().getAddress();
		String addressStr = address.getHost() + ":" + address.getPort();
		ClientConfig hzClientConfig = new ClientConfig();
		hzClientConfig.getNetworkConfig().addAddress(addressStr);
		hzClientConfig.setGroupConfig(hzi.getConfig().getGroupConfig());
		hzClientConfig.getSerializationConfig().addDataSerializableFactoryClass(ProvenMessageIDSFactory.FACTORY_ID,
				ProvenMessageIDSFactory.class);

		// T3 Storage Pipeline
		// For each Knowledge message, if non-measurement content, store in T3.
		// TODO - Give measurements their own stream and message content group
		Pipeline p = Pipeline.create();
		p.drawFrom(Sources.<String, KnowledgeMessage>remoteMapJournal(sourceMsp.getStreamName(), hzClientConfig,
				START_FROM_OLDEST)).withoutTimestamps()

				// STORE IN T3 - only if non-measurement
				.mapUsingContext(t3Service(), (t3s, km) -> {

					Map.Entry<String, ResponseMessage> ret = null;
					ProvenMessage sourceMessage = km.getValue();
					if (sourceMessage.getMessageContent() != MessageContent.Measurement) {
						ResponseMessage response = t3s.add(sourceMessage);
						ret = new AbstractMap.SimpleEntry<String, ResponseMessage>(response.getMessageKey(), response);
					}
					return ret;
				})

				// Drain
				.drainTo(Sinks.<String, ResponseMessage>remoteMap(sinkMsp.getStreamName(), hzClientConfig));

		// Start Jet, populate the input list
		JetInstance jet = Jet.newJetClient(jetConfig);
		JobConfig jobConfig = new JobConfig();
		jobConfig.addClass(T3Pipeline.class, T3Service.class, MessageStreamProxy.class);
		
// TESTING
// For local development/testing
//		JetConfig config = new JetConfig();
//		config.getHazelcastConfig().getNetworkConfig().setPort(4701);
//		config.getHazelcastConfig().getSerializationConfig().addDataSerializableFactoryClass(ProvenMessageIDSFactory.FACTORY_ID,
//				ProvenMessageIDSFactory.class);
//		jobConfig.addJar(new File("/home/d3j766/edev/payara-resources/blazegraph-jar-2.1.4.jar"));
//		jobConfig.addJar(new File("/home/d3j766/edev/payara-resources/pipeline-lib-0.1-all.jar"));
//		JetInstance jet = Jet.newJetInstance(config);
// TESTING

		// Run pipeline
		try {
			// Perform the computation
			// TESTING jet.newJob(p, jobConfig).join();
			jet.newJob(p, jobConfig).join();

		} finally {
			jet.shutdown();
		}
	}

	private static ContextFactory<T3Service> t3Service() {
		File dataDir = new File(Consts.PROVEN_DEFAULT_BASE_DIR);
		String serviceUrl = System.getProperty("proven.hybrid.t3.serviceUrl");
		return ContextFactory.withCreateFn(x -> T3Service.newT3Service(serviceUrl)).toNonCooperative().withLocalSharing();
	}
}

