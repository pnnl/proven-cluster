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
package gov.pnnl.proven.cluster.lib.module.component;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.slf4j.Logger;
import com.hazelcast.core.HazelcastInstance;
import fish.payara.micro.PayaraMicro;
import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.member.MemberProperties;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Managed;
import gov.pnnl.proven.cluster.lib.module.component.annotation.ManagedAnnotationLiteral;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Scalable;
import gov.pnnl.proven.cluster.lib.module.disclosure.DisclosureEntries;
import gov.pnnl.proven.cluster.lib.module.manager.ManagerComponent;
import gov.pnnl.proven.cluster.lib.module.messenger.ScheduledMessage;
import gov.pnnl.proven.cluster.lib.module.messenger.ScheduledMessenger;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.Messenger;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.MessengerAnnotationLiteral;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation;
import gov.pnnl.proven.cluster.lib.module.messenger.event.FailureEvent;
import gov.pnnl.proven.cluster.lib.module.messenger.event.MessageEvent;
import gov.pnnl.proven.cluster.lib.module.messenger.event.StatusEvent;
import gov.pnnl.proven.cluster.lib.module.module.ProvenModule;

/**
 * 
 * Base module component. Represents managed components within a Proven
 * application module, this includes the {@code ProvenModule} component itself.
 * Each component performs specific tasks to support a module's operation. These
 * components are registered with both their local and clustered registries. A
 * {@code ManagedComponent} may share its resources (compute and data) across
 * the cluster.
 * 
 * @author d3j766
 *
 * @see ProvenModule, RegistryComponent
 *
 */
@Managed
public abstract class ManagedComponent implements ManagedStatusOperation {

	@Inject
	Logger log;

	private static final String BASE_NAME = "component.proven.pnnl.gov";

	@Inject
	protected MemberProperties mp;

	@Inject
	protected HazelcastInstance hzi;

	@Inject
	@Managed
	protected Instance<ManagedComponent> componentProvider;
	
	@Inject
	@Any
	protected Instance<ScheduledMessenger> messengerProvider;

	protected ReentrantLock statusLock = new ReentrantLock();

	protected ManagedStatus status;

	protected UUID managerId;

	protected UUID creatorId;

	protected String clusterGroup;

	protected String host;

	protected String memberId;

	protected String containerName;

	protected UUID moduleId;

	protected String moduleName;

	protected UUID id;

	protected Set<ComponentGroup> group;

	protected String doId;

	/**
	 * Created components. The Map contains the component's ID as the key and
	 * object as value.
	 */
	protected Map<UUID, ManagedComponent> createdComponents;
	
	/**
	 * Messenger components. The Map contains the component's ID as the key and
	 * object as value.
	 */
	protected Map<UUID, ScheduledMessenger> messengerComponents;
	
	
	public ManagedComponent() {
		containerName = PayaraMicro.getInstance().getInstanceName();
		id = UUID.randomUUID();
		group = new HashSet<>();
		moduleId = ProvenModule.retrieveModuleId();
		moduleName = ProvenModule.retrieveModuleName();
		if (getComponentType() == ComponentType.ProvenModule) {
			this.id = moduleId;
			this.group.add(ComponentGroup.Module);
		}
		group.add(ComponentGroup.Managed);
		createdComponents = new HashMap<>();
		messengerComponents = new HashMap<>();
		status = ManagedStatus.Creating;
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void managedComponentInit() {

		doId = new DisclosureDomain(BASE_NAME).getReverseDomain() + "." + id + "_" + getComponentType().toString();
		clusterGroup = hzi.getConfig().getGroupConfig().getName();
		host = hzi.getCluster().getLocalMember().getAddress().getHost();
		memberId = hzi.getCluster().getLocalMember().getUuid();

		// All managed components must have a status messenger, except for the
		// messenger itself. Messengers will include their status report with
		// their owners.
		if ((!ScheduledMessenger.class.isAssignableFrom(this.getClass()))) {

			ScheduledMessenger messenger;

			if (ManagerComponent.class.isAssignableFrom(this.getClass())) {

				// create - managers messenger must be inactive at startup.
				messenger = createMessenger(new MessengerAnnotationLiteral() {
					
					private static final long serialVersionUID = 1L;
					
					@Override 
					public boolean activateOnStartup() {
						return false;
					};	
				});
			}

			else {
				// create - use default Messenger configuration
				messenger = createComponent(ScheduledMessenger.class, new MessengerAnnotationLiteral() {
					
					private static final long serialVersionUID = 1L;
				});
			}

			// register supplier
			messenger.register(() -> {
				return checkAndUpdate();
			});

		}
	}

	@PreDestroy
	public void managedComponentDestroy() {
		log.debug("ProvenComponent PreDestroy..." + this.getClass().getSimpleName());
	}

	public boolean acquireStatusLock() {
		return statusLock.tryLock();
	}

	public void releaseStatusLock() {
		statusLock.unlock();
	}

	public String getClusterGroup() {
		return clusterGroup;
	}

	public String getHost() {
		return host;
	}

	public String getMemberId() {
		return memberId;
	}

	public String getContainerName() {
		return containerName;
	}

	public UUID getModuleId() {
		return moduleId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public UUID getId() {
		return id;
	}

	public Set<ComponentGroup> getComponentGroups() {
		return group;
	}

	public abstract ComponentType getComponentType();

	public String getDoId() {
		return doId;
	}

	public boolean isScalable() {
		return this.getClass().isAnnotationPresent(Scalable.class);
	}

	public ScheduledMessenger createMessenger(Messenger messenger) {
		ScheduledMessenger sm = messengerProvider.select(messenger).get();
		addLineage(sm);
		messengerComponents.put(sm.getId(), sm);
		return sm;
	}
	
	public void startAllMessengers() {
		for (ScheduledMessenger messenger : messengerComponents.values()) {
			messenger.start();
		}
	}
	
	public void stopAllMessengers() {
		for (ScheduledMessenger messenger : messengerComponents.values()) {
			messenger.stop();
		}		
	}
	
	@Override
	public <T extends ManagedComponent> T createComponent(Class<T> subtype, Annotation... qualifiers) {
		// Get component
		T mc = componentProvider.select(subtype, qualifiers).get();
		addLineage(mc);
		createdComponents.put(mc.getId(), mc);
		return mc;
	}

	@Override
	public <T extends ManagedComponent> List<T> createComponents(Class<T> subtype, Annotation... qualifiers) {
		List<T> ret = new ArrayList<>();
		Iterator<T> mcItr = componentProvider.select(subtype, qualifiers).iterator();
		while (mcItr.hasNext()) {
			T mc = mcItr.next();
			addLineage(mc);
			createdComponents.put(mc.getId(), mc);
			ret.add(mc);
		}
		return ret;
	}

	private void addLineage(ManagedComponent mc) {

		// If the caller is a manager component, then use its identifier,
		// otherwise pass through the manager identifier for the caller to the
		// new component.
		if (this instanceof ManagerComponent) {
			mc.setManagerId(getId());
		} else {
			mc.setManagerId(getManagerId());
		}

		// Creator is always the caller's identifier
		mc.setCreatorId(getCreatorId());

	}

	/**
	 * All managed components must support their own activation.
	 */
	protected void activateCreated() {
		for (ManagedComponent mc : createdComponents.values()) {
			mc.activate();
		}
	}

	/**
	 * All managed components must support their own activation.
	 */
	protected void deactivateCreated() {
		for (ManagedComponent mc : createdComponents.values()) {
			mc.deactivate();
		}
	}

	public UUID getManagerId() {
		return managerId;
	}

	protected void setManagerId(UUID managerId) {
		this.managerId = managerId;
	}

	public UUID getCreatorId() {
		return creatorId;
	}

	protected void setCreatorId(UUID creatorId) {
		this.creatorId = creatorId;
	}

	public Set<UUID> getCreatedIds() {
		return createdComponents.keySet();
	}

	public ManagedStatus getStatus() {
		return status;
	}

	public void setStatus(ManagedStatus status) {

		if (statusLock.tryLock()) {
			try {
				this.status = status;
			} finally {
				statusLock.unlock();
			}
		}
	}

	@Override
	@StatusOperation
	public void activateCreated(UUID created) {
		log.debug("No activation created operation for :: " + this.getComponentType());
	}

	@Override
	@StatusOperation
	public void activate() {
		log.debug("No activation operation for :: " + this.getComponentType());
	}

	@Override
	@StatusOperation
	public void failedCreated(UUID created) {
		log.debug("No fail created operation for :: " + this.getComponentType());
	}

	@Override
	@StatusOperation
	public void failed() {
		log.debug("No fail operation for :: " + this.getComponentType());
	}

	@Override
	@StatusOperation
	public void retry() {
		log.debug("No retry operation for :: " + this.getComponentType());
	}

	@Override
	@StatusOperation
	public void deactivateCreated(UUID created) {
		log.debug("No deactivation created operation for :: " + this.getComponentType());
	}

	@Override
	@StatusOperation
	public void deactivate() {
		log.debug("No deactivate operation for :: " + this.getComponentType());
	}

	@Override
	@StatusOperation
	public void remove() {
		log.debug("No remove operation for :: " + this.getComponentType());
	}

	@Override
	@StatusOperation
	public void failure(FailureEvent event, boolean noRetry) {
		log.debug("No status operation for :: " + this.getComponentType());
	}

	@Override
	@StatusOperation
	public List<ScheduledMessage> checkAndUpdate() {
		log.debug("No status operation for :: " + this.getComponentType());
		List<ScheduledMessage> ret = new ArrayList<>();
		return ret;
	}

}
