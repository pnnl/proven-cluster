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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.slf4j.Logger;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.module.component.ComponentType;
import gov.pnnl.proven.cluster.lib.module.component.ModuleComponent;
import gov.pnnl.proven.cluster.lib.module.component.annotation.ActiveManagers;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Managed;
import gov.pnnl.proven.cluster.lib.module.manager.ExchangeManager;
import gov.pnnl.proven.cluster.lib.module.manager.ManagerComponent;
import gov.pnnl.proven.cluster.lib.module.manager.PipelineManager;
import gov.pnnl.proven.cluster.lib.module.manager.RequestManager;
import gov.pnnl.proven.cluster.lib.module.manager.StreamManager;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.Manager;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.Managers;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.Module;
import gov.pnnl.proven.cluster.lib.module.messenger.event.ShutdownEvent;
import gov.pnnl.proven.cluster.lib.module.messenger.event.StartupEvent;
import gov.pnnl.proven.cluster.lib.module.messenger.observer.ModuleObserver;

/**
 * Represents a Proven module and is responsible for activation/deactivation of
 * {@code ManagerComponent}s. Implementations represent the module application,
 * and is activated on platform startup.
 * 
 * @author d3j766
 *
 */
@ApplicationScoped
@Module
public abstract class ProvenModule extends ModuleComponent implements ModuleObserver {

	@Inject
	Logger log;

	private static final String JNDI_MODULE_NAME = "java:module/ModuleName";

	@Inject
	@Managed
	Instance<ManagerComponent> managerProvider;

	// Map of managers created for the module
	Map<UUID, ManagerComponent> managers = new HashMap<>();

	// Module identifier
	private static UUID moduleId;

	// Module name
	private static String moduleName;

	public ProvenModule() {
	}

	@PostConstruct
	public void init() {
	}

	public <T extends ManagerComponent> void addManager(Class<T> clazz) {
		T manager = managerProvider.select(clazz).get();
		managers.put(manager.getId(), manager);
	}

	public <T extends ManagerComponent> T getManager(Class<T> manager) {

		T ret = null;
		for (ManagerComponent mc : managers.values()) {

			// CDI bean proxy is subclass
			if (manager.isAssignableFrom(mc.getClass())) {
				ret = (T) mc;
				break;
			}
		}

		return ret;
	}

	public static UUID retrieveModuleId() {
		if (null == moduleId) {
			moduleId = UUID.randomUUID();
		}
		return moduleId;
	}

	public static String retrieveModuleName() {
		if (null == moduleName) {
			try {
				moduleName = (String) InitialContext.doLookup(JNDI_MODULE_NAME);
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		return moduleName;
	}

	@Override
	public ComponentType getComponentType() {
		return ComponentType.ProvenModule;
	}

	@Override
	public void observeModuleStartup(@Observes @Module StartupEvent moduleStartup) {

		log.debug("ProvenModule startup message observed");

		Set<Class<?>> managers;

		// Get list of managers to activate
		ActiveManagers toActivate = this.getClass().getAnnotation(ActiveManagers.class);
		if ((null != toActivate) && (toActivate.managers().length != 0)) {
			managers = new HashSet<Class<?>>(Arrays.asList(toActivate.managers()));
		} else {
			managers = new HashSet<Class<?>>(ManagerFactory.getManagerTypes().keySet());
		}

		// Verify required managers are present
		Map<Class<?>, Boolean> allManagers = ManagerFactory.getManagerTypes();
		for (Class<?> k : allManagers.keySet()) {
			if (allManagers.get(k)) {
				if (!managers.contains(k)) {
					managers.add(k);
				}
			}
		}

		// Start managers
		for (Class<?> c : managers) {
			if (ManagerComponent.class.isAssignableFrom(c)) {
				addManager((Class<ManagerComponent>) c);
			}
		}

		// TEST DESTROY
		// Ensure injected manager instances into a managed component is not
		// destroyed
//		ExchangeManager em = getManager(ExchangeManager.class);
//		managerProvider.destroy(em);
//		managers.remove(em);
//		StreamManager sm = getManager(StreamManager.class);
//		List<DisclosureDomain> dds = sm.getManagedDomains();

		log.info("ProvenModule startup completed.");
	}

	@Override
	public void observeModuleShutdown(@Observes @Module ShutdownEvent moduleShutdown) {

		log.debug("ProvenModule shutdown message observed");

		// Make sure pre-destroy callbacks are in place for cleanup. This should
		// be the same as what is called for an out of service status change.
		for (ManagerComponent mc : managers.values()) {
			managerProvider.destroy(mc);
		}

		// Log shutdown message
		log.info("ProvenModule shutdown completed.");
	}

}
