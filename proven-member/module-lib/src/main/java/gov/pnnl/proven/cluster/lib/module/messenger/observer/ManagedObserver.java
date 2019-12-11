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

import static gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation.Activate;
import static gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation.Deactivate;
import static gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation.Fail;
import static gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation.Remove;
import static gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation.Scale;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.slf4j.Logger;

import gov.pnnl.proven.cluster.lib.module.component.ManagedComponent;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Eager;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Managed;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.MemberRegistry;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation;
import gov.pnnl.proven.cluster.lib.module.messenger.event.StatusEvent;
import gov.pnnl.proven.cluster.lib.module.registry.MemberComponentRegistry;

/**
 * Observer methods for {@code ManagedComponent} events.
 * 
 * @author d3j766
 *
 */
@ApplicationScoped
public class ManagedObserver {

	@Inject
	Logger log;

	// Map of managed component observers
	Map<UUID, ManagedComponent> registeredObservers = new HashMap<>();

	public void register(ManagedComponent mc) {
		registeredObservers.put(mc.getId(), mc);
	}

	public void unregister(ManagedComponent mc) {
		registeredObservers.remove(mc.getId());
	}

	public boolean isRegistered(UUID id) {
		return registeredObservers.containsKey(id);
	}

	@Inject
	public ManagedObserver() {
	}

	public void activate(@ObservesAsync @Managed @StatusOperation(operation = Activate) StatusEvent event) {

		log.debug("(Observing) Inside activate operation");

		UUID opCandidateId = event.getOpCandidateId(); // child
		if (isRegistered(opCandidateId)) {
			ManagedComponent mc = registeredObservers.get(event.getOpCandidateId());
			log.debug("ACTIVATING CREATED: " + mc.getDoId() + " CREATOR: " + event.getDoId());
			mc.activate();
		}
	}

	public void scale(@ObservesAsync @Managed @StatusOperation(operation = Scale) StatusEvent event) {

		log.debug("(Observing) Inside scale operation");

		UUID opCandidateId = event.getOpCandidateId(); // child
		if (isRegistered(event.getRequestorId())) {
			ManagedComponent mc = registeredObservers.get(event.getRequestorId()); // parent
			Optional<ManagedComponent> scaledOpt = mc.getCreated(opCandidateId);
			if (scaledOpt.isPresent()) {
				ManagedComponent scaled = scaledOpt.get();
				log.debug("SCALING FOR CREATED: " + scaled.getDoId() + " CREATOR: " + event.getDoId());
				mc.scale(opCandidateId);
			}
		}
	}

	public void deactivate(@ObservesAsync @Managed @StatusOperation(operation = Deactivate) StatusEvent event) {

		log.debug("(Observing) Inside deactivate operation");

		UUID opCandidateId = event.getOpCandidateId(); // child
		if (isRegistered(opCandidateId)) {
			ManagedComponent mc = registeredObservers.get(event.getOpCandidateId());
			log.debug("DEACTIVATING CREATED: " + mc.getDoId() + " CREATOR: " + event.getDoId());
			mc.deactivate();
		}

	}

	public void fail(@ObservesAsync @Managed @StatusOperation(operation = Fail) StatusEvent event) {

		log.debug("(Observing) Inside fail operation");

		UUID opCandidateId = event.getOpCandidateId(); // child
		if (isRegistered(opCandidateId)) {
			ManagedComponent mc = registeredObservers.get(event.getOpCandidateId());
			log.debug("FAILING CREATED: " + mc.getDoId() + " CREATOR: " + event.getDoId());
			mc.fail();
		}

	}

	public void remove(@ObservesAsync @Managed @StatusOperation(operation = Remove) StatusEvent event) {

		log.debug("(Observing) Inside remove operation");

		UUID opCandidateId = event.getOpCandidateId(); // child
		if (isRegistered(opCandidateId)) {
			ManagedComponent mc = registeredObservers.get(event.getOpCandidateId());
			log.debug("REMOVING CREATED: " + event.getDoId() + " CREATOR: " + mc.getDoId());
			mc.remove();
		}

	}

	public void statusReport(@ObservesAsync @MemberRegistry StatusEvent event, @Eager MemberComponentRegistry mcr) {

		log.debug("(Observing) Inside registry statusReport operation");

		// TODO

		// Registry records/updates provided managed component information

	}

	public void heartbeat(@ObservesAsync @Managed StatusEvent event, @Eager MemberComponentRegistry mcr) {

		log.debug("(Observing) Inside registry statusReport operation");

		// TODO

		// (Sender) Both status reports and maintenance reports should be sent
		// to registry
		// If either are missing - registry will set to unknown and send
		// heartbeat request

		// (Receiver) Check both schedulers (status & Maintenance)
		// Perform check operation for both schedulers
		// Adjust status accordingly
		// Fire "Heartbeat" report to registry

	}

}
