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

import java.io.IOException;
import java.util.Date;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;

import gov.pnnl.proven.cluster.lib.module.component.ManagedComponent;
import gov.pnnl.proven.cluster.lib.module.messenger.event.ComponentEvent;
import gov.pnnl.proven.cluster.lib.module.module.ModuleStatus;
import gov.pnnl.proven.cluster.lib.module.util.ModuleIDSFactory;

/**
 * Provides information for a {@code ManagedComponent} that can be entered into
 * and processed by a {@code ComponentRegistry}.
 * 
 * @author d3j766
 *
 */
public class ComponentEntry extends ComponentEvent implements Comparable<ComponentEntry> {

	private static final long serialVersionUID = 1L;

	/**
	 * ComponentEntry creation time
	 */
	private long entryCreation;

	/**
	 * Initialized from ManagedComponent properties
	 */
	private EntryIdentifier entryId;
	private EntryLocation location;
	private EntryProperties properties;
	private long componentCreation;

	/**
	 * Initialized by StatusSchedule before reporting to registry
	 */
	private ModuleStatus moduleStatus = ModuleStatus.Unknown;
	private String moduleName = "";
	private long moduleCreation;
	private long overdueMillis;

	/**
	 * Initialized by ComponentRegistry at record/remove time
	 */
	private long recorded;
	private boolean isRemove = false;

	public ComponentEntry() {
	}

	public ComponentEntry(ManagedComponent mc) {
		super(mc);
		this.entryCreation = new Date().getTime();
		this.entryId = mc.entryIdentifier();
		this.location = mc.entryLocation();
		this.properties = mc.entryProperties();
		this.componentCreation = mc.getCreationeTime();
	}

	public long getRecorded() {
		return recorded;
	}

	public void setRecorded(long recorded) {
		this.recorded = recorded;
	}

	public boolean isRemove() {
		return isRemove;
	}

	public void setRemove(boolean isRemove) {
		this.isRemove = isRemove;
	}

	public long getEntryCreation() {
		return entryCreation;
	}

	public EntryIdentifier getEntryId() {
		return entryId;
	}

	public EntryLocation getLocation() {
		return location;
	}

	public EntryProperties getProperties() {
		return properties;
	}

	public long getComponentCreation() {
		return componentCreation;
	}

	public ModuleStatus getModuleStatus() {
		return moduleStatus;
	}

	public void setModuleStatus(ModuleStatus moduleStatus) {
		this.moduleStatus = moduleStatus;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public long getModuleCreation() {
		return moduleCreation;
	}

	public void setModuleCreation(long moduleCreation) {
		this.moduleCreation = moduleCreation;
	}

	public long getOverdueMillis() {
		return overdueMillis;
	}

	public void setOverdueMillis(long overdueMillis) {
		this.overdueMillis = overdueMillis;
	}

	@Override
	public int compareTo(ComponentEntry other) {

		int ret;

		if (!location.equals(other.location)) {
			ret = location.compareTo(other.location);
		} else if (!entryId.equals(other.entryId)) {
			ret = entryId.compareTo(other.entryId);
		} else {
			ret = 0;
		}

		return ret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entryId == null) ? 0 : entryId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ComponentEntry)) {
			return false;
		}
		ComponentEntry other = (ComponentEntry) obj;
		if (cId == null) {
			if (other.cId != null) {
				return false;
			}
		} else if (!cId.equals(other.cId)) {
			return false;
		}
		return true;
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		super.readData(in);
		this.entryCreation = in.readLong();

		this.entryId = new EntryIdentifier();
		this.entryId.readData(in);

		this.location = new EntryLocation();
		this.location.readData(in);

		this.properties = new EntryProperties();
		this.properties.readData(in);

		this.componentCreation = in.readLong();
		this.moduleStatus = ModuleStatus.valueOf(in.readString());
		this.moduleName = in.readString();
		this.moduleCreation = in.readLong();
		this.overdueMillis = in.readLong();
		this.recorded = in.readLong();
		this.isRemove = in.readBoolean();
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		super.writeData(out);
		out.writeLong(this.entryCreation);
		this.entryId.writeData(out);
		this.location.writeData(out);
		this.properties.writeData(out);
		out.writeLong(this.componentCreation);
		out.writeString(this.moduleStatus.toString());
		out.writeString(this.moduleName);
		out.writeLong(this.moduleCreation);
		out.writeLong(this.overdueMillis);
		out.writeLong(this.recorded);
		out.writeBoolean(this.isRemove);
	}

	@Override
	public int getFactoryId() {
		return ModuleIDSFactory.FACTORY_ID;
	}

	@Override
	public int getClassId() {
		return ModuleIDSFactory.COMPONENT_ENTRY_TYPE;
	}

}
