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
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.module.registry.EntryProperty.BooleanProp;
import gov.pnnl.proven.cluster.lib.module.registry.EntryProperty.DoubleProp;
import gov.pnnl.proven.cluster.lib.module.registry.EntryProperty.EntryType;
import gov.pnnl.proven.cluster.lib.module.registry.EntryProperty.FloatProp;
import gov.pnnl.proven.cluster.lib.module.registry.EntryProperty.IntegerProp;
import gov.pnnl.proven.cluster.lib.module.registry.EntryProperty.LongProp;
import gov.pnnl.proven.cluster.lib.module.registry.EntryProperty.StringProp;
import gov.pnnl.proven.cluster.lib.module.util.ModuleIDSFactory;

/**
 * Simple wrapper around a set of {@code EntryProperty}s. Class provides getters
 * to retrieve {@code EntryProperty} elements.
 * 
 * Common entry component property definitions are also provided.
 */
public final class EntryProperties implements IdentifiedDataSerializable, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Container of entry properties.
	 */
	Set<EntryProperty> entryProperties = new HashSet<>();

	public EntryProperties() {
	}
	
	/**
	 * Copy constructor
	 */
	public EntryProperties(EntryProperties props) {
		entryProperties.addAll(props.getEntryProperties());
	}

	public boolean add(EntryProperty prop) {
		return entryProperties.add(prop);
	}

	public boolean addAll(Collection<EntryProperty> props) {
		return entryProperties.addAll(props);
	}

	public Integer get(IntegerProp key) {
		Integer ret = null;
		EntryProperty val = getByNameAndType(key.name, EntryType.Integer);
		if (null != val) {
			ret = Integer.valueOf(val.getValue());
		}
		return ret;
	}

	public Long get(LongProp key) {
		Long ret = null;
		EntryProperty val = getByNameAndType(key.name, EntryType.Long);
		if (null != val) {
			ret = Long.valueOf(val.getValue());
		}
		return ret;
	}

	public Float get(FloatProp key) {
		Float ret = null;
		EntryProperty val = getByNameAndType(key.name, EntryType.Float);
		if (null != val) {
			ret = Float.valueOf(val.getValue());
		}
		return ret;
	}

	public Double get(DoubleProp key) {
		Double ret = null;
		EntryProperty val = getByNameAndType(key.name, EntryType.Double);
		if (null != val) {
			ret = Double.valueOf(val.getValue());
		}
		return ret;
	}

	public Boolean get(BooleanProp key) {
		Boolean ret = null;
		EntryProperty val = getByNameAndType(key.name, EntryType.Boolean);
		if (null != val) {
			ret = Boolean.valueOf(val.getValue());
		}
		return ret;
	}

	public String get(StringProp key) {
		String ret = null;
		EntryProperty val = getByNameAndType(key.name, EntryType.String);
		if (null != val) {
			ret = val.getValue();
		}
		return ret;
	}

	private EntryProperty getByNameAndType(String name, EntryType type) {

		EntryProperty ret = null;

		Optional<EntryProperty> epOpt = entryProperties.stream()
				.filter((ep) -> ((ep.getName().equals(name)) && (ep.getEntryType() == type))).findFirst();
		if (epOpt.isPresent()) {
			ret = epOpt.get();
		}

		return ret;
	}

	/**
	 * @return the entryProperties
	 */
	public Set<EntryProperty> getEntryProperties() {
		return entryProperties;
	}
	
	@Override
	public void readData(ObjectDataInput in) throws IOException {
		int count = in.readInt();
		for (int i = 0; i < count; i++) {
			EntryProperty prop = new EntryProperty();
			prop.readData(in);
			entryProperties.add(prop);
		}
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		out.writeInt(((null == entryProperties) ? 0 : entryProperties.size()));
		for (EntryProperty prop : entryProperties) {
			prop.writeData(out);
		}		
	}

	@Override
	public int getFactoryId() {
		return ModuleIDSFactory.FACTORY_ID;
	}

	@Override
	public int getId() {
		return ModuleIDSFactory.ENTRY_PROPERTIES_TYPE;
	}

}