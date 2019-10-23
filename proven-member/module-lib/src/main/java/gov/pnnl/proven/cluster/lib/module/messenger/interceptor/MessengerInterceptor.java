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
package gov.pnnl.proven.cluster.lib.module.messenger.interceptor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Intercepted;
import javax.enterprise.inject.spi.Bean;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.slf4j.Logger;
import gov.pnnl.proven.cluster.lib.module.component.ModuleComponent;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Scalable;
import gov.pnnl.proven.cluster.lib.module.messenger.MetricsMessenger;
import gov.pnnl.proven.cluster.lib.module.messenger.MetricsReporter;
import gov.pnnl.proven.cluster.lib.module.messenger.ScheduledMessenger;
import gov.pnnl.proven.cluster.lib.module.messenger.ScheduledMessengerType;
import gov.pnnl.proven.cluster.lib.module.messenger.StatusMessenger;
import gov.pnnl.proven.cluster.lib.module.messenger.StatusReporter;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.Messenger;
import gov.pnnl.proven.cluster.lib.module.messenger.event.Reporter;
import gov.pnnl.proven.cluster.lib.module.messenger.exception.MessengerConfigurationException;

/**
 * Adds new messengers to the component at construction, based on provided
 * {@code Messenger} annotation member properties. The injection point must
 * implement the associated {@code Reporter} so that it can be registered with
 * the new messenger.
 * 
 * This only applies to {@code ManagedComponent}s. Other types will be ifnored.
 * 
 * @author d3j766
 * 
 */
@Messenger
@Interceptor
public class MessengerInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	Logger log;

	@Inject
	@Intercepted
	private Bean<?> intercepted;

	@PostConstruct
	public Object addMessengers(InvocationContext ctx) throws Exception {

		// OK to proceed
		ctx.proceed();

		Object result = ctx.getTarget();

		Class<?> ic = intercepted.getBeanClass();

		ModuleComponent mc = (ModuleComponent) result;

		Reporter reporter = null;
		if (Reporter.class.isAssignableFrom(mc.getClass())) {
			reporter = (Reporter) mc;
		}

		if (null == reporter) {
			throw new MessengerConfigurationException(
					"Messenger annotation used for non-reporter type " + ic.getSimpleName());
		}

		else {

			// Event type determines the type of scheduled messenger to
			// inject. More than one declared annotation of the same type is
			// an error. A check for each scheduled messenger type is made,
			// if found it is injected.
			Map<ScheduledMessengerType, Messenger> finalConfigs = new HashMap<>();
			Class<?> declared = ic;
			while (null != declared) {
				Map<ScheduledMessengerType, Messenger> tempConfigs = new HashMap<>();
				for (Messenger annotation : declared.getDeclaredAnnotationsByType(Messenger.class)) {

					ScheduledMessengerType smt = annotation.messengerType();
					if (tempConfigs.containsKey(smt)) {
						throw new MessengerConfigurationException("Ambiguous Messenger annotation used on "
								+ ic.getSimpleName() + " for " + smt.toString());
					} else {
						tempConfigs.put(smt, annotation);
					}
				}

				// Merge with final - do not overwrite existing/sub-class's
				// annotation.
				tempConfigs.forEach((key, value) -> finalConfigs.merge(key, value, (v1, v2) -> v1));

				declared = declared.getSuperclass();
			}

			// reset to base class
			declared = ic;

			// Inject Messenger(s)
			for (Entry<ScheduledMessengerType, Messenger> entry : finalConfigs.entrySet()) {

				ScheduledMessengerType messengerType = entry.getKey();
				ScheduledMessenger sm = null;

				try {

					switch (messengerType) {
					case StatusMessenger:

						Scalable scalable = declared.getDeclaredAnnotation(Scalable.class);
						sm = mc.getMessenger(StatusMessenger.class);
						addScheduleProperties(sm, entry.getValue());
						// Check if scalable and add properties
						if (null != scalable) {
							addScalableProperties((StatusMessenger) sm, scalable);
						}
						StatusReporter sr = StatusReporter.class.cast(reporter);
						sm.register(sr::reportStatus);
						break;

					case MetricsMessenger:
						sm = mc.getMessenger(MetricsMessenger.class);
						addScheduleProperties(sm, entry.getValue());
						MetricsReporter mr = MetricsReporter.class.cast(reporter);
						sm.register(mr::reportMetrics);
						break;

					default:
						throw new MessengerConfigurationException("Unsupported scheduled messenger type,"
								+ messengerType.toString() + ", used on " + ic.getSimpleName());
					}

					// Ensure a report supplier has been registered with
					// messenger
					if (null == sm.getReporter()) {
						throw new MessengerConfigurationException(messengerType + " missing report supplier");
					}

				} catch (ClassCastException ex) {
					throw new MessengerConfigurationException(
							"Reporter interface not implemented for " + messengerType.toString(), ex);
				}

			}

		}

		return result;
	}

	private void addScheduleProperties(ScheduledMessenger sm, Messenger m) {
		sm.setDelay(m.delay());
		sm.setTimeUnit(m.timeUnit());
		sm.setJitterPercent(m.jitterPercent());
	}

	private void addScalableProperties(StatusMessenger sm, Scalable s) {
		sm.setAllowedScalePerComponent(s.alowedPerComponent());
		sm.setMinScalableCount(s.minCount());
		sm.setMaxScalableCount(s.maxCount());
		sm.setMaxScalableRetries(s.retries());
	}
}
