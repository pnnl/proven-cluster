package gov.pnnl.proven.cluster.lib.module.registry;
//package gov.pnnl.proven.cluster.lib.module.manager;

import java.util.function.Supplier;

//package gov.pnnl.proven.cluster.lib.module.manager;
//package gov.pnnl.proven.cluster.lib.module.component;
//
//import gov.pnnl.oac.masterNode.Consts;
//import gov.pnnl.oac.masterNode.Consts.MaintenanceStatus;
//import gov.pnnl.oac.masterNode.Consts.ScheduleType;
//import gov.pnnl.oac.masterNode.Utils;
//import gov.pnnl.oac.masterNode.entity.Schedule;
//import gov.pnnl.oac.masterNode.service.ReloadService;
//
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import javax.ejb.DependsOn;
//import javax.ejb.EJB;
//import javax.ejb.EJBException;
//import javax.ejb.LocalBean;
//import javax.ejb.NoMoreTimeoutsException;
//import javax.ejb.NoSuchObjectLocalException;
//import javax.ejb.ScheduleExpression;
//import javax.ejb.Singleton;
//import javax.ejb.Timeout;
//import javax.ejb.Timer;
//import javax.ejb.TimerConfig;
//import javax.ejb.TimerService;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
///**
// * {@code ScheduledEventManager} is responsible for managing and submitting
// * scheduled reporting events on behalf of Proven components.
// */
//@Singleton
//@DependsOn(value = "ModuleManager")

import javax.enterprise.context.ApplicationScoped;

import gov.pnnl.proven.cluster.lib.module.component.ModuleComponent;
import gov.pnnl.proven.cluster.lib.module.component.event.ScheduledEvent;

@ApplicationScoped
public class ScheduledEventRegistry extends RegistryComponent {

	public void register(ModuleComponent mc, Class<? extends ScheduledEvent> se, Supplier<? extends ScheduledEvent> supplier, String schedule) {

		log.debug("REGISTERING SCHEDULED EVENT");
		log.debug("\tRegistering for class: " + mc.getClass().getSimpleName());
		log.debug("\tFor event type: " + se.getSimpleName());
		log.debug("\tFor supplier " + supplier.toString());
		log.debug("\tFor schedule: " + schedule);
	}

	// default void registerScheduledEvent() {
	//
	// }
	//
	// default void registerObservedEvent() {
	//
	// }
	//
	// default void unregisterAll() {
	//
	// }
	//
	// default void sendEvent() {

	//
	// /**
	// * Name of method to call when performing scheduled work.
	// */
	// private static final String WORK_METHOD = "doWork";
	//
	// /**
	// * Name of method to call when starting schedule.
	// */
	// private static final String START_METHOD = "doStart";
	//
	// /**
	// * Name of method to call when stopping schedule.
	// */
	// private static final String STOP_METHOD = "doStop";
	//
	// /**
	// * Used to create and control timers.
	// */
	// @Resource
	// private TimerService ts;
	//
	// /**
	// * Used to loads current schedules when manager is initialized. Should
	// only
	// * be used for initialization.
	// */
	// @EJB
	// private ReloadService rs;
	//
	// @EJB
	// private PropertiesManager pm;
	//
	// /**
	// * List of current schedules under management of the ScheduleManager
	// */
	// private List<Schedule> schedules = new ArrayList<Schedule>();
	//
	// /**
	// * Map of activated schedules (by scheduleId) and it's start time. This
	// can
	// * be used by work components to ensure only a single work component is
	// * running at a time. TODO - Modify this to be based on a variable number
	// of
	// * concurrent work components. For now, used to manage single.
	// */
	// private Map<Integer, Long> activatedSchedules = new HashMap<Integer,
	// Long>();
	//
	// /**
	// * Initializes ScheduleManager. By default, all schedules are disabled.
	// */
	// @PostConstruct
	// public void initialize() {
	// setSchedules(rs.getSchedules(), true);
	// log.debug("ScheduleManager Initialized...");
	// Function<T, R>
	// }
	//
	// public List<Schedule> getSchedules() {
	// return this.schedules;
	// }
	//
	// public void setSchedules(List<Schedule> newSchedules) {
	// setSchedules(newSchedules, false);
	// }
	//
	// public String changeSchedule(String name, String scheduleStr) {
	//
	// String ret = null;
	//
	// for (Schedule schedule : schedules) {
	//
	// if (schedule.getName().equals(name)) {
	// schedule.setSchedule(scheduleStr);
	// if (isScheduleActive(schedule.getName())) {
	// stop(schedule);
	// start(schedule);
	// }
	// ret = "Schedule changed (and restarted if it was active) " + Consts.NL +
	// schedule.getName()
	// + " now using new schedule :: " + scheduleStr;
	// break;
	// }
	//
	// }
	//
	// if (null == ret) {
	// ret = "Schedule NOT found";
	// }
	//
	// return ret;
	// }
	//
	// public void setSchedules(List<Schedule> newSchedules, boolean init) {
	//
	// if (!init) {
	//
	// log.debug("--------------------------------");
	// log.debug("NEW SCHEDULES");
	// for (Schedule schedule : newSchedules) {
	// log.debug(schedule.getName() + " :: " + schedule.getSchedule());
	// }
	// log.debug("--------------------------------");
	//
	// for (Schedule schedule : schedules) {
	//
	// // Stop schedules that are no longer current
	// // New schedules being added are not started automatically
	// if (!newSchedules.contains(schedule)) {
	// log.debug("Schedule NOT exists :: " + schedule.getName());
	// log.debug("!!!!!!!!!!!!!!!!!!!!! stopping schedule :: " +
	// schedule.getName());
	// stop(schedule);
	// log.debug("Schedule stopped :: " + schedule.getName());
	// }
	//
	// // Update schedules that have changed
	// else {
	// String newSchedule =
	// newSchedules.get(newSchedules.indexOf(schedule)).getSchedule();
	// String oldSchedule = schedule.getSchedule();
	// log.debug("Schedule exists :: " + schedule.getName());
	// log.debug("Old schedule :: " + oldSchedule);
	// log.debug("New schedule :: " + newSchedule);
	// if (!oldSchedule.equals(newSchedule)) {
	// log.debug("!!!!!!!!!!!!!!!!!!!!! updating schedule :: " +
	// schedule.getName());
	// schedule.setSchedule(newSchedule);
	// stop(schedule);
	// start(schedule);
	// log.debug("Schedule updated :: " + schedule.getName() + " :: Using new
	// schedule :: "
	// + newSchedule);
	//
	// }
	// }
	// }
	// }
	// this.schedules = newSchedules;
	// }
	//
	// public Map<Integer, Long> getActivatedSchedules() {
	// return activatedSchedules;
	// }
	//
	// public boolean isActivated(Schedule schedule) {
	// return activatedSchedules.containsKey(schedule.getScheduleId());
	// }
	//
	// public boolean activateSchedule(Schedule schedule) {
	//
	// boolean ret = false;
	//
	// if (!isActivated(schedule)) {
	// activatedSchedules.put(schedule.getScheduleId(),
	// Utils.getLongForCurrentTime());
	// ret = true;
	// }
	// return ret;
	// }
	//
	// public void deactivateSchedule(Schedule schedule) {
	// log.debug("Removing schedule, Before..." + "ID:: " + schedule.getName() +
	// "Name :: " + schedule.getScheduleId()
	// + " Count :: " + activatedSchedules.size());
	// activatedSchedules.remove(schedule.getScheduleId());
	// log.debug("Removing schedule, After...: Count :: " +
	// activatedSchedules.size());
	// }
	//
	// public void resetMaintenanceStatus() {
	// for (Schedule schedule : getSchedules()) {
	// if (schedule.getIsMaintenance()) {
	// resetMaintenanceStatus(schedule);
	// }
	// }
	// }
	//
	// public void resetMaintenanceStatus(Schedule schedule) {
	// if (schedule.getIsMaintenance()) {
	// schedule.setStatus(MaintenanceStatus.OFF);
	// schedule.setDtmStatus(new Date());
	// schedule.setScheduledAttempts(0);
	// }
	// }
	//
	// public String getMaintenanceStatus() {
	//
	// String ret = "";
	//
	// for (Schedule schedule : getSchedules()) {
	//
	// if (schedule.getIsMaintenance()) {
	//
	// ret += Consts.NL;
	// ret += "-----------------------------------" + Consts.NL;
	// ret += "Name: " + schedule.getName() + Consts.NL;
	// ret += "Status: " + schedule.getStatus().toString() + Consts.NL;
	// ret += "Status DTM: " + schedule.getDtmStatus().toString() + Consts.NL;
	// ret += "Scheduled Attempts: " + schedule.getScheduledAttempts() +
	// Consts.NL;
	// ret += "-----------------------------------" + Consts.NL;
	// ret += Consts.NL;
	// }
	// }
	//
	// return ret;
	// }
	//
	// public boolean isMaintenanceOn() {
	//
	// boolean ret = false;
	// for (Schedule schedule : getSchedules()) {
	// if ((schedule.getIsMaintenance()) && (schedule.getStatus() ==
	// MaintenanceStatus.ON)) {
	// ret = true;
	// break;
	// }
	// }
	//
	// return ret;
	// }
	//
	// /**
	// * Callback for schedule timer expirations. Determines the EJB timer, by
	// * name, and calls work method from associated SLSB.
	// *
	// * For each Schedule there should be a SLSB having name equal to
	// * Schedule.workComponent value, and an asynchronous doWork(Schedule)
	// method
	// * which will be called.
	// *
	// * If application currently has scheduled maintenance running, no new
	// * non-maintenance work will be invoked.
	// *
	// * @param timer
	// * ejb timer
	// */
	// @SuppressWarnings("unused")
	// @Timeout
	// private void doWork(Timer timer) {
	//
	// log.debug("INSIDE Schedule timeout for :: " + timer.getInfo());
	// String timerName = (String) timer.getInfo();
	//
	// boolean isMaintenanceOn = isMaintenanceOn();
	// boolean invokeSchedule = false;
	//
	// for (Schedule schedule : schedules) {
	//
	// if (schedule.getName().equals(timerName)) {
	//
	// if (schedule.getIsMaintenance()) {
	//
	// if (schedule.getStatus() == MaintenanceStatus.ON) {
	//
	// if (schedule.getScheduledAttempts() <
	// pm.getMaxScheduledMaintenanceAttempts()) {
	// schedule.setScheduledAttempts(schedule.getScheduledAttempts() + 1);
	// invokeSchedule = true;
	// } else {
	// schedule.setStatus(MaintenanceStatus.FAIL);
	// schedule.setDtmStatus(new Date());
	// schedule.setScheduledAttempts(0);
	// isMaintenanceOn = isMaintenanceOn();
	// }
	// } else {
	// Long currenTimeMillis = new Date().getTime();
	// Long statusTimeMillis = schedule.getDtmStatus().getTime();
	// Long intervalMillis = currenTimeMillis - statusTimeMillis;
	// Integer intervalHours = Utils.getHoursFromMillis(intervalMillis);
	//
	// // TODO REMOVE This is testing only
	// // intervalHours = 2;
	//
	// if (intervalHours >= pm.getMaxMaintenanceIntervalHours()) {
	// schedule.setStatus(MaintenanceStatus.ON);
	// schedule.setScheduledAttempts(1);
	// invokeSchedule = true;
	// isMaintenanceOn = true;
	// }
	// }
	// }
	//
	// else {
	// if (!isMaintenanceOn) {
	// invokeSchedule = true;
	// }
	// }
	//
	// if (invokeSchedule) {
	// log.debug("INVOKING WORK METHOD FOR :: " + timer.getInfo());
	// invokeScheduleMethod(WORK_METHOD, schedule);
	// }
	// }
	// }
	// log.debug("LEAVING Schedule timeout for :: " + timer.getInfo());
	// }
	//
	// private void invokeScheduleMethod(String methodName, Schedule schedule) {
	//
	// Object ejb = Utils.lookupEjb(schedule.getWorkComponent());
	//
	// if (ejb != null) {
	// try {
	// Method method = ejb.getClass().getMethod(methodName, Schedule.class);
	// method.invoke(ejb, schedule);
	// log.debug(methodName + " invoked for ejb :: " +
	// schedule.getWorkComponent() + ", for schedule "
	// + schedule.getName());
	// } catch (Exception e) {
	// log.error(methodName + " method not found or invocation failed for
	// schedule :: " + schedule.getName());
	// e.printStackTrace();
	// }
	// } else {
	// log.error("Failed to lookup EJB :: " + schedule.getWorkComponent());
	// }
	// }
	//
	// /**
	// * Removes ejb timer, by name
	// *
	// * @param timer
	// * name of timer to remove
	// */
	// private boolean removeTimer(String timer) {
	// boolean ret = false;
	//
	// try {
	//
	// if (ts.getTimers() != null) {
	// for (Timer t : ts.getTimers()) {
	// if (t.getInfo().equals(timer)) {
	// t.cancel();
	// ret = true;
	// }
	// }
	// }
	// } catch (IllegalArgumentException e) {
	// log.error(e);
	// } catch (NoSuchObjectLocalException e) {
	// log.error(e);
	// } catch (IllegalStateException e) {
	// log.error(e);
	// } catch (EJBException e) {
	// log.error(e);
	// }
	// return ret;
	// }
	//
	// /**
	// * Adds a new ejb timer. Timers created are by default non-persistent.
	// *
	// * @param schedule
	// * string representation of a ScheduleExpression. All attributes
	// * should be delimited by a colon. All attributes should be
	// * included, even if empty. Attributes should be specified in the
	// * following order: <br>
	// * <ol>
	// * <li>second</li>
	// * <li>minute</li>
	// * <li>hour</li>
	// * <li>dayOfMonth</li>
	// * <li>month</li>
	// * <li>dayOfWeek</li>
	// * <li>year</li>
	// * </ol>
	// * <br>
	// * For example the following is a schedule for every 15 minutes
	// * for the hours of 1am and 2am, every Friday:<br>
	// * <b>:0/15:1,2:::Fri:<b>
	// *
	// * @see java documentation for ScheduleExpression
	// *
	// *
	// * @param timer
	// * name of timer to add
	// *
	// * @return returns true if timer was created, false otherwise.
	// */
	// private boolean addTimer(String schedule, String timer) {
	//
	// boolean ret = false;
	//
	// try {
	//
	// if (schedule == null) {
	// throw new IllegalArgumentException("Invalid Schedule");
	// }
	//
	// String[] attributes = schedule.split(":", 7);
	//
	// // Simple check to see if the correct number of attributes were
	// // provided.
	// if (attributes.length == 7) {
	// ScheduleExpression se = new ScheduleExpression();
	// se.second(attributes[0]);
	// se.minute(attributes[1]);
	// se.hour(attributes[2]);
	// se.dayOfMonth(attributes[3]);
	// se.month(attributes[4]);
	// se.dayOfWeek(attributes[5]);
	// se.year(attributes[6]);
	// ts.createCalendarTimer(se, new TimerConfig(timer, false));
	// ret = true;
	// }
	// } catch (IllegalArgumentException e) {
	// log.error(e);
	// } catch (IllegalStateException e) {
	// log.error(e);
	// } catch (EJBException e) {
	// log.error(e);
	// }
	//
	// return ret;
	// }
	//
	// /**
	// * Determines if schedule has been started.
	// *
	// * @param name
	// * name of schedule.
	// *
	// * @return true if schedule has been started, false otherwise.
	// */
	// public boolean isScheduleActive(String name) {
	// boolean ret = false;
	//
	// try {
	// if (ts.getTimers() != null) {
	// for (Timer t : ts.getTimers()) {
	// if (t.getInfo().equals(name)) {
	// t.getNextTimeout();
	// ret = true;
	// }
	// }
	// }
	// } catch (NoSuchObjectLocalException e) {
	// log.info(e);
	// } catch (NoMoreTimeoutsException e) {
	// log.info(e);
	// } catch (IllegalStateException e) {
	// log.error(e);
	// } catch (EJBException e) {
	// log.error(e);
	// }
	// return ret;
	// }
	//
	// /**
	// * Provided list of schedules are started.
	// *
	// * @param schedules
	// * list of schedules to enable
	// */
	// public void start(List<Schedule> schedules) {
	// if (schedules != null) {
	// for (Schedule schedule : schedules) {
	// start(schedule);
	// }
	// }
	// }
	//
	// /**
	// * Provided list of schedules are stopped.
	// *
	// * @param schedules
	// * list of schedules to disable
	// */
	// public void stop(List<Schedule> schedules) {
	// if (schedules != null) {
	// for (Schedule schedule : schedules) {
	// stop(schedule);
	// }
	// }
	// }
	//
	// /**
	// * All schedules are started.
	// */
	// public void start() {
	// // TODO Filter PROPERTIES schedules from list - it can only be turned on
	// // manually
	// // The PROPERTIES schedule work component is causing app to hang because
	// // of
	// // a L2 cache lock deadlock problem, needs investigation. Can turn these
	// // on/off manually w/o problems - but if left running will likely cause
	// // a
	// // hang.
	// List<Schedule> schedules = new ArrayList<Schedule>();
	// for (Schedule schedule : getSchedules()) {
	// if (!(schedule.getType().equals(ScheduleType.PROPERTIES.toString()))) {
	// schedules.add(schedule);
	// }
	// }
	// start(schedules);
	//
	// // TODO Return to starting ALL schedules when L2 cache issue resolved
	// // w/properties
	// // schedules
	// // start(getSchedules());
	// }
	//
	// /**
	// * All schedules are stopped.
	// */
	// public void stop() {
	// stop(getSchedules());
	// }
	//
	// /**
	// * All application schedules for specified type are started.
	// *
	// * @param scheduleType
	// * type of schedules to start
	// */
	// public void start(Consts.ScheduleType scheduleType) {
	// List<Schedule> schedules = new ArrayList<Schedule>();
	// for (Schedule schedule : getSchedules()) {
	// if (schedule.getType().equals(scheduleType.toString())) {
	// schedules.add(schedule);
	// }
	// }
	// start(schedules);
	// }
	//
	// /**
	// * All application schedules for specified type are stopped.
	// *
	// * @param scheduleType
	// * type of schedules to stop
	// */
	// public void stop(Consts.ScheduleType scheduleType) {
	// List<Schedule> schedules = new ArrayList<Schedule>();
	// for (Schedule schedule : getSchedules()) {
	// if (schedule.getType().equals(scheduleType.toString())) {
	// schedules.add(schedule);
	// }
	// }
	// stop(schedules);
	// }
	//
	// /**
	// * Provided schedule is started.
	// *
	// * @param schedule
	// * the schedule to start
	// * @return returns true if schedule was started
	// */
	// public boolean start(Schedule schedule) {
	//
	// boolean ret = false;
	//
	// if (schedule != null) {
	//
	// if (!isScheduleActive(schedule.getName())) {
	//
	// resetMaintenanceStatus(schedule);
	//
	// invokeScheduleMethod(START_METHOD, schedule);
	//
	// ret = addTimer(schedule.getSchedule(), schedule.getName());
	// if (ret) {
	// log.debug("Started schedule :: " + schedule.getName());
	// } else {
	// log.error("Failed to start schedule :: " + schedule.getName());
	// }
	// }
	// }
	//
	// return ret;
	// }
	//
	// /**
	// * Determines which schedules are currently active.
	// *
	// * @return a list of currently active schedules
	// */
	// public List<String> getActiveSchedules() {
	//
	// List<String> activeSchedules = new ArrayList<String>();
	//
	// for (Schedule schedule : getSchedules()) {
	// if (isScheduleActive(schedule.getName())) {
	// activeSchedules.add(schedule.getName());
	// }
	// }
	//
	// return activeSchedules;
	// }
	//
	// /**
	// * Schedule for specified name is started.
	// *
	// * @param name
	// * name of schedule to start
	// */
	// public void start(String name) {
	// Schedule scheduleToStart = null;
	// for (Schedule schedule : getSchedules()) {
	// if (schedule.getName().equals(name)) {
	// scheduleToStart = schedule;
	// break;
	// }
	// }
	// if (scheduleToStart != null) {
	// start(scheduleToStart);
	// }
	// }
	//
	// /**
	// * Schedule for specified name is stopped.
	// *
	// * @param name
	// * name of schedule to stopped.
	// */
	// public void stop(String name) {
	// Schedule scheduleToStop = null;
	// for (Schedule schedule : getSchedules()) {
	// if (schedule.getName().equals(name)) {
	// scheduleToStop = schedule;
	// break;
	// }
	// }
	// if (scheduleToStop != null) {
	// stop(scheduleToStop);
	// }
	// }
	//
	// /**
	// * Provided schedule is stopped.
	// *
	// * @param schedule
	// * the schedule to stop
	// * @return returns true if schedule was stopped
	// */
	// public boolean stop(Schedule schedule) {
	//
	// boolean ret = false;
	//
	// if (schedule != null) {
	//
	// if (isScheduleActive(schedule.getName())) {
	//
	// invokeScheduleMethod(STOP_METHOD, schedule);
	//
	// ret = removeTimer(schedule.getName());
	//
	// if (ret) {
	// log.debug("Stopped schedule :: " + schedule.getName());
	// } else {
	// log.error("Failed to stop schedule :: " + schedule.getName());
	// }
	// }
	// }
	//
	// return ret;
	// }
	//
	// /**
	// * Determines next scheduled time for a schedule.
	// *
	// * @param name
	// * scheduleName name of schedule. scheduleType type of schedule
	// *
	// * @return returns date/time of next schedule invocation. Null is returned
	// * if schedule could not be found.
	// */
	// public Date getNextCreateTime(String scheduleName) {
	//
	// Date ret = null;
	//
	// try {
	// if (ts.getTimers() != null) {
	// for (Timer t : ts.getTimers()) {
	// if (t.getInfo().equals(scheduleName)) {
	// ret = t.getNextTimeout();
	// }
	// }
	// }
	// } catch (NoSuchObjectLocalException e) {
	// log.info(e);
	// } catch (NoMoreTimeoutsException e) {
	// log.info(e);
	// } catch (IllegalStateException e) {
	// log.error(e);
	// } catch (EJBException e) {
	// log.error(e);
	// }
	// return ret;
	// }
	//
}
