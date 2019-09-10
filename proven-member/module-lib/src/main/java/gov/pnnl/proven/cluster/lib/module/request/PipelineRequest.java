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

import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.pipeline.Pipeline;
import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.module.component.ComponentStatus;
import gov.pnnl.proven.cluster.lib.module.component.event.StatusReport;
import gov.pnnl.proven.cluster.lib.module.manager.StreamManager;

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

	@Inject
	protected HazelcastInstance hzi;

	@Inject
	protected StreamManager sm;

	/**
	 * Represents a client connection from the pipeline's job to Proven's
	 * IMDG environment, used by source and sink stages of the job's pipeline to draw
	 * from and drain to respectively.
	 */
	protected ClientConfig clientConfig;

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
		log.debug("Post construct for PipelineRequest");
	}

	@Inject
	public PipelineRequest() {
		super();
		log.debug("DefaultConstructer for PipelineRequest");
	}

	@PreDestroy
	void destroy() {
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

}
