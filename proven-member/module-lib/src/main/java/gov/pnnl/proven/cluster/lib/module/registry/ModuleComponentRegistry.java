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

import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;

import static gov.pnnl.proven.cluster.lib.module.registry.EntryDomain.*;

import static gov.pnnl.proven.cluster.lib.disclosure.DomainProvider.*;

import gov.pnnl.proven.cluster.lib.member.MemberProperties;
import gov.pnnl.proven.cluster.lib.module.component.ManagedComponent;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Eager;

/**
 * Component Registry at the Module level.
 * 
 * @author d3j766
 *
 */
@ApplicationScoped
@Eager
public class ModuleComponentRegistry
		implements ItemListener<ComponentEntry>, EntryAddedListener<EntryLocation, Set<ComponentEntry>>,
		EntryRemovedListener<EntryLocation, Set<ComponentEntry>>,
		EntryUpdatedListener<EntryLocation, Set<ComponentEntry>> {

	@Inject
	Logger log;

	@Inject
	HazelcastInstance hzi;

	/**
	 * Member properties
	 */
	MemberProperties props = MemberProperties.getInstance();

	/**
	 * Member registry
	 */
	ISet<ManagedComponent> memberComponents;

	/**
	 * Cluster registry
	 */
	IMap<EntryLocation, Set<ComponentEntry>> clusterCompoonents;

	/**
	 * Module registry
	 */

	@PostConstruct
	public void initialize() {
		log.debug("Inside MemberComponentRegistry PostConstruct");
	}

	public ModuleComponentRegistry() {
		System.out.println("Inside MemberComponentRegistry constructor");
	}

	public void record(ComponentEntry event) {
		// TODO
		// record status information
	}

	@Schedule(dayOfWeek = "Sun", hour = "0")
	public void unregister(UUID componentId) {

		// TODO
		// Ungegister
		// Stop scheduler
		// Also stop maintenance schedule for good measure
	}

	@Override
	public void entryUpdated(EntryEvent<EntryLocation, Set<ComponentEntry>> event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void entryRemoved(EntryEvent<EntryLocation, Set<ComponentEntry>> event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void entryAdded(EntryEvent<EntryLocation, Set<ComponentEntry>> event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void itemAdded(ItemEvent<ComponentEntry> item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void itemRemoved(ItemEvent<ComponentEntry> item) {
		// TODO Auto-generated method stub
		
	}

}
