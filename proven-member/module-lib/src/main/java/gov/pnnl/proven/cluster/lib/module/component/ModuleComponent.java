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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.slf4j.Logger;
import com.hazelcast.core.HazelcastInstance;
import fish.payara.micro.PayaraMicro;
import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.member.MemberProperties;
import gov.pnnl.proven.cluster.lib.module.messenger.ScheduledMessenger;
import gov.pnnl.proven.cluster.lib.module.module.ProvenModule;

/**
 * The base component, with each performing activities to support operation
 * of a Proven module/application
 * 
 * @author d3j766
 *
 */
public abstract class ModuleComponent {

	@Inject
	Logger log;

	private static final String BASE_NAME = "component.proven.pnnl.gov";

	@Inject
	protected Instance<ScheduledMessenger> messengerProvider;
	
	@Inject
	protected MemberProperties mp;

	@Inject
	protected HazelcastInstance hzi;

	protected String clusterGroup;

	protected String host;

	protected String memberId;

	protected String containerName;

	protected UUID moduleId;

	protected String moduleName;

	protected UUID id;

	protected Set<ComponentGroup> group;

	protected String doId;

	protected Boolean isManaged;

	protected Map<UUID, ScheduledMessenger> messengers = new HashMap<>();

	public ModuleComponent() {
		containerName = PayaraMicro.getInstance().getInstanceName();
		id = UUID.randomUUID();
		group = new HashSet<>();
		moduleId = ProvenModule.retrieveModuleId();
		moduleName = ProvenModule.retrieveModuleName();
		if (getComponentType() == ComponentType.ProvenModule) {
			this.id = moduleId;
			this.group.add(ComponentGroup.Module);
		}
	}

	@PostConstruct
	public void moduleComponentInit() {
		doId = new DisclosureDomain(BASE_NAME).getReverseDomain() + "." + id + "_" + getComponentType().toString();
		clusterGroup = hzi.getConfig().getGroupConfig().getName();
		host = hzi.getCluster().getLocalMember().getAddress().getHost();
		memberId = hzi.getCluster().getLocalMember().getUuid();
	}

	@PreDestroy
	public void moduleComponentDestroy() {
		log.debug("ProvenComponent PreDestroy..." + this.getClass().getSimpleName());
	}

	public <T extends ScheduledMessenger> T getMessenger(Class<T> subtype, Annotation... qualifiers) {
		T mc = messengerProvider.select(subtype, qualifiers).get();
		addMessenger(mc);
		return mc;
	}
	
	protected void addMessenger(ScheduledMessenger sm) {
		messengers.put(sm.getId(), sm);
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

}
