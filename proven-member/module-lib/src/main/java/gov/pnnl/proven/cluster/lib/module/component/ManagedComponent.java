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

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.pnnl.proven.cluster.lib.module.component.annotation.ManagedComponentType;
import gov.pnnl.proven.cluster.lib.module.component.annotation.ScheduledEventReporter;
import gov.pnnl.proven.cluster.lib.module.component.event.StatusReport;

/**
 * 
 * Represents managed components. These components are {@code ComponentManager}s
 * or are managed by a {@code ComponentManager} and all report their
 * {@code ComponentStatus} to a {@code MemberComponentRegistry}.
 * 
 * @author d3j766
 *
 * @see ModuleComponent
 *
 */
@ScheduledEventReporter(event = StatusReport.class, schedule = StatusReport.STATUS_REPORT_SCHEDULE)
@ManagedComponentType
public abstract class ManagedComponent extends ModuleComponent implements StatusReporter {

	static Logger log = LoggerFactory.getLogger(ManagedComponent.class);

	protected ComponentStatus status;

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

	public ManagedComponent() {
		super();
		group.add(ComponentGroup.Managed);
		status = ComponentStatus.Offline;
		createdComponents = new HashMap<>();
	}

	protected <T extends ManagedComponent> T getComponent(Class<T> subtype, Annotation... qualifiers) {
		T mc = instanceProvider.select(subtype, qualifiers).get();
		mc.setManagerId(getManagerId());
		mc.setCreatorId(getCreatorId());
		createdComponents.put(mc.getId(), mc);
		return mc;
	}

	protected <T extends ManagedComponent> List<T> getComponents(Class<T> subtype, Annotation... qualifiers) {
		List<T> ret = new ArrayList<>();
		Iterator<T> mcItr = instanceProvider.select(subtype, qualifiers).iterator();
		while (mcItr.hasNext()) {
			T mc = mcItr.next();
			mc.setManagerId(getManagerId());
			mc.setCreatorId(getCreatorId());
			createdComponents.put(mc.getId(), mc);
			ret.add(mc);
		}
		return ret;
	}

	public UUID getManagerId() {
		return managerId;
	}

	public void setManagerId(UUID managerId) {
		this.managerId = managerId;
	}

	public UUID getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(UUID creatorId) {
		this.creatorId = creatorId;
	}

	public ComponentStatus getStatus() {
		return status;
	}

	protected void setStatus(ComponentStatus status) {
		this.status = status;
	}

	@Override
	public StatusReport getStatusReport() {
		return null;
	}

}
