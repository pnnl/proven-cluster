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
package gov.pnnl.proven.cluster.module.member;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
//import com.hazelcast.client.impl.protocol.task.BlockingMessageTask;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import gov.pnnl.proven.cluster.lib.disclosure.DomainProvider;
import gov.pnnl.proven.cluster.lib.module.stream.MessageStreamProxy;
import gov.pnnl.proven.cluster.lib.module.stream.MessageStreamType;
import gov.pnnl.proven.cluster.lib.module.stream.annotation.StreamConfig;
import static com.hazelcast.jet.Traversers.traverseArray;
import static com.hazelcast.jet.aggregate.AggregateOperations.counting;
//import static com.hazelcast.jet.function.DistributedFunctions.wholeItem;


public class TestPipeline implements Serializable {

	private static final long serialVersionUID = 1L;

	// TODO - provide logger classes on jet cluster classpath at startup so
	// common logging utility can be used between imdg and jet clusters. Should not have to
	// pass it for each job configuration.  
	// static Logger log = LoggerFactory.getLogger(TestPipeline.class);

	// Compile time message stream access
	@Inject
	@StreamConfig(domain = DomainProvider.PROVEN_DISCLOSURE_DOMAIN, streamType = MessageStreamType.Knowledge)
	MessageStreamProxy mspCompileTime;

	@PostConstruct
	public void init() {
		System.out.println("TestPipeline post construct");
	}

	public TestPipeline() {
	}

	public void submit(MessageStreamProxy mspRunTime) {

		System.out.println("START CLIENT:: " + Calendar.getInstance().getTime().toString());
	
		ClientConfig config = new ClientConfig();
		ClientNetworkConfig networkConfig = config.getNetworkConfig();
		networkConfig.addAddress("127.0.0.1:4701", "127.0.0.1:4702", "127.0.0.1:4703");
		GroupConfig gconfig = config.getGroupConfig();
		gconfig.setName("jet");
		gconfig.setPassword("jet-pass");

		// Create the specification of the computation pipeline. Note
		// it's a pure POJO: no instance of Jet needed to create it.
		Pipeline p = Pipeline.create();
		
//		p.drawFrom(Sources.<String>list("text")).flatMap(line -> traverseArray(line.toLowerCase().split("\\W+")))
//				.filter(word -> !word.isEmpty()).groupingKey(wholeItem()).aggregate(counting())
//				.drainTo(Sinks.map("counts"));

		p.drawFrom(Sources.<String>list("text")).flatMap(line -> traverseArray(line.toLowerCase().split("\\W+")))
		.filter(word -> !word.isEmpty()).groupingKey((s2) -> {return s2;}).aggregate(counting())
		.drainTo(Sinks.map("counts"));

		
		// Start Jet, populate the input list
		JetInstance jet = Jet.newJetClient(config);

		try {
			List<String> text = jet.getList("text");
			text.add("hello world hello hello world");
			text.add("world world hello world");

			// Perform the computation
			JobConfig jobConfig = new JobConfig();
			jobConfig.addClass(TestPipeline.class, MessageStreamProxy.class);

			// Perform the computation
			jet.newJob(p, jobConfig).join();

			// Check the results
			Map<String, Long> counts = jet.getMap("counts");
			System.out.println("Count of hello: " + counts.get("hello"));
			System.out.println("Count of world: " + counts.get("world"));
			System.out.println("MSP Stream: " + mspCompileTime.getStreamName());
			System.out.println("MSP Stream: " + mspRunTime.getStreamName());

		} finally {
			jet.shutdown();
		}
	}

}
