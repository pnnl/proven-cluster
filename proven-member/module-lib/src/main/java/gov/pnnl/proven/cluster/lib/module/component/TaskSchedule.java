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
import static gov.pnnl.proven.cluster.lib.member.MemberUtils.exCauseName;
import static gov.pnnl.proven.cluster.lib.module.util.LoggerResource.currentThreadLog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.event.Event;
import javax.enterprise.event.NotificationOptions;
import javax.inject.Inject;

import org.slf4j.Logger;

import gov.pnnl.proven.cluster.lib.member.MemberProperties;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Scheduler;
import gov.pnnl.proven.cluster.lib.module.messenger.RegistryReporter;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.ModuleRegistryAnnotationLiteral;
import gov.pnnl.proven.cluster.lib.module.messenger.event.MessageEvent;

/**
 * Provides a fixed delay task schedule. Schedule properties are defined by
 * {@code Scheduler} annotation at injection point.
 * 
 * 
 * @see Scheduler
 * 
 * @author d3j766
 *
 */
@Scheduler
public abstract class TaskSchedule implements RegistryReporter, Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	Logger log;

	MemberProperties mp = MemberProperties.getInstance();

	private enum ScheduleStatus {

		/**
		 * Scheduled tasks are executing.
		 */
		STARTED,

		/**
		 * Scheduled tasks are no longer being executed due to cancellation or
		 * failure.
		 */
		STOPPED;
	}

	@Inject
	protected Event<MessageEvent> eventInstance;

	/**
	 * (Optional) Schedule operator (i.e. owner of the scheduler)
	 */
	protected Optional<ManagedComponent> operatorOpt = Optional.empty();

	// Properties used by maintenance check to determine scheduler status.
	ScheduleStatus status = ScheduleStatus.STOPPED;
	int failureCount = 0;
	List<Throwable> failures = new ArrayList<>();

	public static final String SM_EXECUTOR_SERVICE = "concurrent/ScheduledTasks";

	@Resource(lookup = SM_EXECUTOR_SERVICE)
	public ManagedScheduledExecutorService scheduler;

	protected ScheduledFuture<?> scheduledFuture;

	@Resource(lookup = "concurrent/RequestExchange")
    javax.enterprise.concurrent.ManagedExecutorService threadPool;
	
	/**
	 * @see {@link Scheduler#delay()}
	 */
	protected long delay;

	/**
	 * @see {@link Scheduler#timeUnit()}
	 */
	protected TimeUnit timeUnit;

	/**
	 * @see {@link Scheduler#jitterPercent()}
	 */
	protected int jitterPercent;

	/**
	 * @see {@link Scheduler#activateOnStartup()}
	 */
	protected boolean activateOnStartup;

	// Registry reporting properties
	private MessageEvent reported = null;
	private int skippedReports = 0;
	private int maxSkippedBeforeReporting = mp.getTaskScheduleMaxSkippedEntryReports();
	private long registryOverdueMillis;

	public TaskSchedule() {
	}

	@PostConstruct
	public void initScheduler() {
		calculateRegistryOverdueMillis();
		if (isActivateOnStartup()) {
			start();
		}
	}

	@PreDestroy
	public void destroyScheduler() {
		stop();
	}

	@Override
	public int skippedReportsCount() {
		return skippedReports;
	}

	@Override
	public MessageEvent reported() {
		return reported;
	}

	@Override
	public int maxSkippedBeforeReporting() {
		return maxSkippedBeforeReporting;
	}

	@Override
	public void calculateRegistryOverdueMillis() {
		long delay = getTimeUnit().toMillis(getDelay());
		long maxJitter = (delay * (getJitterPercent() / 100));
		long maxDelay = delay + maxJitter;
		this.registryOverdueMillis = (maxSkippedBeforeReporting + 1) * maxDelay;
	}

	@Override
	public long registryOverdueMillis() {
		return registryOverdueMillis;
	}

	@Override
	public void notifyRegistry(MessageEvent event, boolean isAsync) {

		if ((isReportable(event)) || (skippedReports >= maxSkippedBeforeReporting)) {

			skippedReports = 0;
			reported = event;

			if (isAsync) {
			//eventInstance.select(new ModuleRegistryAnnotationLiteral() {
			//	}).fireAsync(event);
			
			eventInstance.select(new ModuleRegistryAnnotationLiteral() {
			}).fireAsync(event, NotificationOptions.ofExecutor(threadPool));
					
			} else {
				eventInstance.select(new ModuleRegistryAnnotationLiteral() {
				}).fire(event);
			}

		} else {
			skippedReports++;
		}
	}

	/**
	 * Starts the fixed delay scheduler. Tasks will continue to be run as long
	 * as an unmanaged error condition (this includes either an Error or
	 * unmanaged Exceptions) does not cause it to be stopped. An error condition
	 * triggers a fail StatusOperation.
	 */
	public void start() {

		synchronized (status) {
			if (status == ScheduleStatus.STOPPED) {
				failureCount = 0;
				failures.clear();
				applyJitter();
				scheduledFuture = scheduler.scheduleWithFixedDelay(() -> {
					log.debug(currentThreadLog("START SCHEDULER STARTED"));
					try {
						log.debug("Scheduled task started");
						apply();
						log.debug("Scheduled task completed normally");
					} catch (Throwable e) {
						if (e instanceof CancellationException) {
							log.debug("Scheduled task execution has been cancelled.");
						} else if ((e instanceof RejectedExecutionException) || (e instanceof ExecutionException)
								|| (e instanceof InterruptedException)) {
							exCause(e).printStackTrace();
							log.warn("Scheduled task managed execution exception: \n" + exCauseName(e));
						} else if ((e instanceof Error) || (e instanceof Exception)) {
							exCause(e).printStackTrace();
							log.error("Scheduled task execution encountered an unmanaged error condition: "
									+ exCauseName(e) + "\nScheduler: " + this.getClass() + " \nOperator:"
									+ this.operatorOpt.get().entryIdentifier());
							failureCount++;
							failures.add(e);
						}
					}

				}, delay, delay, timeUnit);
			}
			status = ScheduleStatus.STARTED;
		}

	}

	/**
	 * Controlled stop of task scheduler
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

	public void restart() {
		stop();
		start();
	}

	/**
	 * Registers operator.
	 */
	public void register(ManagedComponent operator) {
		this.operatorOpt = Optional.of(operator);
	}

	/**
	 * Applies task of type T
	 */
	protected abstract void apply();

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

	/**
	 * @return the failureCount
	 */
	public int getFailureCount() {
		return failureCount;
	}

	/**
	 * @return the failures
	 */
	public List<Throwable> getFailures() {
		return failures;
	}

}
