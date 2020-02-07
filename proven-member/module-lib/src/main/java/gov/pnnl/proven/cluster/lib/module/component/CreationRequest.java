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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import gov.pnnl.proven.cluster.lib.module.component.exception.InvalidCreationRequestException;

/**
 * Represents a request for the creation of a new ManagedComponent.
 * 
 * The class constructor will verify that the request is valid, if not an
 * exception will be thrown.
 * 
 * @see {@link #CreationRequest(Class, List)}
 * 
 * @author d3j766
 *
 */
public class CreationRequest {

	/**
	 * Component type to create
	 */
	private Class<? extends ManagedComponent> clazz;
	private Optional<UUID> scaleSource;

	/**
	 * List of configuration objects, if any.
	 */
	private List<Object> config;

	/**
	 * A request used to create a new managed component.
	 * 
	 * @param clazz
	 *            type of component to create.
	 * @param config
	 *            (Optional) list of configuration objects to apply post
	 *            creation.
	 * 
	 * @throws InvalidCreationRequestException
	 *             if provided configuration does not match configuration
	 *             expected for the provided component type, or provided
	 *             component type is null, or provided configuration list is
	 *             null.
	 */
	public CreationRequest(Class<? extends ManagedComponent> clazz, Object... config) {
		this(clazz, Optional.empty(), config);
	}

	/**
	 * A request used to create a new managed component.
	 * 
	 * @param clazz
	 *            type of component to create.
	 * 
	 * @param scaleSource
	 *            (Optional) the component identifier of the managed component
	 *            that triggered a scale request for creation of the new managed
	 *            component.
	 * @param config
	 *            (Optional) list of configuration objects to apply post
	 *            creation.
	 * 
	 * @throws InvalidCreationRequestException
	 *             if provided configuration does not match configuration
	 *             expected for the provided component type, or provided
	 *             component type is null, or provided scaleSource is null, or
	 *             provided configuration list is null.
	 */
	public CreationRequest(Class<? extends ManagedComponent> clazz, Optional<UUID> scaleSource, Object... config) {

		if (!isValidRequest(clazz, scaleSource, config)) {
			throw new InvalidCreationRequestException();
		} else {
			this.clazz = clazz;
			this.config = Arrays.asList(config);
		}

	}

	private boolean isValidRequest(Class<? extends ManagedComponent> clazz, Optional<UUID> scaleSource,
			Object[] config) {

		boolean ret = true;

		if ((null != clazz) && (null != scaleSource) && (null != config)) {

			List<Class<?>> expected = Creator.configuration(clazz);
			int expectedSize = expected.size();

			// List must be same size
			if (config.length != expectedSize) {
				ret = false;
			} else {

				if (!expected.isEmpty()) {
					for (int i = 0; i < expectedSize; i++) {
						if (!expected.get(i).equals(config[i].getClass())) {
							ret = false;
						}
					}
				}
			}
		}

		return ret;
	}

	/**
	 * @return the clazz
	 */
	public Class<? extends ManagedComponent> getClazz() {
		return clazz;
	}

	/**
	 * @return the scaleSource
	 */
	public Optional<UUID> getScaleSource() {
		return scaleSource;
	}

	/**
	 * @return the config
	 */
	public List<Object> getConfig() {
		return config;
	}

}
