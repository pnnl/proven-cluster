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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.pnnl.proven.cluster.lib.module.component.annotation.ManagedComponentType;
import gov.pnnl.proven.cluster.lib.module.component.annotation.ScheduledEventReporter;
import gov.pnnl.proven.cluster.lib.module.component.event.StatusReport;
import gov.pnnl.proven.cluster.lib.module.manager.ManagerComponent;

/**
 * 
 * Represents managed components within a Proven module. They each perform
 * specific tasks to support the operation of the platform. These components are
 * registered with their {@code MemberComponentRegistry}, report their
 * {@code ComponentStatus} to their registry, and report their
 * {@code ComponentStatus} as well to their {@code ManagerComponent}.
 * 
 * @author d3j766
 *
 * @see ModuleComponent
 *
 */
@ScheduledEventReporter(event = StatusReport.class, schedule = StatusReport.STATUS_REPORT_SCHEDULE)
@ManagedComponentType
public abstract class ManagedComponent extends ModuleComponent implements Activator, StatusReporter {

	static Logger log = LoggerFactory.getLogger(ManagedComponent.class);

	protected ComponentStatus status;

	protected ComponentStatus previousStatus;

	protected int remainingRetries;

	protected UUID managerId;

	protected UUID creatorId;

	@Inject
	@ManagedComponentType
	protected Instance<ManagedComponent> instanceProvider;

	/**
	 * Created components. The Map contains the component's ID as the key and
	 * object as value.
	 */
	protected Map<UUID, ManagedComponent> createdComponents;

	@PostConstruct
	public void managedComponentInit() {
		remainingRetries = mp.getManagedComponentMaxRetries();
	}

	public ManagedComponent() {
		super();
		group.add(ComponentGroup.Managed);
		status = ComponentStatus.Offline;
		previousStatus = ComponentStatus.Offline;
		createdComponents = new HashMap<>();
	}

	protected <T extends ManagedComponent> T getComponent(Class<T> subtype, Annotation... qualifiers) {
		// Get component
		T mc = instanceProvider.select(subtype, qualifiers).get();
		addLineage(mc);
		createdComponents.put(mc.getId(), mc);
		return mc;
	}

	protected <T extends ManagedComponent> List<T> getComponents(Class<T> subtype, Annotation... qualifiers) {
		List<T> ret = new ArrayList<>();
		Iterator<T> mcItr = instanceProvider.select(subtype, qualifiers).iterator();
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
		if (ManagerComponent.class.isAssignableFrom(this.getClass())) {
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

	@Override
	public <T extends ManagedComponent> void activateNew(Class<T> clazz, Annotation... qualifiers) {

		boolean foundScaledComponent = false;
		ManagedComponent deactivatedComponent = null;
		for (ManagedComponent component : createdComponents.values()) {
			if (component.getClass().equals(clazz)) {
				foundScaledComponent = true;
				if (component.getStatus() == ComponentStatus.Deactivated) {
					deactivatedComponent = component;
					break;
				}
			}
		}

		// Scaling only applies if adding another component of the same type
		if (foundScaledComponent) {
			// Recycle existing deactivated component, if possible.
			if (null != deactivatedComponent) {
				deactivatedComponent.activate();
			} else {
				T component = getComponent(clazz, qualifiers);
				component.activate();
			}
		}
	}

	/**
	 * Retry activating or deactivating a component having a FailedRetry status
	 */
	@Override
	public void retry() {

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

	public ComponentStatus getStatus() {
		synchronized (status) {
			return status;
		}
	}

	protected void setStatus(ComponentStatus status) {
		synchronized (status) {
			this.status = status;
		}
	}

	@Override
	public StatusReport reportStatus() {
		updateStatus();
		return new StatusReport(this);
	}

}
