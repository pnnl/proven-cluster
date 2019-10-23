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
package gov.pnnl.proven.cluster.lib.module.messenger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.slf4j.Logger;

import gov.pnnl.proven.cluster.lib.member.MemberProperties;
import gov.pnnl.proven.cluster.lib.module.component.ComponentType;
import gov.pnnl.proven.cluster.lib.module.component.ManagedStatus;
import gov.pnnl.proven.cluster.lib.module.messenger.event.ComponentEvent;
import gov.pnnl.proven.cluster.lib.module.messenger.event.StatusEvent;

import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.*;

import java.util.HashMap;
import java.util.Map;

public class StatusMessenger extends ScheduledMessenger {

	@Inject
	Logger log;

	@Inject
	MemberProperties mp;

	// General status properties
	ManagedStatus status;
	ManagedStatus previousStatus;
	int maxRetries;
	
	// Scalable properties
	boolean isScalable;
	int maxScalableRetries;
	int allowedScalePerComponent;
	int minScalableCount;
	int maxScalableCount;
	
	// Retries per status
	Map<ManagedStatus, Integer> retries;
	
	public StatusMessenger() {
		super();
		this.status = Unknown;
		this.previousStatus = Unknown;
		this.isScalable = false;
		this.retries = new HashMap<>();
	}
	
	@PostConstruct
	public void init() {
		this.maxRetries = mp.getManagedComponentMaxRetries();
	}
	
	@Override
	protected void send(ComponentEvent ce) {		
	}
	
	@Override
	public ComponentType getComponentType() {
		return ComponentType.StatusMessenger;
	}

	public MemberProperties getMp() {
		return mp;
	}

	public void setMp(MemberProperties mp) {
		this.mp = mp;
	}

	public ManagedStatus getStatus() {
		return status;
	}

	public void setStatus(ManagedStatus status) {
		this.status = status;
	}

	public ManagedStatus getPreviousStatus() {
		return previousStatus;
	}

	public void setPreviousStatus(ManagedStatus previousStatus) {
		this.previousStatus = previousStatus;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	public boolean isScalable() {
		return isScalable;
	}

	public void setScalable(boolean isScalable) {
		this.isScalable = isScalable;
	}

	public int getMaxScalableRetries() {
		return maxScalableRetries;
	}

	public void setMaxScalableRetries(int maxScalableRetries) {
		this.maxScalableRetries = maxScalableRetries;
	}

	public int getAllowedScalePerComponent() {
		return allowedScalePerComponent;
	}

	public void setAllowedScalePerComponent(int allowedScalePerComponent) {
		this.allowedScalePerComponent = allowedScalePerComponent;
	}

	public int getMinScalableCount() {
		return minScalableCount;
	}

	public void setMinScalableCount(int minScalableCount) {
		this.minScalableCount = minScalableCount;
	}

	public int getMaxScalableCount() {
		return maxScalableCount;
	}

	public void setMaxScalableCount(int maxScalableCount) {
		this.maxScalableCount = maxScalableCount;
	}

	public Map<ManagedStatus, Integer> getRetries() {
		return retries;
	}

	public void setRetries(Map<ManagedStatus, Integer> retries) {
		this.retries = retries;
	}
	
}
