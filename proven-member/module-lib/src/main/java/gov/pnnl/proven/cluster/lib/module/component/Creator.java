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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.enterprise.event.ObservesAsync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.cluster.lib.module.component.annotation.Configuration;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Scalable;
import gov.pnnl.proven.cluster.lib.module.component.exception.InvalidCreationRequestException;
import gov.pnnl.proven.cluster.lib.module.component.exception.MissingConfigureImplementationException;
import gov.pnnl.proven.cluster.lib.module.module.ModuleStatus;

public interface Creator {

	static Logger log = LoggerFactory.getLogger(Creator.class);

	/**
	 * Creates a ManagedComponent and returns a new CreationResponse<T>, using
	 * the provided CreationRequest.
	 * 
	 * <b>Note:</b> Creation requests will be honored even if the module's
	 * status is {@code ModuleStatus#Suspended} or
	 * {@code ModuleStatus#Shutdown}. The new component(s) status will
	 * immediately be set to {@code ManagedStatus#Offline} (for suspended) or
	 * {@code ManagedStatus#OutOfService} (for shutdown) at their next status
	 * reporting interval.
	 * 
	 * @throws InvalidCreationRequestException
	 *             if the provided creation request cannot be satisfied by the
	 *             ManagedComponent's Instance Provider.
	 * 
	 * @see ModuleStatus
	 */
	<T extends ManagedComponent> CreationResponse<T> create(CreationRequest<T> request);

	/**
	 * Creates a ManagedComponent using the provided CreationRequest. The
	 * request is forwarded to this components {@code ScaleSchedule} for execution.
	 * The call returns immediately after the request has been forwarded.
	 * 
	 * <b>Note:</b> Creation requests will not be honored if the module's
	 * status is not {@code ModuleStatus#Running}.
	 * 
	 * @throws InvalidCreationRequestException
	 *             if the provided creation request cannot be satisfied by the
	 *             ManagedComponent's Instance Provider.
	 * 
	 * @see ScaleSchedule, ModuleStatus
	 */
	 <T extends ManagedComponent> void createAsync(CreationRequest<T> request);
	 //void createAsync(CreationRequest<ManagedComponent> request);

	/**
	 * Creates a new ManagedComponent using the provided CreationRequest.
	 * 
	 * The CreationRequest must identify the source component responsible for
	 * triggering this scale operation. If source component is not provided, a
	 * InvalidCreationRequestException will be thrown.
	 * 
	 * The new component(s) will be added to the callers collection of created
	 * components.
	 * 
	 * @return Optionally a CreationResponse if the scale operation was invoked,
	 *         an empty Optional otherwise. The scale may not be invoked if
	 *         request is no longer valid.
	 * 
	 * @throws InvalidCreationRequestException
	 *             if scale source is missing from the CreationRequest
	 *
	 * @see CreationRequest, CreationResponse
	 * 
	 */
	<T extends ManagedComponent> Optional<CreationResponse<T>> scale(CreationRequest<T> request);

	/**
	 * Configures component using provided Object list.
	 * 
	 * Default is no configuration. Component's that require configuration must
	 * override this default method.
	 * 
	 * @param config
	 *            list of configuration Objects
	 * 
	 * @throws MissingConfigureImplementationException
	 *             if the provided Object list is not empty indicating a
	 *             configure method should be provided by the target component.
	 * @throws NullPointerException
	 *             if provided configuration list is null
	 */
	default void configure(List<Object> config) {
		if (!config.isEmpty()) {
			throw new MissingConfigureImplementationException();
		}
	}

	/**
	 * Returns the configuration for a managed component. Returns an empty list
	 * if component does not require configuration as part of its creation
	 * process. The list contains instances of the types specified in the
	 * component's {@link Configuration} annotation, if any.
	 * 
	 * Component's that require a configuration must override this method.
	 * 
	 * @see Configuration
	 *
	 */
	Object[] configuration();

	/**
	 * Indicates if the provided ManagedComponent type is configurable. That is,
	 * it has specified its configuration with a {@link Configuration}
	 * annotation.
	 * 
	 * @return true if component has specified a configuration, false otherwise.
	 * 
	 * @see Configuration
	 */
	static <T extends ManagedComponent> boolean configurable(Class<T> componentType) {

		boolean ret = false;

		Configuration configAnno = componentType.getAnnotation(Configuration.class);
		if (null != configAnno) {
			Object[] config = configAnno.value();
			if (config.length > 0) {
				ret = true;
			}
		}
		return ret;
	}

	/**
	 * Returns the provided component's configuration, if any.
	 * 
	 * @return the list of configuration types, as specified in a
	 *         {@link Configuration} annotation, for the component. List will be
	 *         empty if no configuration specified for the component.
	 * 
	 * @see Configuration
	 */
	static <T extends ManagedComponent> List<Class<?>> configuration(Class<T> componentType) {

		List<Class<?>> ret = new ArrayList<>();

		Configuration config = componentType.getAnnotation(Configuration.class);
		if (null != config) {
			ret = Arrays.asList(config.value());
		}

		return ret;
	}

	/**
	 * Determines if the provided object list is a valid configuration for the
	 * provided component type.
	 * 
	 * @param componentType
	 *            component type for provided configuration
	 * @param config
	 *            configuration for provided component type
	 * 
	 * @return true if configuration is valid for component, false otherwise.
	 */
	static <T extends ManagedComponent> boolean validConfiguration(Class<T> componentType, List<Object> config) {

		boolean ret = true;

		List<Class<?>> expected = configuration(componentType);
		int expectedSize = expected.size();

		// Lists must be same size
		if (config.size() != expectedSize) {
			ret = false;
		} else {
			if (!expected.isEmpty()) {
				for (int i = 0; i < expectedSize; i++) {
					if (!expected.get(i).equals(config.get(i).getClass())) {
						ret = false;
					}
				}
			}
		}

		return ret;
	}

	static <T extends ManagedComponent> boolean scalable(Class<T> componentType) {
		return (null != componentType.getDeclaredAnnotation(Scalable.class));
	}

	static <T extends ManagedComponent> int scaleAllowedCount(Class<T> componentType) {

		int ret = 0;

		Scalable scalable = componentType.getDeclaredAnnotation(Scalable.class);
		if (null != scalable) {
			ret = scalable.allowedCount();
		}

		return ret;
	}

	static <T extends ManagedComponent> int scaleMinCount(Class<T> componentType) {

		int ret = 0;

		Scalable scalable = componentType.getDeclaredAnnotation(Scalable.class);
		if (null != scalable) {
			ret = scalable.minCount();
		}

		return ret;
	}

	static <T extends ManagedComponent> int scaleMaxCount(Class<T> componentType) {

		int ret = 0;

		Scalable scalable = componentType.getDeclaredAnnotation(Scalable.class);
		if (null != scalable) {
			ret = scalable.maxCount();
		}

		return ret;
	}

	static <T extends ManagedComponent> boolean isValidScaleConfiguration(Class<T> componentType) {

		int allowedCount = scaleAllowedCount(componentType);
		int minCount = scaleMinCount(componentType);
		int maxCount = scaleMaxCount(componentType);
		boolean validAllowedCount = allowedCount > 0;
		boolean validMinCount = minCount > 0;
		boolean validMaxCount = maxCount > 0;
		boolean validMinMaxCounts = minCount <= maxCount;

		return (validAllowedCount && validMinCount && validMaxCount && validMinMaxCounts);
	}

}
