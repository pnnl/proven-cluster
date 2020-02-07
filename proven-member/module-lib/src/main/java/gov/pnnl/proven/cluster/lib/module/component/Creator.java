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

import gov.pnnl.proven.cluster.lib.module.component.annotation.Configuration;

public interface Creator {

	/**
	 * Creates and returns a new ManagedComponent using the provided
	 * CreationRequest.
	 */
	ManagedComponent create(CreationRequest request);

	/**
	 * Creates a new ManagedComponent using the provided CreationRequest. The
	 * CreationRequest must identify the source component responsible for
	 * triggering this scale operation. If source component is not provided, the
	 * request is ignored. The new component will be added to the callers
	 * collection of created components.
	 *
	 * @see CreationRequest#CreationRequest(Class, java.util.Optional,
	 *      Object...)
	 */
	void scale(CreationRequest request);

	/**
	 * Asynchronous creation a new ManagedComponent using the provided
	 * CreationRequest. The request is submitted and method returns immediately.
	 * The new component will be added to the callers collection of created
	 * components.
	 */
	void requestCreate(CreationRequest request);

	/**
	 * Generates a CreationRequest that can be used to create a new
	 * ManagedComponent.
	 * 
	 * @return the CreationRequest
	 */
	CreationRequest requestCreate();

	/**
	 * Configures component using the information provided in the
	 * CreationRequest.
	 * 
	 * @param request
	 *            the CreationRequest which includes configuration information
	 */
	void configure(CreationRequest request);

	/**
	 * Returns the configuration for a managed component. Returns an empty list
	 * if component does not require configuration as part of its creation
	 * process. The list contains instances of the types specified in the
	 * component's {@link Configuration} annotation, if any. Default is no
	 * configuration; override to provide a component's configuration.
	 * 
	 * @see Configuration
	 *
	 */
	default List<Object> configuration() {
		return new ArrayList<Object>();
	}

	/**
	 * Indicates if the provided ManagedComponent type is configurable. That is,
	 * it has specified its configuration with a {@link Configuration}
	 * annotation.
	 * 
	 * @return true if component has specified a configuration, false otherwise.
	 * 
	 * @see Configuration
	 */
	static boolean configurable(Class<? extends ManagedComponent> componentType) {

		Configuration config = componentType.getAnnotation(Configuration.class);

		return (null != config);
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
	static List<Class<?>> configuration(Class<? extends ManagedComponent> componentType) {

		List<Class<?>> ret = new ArrayList<>();

		Configuration config = componentType.getAnnotation(Configuration.class);
		if (null != config) {
			ret = Arrays.asList(config.value());
		}

		return ret;
	}
}
