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

import static gov.pnnl.proven.cluster.lib.member.MemberUtils.exCause;
import static gov.pnnl.proven.cluster.lib.module.util.LoggerResource.currentThreadLog;

import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;

import org.slf4j.Logger;

import gov.pnnl.proven.cluster.lib.module.component.annotation.TaskSchedule;

/**
 * Provides a fixed delay schedule to apply a task for a registered type.
 * Schedule properties are defined by {@code TaskSchedule} annotation at injection
 * point. Scheduled tasks must register their supplier of T.
 * 
 * @param T
 *            the type provided to the applied task
 * 
 * @see TaskSchedule
 * 
 * @author d3j766
 *
 */
@TaskSchedule
public abstract class ScheduledTask<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	Logger log;

	public enum ScheduleStatus {

		/**
		 * Messenger is executing its scheduled tasks.
		 */
		STARTED,

		/**
		 * Messenger's scheduled tasks are no longer being executed due to
		 * cancellation or failure.
		 */
		STOPPED;
	}

	public static final String SM_EXECUTOR_SERVICE = "concurrent/ScheduledTasks";

	@Resource(lookup = SM_EXECUTOR_SERVICE)
	public ManagedScheduledExecutorService scheduler;

	protected ScheduledFuture<?> scheduledFuture;

	/**
	 * @see {@link TaskSchedule#delay()}
	 */
	protected long delay;

	/**
	 * @see {@link TaskSchedule#timeUnit()}
	 */
	protected TimeUnit timeUnit;

	/**
	 * @see {@link TaskSchedule#jitterPercent()}
	 */
	protected int jitterPercent;

	/**
	 * @see {@link TaskSchedule#activateOnStartup()}
	 */
	protected boolean activateOnStartup;

	/**
	 * Registered supplier.
	 */
	protected Supplier<Optional<T>> supplier;

	/**
	 * Messenger status
	 */
	ScheduleStatus status = ScheduleStatus.STOPPED;

	public ScheduledTask() {
	}

	@PostConstruct
	public void initScheduler() {
		if (isActivateOnStartup()) {
			start();
		}
	}

	@PreDestroy
	public void destroyScheduler() {
		stop();
	}

	/**
	 * Starts the fixed delay scheduler. Tasks will continue to be run as long
	 * as error conditions or cancel exceptions are encountered.
	 */
	public void start() {

		synchronized (status) {

			if (status == ScheduleStatus.STOPPED) {

				applyJitter();
				scheduledFuture = scheduler.scheduleWithFixedDelay(() -> {

					log.debug(currentThreadLog("START SCHEDULER"));
					
					try {
						log.debug("Scheduled task started");
						if (hasRegisteredSupplier()) {
							apply(supplier.get());
						} else {
							log.debug("No registered supplier for task schedule");
						}
						log.debug("Scheduled task completed normally");
					} catch (Throwable e) {

						log.debug("Scheduled task exception has occurred: " + exCause(e));

						if (e instanceof CancellationException) {
							log.debug("Scheduled task execution has been cancelled: " + exCause(e));
							stop();
						}

					}
					finally {
						log.debug(currentThreadLog("START SCHEDULER"));
					}
				}, delay, delay, timeUnit);
			}
			status = ScheduleStatus.STARTED;
		}

	}

	@Deprecated
	private boolean isUnhandledException(Throwable e) {

		Throwable cause = e.getCause();
		if (null == cause) {
			cause = e;
		}
		//@formatter:off
		return (  
				(cause instanceof Error) || 
				(cause instanceof CancellationException) ||
				(cause instanceof RejectedExecutionException) ||
				(!(cause instanceof ExecutionException)) || 
				(!(cause instanceof InterruptedException))
			   );
		//@formatter:on
	}

	private boolean hasRegisteredSupplier() {
		return (null != supplier);
	}

	/**
	 * Shutdown of task scheduler and failure event recorded.
	 */
	public void stop() {

		log.debug("Task Scheduler stopping...");
		synchronized (status) {

			if (null != scheduledFuture) {
				scheduledFuture.cancel(true);
				scheduledFuture = null;
			}
			status = ScheduleStatus.STOPPED;
		}
		log.debug("Task scheduler stopped");
	}

	/**
	 * Registers supplier.
	 */
	public void register(Supplier<Optional<T>> supplier) {
		this.supplier = supplier;
	}

	/**
	 * Applies task for type T 
	 *  
	 * @param message
	 *            optional supplied type T
	 */
	protected abstract void apply(Optional<T> message);

	/**
	 * Will be applied at schedule start.
	 */
	synchronized private void applyJitter() {
		long duration = timeUnit.toMillis(delay);
		long min = Math.round(duration - ((jitterPercent / 100.0) * duration));
		long max = Math.round(duration + ((jitterPercent / 100.0) * duration));
		duration = ThreadLocalRandom.current().nextLong(min, max + 1);
		log.debug("MIN schedule delay: " + min);
		log.debug("MAX schedule delay: " + max);
		log.debug("New scheduled delay is: " + duration);
		this.delay = duration;
		this.timeUnit = TimeUnit.MILLISECONDS;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
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

	public boolean isActivateOnStartup() {
		return activateOnStartup;
	}

	public void setActivateOnStartup(boolean activateOnStartup) {
		this.activateOnStartup = activateOnStartup;
	}

}
