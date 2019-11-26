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
package gov.pnnl.proven.cluster.lib.module.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.slf4j.Logger;

import gov.pnnl.proven.cluster.lib.module.manager.ExchangeManager;
import gov.pnnl.proven.cluster.lib.module.manager.ManagerComponent;
import gov.pnnl.proven.cluster.lib.module.manager.PipelineManager;
import gov.pnnl.proven.cluster.lib.module.manager.RequestManager;
import gov.pnnl.proven.cluster.lib.module.manager.StreamManager;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.Manager;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.Managers;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.Module;

/**
 * Manages the production of {@code ManagerComponent}s.
 * 
 * @author d3j766
 *
 */
@ApplicationScoped
public class ManagerFactory {

	@Inject
	Logger log;

	@Inject
	@Module
	ProvenModule pm;

	/**
	 * Provide a static Map of manager component types. Key is type and value
	 * indicates if the manager is required at module startup.
	 */
	protected static Map<Class<?>, Boolean> managerTypes = new HashMap<>();
	static {
		managerTypes.put(ExchangeManager.class, false);
		managerTypes.put(PipelineManager.class, false);
		managerTypes.put(RequestManager.class, false);
		managerTypes.put(StreamManager.class, true);
	}

	public static Map<Class<?>, Boolean> getManagerTypes() {
		return managerTypes;
	}

	@PostConstruct
	public void init() {
	}

	public ManagerFactory() {
	}

	@Produces
	@Manager
	public StreamManager streamManagerProducer(InjectionPoint ip) {
		return retrieveManager(StreamManager.class);
	}

	@Produces
	@Manager
	public ExchangeManager exchangeManagerProducer(InjectionPoint ip) {
		return retrieveManager(ExchangeManager.class);
	}

	@Produces
	@Manager
	public PipelineManager pipelineManagerProducer(InjectionPoint ip) {
		return retrieveManager(PipelineManager.class);
	}

	@Produces
	@Manager
	public RequestManager requestManagerProducer(InjectionPoint ip) {
		return retrieveManager(RequestManager.class);
	}

	private <T extends ManagerComponent> T retrieveManager(Class<T> clazz) {
		return pm.getOrCreateManager(clazz);
	}

	@Produces
	@Managers
	public List<StreamManager> streamManagersProducer(InjectionPoint ip) {
		return retrieveManagers(StreamManager.class);
	}

	@Produces
	@Managers
	public List<ExchangeManager> exchangeManagersProducer(InjectionPoint ip) {
		return retrieveManagers(ExchangeManager.class);
	}

	@Produces
	@Managers
	public List<PipelineManager> pipelineManagersProducer(InjectionPoint ip) {
		return retrieveManagers(PipelineManager.class);
	}

	@Produces
	@Managers
	public List<RequestManager> requestManagersProducer(InjectionPoint ip) {
		return retrieveManagers(RequestManager.class);
	}

	private <T extends ManagerComponent> List<T> retrieveManagers(Class<T> clazz) {
		return pm.getOrCreateManagers(clazz);
	}
	
}
