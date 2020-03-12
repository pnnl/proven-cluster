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
package gov.pnnl.proven.cluster.lib.module.component.interceptor;

import static gov.pnnl.proven.cluster.lib.module.component.ManagedComponent.ComponentLock.CREATED_LOCK;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedComponent.ComponentLock.STATUS_LOCK;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

import org.slf4j.Logger;

import gov.pnnl.proven.cluster.lib.module.component.CreationRequest;
import gov.pnnl.proven.cluster.lib.module.component.CreationResponse;
import gov.pnnl.proven.cluster.lib.module.component.Creator;
import gov.pnnl.proven.cluster.lib.module.component.ManagedComponent;
import gov.pnnl.proven.cluster.lib.module.component.ManagedComponent.ComponentLock;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Managed;
import gov.pnnl.proven.cluster.lib.module.component.exception.InvalidCreationRequestException;

@Decorator
@Priority(value = Interceptor.Priority.APPLICATION)
@Dependent
public abstract class CreatorDecorator implements Creator {

	@Inject
	Logger log;

	@Inject
	@Delegate
	@Managed
	ManagedComponent mc;

	@PostConstruct
	public void statusDecoratorPostConstruct() {
		log.debug("Inside creator decorator post construct");
	}

//	@Override
//	public <T extends ManagedComponent> CreationResponse<T> create(CreationRequest<T> request) {
//
//		CreationResponse<T> ret;
//
//		try {
//			mc.acquireLockWait(STATUS_LOCK);
//			mc.acquireLockWait(CREATED_LOCK);
//
//			ret = mc.create(request);
//
//		} finally {
//			mc.releaseLock(CREATED_LOCK);
//			mc.releaseLock(STATUS_LOCK);
//		}
//
//		return ret;
//	}
//
//	@Override
//	public <T extends ManagedComponent> Optional<CreationResponse<T>> scale(CreationRequest<T> request) {
//
//		Optional<CreationResponse<T>> response = Optional.empty();
//
//		try {
//
//			mc.acquireLockWait(STATUS_LOCK);
//			mc.acquireLockWait(CREATED_LOCK);
//
//			/**
//			 * Get lock on scale candidate component.
//			 * 
//			 * Because the CANDIDATE_LOCK has already been acquired, the
//			 * candidate cannot be removed/destroyed (if it does exists) until
//			 * this lock has been released.
//			 */
//			if (!request.getScaleSource().isPresent()) {
//				throw new InvalidCreationRequestException(
//						"Creation request for scale operation was missing source candidate component identifier.");
//			} else {
//
//				// Proceed if the candidate still exists
//				Optional<ManagedComponent> scaleCandidateOpt = mc.getCreated(request.getScaleSource().get());
//				if (scaleCandidateOpt.isPresent()) {
//
//					ManagedComponent source = scaleCandidateOpt.get();
//					try {
//
//						source.acquireLockWait(STATUS_LOCK);
//
//						if (mc.validScaleCandidate(source)) {
//
//							response = mc.scale(request);
//
//							/**
//							 * Increment scaled count for source scale candidate
//							 * by the number of components created by the scale
//							 * operation provided by the response, if present.
//							 */
//							if (response.isPresent()) {
//								source.setScaledCount(source.getScaledCount() + response.get().createdCount());
//							}
//						}
//					} finally {
//						source.releaseLock(STATUS_LOCK);
//					}
//				}
//			}
//
//		} finally {
//			mc.releaseLock(CREATED_LOCK);
//			mc.releaseLock(STATUS_LOCK);
//		}
//
//		return response;
//	}
//
//	@Override
//	public void configure(List<Object> config) {
//
//		if (!Creator.validConfiguration(mc.getType(), config)) {
//			throw new IllegalArgumentException(
//					"Invalid configuration passed to configure method for component type: " + mc.getType());
//		}
//
//		// Configuration okay, perform configure
//		mc.configure(config);
//	}

}
