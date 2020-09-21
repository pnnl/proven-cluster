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
import java.util.Arrays;
import java.util.UUID;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessageIDSFactory;
import gov.pnnl.proven.cluster.lib.module.util.ModuleIDSFactory;

/**
 * Identifies the location of a reported {@code ComponentEntry} inside a
 * cluster.
 * 
 * The location is comprised of 4 coordinates, shown below:
 * 
 * <ol>
 * <li><b>Member</b>: identifier of component's cluster member</li>
 * <li><b>Module</b>: identifier of component's module application</li>
 * <li><b>Manager</b>: identifier of component's manager component</li>
 * <li><b>Creator</b>: identifier of a component's creator component</li>
 * </ol>
 * 
 * @author d3j766
 *
 */
public class EntryLocation implements IdentifiedDataSerializable, Serializable, Comparable<EntryLocation> {

	private static final long serialVersionUID = 1L;

	public static final int MEMBER = 0;
	public static final int MODULE = 1;
	public static final int MANAGER = 2;
	public static final int CREATOR = 3;

	public static final int COORDINATES = 4;

	private String[] location = new String[COORDINATES];

	public EntryLocation() {
	}

	public EntryLocation(UUID memberId, UUID moduleId, UUID managerId, UUID creatorId) {
		location[MEMBER] = memberId.toString();
		location[MODULE] = moduleId.toString();
		location[MANAGER] = managerId.toString();
		location[CREATOR] = creatorId.toString();
	}

	public UUID getCreatorId() {
		return UUID.fromString(location[CREATOR]);
	}

	public UUID getManagerId() {
		return UUID.fromString(location[MANAGER]);
	}

	public UUID getModuleId() {
		return UUID.fromString(location[MODULE]);
	}

	public UUID getMemberId() {
		return UUID.fromString(location[MEMBER]);
	}

	@Override
	public String toString() {
		return getModuleId().toString() + "_" + getMemberId().toString() + "_" + getManagerId().toString() + "_"
				+ getCreatorId().toString();
	}

	@Override
	public int compareTo(EntryLocation other) {

		int ret = 0;

		for (int i = 0; i < COORDINATES; i++) {
			if (!location[i].equals(other.location[i])) {
				ret = location[i].compareTo(other.location[i]);
				break;
			}
		}
		return ret;
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		location = in.readUTFArray();
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		out.writeUTFArray(location);
	}

	@Override
	public int getFactoryId() {
		return ModuleIDSFactory.FACTORY_ID;
	}

	@Override
	public int getId() {
		return ModuleIDSFactory.ENTRY_LOCATION_TYPE;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(location);
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
		if (!(obj instanceof EntryLocation)) {
			return false;
		}
		EntryLocation other = (EntryLocation) obj;
		if (!Arrays.equals(location, other.location)) {
			return false;
		}
		return true;
	}
}
