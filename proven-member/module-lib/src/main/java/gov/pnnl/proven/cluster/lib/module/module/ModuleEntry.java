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
package gov.pnnl.proven.cluster.lib.module.module;

import java.io.IOException;
import java.util.UUID;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

/**
 * Represents a {@code ProvenModule}
 * 
 * @author d3j766
 * 
 * @see ProvenModule
 *
 */
public class ModuleEntry implements Comparable<ModuleEntry>, IdentifiedDataSerializable {
	
	private UUID moduleId;
	private ModuleStatus moduleStatus;
	private String moduleName;
	private long moduleCreation;

	public ModuleEntry(ProvenModule module) {
		this.moduleId = module.getId();
		this.moduleStatus = module.retrieveModuleStatus();
		this.moduleName = module.getModuleName();
		this.moduleCreation = module.getModuleCreation();
	}

	public UUID getModuleId() {
		return moduleId;
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

	public long getModuleCreation() {
		return moduleCreation;
	}

	@Override
	public int compareTo(ModuleEntry other) {

		int ret;

		if (moduleId.equals(other.getModuleId())) {
			ret = 0;
		} else {
			if (moduleCreation != other.getModuleCreation()) {
				int diff = (int) (moduleCreation - other.getModuleCreation());
				ret = ((diff < 0) ? (-1) : (1));
			} else {
				/**
				 * Need to pick a non-zero value here. Returning zero would
				 * violate equals()
				 */
				ret = -1;
			}
		}

		return ret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((moduleId == null) ? 0 : moduleId.hashCode());
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
		if (!(obj instanceof ModuleEntry)) {
			return false;
		}
		ModuleEntry other = (ModuleEntry) obj;
		if (moduleId == null) {
			if (other.moduleId != null) {
				return false;
			}
		} else if (!moduleId.equals(other.moduleId)) {
			return false;
		}
		return true;
	}

	@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFactoryId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getClassId() {
		// TODO Auto-generated method stub
		return 0;
	}

}
