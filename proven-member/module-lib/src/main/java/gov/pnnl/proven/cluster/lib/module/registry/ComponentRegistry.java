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
package gov.pnnl.proven.cluster.lib.module.registry;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;

import gov.pnnl.proven.cluster.lib.member.MemberProperties;
import gov.pnnl.proven.cluster.lib.module.component.ManagedComponent;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Eager;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.Module;
import gov.pnnl.proven.cluster.lib.module.module.ProvenModule;

/**
 * A registry of component entries. These entries represent the
 * {@code ManagedComponent}s located within the proven cluster.
 * 
 * @author d3j766
 *
 * @see ComponentEntry, ModuleEntry, MemberEntry, EntryLocation
 *
 */
@ApplicationScoped
@Eager
public class ComponentRegistry {

	@Inject
	Logger log;

	private static final int MAX_OVERDUE_NOTIFICATIONS = 3;

	@Inject
	@Module
	ProvenModule pm;

	@Inject
	HazelcastInstance hzi;

	/**
	 * Used by the scheduled {@link #cleanup()} method to ensure only one
	 * cleanup task is running at any given time.
	 */
	AtomicBoolean cleaning = new AtomicBoolean(false);

	/**
	 * Overdue notifications. Maintains a map of an entry by id and its number
	 * of overdue notifications that have been sent, if any.
	 */
	Map<UUID, Integer> overdue = new HashMap<>();

	/**
	 * Member properties
	 */
	MemberProperties props = MemberProperties.getInstance();

	/**
	 * (Local) Module registry
	 */
	Set<ComponentEntry> moduleRegistry = new HashSet<>();

	/**
	 * (IMDG) Member registry
	 */
	ISet<ModuleEntry> memberRegistry;

	/**
	 * (IMDG) Cluster registry
	 */
	IMap<UUID, Set<MemberEntry>> iClusterComponents;

	public ComponentRegistry() {
		System.out.println("Inside MemberComponentRegistry constructor");
	}

	@PostConstruct
	public void initialize() {

		log.debug("Inside MemberComponentRegistry PostConstruct");

		/**
		 * Create IMDG registries.
		 */
		memberRegistry = hzi.getSet(props.getMemberRegistryDoName() + "." + pm.getMemberId());
		iClusterComponents = hzi.getMap(props.getClusterRegistryDoName());
	}

	/**
	 * Records reported ComponentEntry event made by a ManagedComponent.
	 * Following steps, in the order they are performed, records entry at the
	 * module, member, and cluster levels:
	 * 
	 * <ol>
	 * <li>Add time of recording, used for overdue reporting calculations</li>
	 * <li>Adds entry to local module registry (heap storage)</li>
	 * <li>Adds entry to IMDG member registry (IMDG storage)</li>
	 * <li>Adds entry to IMDG cluster registry (IMDG storage)</li>
	 * </ol>
	 * 
	 * Module registry information is accessed directly from its instance (heap
	 * storage). Both member and cluster registries are accessed via IMDG access
	 * using ISet and IMap distributed data structures respectively.
	 * 
	 * @param entry
	 *            reported Component entry to store in module, member, and
	 *            cluster registries.
	 */
	public void record(ComponentEntry entry) {

		// 1
		entry.setRecordedTimestamp(new Date().getTime());

		// 2
		recordLocalModuleComponent(entry);

		// 3
		recordLocalMemberComponent(entry);

		// 4
		recordImdgMemberComponent(entry);

		// 5
		recordImdgClusterComponent(entry);
	}

	/**
	 * A scheduled cleanup and maintenance task. Removes registry entries for
	 * components that no longer exist or are not reporting their current
	 * information.
	 * 
	 * If a component is overdue in reporting its status it is immediately set
	 * to {@code ManagedStatus#Unknown}, so it cannot be selected for exchange.
	 * 
	 * Component entries will be removed if they no longer exist.
	 * 
	 * If it does exist, but is not reporting, an overdue message is sent to the
	 * component requesting that it report its status.
	 * 
	 * A {@code ManagedStatusOperation#shutdown()} operation event message will
	 * be sent to the component if the number of overdue notifications exceeds
	 * {@code #MAX_OVERDUE_NOTIFICATIONS}.
	 * 
	 */
	@Schedule(minute = "*/1", hour = "*", persistent = false)
	public void cleanup() {

		// Do work only if it's not already running
		if (!cleaning.compareAndSet(false, true)) {
			return;
		}
		try {

			// Cleanup work here
			log.debug("CLEANUP TASK INVOKED");
			

		} finally {
			cleaning.set(false);
		}

	}

	private void recordLocalModuleComponent(ComponentEntry entry) {

		synchronized (moduleRegistry) {
			if (!moduleRegistry.add(entry)) {
				moduleRegistry.remove(entry);
				moduleRegistry.add(entry);
			}
		}
	}

	private void recordLocalMemberComponent(ComponentEntry entry) {

		synchronized (lMemberComponents) {
			if (!lMemberComponents.add(entry)) {
				lMemberComponents.remove(entry);
				lMemberComponents.add(entry);
			}
		}
	}

	private void recordImdgMemberComponent(ComponentEntry entry) {

		if (!memberRegistry.add(entry)) {
			memberRegistry.remove(entry);
			memberRegistry.add(entry);
		}
	}

	private void recordImdgClusterComponent(ComponentEntry entry) {

		Set<ComponentEntry> entries;
		EntryLocation location = entry.getLocation();
		if (iClusterComponents.containsKey(location)) {
			entries = iClusterComponents.get(location);
			if (!entries.add(entry)) {
				entries.remove(entry);
				entries.add(entry);
			}
		} else {
			entries = new HashSet<>();
			entries.add(entry);
		}
		iClusterComponents.put(location, entries);
	}

	private void removeLocalModuleComponent(ComponentEntry entry) {

		synchronized (moduleRegistry) {
			moduleRegistry.remove(entry);
			removeLocalMemberComponent(entry);
		}
	}

	private void removeLocalMemberComponent(ComponentEntry entry) {
		synchronized (lMemberComponents) {
			lMemberComponents.remove(entry);
		}
	}

}
