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

import static com.hazelcast.util.Preconditions.checkHasText;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;

import gov.pnnl.proven.cluster.lib.module.util.ModuleIDSFactory;

/**
 * Represents a {@code ComponentEntry} property. These properties augment a
 * {@code ComponentEntry}'s information with, if any, component specific
 * information. These properties are provided to support component registry
 * processing.
 * 
 * Property is identified by it's name and type.
 * 
 * @author d3j766
 *
 */
public class EntryProperty implements IdentifiedDataSerializable, Serializable, Comparable<EntryProperty> {

	private static final long serialVersionUID = 1L;

	private String name;
	private String value;
	private TimeUnit timeUnit;
	private EntryType entryType;

	protected enum EntryType {
		Integer,
		Long,
		Float,
		Double,
		Boolean,
		String,
		Clazz
	}

	/**
	 * Entry property definition.
	 * 
	 * @author d3j766
	 *
	 * @param <T>
	 *            type of property value
	 */
	public abstract static class Prop<T> {
		protected String name;
		protected TimeUnit timeUnit;

		public String getName() {
			return name;
		}

		public TimeUnit getTimeUnit() {
			return timeUnit;
		}
	}

	/**
	 * Integer value property definition.
	 * 
	 * @author d3j766
	 *
	 */
	public final static class IntegerProp extends Prop<Integer> {
		public IntegerProp(String name) {
			this(name, null);
		}

		public IntegerProp(String name, TimeUnit timeUnit) {
			this.name = name;
			this.timeUnit = timeUnit;
		}
	}

	/**
	 * Long value property definition.
	 * 
	 * @author d3j766
	 *
	 */
	public final static class LongProp extends Prop<Long> {
		public LongProp(String name) {
			this(name, null);
		}

		public LongProp(String name, TimeUnit timeUnit) {
			this.name = name;
			this.timeUnit = timeUnit;
		}
	}

	/**
	 * Float value property definition.
	 * 
	 * @author d3j766
	 *
	 */
	public final static class FloatProp extends Prop<Float> {
		public FloatProp(String name) {
			this(name, null);
		}

		public FloatProp(String name, TimeUnit timeUnit) {
			this.name = name;
			this.timeUnit = timeUnit;
		}
	}

	/**
	 * Double value property definition.
	 * 
	 * @author d3j766
	 *
	 */
	public final static class DoubleProp extends Prop<Double> {
		public DoubleProp(String name) {
			this(name, null);
		}

		public DoubleProp(String name, TimeUnit timeUnit) {
			this.name = name;
			this.timeUnit = timeUnit;
		}
	}

	/**
	 * Boolean value property definition.
	 * 
	 * @author d3j766
	 *
	 */
	public final static class BooleanProp extends Prop<Boolean> {
		public BooleanProp(String name) {
			this.name = name;
		}
	}

	/**
	 * String value property definition.
	 * 
	 * @author d3j766
	 *
	 */
	public final static class StringProp extends Prop<String> {
		public StringProp(String name) {
			this.name = name;
		}

	}

	/**
	 * Class value property definition.
	 * 
	 * @author d3j766
	 *
	 */
	public final static class ClassProp extends Prop<Class<?>> {
		public ClassProp(String name) {
			this.name = name;
		}
	}
	
	public EntryProperty() {
	}

	public EntryProperty(BooleanProp prop, boolean value) {
		this(prop.getName(), value ? "true" : "false", null, EntryType.Boolean);
	}

	public EntryProperty(IntegerProp prop, Integer value) {
		this(prop.getName(), String.valueOf(value), null, EntryType.Integer);
	}

	public EntryProperty(IntegerProp prop, Integer value, TimeUnit timeUnit) {
		this(prop.getName(), String.valueOf(value), timeUnit, EntryType.Integer);
	}

	public EntryProperty(LongProp prop, Long value) {
		this(prop.getName(), Long.toString(value), null, EntryType.Long);
	}

	public EntryProperty(LongProp prop, Long value, TimeUnit timeUnit) {
		this(prop.getName(), Long.toString(value), timeUnit, EntryType.Long);
	}

	public EntryProperty(FloatProp prop, Float value) {
		this(prop.getName(), Float.toString(value), null, EntryType.Float);
	}

	public EntryProperty(FloatProp prop, Float value, TimeUnit timeUnit) {
		this(prop.getName(), Float.toString(value), timeUnit, EntryType.Float);
	}

	public EntryProperty(DoubleProp prop, Double value) {
		this(prop.getName(), Double.toString(value), null, EntryType.Double);
	}

	public EntryProperty(DoubleProp prop, Double value, TimeUnit timeUnit) {
		this(prop.getName(), Double.toString(value), timeUnit, EntryType.Double);
	}

	public EntryProperty(StringProp prop, String value) {
		this(prop.getName(), value, null, EntryType.String);
	}

	public EntryProperty(ClassProp prop, Class<?> value) {
		this(prop.getName(), value.getName(), null, EntryType.Clazz);
	}
	
	protected EntryProperty(String name, String value, TimeUnit timeUnit, EntryType entryType) {
		checkHasText(name, "The property name cannot be null or empty!");
		this.name = name;
		Preconditions.checkNotNull(value, "The property value cannot be null");
		this.value = value;
		this.timeUnit = timeUnit;
		this.entryType = entryType;
	}

	/**
	 * Returns the property name.
	 *
	 * @return the property name
	 */
	protected String getName() {
		return name;
	}

	/**
	 * Returns the value of the property, as a String. Property values are
	 * stored internally as a String type.
	 *
	 * @return the value or <tt>null</tt> if none is defined
	 */
	protected String getValue() {
		return value;
	}

	/**
	 * Returns the {@link TimeUnit} of the property.
	 *
	 * @return the {@link TimeUnit}, null if there is no time unit.
	 * 
	 */
	protected TimeUnit getTimeUnit() {
		return timeUnit;
	}

	/**
	 * @return the entryType
	 */
	public EntryType getEntryType() {
		return entryType;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(EntryProperty other) {

		int ret = 0;
		int nameCompare = this.name.compareTo(other.name);
		int typeCompare = this.entryType.compareTo(other.entryType);
		if (nameCompare != 0) {
			return nameCompare;
		}
		if (typeCompare != 0) {
			return typeCompare;
		}
		return ret;
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		this.name = in.readUTF();
		this.value = in.readUTF();
		String timeUnitStr = in.readUTF();
		this.timeUnit = ((timeUnitStr.isEmpty()) ? null : TimeUnit.valueOf(timeUnitStr));
		this.entryType = EntryType.valueOf(in.readUTF());
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		out.writeUTF(this.name);
		out.writeUTF(this.value);
		out.writeUTF((null == this.timeUnit) ? ("") : this.timeUnit.toString());
		out.writeUTF(entryType.toString());
	}

	@Override
	public int getFactoryId() {
		return ModuleIDSFactory.FACTORY_ID;
	}

	@Override
	public int getId() {
		return ModuleIDSFactory.ENTRY_PROPERTY_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entryType == null) ? 0 : entryType.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (!(obj instanceof EntryProperty)) {
			return false;
		}
		EntryProperty other = (EntryProperty) obj;
		if (entryType != other.entryType) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
	
}
