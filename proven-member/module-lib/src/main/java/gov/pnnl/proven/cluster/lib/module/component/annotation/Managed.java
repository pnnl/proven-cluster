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
/**
 * 
 */
package gov.pnnl.proven.cluster.lib.module.component.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import javax.interceptor.InterceptorBinding;

/**
 * Identifies the bean as a managed component. Meaning, the component's creation
 * and destruction must be performed through the {@code ProvenModule} or another
 * {@code Managed} component that can be traced back to the
 * {@code ProvenModule}.
 * 
 * This may be further restricted by the use of {@code ManagedBy} to identify a
 * restricted list of {@code ManagerComponent}(s) that are allowed to serve as
 * as managers of the new {@code Managed} component.
 * 
 * Annotation member values make up the {@code EntryLocation} for the managed
 * component. Values are required for component construction, providing its
 * location in the cluster. If annotation is used as an event message qualifier,
 * the member values are unused.
 * 
 * @author d3j766
 *
 * @see ProvenModule, ManagerComponent, ManagedBy, EntryLocation
 * 
 */
@Documented
@Inherited
@Qualifier
@InterceptorBinding
@Retention(RUNTIME)
@Target({ TYPE, FIELD, METHOD, PARAMETER })
public @interface Managed {

	/**
	 * (Optional) Identifies the {@code EntryLocation#MEMBER} coordinate value
	 * for this managed component. This must be a string representation of a
	 * {@code UUID} value.
	 * 
	 * see EntryLocation, UUID
	 */
	@Nonbinding
	String memberId() default ManagedAnnotationLiteral.UNUSED;

	/**
	 * (Optional) Identifies the {@code EntryLocation#MODULE} coordinate value
	 * for this managed component. This must be a string representation of a
	 * {@code UUID} value.
	 * 
	 * see EntryLocation
	 */
	@Nonbinding
	String moduleId() default ManagedAnnotationLiteral.UNUSED;

	/**
	 * (Optional) Identifies the {@code EntryLocation#MANAGER} coordinate value
	 * for this managed component. This must be a string representation of a
	 * {@code UUID} value.
	 * 
	 * see EntryLocation
	 */
	@Nonbinding
	String managerId() default ManagedAnnotationLiteral.UNUSED;

	/**
	 * (Optional) Identifies the {@code EntryLocation#CREATOR} coordinate value
	 * for this managed component. This must be a string representation of a
	 * {@code UUID} value.
	 * 
	 * see EntryLocation
	 */
	@Nonbinding
	String creatorId() default ManagedAnnotationLiteral.UNUSED;

}
