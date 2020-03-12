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
package gov.pnnl.proven.cluster.lib.module.component.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;

import gov.pnnl.proven.cluster.lib.module.component.ManagedStatus;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation;

/**
 * Indicates the annotated type is a scalable component. Provided member
 * properties are used to define it's scaling configuration.
 * 
 * @author d3j766
 *
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE })
public @interface Scalable {

	/**
	 * (Optional) The number of components allowed to be created as a result of
	 * a specific component's status value that indicates it can no longer
	 * support new task processing. Failed and/or busy states will trigger a
	 * scale operation to create a new component(s) of the same type.
	 * 
	 * Default is 1 per triggering component.
	 * 
	 * Allowed count must be greater then 0. If this is not the case an
	 * InvalidScalableConfigurationException will be thrown when Scalable
	 * component is created
	 * 
	 * see ManagedComponentStatus,
	 * {@link StatusOperation.Operation#RequestScale}
	 * 
	 */
	@Nonbinding
	int allowedCount() default 1;

	/**
	 * (Optional) Minimum number of {@link ManagedStatus#Online} components for
	 * this scalable type. A count below this setting will trigger a scale
	 * operation.
	 * 
	 * Default is 1.
	 * 
	 * Min count must be greater then 0 and it must be less then or equal to max
	 * count. If this is not the case an InvalidScalableConfigurationException
	 * will be thrown when Scalable component is created
	 * 
	 */
	@Nonbinding
	int minCount() default 1;

	/**
	 * (Optional) Indicates maximum number of {@link ManagedStatus#Online}
	 * components for this scalable type.
	 * 
	 * Default is 5.
	 * 
	 * Max count must be greater then 0 and it must be greater then or equal to
	 * min count. If this is not the case an
	 * InvalidScalableConfigurationException will be thrown when Scalable
	 * component is created
	 * 
	 */
	@Nonbinding
	int maxCount() default 5;

}
