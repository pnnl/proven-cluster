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

import static gov.pnnl.proven.cluster.lib.disclosure.DomainProvider.LS;
import static gov.pnnl.proven.cluster.lib.disclosure.DomainProvider.PROVEN_DOMAIN;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessageIDSFactory;
import gov.pnnl.proven.cluster.lib.module.component.ComponentGroup;
import gov.pnnl.proven.cluster.lib.module.component.ManagedComponent;
import gov.pnnl.proven.cluster.lib.module.util.ModuleIDSFactory;

/**
 * Represents an entry identifier for a managed component. It's a
 * reverse domain (or reverse DNS) name, used to identify managed component
 * entries and their associated distributed object, if any.
 * 
 * Use {@link #toString()} to get the reverse domain name in String format.
 * 
 * @see ComponentEntry, {@link EntryReporter#entryIdentifier()}
 * 
 * @author d3j766
 *
 */
public class EntryIdentifier extends DisclosureDomain
		implements IdentifiedDataSerializable, Serializable, Comparable<EntryIdentifier> {

	private static final long serialVersionUID = 1L;

	/**
	 * A managed component's base domain value. All component sub-domains should
	 * be added to this value.
	 */
	public static final String COMPONENT_DOMAIN = "component" + LS + PROVEN_DOMAIN;

	protected UUID componentId;
	protected String componentName;
	protected String groupLabel;

	public EntryIdentifier() {
	}

	public EntryIdentifier(UUID id, String name, ComponentGroup group) {
		super(id.toString() + LS + name + LS + group.getGroupLabel() + LS + COMPONENT_DOMAIN);
		this.componentId = id;
		this.componentName = name;
		this.groupLabel = group.getGroupLabel();
	}
		
	/**
	 * @return the componentId
	 */
	public UUID getComponentId() {
		return componentId;
	}

	/**
	 * @return the componentName
	 */
	public String getComponentName() {
		return componentName;
	}

	/**
	 * @return the subDomainLabel
	 */
	public String getDomainLabel() {
		return groupLabel;
	}

	public String getComponentDomain() {
		return COMPONENT_DOMAIN;
	}

	@Override
	public String toString() {
		return getReverseDomain();
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		super.readData(in);
		this.componentId = UUID.fromString(in.readUTF());
		this.componentName = in.readUTF();
		this.groupLabel = in.readUTF();
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		super.writeData(out);
		out.writeUTF(this.componentId.toString());
		out.writeUTF(this.componentName);
		out.writeUTF(this.groupLabel);
	}

	@Override
	public int getFactoryId() {
		return ModuleIDSFactory.FACTORY_ID;
	}

	@Override
	public int getId() {
		return ModuleIDSFactory.ENTRY_IDENTIFIER_TYPE;
	}

	@Override
	public int compareTo(EntryIdentifier other) {

		int ret;

		if (!groupLabel.equals(other.groupLabel)) {
			ret = groupLabel.compareTo(other.groupLabel);
		} else if (!componentName.equals(other.componentName)) {
			ret = componentName.compareTo(other.componentName);
		} else if (!componentId.equals(other.componentId)) {
			ret = componentId.compareTo(other.componentId);
		} else {
			ret = 0;
		}

		return ret;
	}

}
