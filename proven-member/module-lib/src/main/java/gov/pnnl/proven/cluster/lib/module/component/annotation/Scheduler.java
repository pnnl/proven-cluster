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
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import javax.interceptor.InterceptorBinding;

import gov.pnnl.proven.cluster.lib.module.component.TaskSchedule;
import gov.pnnl.proven.cluster.lib.module.messenger.StatusSchedule;

/**
 * {@code TaskSchedule} qualifier. Includes members providing the schedule's
 * properties.
 * 
 * @author d3j766
 *
 * @see StatusSchedule
 * 
 */
@InterceptorBinding
@Qualifier
@Inherited
@Retention(RUNTIME)
@Target({ TYPE, FIELD })
public @interface Scheduler {
	
	public static final int SCHEDULER_DEFAULT_DELAY = 5;
	public static final String SCHEDULER_DEFAULT_TIME_UNIT = TimeUnit.SECONDS.toString();
	public static final int SCHEDULER_DEFAULT_JITTER_PERCENT = 10;
	public static final boolean SCHEDULER_DEFAULT_ACTIVATE_ON_STARTUP = true;

	/**
	 * (Optional) Fixed delay in specified {@link #timeUnit()} between report
	 * messages.
	 * 
	 * Default is {@link #SCHEDULER_DEFAULT_DELAY} for {@value #SCHEDULER_DEFAULT_TIME_UNIT}
	 */
	@Nonbinding
	long delay() default SCHEDULER_DEFAULT_DELAY;

	/**
	 * (Optional) {@code TimeUnit} for {@link #delay()} value.
	 * 
	 * Default is {@link #SCHEDULER_DEFAULT_TIME_UNIT}
	 */
	@Nonbinding
	TimeUnit timeUnit() default TimeUnit.SECONDS;

	/**
	 * (Optional) A controlled variance adjustment applied to the reporting
	 * schedule's fixed {@link #delay()} value. Variance is a +/- value that
	 * ranges from 0 to the provided percentage of the fixed delay.
	 * 
	 * Default is {@link #SCHEDULER_DEFAULT_JITTER_PERCENT}
	 */
	@Nonbinding
	int jitterPercent() default SCHEDULER_DEFAULT_JITTER_PERCENT;

	/**
	 * (Optional) If true, messenger component will be activated on startup.
	 * 
	 * Default is {@link #SCHEDULER_DEFAULT_ACTIVATE_ON_STARTUP}
	 */
	@Nonbinding
	boolean activateOnStartup() default SCHEDULER_DEFAULT_ACTIVATE_ON_STARTUP;

}
