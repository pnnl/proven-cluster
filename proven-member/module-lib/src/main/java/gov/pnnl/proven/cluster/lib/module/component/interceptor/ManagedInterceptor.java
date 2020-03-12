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

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Priority;
import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.Intercepted;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.interceptor.AroundConstruct;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.cluster.lib.module.component.ManagedComponent;
import gov.pnnl.proven.cluster.lib.module.component.TaskSchedule;
import gov.pnnl.proven.cluster.lib.module.component.annotation.CreatedBy;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Managed;
import gov.pnnl.proven.cluster.lib.module.component.annotation.ManagedAnnotationLiteral;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Scheduler;
import gov.pnnl.proven.cluster.lib.module.manager.ManagerComponent;
import gov.pnnl.proven.cluster.lib.module.module.ProvenModule;
import gov.pnnl.proven.cluster.lib.module.registry.EntryLocation;

/**
 * Verifies injection of a {@code Managed} component is being injected by a
 * {@code ProvenModule} or another {@code Managed} component that can be traced
 * back to a {@code ProvenModule}. An {@code InjectionException} is thrown if
 * this is not the case.
 * 
 * Ensures correct utilization of {@code Managed} annotation.
 * 
 * @author d3j766
 * 
 */
@Managed
@Interceptor
@Priority(value = Interceptor.Priority.APPLICATION)
public class ManagedInterceptor {

	static Logger log = LoggerFactory.getLogger(ManagedInterceptor.class);

	@Inject
	@Intercepted
	private Bean<?> intercepted;

	@Inject
	InjectionPoint ip;

	@AroundConstruct
	public Object verifyManagedComponent(InvocationContext ctx) throws Exception {

		log.debug("ManagedComponentInterceptor - BEFORE construction.");
		log.debug("Intercepted component :: " + intercepted.getBeanClass().getName());

		Class<?> ic = intercepted.getBeanClass();
		boolean moduleComponent = false;

		/**
		 * If a ProvenModule - no pre/post construct checks are necessary, this
		 * is root of tree
		 */
		if (ProvenModule.class.isAssignableFrom(ic)) {
			moduleComponent = true;
			log.debug("Managed interceptor for a Proven Module : " + ic.getSimpleName());
		}

		else {

			Bean<?> ipBean = ip.getBean();
			Class<?> ipClass = ipBean.getBeanClass();
			boolean isIpModule = false;
			boolean isIpManager = false;

			if ((null != ipBean) && (ProvenModule.class.isAssignableFrom(ipClass))) {
				isIpModule = true;
			}

			// Injecting ManagerComponent
			if (ManagerComponent.class.isAssignableFrom(ic)) {

				if ((null != ipBean) && (ProvenModule.class.isAssignableFrom(ipClass))) {
					isIpModule = true;
				}

				if (!isIpModule) {
					throw new InjectionException(
							"Injection point for a @Managed manager component must be a proven module");
				}
			}

			// Injecting non-manager component
			else {

				// Verify that it's is a managed component
				if (!ManagedComponent.class.isAssignableFrom(ic)) {
					throw new InjectionException("Intercepted bean must be managed component");
				}

				// Verify a manager component is requesting the new managed
				// component.
				// Bean<?> ipBean = ip.getBean();
				if ((null != ipBean) && (ManagerComponent.class.isAssignableFrom(ipClass))) {
					isIpManager = true;
				}

				// If not a manager, verify it is another managed component
				// performing the injection.
				if (!isIpManager) {
					Managed mc = ipClass.getAnnotation(Managed.class);
					if (null == mc) {
						throw new InjectionException(
								"Injection point for managed component is not a manager component or another managed component");
					}
				}

				// So far so good - check if ManagedBy restriction is in place
				CreatedBy mb = ic.getAnnotation(CreatedBy.class);
				if (null != mb) {
					List<Class<?>> restrictions = Arrays.asList(mb.value());
					if (!restrictions.isEmpty()) {
						if (!restrictions.contains(ipBean.getBeanClass())) {
							throw new InjectionException("Injection point for " + ic.toString()
									+ " not allowed due to ManagedBy restriction");
						}
					}
				}

				log.debug("ManagedComponent verified for:: " + ip.getBean().getBeanClass().getSimpleName());

			}
		}

		// OK to proceed
		Object result = ctx.proceed();

		/**
		 * Set location values for constructed component if it is not the module
		 * component. The module component, being the root component, has
		 * already been initialized and its location values are passed down the
		 * tree.
		 */
		if (!moduleComponent) {

			Object target = ctx.getTarget();
			ManagedComponent mc = (ManagedComponent) target;

			Managed managed = (Managed) ip.getQualifiers().stream()
					.filter((q) -> Managed.class.isAssignableFrom(q.getClass())).findAny().get();

			//@formatter:off
			mc.entryLocation(new EntryLocation(
					UUID.fromString(managed.memberId()), 
					UUID.fromString(managed.moduleId()), 
					UUID.fromString(managed.managerId()),
					UUID.fromString(managed.creatorId())));
			//@formatter:on

		}

		log.debug("ManagedComponentInterceptor - AFTER construction.");

		return result;
	}

}
