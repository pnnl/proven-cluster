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

import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Busy;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.CheckedOffline;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Offline;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Online;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Ready;

import gov.pnnl.proven.cluster.lib.module.component.ManagedStatus;

/**
 * Represents possible status values for a ProvenModule.
 * 
 * @see ProvenModule
 * 
 * @author d3j766
 *
 */
public enum ModuleStatus {

	/**
	 * Indicates the module has been deployed, but has not yet been activated.
	 * This is the starting state for a module.
	 * 
	 */
	Deployed,

	/**
	 * Indicates the module has successfully been started and is running and
	 * operating normally.
	 */
	Running,

	/**
	 * Indicates the module is {@link #Running}, however a majority of its
	 * sub-components are marked as {@link ManagedStatus#Busy} or
	 * {@link ManagedStatus#CheckedOffline}. For this case a module is
	 * considered as Occupied, meaning it and is sub-components will not be
	 * considered as candidates for new work until its status reverts back to
	 * {@link #Running}.
	 */
	Occupied,

	/**
	 * Indicates the module has been suspended. All associated components are or
	 * will be deactivated.
	 */
	Suspended,

	/**
	 * Indicates the module has been shutdown. All associated components are or
	 * will be removed from service. This is a terminal state.
	 */
	Shutdown,

	/**
	 * Indicates status of the module is not known based on its current
	 * ManagedStatus value. This may be due to its ManagedStatus being
	 * in-transition or failed retries are being attempted. The status will not
	 * be known until the transition or retries have completed.
	 * 
	 * @see ManagedStatus
	 */
	Unknown;

	/**
	 * Determines the module status, which is based on the provided ManagedStatus.
	 * 
	 * @param ms
	 *            the ManagedStatus
	 *            
	 * @return the ModuleStatus
	 */
	public static ModuleStatus fromManagedStatus(ManagedStatus ms) {

		ModuleStatus ret = Unknown;

		if (!ManagedStatus.isRecoverable(ms)) {
			ret = Shutdown;
		} else if (Offline == ms) {
			ret = Suspended;
		} else if (Online == ms) {
			ret = Running;
		} else if (Busy == ms || CheckedOffline == ms) {
			ret = Occupied;
		} else if (Ready == ms) {
			ret = Deployed;
		}

		return ret;
	}

}
