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

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;

import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessageIDSFactory;
import gov.pnnl.proven.cluster.lib.module.component.ManagedComponent;
import gov.pnnl.proven.cluster.lib.module.messenger.event.ComponentEvent;

/**
 * Provides information regarding a {@code ManagedComponent} that can be entered
 * into and processed by a registry component.
 * 
 * @author d3j766
 *
 */
public class ComponentEntry extends ComponentEvent implements Comparable<ComponentEntry> {

	private static final long serialVersionUID = 1L;

	private EntryDomain entryId;
	private EntryLocation location;
	private EntryProperties properties;
	private long overdueMillis;

	public ComponentEntry(ManagedComponent mc) {
		super(mc);
		this.entryId = mc.entryIdentifier();
		this.location = mc.entryLocation();
		this.properties = mc.entryProperties();
	}

	/**
	 * @return the entryId
	 */
	public EntryDomain getEntryId() {
		return entryId;
	}

	/**
	 * @return the location
	 */
	public EntryLocation getLocation() {
		return location;
	}

	/**
	 * @return the properties
	 */
	public EntryProperties getProperties() {
		return properties;
	}

	/**
	 * @return the overdueMillis
	 */
	public long getOverdueMillis() {
		return overdueMillis;
	}

	/**
	 * @param overdueMillis
	 *            the overdueMillis to set
	 */
	public void setOverdueMillis(long overdueMillis) {
		this.overdueMillis = overdueMillis;
	}

	@Override
	public int compareTo(ComponentEntry other) {

		int ret = 0;

		if (!entryId.equals(other.getEntryId())) {
			ret = location.compareTo(other.location);
		}

		return ret;
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		this.entryId = in.readObject(EntryDomain.class);
		this.location = in.readObject(EntryLocation.class);
		this.properties = in.readObject(EntryProperties.class);
		this.overdueMillis = in.readLong();
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		this.entryId.writeData(out);
		this.location.writeData(out);
		this.properties.writeData(out);
		out.writeLong(overdueMillis);
	}

	@Override
	public int getId() {
		return ProvenMessageIDSFactory.COMPONENT_ENTRY_TYPE;
	}

}
