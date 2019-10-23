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
package gov.pnnl.proven.cluster.lib.module.messenger;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Qualifier;

import org.slf4j.Logger;

import gov.pnnl.proven.cluster.lib.module.component.ModuleComponent;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.Messenger;
import gov.pnnl.proven.cluster.lib.module.messenger.event.ComponentEvent;

/**
 * Base class for scheduled messengers. Provides a fixed delay schedule for
 * sending message reports.
 * 
 * @author d3j766
 *
 */
public abstract class ScheduledMessenger extends ModuleComponent {

	@Inject
	Logger log;

	public static final String SM_EXECUTOR_SERVICE = "concurrent/ScheduledMessenger";

//	@Resource(lookup = SM_EXECUTOR_SERVICE)
//	protected ManagedScheduledExecutorService scheduler;

	@Inject
	protected Event<ComponentEvent> ceProvider;

	/**
	 * @see {@link Messenger#delay()}
	 */
	protected int delay;

	/**
	 * @see {@link Messenger#timeUnit()}
	 */
	protected TimeUnit timeUnit;

	/**
	 * @see {@link Messenger#jitterPercent()}
	 */
	protected int jitterPercent;

	/**
	 * Registered supplier of message content.
	 */
	protected Supplier<ComponentEvent> reporter;

	ScheduledMessenger() {
	}

	@PostConstruct
	public void init() {
	}

	@PreDestroy
	public void destroy() {
		stop();
	}

	
	/**
	 * Starts the fixed delay scheduler.  
	 */
	protected void start() {
//		scheduler.scheduleWithFixedDelay(() -> {
//			ComponentEvent event = reporter.get();
//			send(event);
//		}, delay, delay, timeUnit);
	}

	protected void stop() {
		//scheduler.shutdown();
	}

	/**
	 * Registers the provided reporter as the message content supplier.
	 * 
	 * @param reporter
	 *            message provider
	 * 
	 */
	public void register(Supplier<ComponentEvent> reporter) {
		this.reporter = reporter;
	}

	/**
	 * Fires a new event message asynchronously.
	 * 
	 * @param event
	 *            reported message content
	 * @param qualifiers
	 *            message qualifiers
	 */
	protected void send(ComponentEvent event, Qualifier... qualifiers) {
		send(event, true, qualifiers);
	}

	/**
	 * Fires a new event message. Message will be sent synchronously, unlsess
	 * isAsync is true.
	 * 
	 * @param event
	 *            reported message content
	 * @param isAsync
	 *            if true, message is sent asynchronously
	 * @param qualifiers
	 *            message qualifiers
	 */
	protected void send(ComponentEvent event, boolean isAsync, Qualifier... qualifiers) {

	}

	/**
	 * Dependent on implementation, their report content and messaging requirements. 
	 * 
	 * @param event reported message content
	 */
	protected abstract void send(ComponentEvent event);

	public Supplier<ComponentEvent> getReporter() {
		return reporter;
	}

	public void setReporter(Supplier<ComponentEvent> reporter) {
		this.reporter = reporter;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public int getJitterPercent() {
		return jitterPercent;
	}

	public void setJitterPercent(int jitterPercent) {
		this.jitterPercent = jitterPercent;
	}

}
