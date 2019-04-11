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

import java.util.Arrays;
import java.util.List;
import javax.ejb.Schedule;
import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.interceptor.AroundConstruct;
import javax.interceptor.Interceptor;
import javax.interceptor.Interceptors;
import javax.interceptor.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.pnnl.proven.cluster.lib.module.component.ComponentManager;
import gov.pnnl.proven.cluster.lib.module.component.ProvenComponent;
import gov.pnnl.proven.cluster.lib.module.component.annotation.ManagedBy;
import gov.pnnl.proven.cluster.lib.module.component.annotation.ManagedComponent;

/**
 * Verifies injection of a {@code ManagedComponent} is a {@code ProvenComponent}
 * and it's being requested by a {@code ComponentManager} or another
 * {@code ManagedComponent}. An {@code InjectionException} is thrown if this is
 * not the case.
 * 
 * @author d3j766
 * 
 */
@ManagedComponent
@Interceptor
public class ManagedComponentInterceptor {

	static Logger log = LoggerFactory.getLogger(ManagedComponentInterceptor.class);

	@AroundConstruct
	public Object verifyInjection(InvocationContext ctx) throws Exception {

		log.debug("ManagedComponentInterceptor - BEFORE construction.");

		// Determine if InjectionPoint has been provided. If not, throw
		// exception.
		InjectionPoint ip = null;
		for (Object obj : ctx.getParameters()) {
			if (obj instanceof InjectionPoint) {
				ip = (InjectionPoint) obj;
			}
		}
		if (null == ip) {
			throw new InjectionException("Injection point missing in ManagedComponent constructor");
		}

		// Verify that it's is a ProvenComponent.
		Class<?> clazz = Class.forName(ip.getType().getTypeName());
		if (!ProvenComponent.class.isAssignableFrom(clazz)) {
			throw new InjectionException("The managed component must be a ProvenComponent type");
		}

		// Verify a ComponentManager is requesting the new managed component.
		boolean isComponentManager = false;
		Bean<?> ipBean = ip.getBean();
		if ((null != ipBean) && (ComponentManager.class.isAssignableFrom(ipBean.getBeanClass()))) {
			isComponentManager = true;
		}

		// If not a component manager, verify it is a managed component
		// performing the injection
		if (!isComponentManager) {
			ManagedComponent mc = ipBean.getBeanClass().getAnnotation(ManagedComponent.class);
			if (null == mc) {
				throw new InjectionException(
						"Injection point for managed component is not from a ComponentManager or another managed component");
			}
		}

		// So far so good - check if ManagedBy restriction is in place
		// Class<?> clazz = Class.forName(ip.getType().getTypeName());
		ManagedBy mb = clazz.getAnnotation(ManagedBy.class);
		if (null != mb) {
			List<Class<?>> restrictions = Arrays.asList(mb.value());
			if (!restrictions.isEmpty()) {
				if (!restrictions.contains(ipBean.getBeanClass())) {
					throw new InjectionException(
							"Injection point for " + clazz.toString() + " not allowed due to ManagedBy restriction");
				}
			}
		}

		// OK to proceed
		Object result = ctx.proceed();

		log.debug("ManagedComponentInterceptor - AFTER construction.");

		return result;
	}

}
