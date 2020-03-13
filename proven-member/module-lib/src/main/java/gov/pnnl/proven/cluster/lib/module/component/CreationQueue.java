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
package gov.pnnl.proven.cluster.lib.module.component;

import static gov.pnnl.proven.cluster.lib.module.component.Creator.scalable;
import static gov.pnnl.proven.cluster.lib.module.component.Creator.scaleMaxCount;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import gov.pnnl.proven.cluster.lib.module.component.annotation.Scalable;

/**
 * Manages creation requests for a ManagedComponent.
 * 
 * Simple wrapper around BlockingQueue. For scale requests, enforces a limit on
 * the number of scale requests that can be in the queue at any one time for a
 * creator and it's scalable candidate type. {@link Scalable#maxCount()} of the
 * scalable candidate is the limit for a creator. Non-scalable requests are not
 * limited.
 *
 * @param T
 *            type of ManagedComponent queued for creation
 * 
 * @author d3j766
 *
 *
 */
public class CreationQueue<T extends ManagedComponent> {

	private Map<SimpleEntry<UUID, Class<T>>, Integer> scaleRequests = new HashMap<>();

	private BlockingQueue<SimpleEntry<UUID, CreationRequest<T>>> requestQueue = new LinkedBlockingQueue<>();

	public CreationQueue() {
	}

	/**
	 * Adds the provided request to the request queue and updates the creator's
	 * count for the component type being created. If addition will violate
	 * scale limit, the request is not added and method returns false.
	 * 
	 * @param creator
	 *            identifier of component making the request
	 * @param cr
	 *            the CreationRequest
	 * @return true if the request was added, false otherwise
	 */
	public boolean addRequest(UUID creator, CreationRequest<T> cr) {

		boolean ret = false;

		Class<T> sourceType = cr.getSubtype();

		boolean validRequest = true;

		if (scalable(sourceType)) {

			synchronized (scaleRequests) {
				int scaleMaxCount = scaleMaxCount(sourceType);
				int requestCount = 0;
				SimpleEntry<UUID, Class<T>> sre = new SimpleEntry<>(creator, sourceType);
				if (!scaleRequests.containsKey(sre)) {
					scaleRequests.put(sre, 0);
				} else {
					requestCount = scaleRequests.get(sre);
				}

				if (requestCount >= scaleMaxCount) {
					validRequest = false;
				} else {
					scaleRequests.put(sre, requestCount + 1);
				}
			}
		}

		if (validRequest) {
			ret = requestQueue.add(new SimpleEntry<UUID, CreationRequest<T>>(creator, cr));
		}

		return ret;

	}

	public CreationRequest<T> removeRequest() throws InterruptedException {

		// Blocks if queue is empty
		SimpleEntry<UUID, CreationRequest<T>> ret = requestQueue.take();
		Class<T> sourceType = ret.getValue().getSubtype();

		synchronized (scaleRequests) {

			if (scalable(sourceType)) {
				SimpleEntry<UUID, Class<T>> sre = new SimpleEntry<>(ret.getKey(), ret.getValue().getSubtype());
				int requestCount = scaleRequests.get(sre);
				scaleRequests.put(sre, requestCount--);
			}
		}

		return ret.getValue();
	}

}
