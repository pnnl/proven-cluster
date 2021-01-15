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

import java.lang.reflect.Method;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;

import gov.pnnl.proven.cluster.lib.module.component.CreationRequest;
import gov.pnnl.proven.cluster.lib.module.service.annotation.PipelineServiceProviderAnnotationLiteral;
import gov.pnnl.proven.cluster.lib.module.service.module.ModuleService;
import gov.pnnl.proven.cluster.lib.module.service.pipeline.PipelineService;

/**
 * A component manager responsible for managing a set of {@code PipelineRequest}
 * components that support domain based stream processing.
 * 
 * @author d3j766
 * 
 * @see ComponentManager, PipelineRequest
 *
 */
public class ServiceManager extends ManagerComponent {

	@Inject
	Logger log;
	
	public ServiceManager() {
		super();
	}
	
	@PostConstruct
	public void initialize() {

		// Create all managed pipeline requests
//		CreationRequest<PipelineRequest> cr = new CreationRequest<>(PipelineRequest.class);
//		cr.getQualifiers().add(new PipelineRequestProviderAnnotationLiteral() {});
//		List<PipelineRequest> prs = create(cr).getCreated();
//		log.info(prs.size() + " PipelineRequests created by PipelineManager: " + this.entryIdentifier());
	}
	

	@Override
	@SuppressWarnings("serial")
	public boolean activate() {
		// Create all pre-existing pipeline and module services
		CreationRequest<PipelineService> cr = new CreationRequest<>(PipelineService.class);
		cr.getQualifiers().add(new PipelineServiceProviderAnnotationLiteral() {});
		createAsync(cr);	
		return true;
	}

	
	/**
	 * TODO
	 * @param mr
	 */
	public <T> void registerService(Class<T> mr) {
	
		System.out.println(mr.getName());
	
		System.out.println(mr.isAssignableFrom(ModuleService.class));
	
		System.out.println(ModuleService.class.isAssignableFrom(mr));
	
		Method[] methods = mr.getMethods();
	
		System.out.println(methods.toString());
	
	}


}
