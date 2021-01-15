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
package gov.pnnl.proven.cluster.lib.module.service.pipeline;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JetConfig;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.nio.Address;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.disclosure.DisclosureIDSFactory;
import gov.pnnl.proven.cluster.lib.disclosure.DomainProvider;
import gov.pnnl.proven.cluster.lib.module.component.CreationRequest;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Managed;
import gov.pnnl.proven.cluster.lib.module.manager.StreamManager;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.Manager;
import gov.pnnl.proven.cluster.lib.module.messenger.event.JobEvent;
import gov.pnnl.proven.cluster.lib.module.service.ServiceComponent;
import gov.pnnl.proven.cluster.lib.module.service.annotation.PipelineServiceProvider;

/**
 * Represents a Hazelcast Jet Pipeline processing workflow. Pipeline invocations
 * result in a {@code PipelineJob}. A Pipeline maintains its associated jobs and
 * will start, suspend, cancel, or restart a job if redirected by its
 * {@code PipelineManager} to do so.
 * 
 * @author d3j766
 * 
 * @see PipelineJob, PipelineManager
 *
 */
public abstract class PipelineService extends ServiceComponent {

	static Logger log = LoggerFactory.getLogger(PipelineService.class);

	public static final String PS_EXECUTOR_SERVICE = "concurrent/PipelineService";

	@Inject
	protected HazelcastInstance hzi;

	@Inject
	@Manager
	protected StreamManager sm;

	// (Observer) - Missing cluster job
	public void job(@ObservesAsync @Managed JobEvent event) {
		// TODO
	}

	/**
	 * Represents a Hazelcast client connection used by the pipeline's job to
	 * connect to Proven's IMDG environment. Source and sink stages of the job's
	 * pipeline use this connection to draw from and drain to Proven's IMDG.
	 */
	protected ClientConfig imdgClientConfig;

	/**
	 * Represents either an internal Jet server node ({@link #internalConfig})
	 * or an external Jet client ({@link #externalConfig}). Default is a Jet
	 * Client. If {@link #isTest} is true an internal Jet server node will be
	 * used. The referenced {@code #computeCluster} is responsible for
	 * processing a pipeline service.
	 * 
	 * @see {@link #isTest},{@link #internalConfig}, {@link #externalConfig}
	 */
	protected JetInstance computeCluster;

	/**
	 * Represents the Jet {{@link #computeCluster} configuration for an
	 * internal/embedded Jet server node. This configuration is used when
	 * {@code #isTest} is set to true. This will be null if {@code #isTest} is
	 * false.
	 */
	protected JetConfig internalConfig;

	/**
	 * Represents the Jet {{@link #computeCluster} configuration for an external
	 * Jet Client connection to a remote Jet cluster. This configuration is used
	 * when {@code #isTest} is set to false. This will be null if
	 * {@code #isTest} is true.
	 */
	protected ClientConfig externalConfig;

	/**
	 * Represents job configuration for job(s) submitted for this
	 * PipelineService. This JobConfig provides general setup, and can be
	 * amended by a service implementation.
	 */
	protected JobConfig jobConfig = new JobConfig();

	/**
	 * Identifies type of pipeline.
	 * 
	 * @see PipelineServiceType
	 */
	private PipelineServiceType pipelineType;

	/*
	 * List of class resources, if any, that will be added to the pipeline's job
	 * configuration. By default, the {@code PipelineService} implementation
	 * class is added along with resources defined in the pipeline-lib library.
	 */
	private List<Class<?>> pipelineResources;

	/**
	 * Indicates if the pipeline should run in a test environment, meaning a
	 * single node internal Jet instance allowing for debugging. Default is
	 * false, indicating pipeline will be submitted to Proven's configured
	 * external Jet cluster.
	 */
	private boolean isTest = false;

	/**
	 * Creates and returns a new {@code Pipeline} instance for the provided
	 * domain. If {@code #pipelineType} is {@code PipelineServiceType#Custom},
	 * domain value will not be provided in call. Meaning, implementation is
	 * responsible for domain selection and use for this case.
	 * {@code PipelineServiceType} is set in {@code PipelineServiceProvider}
	 * annotation, which identifies implementation classes of the
	 * {@code PipelineService}.
	 * 
	 * @param domain
	 *            {@DisclosureDomian} value.
	 * @return a new {@code Pipeline}
	 * 
	 * @see PipelineType, PipelineServiceProvider
	 * 
	 */
	public abstract Pipeline createPipeline(DisclosureDomain domain);

	@PostConstruct
	void init() {

		// Extract provided metadata and add to class
		addPipelineServiceProviderMetadata();

		// Create a new client configuration allowing Jet compute nodes to
		// connect to Proven IMDG source/sink streams.
		imdgClientConfig = createImdgClientConfiguration();

		// Creates the Jet compute cluster reference
		// Must be completed after provide metadata extraction
		createComputeCluster();

		// Create Job(s)
		log.info("CREATING PIPELINE SERVICE JOBS...  " + getClass().getSimpleName());
		if (pipelineType == PipelineServiceType.Domain)
			createDomainJobs();
		if (pipelineType == PipelineServiceType.Proven)
			createProvenJob();
		if (pipelineType == PipelineServiceType.Custom)
			createCustomJob();

	}

	@PreDestroy
	void destroy() {
	}

	public PipelineService() {
		super();
	}

	public PipelineServiceType getPipelineType() {
		return pipelineType;
	}

	public List<Class<?>> getPipelineResources() {
		return pipelineResources;
	}

	public void addPipelineResource(Class<?> resource) {
		pipelineResources.add(resource);
	}

	public boolean isTest() {
		return isTest;
	}

	private void addPipelineServiceProviderMetadata() {

		Class<?> clazz = this.getClass();
		if (clazz.isAnnotationPresent(PipelineServiceProvider.class)) {
			PipelineServiceProvider prp = clazz.getAnnotation(PipelineServiceProvider.class);
			pipelineType = prp.pipelineType();
			pipelineResources = Arrays.asList(prp.resources());
			isTest = prp.isTest();
		}
	}

	private ClientConfig createImdgClientConfiguration() {
		Address address = hzi.getCluster().getLocalMember().getAddress();
		String addressStr = address.getHost() + ":" + address.getPort();
		ClientConfig hzClientConfig = new ClientConfig();
		hzClientConfig.getNetworkConfig().addAddress(addressStr);
		hzClientConfig.setGroupConfig(hzi.getConfig().getGroupConfig());
		hzClientConfig.getSerializationConfig().addDataSerializableFactoryClass(DisclosureIDSFactory.FACTORY_ID,
				DisclosureIDSFactory.class);
		return hzClientConfig;
	}

	protected JetInstance getComputeCluster() {
		return computeCluster;
	}

	private void createComputeCluster() {

		// Creates a single instance for one or more jobs per service
		if (null == computeCluster) {

			if (isTest) {

				// Create internal configuration for Jet server node instance
				internalConfig = new JetConfig();
				internalConfig.getHazelcastConfig().getNetworkConfig().setPort(mp.getJetInstanceTestPort());
				internalConfig.getHazelcastConfig().getSerializationConfig().addDataSerializableFactoryClass(
						DisclosureIDSFactory.FACTORY_ID, DisclosureIDSFactory.class);
				internalConfig.getHazelcastConfig().getGroupConfig().setName(mp.getJetGroupName());

				// Create job configuration for internal Jet. Retrieve any
				// pipeline jars from installation.
				File pipelineDeps = mp.getPipelineServiceLibsDir();
				for (String jarFile : pipelineDeps.list()) {
					if (jarFile.endsWith(".jar")) {
						jobConfig.addJar(new File(jarFile));
					}
				}

				// Create the new Jet server node instance
				computeCluster = Jet.newJetInstance(internalConfig);

				// Set external to null, not used
				externalConfig = null;

			} else {

				// Jet client config
				externalConfig = new ClientConfig();
				externalConfig.getNetworkConfig().setAddresses(mp.getHazelcastMembers());
				externalConfig.getGroupConfig().setName(mp.getJetGroupName());

				// Create Jet instance and job configuration for external jet
				// compute cluster cluster
				computeCluster = Jet.newJetClient(externalConfig);

				// Add class resources to job configuration. Assumption is other
				// dependencies not specific or referred by the pipeline
				// implementation have already been added to external Jet
				// compute cluster.
				jobConfig.addClass(this.getClass());
				Class<?>[] resourceArray = new Class<?>[pipelineResources.size()];
				jobConfig.addClass(pipelineResources.toArray(resourceArray));

				// Set internal to null, not used
				internalConfig = null;
			}
		}
	}

	private void createDomainJobs() {
		List<DisclosureDomain> managedDomains = sm.getManagedDomains();
		for (DisclosureDomain dd : managedDomains) {
			PipelineJob pj = create(new CreationRequest<PipelineJob>(PipelineJob.class)).get();
			pj.addService(this, dd);
		}
	}

	private void createProvenJob() {
		PipelineJob pj = create(new CreationRequest<PipelineJob>(PipelineJob.class)).get();
		pj.addService(this, DomainProvider.getProvenDisclosureDomain());
	}

	private void createCustomJob() {
		PipelineJob pj = create(new CreationRequest<PipelineJob>(PipelineJob.class)).get();
		pj.addService(this, null);
	}

	@Override
	public boolean activate() {

		log.info("PIPELINE SERVICE ACTIVATING...  " + getClass().getSimpleName());
		return true;
	}

}
