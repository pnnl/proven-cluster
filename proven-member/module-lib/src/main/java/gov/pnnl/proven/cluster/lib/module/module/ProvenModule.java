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

import static gov.pnnl.proven.cluster.lib.module.component.ManagedComponent.ComponentLock.CREATED_LOCK;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;

import fish.payara.micro.PayaraMicroRuntime;
import gov.pnnl.proven.cluster.lib.module.component.CreationRequest;
import gov.pnnl.proven.cluster.lib.module.component.annotation.ActiveManagers;
import gov.pnnl.proven.cluster.lib.module.manager.ManagerComponent;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.Module;
import gov.pnnl.proven.cluster.lib.module.module.exception.ProducesInactiveManagerException;
import gov.pnnl.proven.cluster.lib.module.registry.ComponentRegistry;
import gov.pnnl.proven.cluster.lib.module.registry.EntryLocation;

/**
 * A Proven Module is the root component of a module's {@code ManagedComponent}
 * tree. That is, all managed components are either directly or indirectly child
 * components of a ProvenModule. At module startup this component is created,
 * other component creation follows. Specifically, a ProvenModule is responsible
 * for creating all active managers which in turn are responsible for creating
 * their managed components
 * 
 * @see ManagerComponent, ActiveManagers
 * 
 * @author d3j766
 *
 */
@ApplicationScoped
@Module
public abstract class ProvenModule extends ModuleComponent {

	@Inject
	Logger log;

	@Inject
	protected PayaraMicroRuntime pmr;
	
	@Inject
	ComponentRegistry cr;

	private static final String JNDI_MODULE_NAME = "java:module/ModuleName";

	// Set of active managers selected for this module
	Set<Class<? extends ManagerComponent>> activeManagers = new HashSet<>();

	// Module name
	private static String moduleName;
	
	// Time of creation
	private long moduleCreation;

	public ProvenModule() {
		super();
	}

	@PostConstruct
	public void init() {
		UUID memberId = UUID.fromString(pmr.getLocalDescriptor().getMemberUUID());
		UUID moduleId, managerId, creatorId;
		moduleId = managerId = creatorId = this.id;
		entryLocation(new EntryLocation(memberId, moduleId, managerId, creatorId));
		moduleCreation = new Date().getTime();
	}

	public <T extends ManagerComponent> T produceManager(Class<T> clazz) {

		T ret;

		try {
			acquireLockWait(ComponentLock.CREATED_LOCK);

			Optional<T> manager = getCreated(clazz);
			if (manager.isPresent()) {
				ret = manager.get();
			} else {
				ret = createManager(clazz);
			}
		} finally {
			releaseLock(CREATED_LOCK);
		}

		return ret;
	}

	public <T extends ManagerComponent> List<T> produceManagers(Class<T> clazz) {

		List<T> ret;
		Optional<List<T>> managers = getAllCreated(clazz);
		if (managers.isPresent()) {
			ret = managers.get();
		} else {
			T manager = produceManager(clazz);
			ret = new ArrayList<>();
			ret.add(manager);
		}
		return ret;
	}

	/**
	 * Used by ManagerFactory Producer methods to create a new manager
	 * components.
	 * 
	 * Null will be returned if the new component could not be created due its
	 * creation violating scalability rules.
	 * 
	 * @param clazz
	 *            type of manager component to create.
	 * 
	 * @return
	 */
	private <T extends ManagerComponent> T createManager(Class<T> clazz) {

		if (!activeManagers.contains(clazz)) {
			throw new ProducesInactiveManagerException(
					"Cannot produce manager: " + clazz.getSimpleName() + " It has been configured as inactive.");
		}

		// TODO
		// Check scalability count, return null if creation would violate it
		T manager = create(new CreationRequest<T>(clazz)).get();
		return manager;
	}

	public ModuleStatus retrieveModuleStatus() {
		return ModuleStatus.fromManagedStatus(getStatus());
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

	public long getModuleCreation() {
		return moduleCreation;
	}

	/**
	 * Create module's active managers.
	 */
	@SuppressWarnings({"rawtypes","unchecked"})

	@Override
	public boolean activate() {

		log.info("ProvenModule activation started");

		boolean ret = true;

		// Get list of managers to activate
		ActiveManagers toActivate = this.getClass().getAnnotation(ActiveManagers.class);

		// Remove any non-manager types and move to a HashSet
		Set<Class<? extends ManagerComponent>> active = new HashSet<>();
		if (null != toActivate) {
			for (Class<?> clazz : toActivate.managers()) {
				if (ManagerComponent.class.isAssignableFrom(clazz)) {
					active.add((Class<? extends ManagerComponent>) clazz);
				}
			}
		}

		// If manager's are provided then use, else include all
		if ((null != toActivate) && (!active.isEmpty())) {
			activeManagers = new HashSet<Class<? extends ManagerComponent>>(active);
		} else {
			activeManagers = new HashSet<Class<? extends ManagerComponent>>(ManagerFactory.getManagerTypes().keySet());
		}

		// Verify required managers are present
		Map<Class<? extends ManagerComponent>, Boolean> allManagers = ManagerFactory.getManagerTypes();
		for (Class<? extends ManagerComponent> k : allManagers.keySet()) {
			if (allManagers.get(k)) {	
				if (!activeManagers.contains(k)) {
					activeManagers.add(k);
				}
			}
		}

		// Create selected managers
		for (Class<? extends ManagerComponent> c : activeManagers) {
			if (ManagerComponent.class.isAssignableFrom(c)) {
				produceManager(c);
			}
		}

		log.info("ProvenModule activation completed.");

		return ret;
	}

}
