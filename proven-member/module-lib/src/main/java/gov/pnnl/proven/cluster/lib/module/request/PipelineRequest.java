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
package gov.pnnl.proven.cluster.lib.module.request;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.management.request.GetMemberSystemPropertiesRequest;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JetConfig;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.nio.Address;

import fish.payara.micro.PayaraMicro;
import fish.payara.micro.boot.PayaraMicroBoot;
import gov.pnnl.cluster.lib.pipeline.T3Service;
import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessageIDSFactory;
import gov.pnnl.proven.cluster.lib.module.component.ComponentStatus;
import gov.pnnl.proven.cluster.lib.module.component.event.StatusReport;
import gov.pnnl.proven.cluster.lib.module.manager.StreamManager;
import gov.pnnl.proven.cluster.lib.module.request.annotation.PipelineRequestProvider;
import gov.pnnl.proven.cluster.lib.module.stream.MessageStreamProxy;

/**
 * Represents a Hazelcast Jet Pipeline processing workflow. Pipeline invocations
 * result in a {@code PipelineJob}(s). A Pipeline maintains its associated jobs
 * and will start, suspend, cancel, or restart a job if redirected by its
 * {@code PipelineManager} to do so.
 * 
 * @author d3j766
 * 
 * @see PipelineJob, PipelineManager
 *
 */
public abstract class PipelineRequest extends RequestComponent {

	static Logger log = LoggerFactory.getLogger(PipelineRequest.class);

	public static final String PR_EXECUTOR_SERVICE = "concurrent/PipelineRequest";
	public static final int JET_INSTANCE_TEST_PORT = Integer
			.valueOf(System.getProperty("proven.jet.instance.test.port"));

	
	@Inject
	protected HazelcastInstance hzi;

	@Inject
	protected StreamManager sm;

	/**
	 * Represents a client connection from the pipeline's job to Proven's IMDG
	 * environment, used by source and sink stages of the job's pipeline to draw
	 * from and drain to respectively.
	 */
	protected ClientConfig clientConfig;

	/**
	 * Represents either an internal Jet server node instance or a Jet client
	 * that connects to an external Jet cluster. Default is a Jet Client. if
	 * {@link #isTest} is true an internal node will be used. The referenced
	 * {@code #computeCluster} processes the pipeline request.
	 * 
	 * @see #isTest
	 */
	protected JetInstance computeCluster;

	/**
	 * Represents
	 */

	/**
	 * Identifies type of pipeline.
	 * 
	 * @see PipelineRequestType
	 */
	private PipelineRequestType pipelineType;

	/*
	 * List of class resources, if any, that will be added to the pipeline's job
	 * configuration. By default, the {@code PipelineRequest} implementation
	 * class is added along with resources defined in the pipeline-lib library.
	 */
	private List<Class<?>> pipelineResources;

	/**
	 * Indicates if the pipeline should run in a test environment, meaning a
	 * single node internal Jet instance allowing for debugging. Default is
	 * false, indicating pipeline will be submitted to Proven's configured
	 * external Jet cluster.
	 */
	private boolean isTest;

	/**
	 * Contains the {@code PipelineJob}s for this {@code PipelineRequest}
	 */
	private Set<PipelineJob> jobs;

	/**
	 * Creates and returns a new {@code Pipeline} instance for the provided
	 * domain. If {@code #pipelineType} is {@code PipelineRequestType#Custom},
	 * domain value will not be provided in call. Meaning, implementation is
	 * responsible for domain selection and use for this case.
	 * {@code PipelineRequestType} is set in {@code PipelineRequestProvider}
	 * annotation, which identifies implementation classes of the
	 * {@code PipelineRequest}.
	 * 
	 * @param domain
	 *            {@DisclosureDomian} value.
	 * @return a new {@code Pipeline}
	 * 
	 * @see PipelineType, PipelineRequestProvider
	 * 
	 */
	public abstract Pipeline createPipeline(DisclosureDomain domain);


	@PostConstruct
	void init() {

		// Get root runtime directory
		URI rootDir = PayaraMicro.getInstance().getRootDir().toURI();
		log.debug("ROOT DIR :: " + rootDir.toString());
		
		// Extract provide metadata and add to class
		addPipelineRequestProviderMetadata();

		// Create a new client configuration allowing Jet compute nodes to
		// connect to Proven IMDG source/sink streams.
		clientConfig = createClientConfiguration();

		// Create Jet compute cluster reference
		// Must be completed after provide metadata extraction
		computeCluster = createComputeCluster();

	}

	@Inject
	public PipelineRequest() {
		super();
	}

	@PreDestroy
	void destroy() {
	}

	public PipelineRequestType getPipelineType() {
		return pipelineType;
	}

	public List<Class<?>> getPipelineResources() {
		return pipelineResources;
	}

	public boolean isTest() {
		return isTest;
	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}

	@Override
	public StatusReport getStatusReport() {
		return null;

	}

	@Override
	public ComponentStatus getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStatus(ComponentStatus status) {
		// TODO Auto-generated method stub

	}

	private void addPipelineRequestProviderMetadata() {

		Class<?> clazz = this.getClass();
		if (clazz.isAnnotationPresent(PipelineRequestProvider.class)) {
			PipelineRequestProvider prp = clazz.getAnnotation(PipelineRequestProvider.class);
			pipelineType = prp.pipelineType();
			pipelineResources = Arrays.asList(prp.resources());
			isTest = prp.isTest();
		}
	}

	private ClientConfig createClientConfiguration() {
		Address address = hzi.getCluster().getLocalMember().getAddress();
		String addressStr = address.getHost() + ":" + address.getPort();
		ClientConfig hzClientConfig = new ClientConfig();
		hzClientConfig.getNetworkConfig().addAddress(addressStr);
		hzClientConfig.setGroupConfig(hzi.getConfig().getGroupConfig());
		hzClientConfig.getSerializationConfig().addDataSerializableFactoryClass(ProvenMessageIDSFactory.FACTORY_ID,
				ProvenMessageIDSFactory.class);
		return hzClientConfig;
	}

	private JobConfig createJobConfig() {

		JobConfig ret = new JobConfig();

		return ret;

	}

	private JetInstance createComputeCluster() {

		JetInstance ret;

		if (!isTest) {

//			JetConfig config = new JetConfig();
//			config.getHazelcastConfig().getNetworkConfig().setPort(JET_INSTANCE_TEST_PORT);
//			config.getHazelcastConfig().getSerializationConfig()
//					.addDataSerializableFactoryClass(ProvenMessageIDSFactory.FACTORY_ID, ProvenMessageIDSFactory.class);
//			JobConfig jobConfig = new JobConfig();
//			jobConfig.addJar(new File("/home/d3j766/edev/payara-resources/blazegraph-jar-2.1.4.jar"));
//			jobConfig.addJar(new File("/home/d3j766/edev/payara-resources/pipeline-lib-0.1-all.jar"));
//			JetInstance jet = Jet.newJetInstance(config);

		} else {

//			// Jet client config
//			ClientConfig jetConfig = new ClientConfig();
//			jetConfig.getNetworkConfig().addAddress("127.0.0.1:4701", "127.0.0.1:4702", "127.0.0.1:4703");
//			jetConfig.getGroupConfig().setName("jet");
//
//			// Start Jet, populate the input list
//			JetInstance jet = Jet.newJetClient(jetConfig);
//			JobConfig jobConfig = new JobConfig();
//			// jobConfig.addClass(T3Pipeline.class, T3Service.class,
//			// MessageStreamProxy.class);
//			jobConfig.addClass(this.getClass(), T3Service.class, MessageStreamProxy.class);
			
		}

		return null;

	}

}
