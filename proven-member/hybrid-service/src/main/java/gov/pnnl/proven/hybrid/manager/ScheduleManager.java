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

package gov.pnnl.proven.hybrid.manager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.NoMoreTimeoutsException;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.pnnl.proven.hybrid.concept.MaintenanceSchedule;
import gov.pnnl.proven.hybrid.concept.NativeSourceSchedule;
import gov.pnnl.proven.hybrid.concept.ProductSchedule;
import gov.pnnl.proven.hybrid.concept.Schedule;
import gov.pnnl.proven.hybrid.concept.ConceptUtil.ScheduleType;
import gov.pnnl.proven.hybrid.util.Consts;
import gov.pnnl.proven.hybrid.util.Utils;
import gov.pnnl.proven.hybrid.util.Consts.MaintenanceStatus;

/**
 * ScheduleManager is responsible for managing harvest schedules and submitting to harvest manager
 * for provenance collection.
 */
@Singleton
@LocalBean
@DependsOn(value = "PropertiesManager")
public class ScheduleManager {

	private static Log log = LogFactory.getLog(ScheduleManager.class);

	private static final String REST_WORK_COMPONENT = "RestDataExchange";
	private static final String DEFAULT_TIMER_SCHEDULE = "*/5:*:*:*:*:*:*";

	/**
	 * Used to create and control timers.
	 */
	@Resource
	private TimerService ts;

	@EJB
	private PropertiesManager pm;

	@EJB
	private HarvestManager hm;

	/**
	 * List of schedules under management of the ScheduleManager
	 */
	private List<Schedule> schedules = new ArrayList<Schedule>();

	/**
	 * Initializes ScheduleManager. By default, all schedules are disabled.
	 */
	@PostConstruct
	public void initialize() {

		// TODO
		// Read in existing schedules from repsitory for activation

		log.debug("ScheduleManager Initialized...");
	}

	public List<Schedule> getSchedules() {
		return this.schedules;
	}

	public void setSchedules(List<Schedule> newSchedules) {
		setSchedules(newSchedules, false);
	}

	public String changeSchedule(String name, String scheduleStr) {

		String ret = null;

		for (Schedule schedule : schedules) {

			if (schedule.getName().equals(name)) {
				schedule.setTimerSchedule(scheduleStr);
				if (isScheduleActive(schedule.getName())) {
					stop(schedule);
					start(schedule);
				}
				ret = "Schedule changed (and restarted if it was active) " + Consts.NL
						+ schedule.getName() + " now using new schedule :: " + scheduleStr;
				break;
			}
		}

		if (null == ret) {
			ret = "Schedule NOT found";
		}

		return ret;
	}

	public void setSchedules(List<Schedule> newSchedules, boolean init) {

		if (!init) {

			log.debug("--------------------------------");
			log.debug("NEW SCHEDULES");
			for (Schedule schedule : newSchedules) {
				log.debug(schedule.getName() + " :: " + schedule.getTimerSchedule());
			}
			log.debug("--------------------------------");

			for (Schedule schedule : schedules) {

				// Stop schedules that are no longer current
				// New schedules being added are not started automatically
				if (!newSchedules.contains(schedule)) {
					log.debug("Schedule NOT exists :: " + schedule.getName());
					log.debug("!!!!!!!!!!!!!!!!!!!!!  stopping schedule :: " + schedule.getName());
					stop(schedule);
					log.debug("Schedule stopped :: " + schedule.getName());
				}

				// Update schedules that have changed
				else {
					String newSchedule = newSchedules.get(newSchedules.indexOf(schedule))
							.getTimerSchedule();
					String oldSchedule = schedule.getTimerSchedule();
					log.debug("Schedule exists :: " + schedule.getName());
					log.debug("Old schedule :: " + oldSchedule);
					log.debug("New schedule :: " + newSchedule);
					if (!oldSchedule.equals(newSchedule)) {
						log.debug("!!!!!!!!!!!!!!!!!!!!!  updating schedule :: "
								+ schedule.getName());
						schedule.setTimerSchedule(newSchedule);
						stop(schedule);
						start(schedule);
						log.debug("Schedule updated :: " + schedule.getName()
								+ " :: Using new schedule :: " + newSchedule);

					}
				}
			}
		}
		this.schedules = newSchedules;
	}

	public void resetMaintenanceStatus() {
		for (Schedule schedule : getSchedules()) {
			if (schedule instanceof MaintenanceSchedule) {
				MaintenanceSchedule mSchedule = (MaintenanceSchedule) schedule;
				resetMaintenanceStatus(mSchedule);
			}
		}
	}

	public void resetMaintenanceStatus(MaintenanceSchedule schedule) {
		schedule.setStatus(MaintenanceStatus.OFF.toString());
		schedule.setStatusTime(new Date());
		schedule.setScheduledAttempts(0);
	}

	public String getMaintenanceStatus() {

		String ret = "";

		for (Schedule schedule : getSchedules()) {

			if (schedule instanceof MaintenanceSchedule) {
				MaintenanceSchedule mSchedule = (MaintenanceSchedule) schedule;

				ret += Consts.NL;
				ret += "-----------------------------------" + Consts.NL;
				ret += "Name: " + schedule.getName() + Consts.NL;
				ret += "Status: " + mSchedule.getStatus().toString() + Consts.NL;
				ret += "Status DTM: " + mSchedule.getStatusTime().toString() + Consts.NL;
				ret += "Scheduled Attempts: " + mSchedule.getScheduledAttempts() + Consts.NL;
				ret += "-----------------------------------" + Consts.NL;
				ret += Consts.NL;
			}
		}

		return ret;
	}

	public boolean isMaintenanceOn() {

		boolean ret = false;
		for (Schedule schedule : getSchedules()) {
			if (schedule instanceof MaintenanceSchedule) {
				MaintenanceSchedule mSchedule = (MaintenanceSchedule) schedule;
				if ((mSchedule.getStatus() == MaintenanceStatus.ON.toString())) {
					ret = true;
					break;
				}
			}
		}

		return ret;
	}

	/**
	 * Callback for schedule timer expirations. Determines the EJB timer, by name, and calls work
	 * method from associated SLSB.
	 * 
	 * For each Schedule there should be a SLSB having name equal to Schedule.workComponent value,
	 * and an asynchronous doWork(Schedule) method which will be called.
	 * 
	 * If application currently has scheduled maintenance running, no new non-maintenance work will
	 * be invoked.
	 * 
	 * @param timer
	 *            ejb timer
	 */
	// @SuppressWarnings("unused")
	@Timeout
	private void doWork(Timer timer) {

		log.debug("INSIDE Schedule timeout for :: " + timer.getInfo());
		String timerName = (String) timer.getInfo();

		boolean isMaintenanceOn = isMaintenanceOn();
		boolean invokeSchedule = false;

		for (Schedule schedule : schedules) {

			if (schedule.getName().equals(timerName)) {

				if (schedule instanceof MaintenanceSchedule) {
					MaintenanceSchedule mSchedule = (MaintenanceSchedule) schedule;

					if (mSchedule.getStatus() == MaintenanceStatus.ON.toString()) {

						if (mSchedule.getScheduledAttempts() < Consts.MAX_SCHEDULED_MAINTENANCE_ATTEMPTS) {
							mSchedule.setScheduledAttempts(mSchedule.getScheduledAttempts() + 1);
							invokeSchedule = true;
						} else {
							mSchedule.setStatus(MaintenanceStatus.FAIL.toString());
							mSchedule.setStatusTime(new Date());
							mSchedule.setScheduledAttempts(0);
							isMaintenanceOn = isMaintenanceOn();
						}
					} else {
						Long currenTimeMillis = new Date().getTime();
						Long statusTimeMillis = mSchedule.getStatusTime().getTime();
						Long intervalMillis = currenTimeMillis - statusTimeMillis;
						Integer intervalHours = Utils.getHoursFromMillis(intervalMillis);

						// TODO REMOVE This is testing only
						// intervalHours = 2;

						if (intervalHours >= Consts.MAX_MAINTENANCE_INTERVAL_HOURS) {
							mSchedule.setStatus(MaintenanceStatus.ON.toString());
							mSchedule.setScheduledAttempts(1);
							invokeSchedule = true;
							isMaintenanceOn = true;
						}
					}
				}

				else {
					if (!isMaintenanceOn) {
						invokeSchedule = true;
					}
				}

				if (invokeSchedule) {
					log.debug("INVOKING COMPONENT :: " + timer.getInfo());

					// TTL setting check may remove timer
					// Accessing timer after this call may produce a warning message in log
					hm.startSchedule(schedule);

				}
			}
		}
		log.debug("LEAVING Schedule timeout");
	}

	private void invokeScheduleMethod(String methodName, Schedule schedule) {

		Object ejb = Utils.lookupEjb(REST_WORK_COMPONENT);

		if (ejb != null) {
			try {
				Method method = ejb.getClass().getMethod(methodName, Schedule.class);
				method.invoke(ejb, schedule);
				log.debug(methodName + " invoked for ejb :: " + REST_WORK_COMPONENT
						+ ", for schedule " + schedule.getName());
			} catch (Exception e) {
				log.error(methodName + " method not found or invocation failed for schedule :: "
						+ schedule.getName());
				e.printStackTrace();
			}
		} else {
			log.error("Failed to lookup EJB :: " + REST_WORK_COMPONENT);
		}
	}

	/**
	 * Removes ejb timer, by name
	 * 
	 * @param timer
	 *            name of timer to remove
	 */
	private boolean removeTimer(String timer) {
		boolean ret = false;

		try {

			if (ts.getTimers() != null) {
				for (Timer t : ts.getTimers()) {
					if (t.getInfo().equals(timer)) {
						t.cancel();
						ret = true;
					}
				}
			}
		} catch (IllegalArgumentException e) {
			log.error(e);
		} catch (NoSuchObjectLocalException e) {
			log.error(e);
		} catch (IllegalStateException e) {
			log.error(e);
		} catch (EJBException e) {
			log.error(e);
		}
		return ret;
	}

	/**
	 * Adds a new ejb timer. Timers created are by default non-persistent.
	 * 
	 * @param schedule
	 *            string representation of a ScheduleExpression. All attributes should be delimited
	 *            by a colon. All attributes should be included, even if empty. Attributes should be
	 *            specified in the following order: <br>
	 *            <ol>
	 *            <li>second</li>
	 *            <li>minute</li>
	 *            <li>hour</li>
	 *            <li>dayOfMonth</li>
	 *            <li>month</li>
	 *            <li>dayOfWeek</li>
	 *            <li>year</li>
	 *            </ol>
	 * <br>
	 *            For example the following is a schedule for every 15 minutes for the hours of 1am
	 *            and 2am, every Friday:<br>
	 *            <b>:0/15:1,2:::Fri:<b>
	 * 
	 * @see java documentation for ScheduleExpression
	 * 
	 * 
	 * @param timer
	 *            name of timer to add
	 * 
	 * @return returns true if timer was created, false otherwise.
	 */
	private boolean addTimer(String schedule, String timer) {

		boolean ret = false;

		try {

			if (schedule == null) {
				throw new IllegalArgumentException("Invalid Schedule");
			}

			String[] attributes = schedule.split(":", 7);

			// Simple check to see if the correct number of attributes were
			// provided.
			if (attributes.length == 7) {
				ScheduleExpression se = new ScheduleExpression();
				se.second(attributes[0]);
				se.minute(attributes[1]);
				se.hour(attributes[2]);
				se.dayOfMonth(attributes[3]);
				se.month(attributes[4]);
				se.dayOfWeek(attributes[5]);
				se.year(attributes[6]);
				ts.createCalendarTimer(se, new TimerConfig(timer, false));
				ret = true;
			}
		} catch (IllegalArgumentException e) {
			log.error(e);
		} catch (IllegalStateException e) {
			log.error(e);
		} catch (EJBException e) {
			log.error(e);
		}

		return ret;
	}

	/**
	 * Determines if schedule has been started.
	 * 
	 * @param name
	 *            name of schedule.
	 * 
	 * @return true if schedule has been started, false otherwise.
	 */
	public boolean isScheduleActive(String name) {
		boolean ret = false;

		try {
			if (ts.getTimers() != null) {
				for (Timer t : ts.getTimers()) {
					if (t.getInfo().equals(name)) {
						t.getNextTimeout();
						ret = true;
					}
				}
			}
		} catch (NoSuchObjectLocalException e) {
			log.info(e);
		} catch (NoMoreTimeoutsException e) {
			log.info(e);
		} catch (IllegalStateException e) {
			log.error(e);
		} catch (EJBException e) {
			log.error(e);
		}
		return ret;
	}

	/**
	 * Provided list of schedules are started.
	 * 
	 * @param schedules
	 *            list of schedules to enable
	 */
	public void start(List<Schedule> schedules) {
		if (schedules != null) {
			for (Schedule schedule : schedules) {
				start(schedule);
			}
		}
	}

	/**
	 * Provided list of schedules are stopped.
	 * 
	 * @param schedules
	 *            list of schedules to disable
	 */
	public void stop(List<Schedule> schedules) {
		if (schedules != null) {
			for (Schedule schedule : schedules) {
				stop(schedule);
			}
		}
	}

	/**
	 * All schedules are started.
	 */
	public void start() {
		List<Schedule> schedules = new ArrayList<Schedule>();
		for (Schedule schedule : getSchedules()) {
			schedules.add(schedule);
		}
		start(schedules);
	}

	/**
	 * All schedules are stopped.
	 */
	public void stop() {
		stop(getSchedules());
	}

	/**
	 * All application schedules for specified type are started.
	 * 
	 * @param scheduleType
	 *            type of schedules to start
	 */
	public void start(ScheduleType scheduleType) {
		List<Schedule> schedules = new ArrayList<Schedule>();
		for (Schedule schedule : getSchedules()) {

			if (isScheduleType(schedule, scheduleType)) {
				schedules.add(schedule);
			}

		}
		start(schedules);
	}

	/**
	 * All schedules for specified type are stopped.
	 * 
	 * @param scheduleType
	 *            type of schedules to stop
	 */
	public void stop(ScheduleType scheduleType) {
		List<Schedule> schedules = new ArrayList<Schedule>();
		for (Schedule schedule : getSchedules()) {

			if (isScheduleType(schedule, scheduleType)) {
				schedules.add(schedule);
			}
		}
		stop(schedules);
	}

	public void addSchedule(Schedule schedule, boolean start) {
		if (getSchedules().add(schedule) && start) {
			start(schedule);
		}
	}

	/**
	 * Provided schedule is started.
	 * 
	 * @param schedule
	 *            the schedule to start
	 * @return returns true if schedule was started
	 */
	public boolean start(Schedule schedule) {

		boolean ret = false;

		if (schedule != null) {

			if (!isScheduleActive(schedule.getName())) {

				if (schedule instanceof MaintenanceSchedule) {
					resetMaintenanceStatus((MaintenanceSchedule) schedule);
				}

				// invokeScheduleMethod(START_METHOD, schedule);

				ret = addTimer(schedule.getTimerSchedule(), schedule.getName());
				if (ret) {
					log.debug("Started schedule :: " + schedule.getName());
				} else {
					log.error("Failed to start schedule :: " + schedule.getName());
				}
			}
		}

		return ret;
	}

	/**
	 * Determines which schedules are currently active.
	 * 
	 * @return a list of currently active schedules
	 */
	public List<String> getActiveSchedules() {

		List<String> activeSchedules = new ArrayList<String>();

		for (Schedule schedule : getSchedules()) {
			if (isScheduleActive(schedule.getName())) {
				activeSchedules.add(schedule.getName());
			}
		}

		return activeSchedules;
	}

	/**
	 * Schedule for specified name is started.
	 * 
	 * @param name
	 *            name of schedule to start
	 */
	public void start(String name) {
		Schedule scheduleToStart = null;
		for (Schedule schedule : getSchedules()) {
			if (schedule.getName().equals(name)) {
				scheduleToStart = schedule;
				break;
			}
		}
		if (scheduleToStart != null) {
			start(scheduleToStart);
		}
	}

	/**
	 * Schedule for specified name is stopped.
	 * 
	 * @param name
	 *            name of schedule to stopped.
	 */
	public void stop(String name) {
		Schedule scheduleToStop = null;
		for (Schedule schedule : getSchedules()) {
			if (schedule.getName().equals(name)) {
				scheduleToStop = schedule;
				break;
			}
		}
		if (scheduleToStop != null) {
			stop(scheduleToStop);
		}
	}

	/**
	 * Provided schedule is stopped.
	 * 
	 * @param schedule
	 *            the schedule to stop
	 * @return returns true if schedule was stopped
	 */
	public boolean stop(Schedule schedule) {

		boolean ret = false;

		if (schedule != null) {

			if (isScheduleActive(schedule.getName())) {

				// invokeScheduleMethod(STOP_METHOD, schedule);

				ret = removeTimer(schedule.getName());

				if (ret) {
					log.debug("Stopped schedule :: " + schedule.getName());
				} else {
					log.error("Failed to stop schedule :: " + schedule.getName());
				}
			}
		}

		return ret;
	}

	/**
	 * Determines next scheduled time for a schedule.
	 * 
	 * @param name
	 *            scheduleName name of schedule. scheduleType type of schedule
	 * 
	 * @return returns date/time of next schedule invocation. Null is returned if schedule could not
	 *         be found.
	 */
	public Date getNextCreateTime(String scheduleName) {

		Date ret = null;

		try {
			if (ts.getTimers() != null) {
				for (Timer t : ts.getTimers()) {
					if (t.getInfo().equals(scheduleName)) {
						ret = t.getNextTimeout();
					}
				}
			}
		} catch (NoSuchObjectLocalException e) {
			log.info(e);
		} catch (NoMoreTimeoutsException e) {
			log.info(e);
		} catch (IllegalStateException e) {
			log.error(e);
		} catch (EJBException e) {
			log.error(e);
		}
		return ret;
	}

	private Boolean isScheduleType(Schedule schedule, ScheduleType scheduleType) {

		Boolean ret = false;
		switch (scheduleType) {
//		case BATCH:
//			ret = (schedule instanceof BatchSchedule);
//			break;
		case MAINTENANCE:
			ret = (schedule instanceof MaintenanceSchedule);
			break;
		case NATIVE_SOURCE:
			ret = (schedule instanceof NativeSourceSchedule);
			break;
		case PRODUCT:
			ret = (schedule instanceof ProductSchedule);
			break;
		default:
			log.debug("Missing schedule type");
			break;
		}

		return ret;
	}

}
