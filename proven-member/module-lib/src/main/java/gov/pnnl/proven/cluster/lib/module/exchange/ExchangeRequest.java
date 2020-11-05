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
package gov.pnnl.proven.cluster.lib.module.exchange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.UUID;

import gov.pnnl.proven.cluster.lib.disclosure.exchange.BufferedItem;
import gov.pnnl.proven.cluster.lib.module.module.ModuleStatus;
import gov.pnnl.proven.cluster.lib.module.registry.ComponentEntry;

/**
 * Represents a request for the exchange of {@code BufferedItem}(s) between
 * {@code ExchangeComponent}s.
 * 
 * @author d3j766
 * 
 * @see BufferedItem, ExchangeComponent
 *
 */
public class ExchangeRequest implements Comparable<ExchangeRequest> {

	/**
	 * Contains data items being exchanged.  Requests come in batches up to the maximumm
	 */
	private List<BufferedItem> items = new ArrayList<>();
	
	
	
	
	
	
	
	
	
	private UUID moduleId;
	private ModuleStatus moduleStatus;
	private String moduleName;
	private long moduleCreation;

	/**
	 * Block synchronization lock for adding a new ComponentEntry
	 */
	private final Object componentEntryLock = new Object();

	/**
	 * (Local) Module component and exchange entries
	 */
	private TreeSet<ComponentEntry> moduleComponents = new TreeSet<ComponentEntry>();
	private NavigableSet<ComponentEntry> moduleExchange = Collections.unmodifiableNavigableSet(moduleComponents);

	public ExchangeRequest(ComponentEntry ce) {
		this.moduleId = ce.getLocation().getModuleId();
		this.moduleStatus = ce.getModuleStatus();
		this.moduleName = ce.getModuleName();
		this.moduleCreation = ce.getModuleCreation();
	}

	/**
	 * @return the number of component entries for the module
	 */
	public int getEntryCount() {
		return moduleComponents.size();
	}

	/**
	 * Adds the provided ComponentEntry to this module. Provided entry will
	 * replace existing entry if it exists.
	 * 
	 * @param ce
	 *            entry to add
	 * 
	 * @throw IllegalArgumentException if provided entry's module does not match
	 *        this module entry.
	 */
	public void addComponent(ComponentEntry ce) {

		if (!ce.getLocation().getModuleId().equals(moduleId)) {
			throw new IllegalArgumentException("Component entry's module does not match ModuleEntry");
		}

		synchronized (componentEntryLock) {
			if (!moduleComponents.add(ce)) {
				moduleComponents.remove(ce);
				moduleComponents.add(ce);
			}
		}
	}

	public void removeComponent(ComponentEntry ce) {

		if (!ce.getLocation().getModuleId().equals(moduleId)) {
			throw new IllegalArgumentException("Component entry's module does not match ModuleEntry");
		}

		synchronized (componentEntryLock) {
			moduleComponents.remove(ce);
		}
	}

	public ComponentEntry exchangeComponent() {
		// TODO
		return null;
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
	public int compareTo(ExchangeRequest other) {

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
		if (!(obj instanceof ExchangeRequest)) {
			return false;
		}
		ExchangeRequest other = (ExchangeRequest) obj;
		if (moduleId == null) {
			if (other.moduleId != null) {
				return false;
			}
		} else if (!moduleId.equals(other.moduleId)) {
			return false;
		}
		return true;
	}

}
