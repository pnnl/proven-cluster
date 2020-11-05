/*******************************************************************************
 * CopFyright (c) 2017, Battelle Memorial Institute All rights reserved.
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
package gov.pnnl.proven.cluster.lib.module.registry;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISet;
import com.hazelcast.cp.lock.FencedLock;
import gov.pnnl.proven.cluster.lib.member.MemberProperties;
import gov.pnnl.proven.cluster.lib.module.component.ManagedStatus;
import gov.pnnl.proven.cluster.lib.module.exchange.Exchange;
import gov.pnnl.proven.cluster.lib.module.exchange.ExchangeRequest;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.Module;
import gov.pnnl.proven.cluster.lib.module.module.ModuleEntry;
import gov.pnnl.proven.cluster.lib.module.module.ModuleStatus;
import gov.pnnl.proven.cluster.lib.module.module.ProvenModule;

/**
 * A registry of component entries at the module, member, and cluster levels.
 * 
 * @author d3j766
 *
 * @see ComponentEntry, ModuleEntry, EntryLocation
 *
 */
@ApplicationScoped
public class ComponentRegistry implements Exchange {

	@Inject
	Logger log;

	/**
	 * Member properties
	 */
	MemberProperties props = MemberProperties.getInstance();

	@Inject
	HazelcastInstance hzi;

	@Inject
	@Module
	ProvenModule pm;

	/**
	 * IMDG module exchange queue
	 */
	String moduleExchangeQueueName;

	/**
	 * IMDG member exchange queue
	 */
	String memberExchangeQueueName;

	/**
	 * IMDG cluster registry name
	 */
	String clusterRegistryName;

	/**
	 * Cluster listener key. Used to add/remove cluster listener if lead module
	 * status is removed.
	 * 
	 * @see this{@link #leadModule()}
	 */
	UUID clusterListenerKey = null;

	/**
	 * Used by the scheduled {@link #localCleanup()} method to ensure only one
	 * local cleanup task is running at any given time.
	 */
	AtomicBoolean localClean = new AtomicBoolean(false);

	/**
	 * (Local) Module Components
	 * 
	 * Local storage of all component entries for this Module.
	 * 
	 */
	TreeSet<ComponentEntry> moduleComponents = new TreeSet<ComponentEntry>();

	/**
	 * (Local) Module Exchange
	 * 
	 * An unmodifiable view of {@link #moduleComponents} used to support
	 * exchange requests within this module.
	 * 
	 */
	NavigableSet<ComponentEntry> moduleExchange = Collections.unmodifiableNavigableSet(moduleComponents);

	/**
	 * (IMDG) Module Entries
	 * 
	 * Set of member modules. A ComponentRegistry creates and adds its
	 * ModuleEntry to this Set at startup.
	 */
	@Deprecated
	ISet<ModuleEntry> modules;

	/**
	 * A Hazelcast FencedLock used to support concurrent (read and write) access
	 * to {@link #modules}
	 * 
	 */
	@Deprecated
	FencedLock modulesFencedLock;

	/**
	 * This module's exchange queue. Contains pending exchange requests.
	 * Requests may be added to the queue by this module or other modules within
	 * the same member. This module reads and processes these requests.
	 */
	@Deprecated
	IQueue<ExchangeRequest> localModuleExchangeQueue;

	/**
	 * This members exchange queue. Contains pending member exchange requests.
	 * Requests may be added to the queue by modules outside this member. Any
	 * module within this member may read and process these requests.
	 */
	@Deprecated
	IQueue<ExchangeRequest> localMemberExchangeQueue;

	/**
	 * (IMDG) Cluster components
	 * 
	 * All cluster component entries.
	 * 
	 */
	IMap<EntryIdentifier, ComponentEntry> clusterComponents;

	/**
	 * Default constructor
	 */
	public ComponentRegistry() {
		System.out.println("Inside ComponentRegistry constructor");
	}

	@PostConstruct
	public void initialize() {

		log.debug("Inside MemberComponentRegistry PostConstruct");

		/**
		 * Initialize IMDG objects
		 */
		modules = hzi.getSet(props.getMemberModuleRegistryName());
		// hzi.getCPSubsystem().getLock("myLock");
		clusterComponents = hzi.getMap(props.getClusterComponentRegistryName());
	}

	/**
	 * The lead module is the oldest module in a member, based on creation time,
	 * with non-shutdown module status.
	 * 
	 * @return true if this is the lead module, false otherwise.
	 */
	public boolean leadModule() {

		boolean leader = false;

		Iterator<ModuleEntry> meItr = modules.iterator();

		while (meItr.hasNext()) {
			ModuleEntry me = meItr.next();
			if (me.getModuleStatus() != ModuleStatus.Shutdown) {
				if (me.getModuleId().equals(pm.getModuleId())) {
					leader = true;
				}
				break;
			}
		}

		if (leader) {
			if (null == clusterListenerKey) {
				clusterListenerKey = UUID
						.fromString(clusterComponents.addEntryListener(new ClusterRegistryListener(), true));
			}
		} else {
			if (null != clusterListenerKey) {
				clusterComponents.removeEntryListener(clusterListenerKey.toString());
			}
		}

		return leader;
	}

	/**
	 * The lead member is determined by Hazelcast's {@link Cluster#getMembers()}
	 * call
	 * 
	 * @return true if this module is the lead member, false otherwise
	 */
	public boolean leadMember() {

		return hzi.getCluster().getMembers().iterator().next().getUuid().equals(pm.getMemberId().toString());
	}

	/**
	 * The lead cluster module is a lead module that's contained by the lead
	 * member.
	 * 
	 * @return true if this module is the lead cluster module, false otherwise.
	 * 
	 * @see this{@link #leadMember()}
	 * 
	 */
	public boolean leadClusterModule() {

		return (leadMember() && leadModule());
	}

	/**
	 * Records reported ComponentEntry event made by a ManagedComponent.
	 * Following are the steps performed to record entries at the module,
	 * member, and cluster levels:
	 * 
	 * <ol>
	 * <li>Add time of recording, used for overdue reporting calculations</li>
	 * <li>Adds entry to local module components</li>
	 * <li>Adds entry to IMDG cluster components (IMap storage)</li>
	 * </ol>
	 * 
	 * @param entry
	 *            reported Component entry to record
	 */
	public void record(ComponentEntry entry) {

		// 1
		entry.setRecorded(new Date().getTime());

		// 2 (Local)
		recordModuleComponent(entry);

		// 4 (IMDG)
		recordClusterComponent(entry);
	}

	/**
	 * A scheduled cleanup task.
	 * 
	 * Manages local module component entries that are overdue in reporting
	 * their status.
	 * 
	 * If it can be determined that the component no longer exists, its status
	 * will be updated to {@value ManagedStatus#Destroyed}, otherwise its status
	 * will be set to {@value ManagedStatus#Unknown}.
	 * 
	 * Updated entries will be recorded/removed from the cluster registry for
	 * Unknown and Destroyed, respectively.
	 * 
	 */
	@Schedule(minute = "*/5", hour = "*", persistent = false)
	public void localCleanup() {

		// Do work only if it's not already running
		if (!localClean.compareAndSet(false, true)) {
			return;
		}
		try {

			// TODO Cleanup work here
			log.debug("LOCAL CLEANUP TASK INVOKED");

		} finally {
			localClean.set(false);
		}

	}

	private void recordModuleComponent(ComponentEntry entry) {

		/**
		 * Create new ModuleEntry, if necessary. And add entry to module.
		 */
		synchronized (moduleComponents) {

			if (!moduleComponents.add(entry)) {
				moduleComponents.remove(entry);
				moduleComponents.add(entry);
			}
		}
	}

	private void recordClusterComponent(ComponentEntry entry) {
		clusterComponents.set(entry.getEntryId(), entry);
	}

	private void removeModuleComponent(ComponentEntry entry) {

		synchronized (moduleComponents) {
			moduleComponents.remove(entry);
		}
	}

	private void removeClusterComponent(ComponentEntry entry) {
		clusterComponents.remove(entry.getEntryId());
	}

}
