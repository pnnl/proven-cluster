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
package gov.pnnl.proven.cluster.lib.module.messenger.observer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;

import org.slf4j.Logger;

import gov.pnnl.proven.cluster.lib.module.component.ManagedStatus;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Eager;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.ModuleRegistry;
import gov.pnnl.proven.cluster.lib.module.registry.ComponentEntry;
import gov.pnnl.proven.cluster.lib.module.registry.MaintenanceResultEntry;
import gov.pnnl.proven.cluster.lib.module.registry.ComponentRegistry;
import gov.pnnl.proven.cluster.lib.module.registry.MaintenanceRegistry;

/**
 * Observer methods for registry events.
 * 
 * @author d3j766
 *
 */
@ApplicationScoped
@Eager
public class RegistryObserver {

    @Inject
    Logger log;

    public RegistryObserver() {
    }

    @PostConstruct
    public void init() {
    }

    public void componentEntry(@Observes(notifyObserver = Reception.ALWAYS) @ModuleRegistry ComponentEntry event,
	    ComponentRegistry cr) {
	log.debug("(Observing) Inside registry component status/reporting operation");
	
	/**
	 * Classes in WAR not found.  Hazelcast bug.
	 * Should be fixed after upgrading payara and hazelcast along with it.
	 */
	//cr.record(event);
	if ((event.getcName().equals("MemberModule")) || (event.getcName().equals("T3Pipeline"))) {
	    log.debug("Invalid class...");
	} else {
	    cr.record(event);
	}
    }

    public void maintenanceEntry(@Observes @ModuleRegistry MaintenanceResultEntry event,
	    @Eager MaintenanceRegistry mr) {

	log.debug("(Observing) Inside registry maintenance/reporting operation");
	mr.recordMaintenance(event);

	// If no longer a maintained component, then unregister.
	if (!ManagedStatus.isRecoverable(event.getResult().getSeverity().getStatus())) {
	    mr.unregister(event.getcId());
	}
    }
}
