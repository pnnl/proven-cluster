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
package gov.pnnl.proven.cluster.lib.module.registry;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

import gov.pnnl.proven.cluster.lib.module.component.ManagedComponent;
import gov.pnnl.proven.cluster.lib.module.component.ManagedStatusOperation;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Eager;
import gov.pnnl.proven.cluster.lib.module.component.exception.InvalidMaintenanceOperationException;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.ComponentMaintenance;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperation;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceScheduleCheck;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.ScheduleCheck;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.StatusScheduleCheck;
import gov.pnnl.proven.cluster.lib.module.messenger.event.MaintenanceEvent;
import gov.pnnl.proven.cluster.lib.module.messenger.event.MaintenanceOperationEvent;

/**
 * Retains registered {@code MaintenanceOperation}s performed by a
 * {@code ManagedComponent} operator.
 * 
 * The registry also collects and retains recent historical
 * {@code MaintenanceEvent} information, describing performance and outcome of
 * maintenance operations for the different component types and the different
 * maintenance operations.
 * 
 * @author d3j766
 *
 */
@ApplicationScoped
@Eager
public class ModuleMaintenanceRegistry {

	public static final int MAINTENANCE_EVENT_LIMIT = 1000;

	@Inject
	Instance<MaintenanceOperation> moProvider;

	// Component maintenance operations
	private Map<ManagedComponent, SortedSet<MaintenanceOperation>> componentOperations = new HashMap<>();

	// Schedule maintenance operations
	private Map<ManagedComponent, SortedSet<ScheduleCheck>> scheduleOperations = new HashMap<>();

	/**
	 * Component's historical data. Stores MaintenanceEvent objects in a limited
	 * buffer.
	 * 
	 * Key = Component DO Identifier, Value = buffer
	 * 
	 * TODO - Add this to a cluster version and store in IMDG
	 */
	private Map<String, CircularFifoBuffer> componentEventData = new HashMap<>();

	/**
	 * ComponentType's historical data. Stores MaintenanceOperationEvent objects
	 * in a limited buffer.
	 * 
	 * Key = ComponentType, Value = buffer
	 * 
	 * TODO - Add this to a cluster version and store in IMDG
	 */
	private Map<Class<?>, CircularFifoBuffer> componentTypeEventData = new HashMap<>();

	/**
	 * Maintenance Operation's historical data. Stores MaintenanceOperationEvent
	 * objects in a limited buffer.
	 * 
	 * Key = MaintenanceOperation class name, Value = buffer
	 * 
	 * TODO - Add this to a cluster version and store in IMDG
	 */
	private Map<String, CircularFifoBuffer> operationEventData = new HashMap<>();

	/**
	 * Identifies common maintenance all components must perform during a
	 * {@link ManagedStatusOperation#check(SortedSet)} status operation.
	 */
	private static final Set<Class<?>> commonMaintenance = new HashSet<>();

	/**
	 * Identifies scheduler maintenance all components must perform during a
	 * {@link ManagedStatusOperation#schedulerCheck(SortedSet)} status
	 * operation.
	 */
	private static final Set<Class<?>> schedulerMaintenance = new HashSet<>(
			Arrays.asList(MaintenanceScheduleCheck.class, StatusScheduleCheck.class));

	public ModuleMaintenanceRegistry() {
	}

	/**
	 * Determines if the provided operator has been registered.
	 * 
	 * @param operator
	 *            the managed component performing the maintenance.
	 * @return true if registered, false otherwise.
	 */
	public boolean isRegistered(ManagedComponent operator) {
		return componentOperations.containsKey(operator);
	}

	/**
	 * For a managed component, registers provided ComponentMaintenance with the
	 * registry. The provided maintenance will augment previous registrations,
	 * if any.
	 * 
	 * Use {@code #removeOps(ManagedComponent)} before
	 * {@code #register(ComponentMaintenance)} to replace an existing
	 * registration.
	 * 
	 * 
	 * @param cm
	 *            the component maintenance.
	 * 
	 * @throws InvalidMaintenanceOperation
	 *             if the provided maintenance includes class definitions that
	 *             are not maintenance operations (i.e. not a sub-class of
	 *             {@code MaintenanceOperation}) or cannot be constructed.
	 * 
	 */
	public void register(ManagedComponent operator, ComponentMaintenance cm) {

		// ManagedComponent operator = cm.getOperator();

		// First check to ensure all maintenance operations are valid
		for (Class<?> clazz : cm.getMaintenanceOps()) {
			if (!clazz.getSuperclass().equals(MaintenanceOperation.class)) {
				throw new InvalidMaintenanceOperationException(
						clazz.getSimpleName() + " is not a MaintenanceOperation");
			}
		}

		// Ensure comon maintenance is included
		cm.getMaintenanceOps().addAll(commonMaintenance);

		// Create sorted set of the maintenance operations
		// Sorted by severity from high to low
		SortedSet<MaintenanceOperation> mOps = new TreeSet<>(new MaintenanceOperationComparator());

		// Create component operations
		for (Class<?> opClazz : cm.getMaintenanceOps()) {
			mOps.add(createOp(operator, opClazz));
		}

		// Add operations
		synchronized (componentOperations) {

			SortedSet<MaintenanceOperation> val = componentOperations.get(operator);
			if (null == val) {
				componentOperations.put(operator, mOps);
			} else {
				val.addAll(mOps);
			}
		}

		// Add schedule operations - this is only done once
		synchronized (scheduleOperations) {

			if (!scheduleOperations.containsKey(operator)) {

				SortedSet<ScheduleCheck> sOps = new TreeSet<>(new MaintenanceOperationComparator());

				for (Class<?> opClazz : schedulerMaintenance) {
					sOps.add((ScheduleCheck) createOp(operator, opClazz));
				}

				scheduleOperations.put(operator, sOps);
			}
		}
	}

	/**
	 * Constructs and returns a new MaintenanceOperation instance.
	 * 
	 * @param operator
	 *            the managed component responsible for performing the
	 *            maintenance operation.
	 * @param subtype
	 *            the class object used to construct a new instance of the
	 *            maintenance operation.
	 * 
	 * 
	 * @return the new {@code MaintenanceOperation} instance.
	 * @throws InvalidMaintenanceOperationException
	 *             if the provided Class is not a subtype of
	 *             MaintenanceOperation
	 */

	public MaintenanceOperation createOp(ManagedComponent operator, Class<?> clazz) {

		MaintenanceOperation mo;

		if (MaintenanceOperation.class.isAssignableFrom(clazz)) {
			Class<MaintenanceOperation> subtype = (Class<MaintenanceOperation>) clazz;
			mo = moProvider.select(subtype).get();
			mo.addOperator(operator);
		} else {
			throw new InvalidMaintenanceOperationException();
		}

		return mo;
	}

	/**
	 * Returns the {@code SortedSet} of {@code MaintenanceOperation}s for a
	 * given ManagedComponent. The set is sorted by {@code MaintenanceSeverity}
	 * of the operation, from high to low severity.
	 * 
	 * @param operator
	 *            the managed component responsible for performing the
	 *            maintenance operations.
	 * @return a sorted set of maintenance operations. An empty set is returned,
	 *         if there are no operations registered for the managed component.
	 * 
	 */
	public SortedSet<MaintenanceOperation> getOps(ManagedComponent operator) {

		SortedSet<MaintenanceOperation> ret = componentOperations.get(operator);
		if (null == ret) {
			ret = new TreeSet<MaintenanceOperation>();
		}

		return ret;

	}

	/**
	 * Returns the {@code SortedSet} of the common {@code MaintenanceOperation}s
	 * for a given ManagedComponent. The set is sorted by
	 * {@code MaintenanceSeverity} of the operation, from high to low severity.
	 * 
	 * @param operator
	 *            the managed component responsible for performing the
	 *            maintenance operations.
	 * @return a sorted set of the common maintenance operations. An empty set
	 *         is returned, if there are no common operations.
	 * 
	 */
	public SortedSet<MaintenanceOperation> getCommonOps(ManagedComponent operator) {

		SortedSet<MaintenanceOperation> ret = new TreeSet<MaintenanceOperation>();
		if (componentOperations.containsKey(operator)) {
			ret = componentOperations.get(operator);
		}

		return ret;
	}

	/**
	 * Returns the {@code SortedSet} of the schedule
	 * {@code MaintenanceOperation}s for a given ManagedComponent. The set is
	 * sorted by {@code MaintenanceSeverity} of the operation, from high to low
	 * severity.
	 * 
	 * @param operator
	 *            the managed component responsible for performing the
	 *            maintenance operations.
	 * @return a sorted set of the schedule maintenance operations. An empty set
	 *         is returned if there are no operations.
	 * 
	 */
	public SortedSet<ScheduleCheck> getScheduleOps(ManagedComponent operator) {

		SortedSet<ScheduleCheck> ret = new TreeSet<ScheduleCheck>();
		if (scheduleOperations.containsKey(operator)) {
			ret = scheduleOperations.get(operator);
		}

		return ret;
	}

	/**
	 * Removes registered maintenance for the provided operator.
	 */
	public void removeOps(ManagedComponent operator) {

		synchronized (componentOperations) {
			componentOperations.remove(operator);
		}
	}

	/**
	 * Records a maintenance event.
	 * 
	 * @param me
	 *            the maintenance event to record.
	 */
	public void recordMaintenance(MaintenanceEvent me) {
		addComponentEvent(me);
	}

	private void addComponentEvent(MaintenanceEvent me) {

		synchronized (componentEventData) {
			String key = me.getDoId();
			CircularFifoBuffer val = componentEventData.get(key);
			if (null == val) {
				val = new CircularFifoBuffer(MAINTENANCE_EVENT_LIMIT);
				val.add(me);
				componentEventData.put(key, val);
			} else {
				val.add(me);
			}
		}

	}

	/**
	 * Records a maintenance operation event.
	 * 
	 * @param me
	 *            the maintenance operation event to record.
	 */
	public void recordMaintenanceOperation(MaintenanceOperationEvent moe) {
		addComponentTypeEvent(moe);
		addOperationEvent(moe);
	}

	private void addComponentTypeEvent(MaintenanceOperationEvent me) {

		synchronized (componentTypeEventData) {
			Class<?> key = me.getComponentType();
			CircularFifoBuffer val = componentTypeEventData.get(key);
			if (null == val) {
				val = new CircularFifoBuffer(MAINTENANCE_EVENT_LIMIT);
				val.add(me);
				componentTypeEventData.put(key, val);
			} else {
				val.add(me);
			}
		}

	}

	private void addOperationEvent(MaintenanceOperationEvent me) {

		synchronized (operationEventData) {
			String key = me.getOpName();
			CircularFifoBuffer val = operationEventData.get(key);
			if (null == val) {
				val = new CircularFifoBuffer(MAINTENANCE_EVENT_LIMIT);
				val.add(me);
				operationEventData.put(key, val);
			} else {
				val.add(me);
			}
		}

	}

	/**
	 * Unregisters MaintenanceOperations for a registered component. This
	 * includes both the general component maintenance as well as its scheduler
	 * maintenance. Unregister will stop a component's maintenance schedule.
	 * 
	 * Request is ignored if component isn't found with provided identifier.
	 * 
	 * @param operatorId
	 *            managed component identifier.
	 */
	public void unregister(UUID operatorId) {

		ManagedComponent operator = null;
		for (ManagedComponent mc : componentOperations.keySet()) {
			if (mc.getId().equals(operatorId)) {
				operator = mc;
				break;
			}
		}

		if (null != operator) {

			// Ensure maintenance scheduler has been stopped. If here because of
			// a shutdown operation, the scheduler may still be running.
			operator.getMaintenanceSchedule().stop();

			synchronized (componentOperations) {

				// Cleanup - remove operator and its operations
				SortedSet<MaintenanceOperation> mos = componentOperations.get(operator);
				Iterator<MaintenanceOperation> moIt = mos.iterator();
				while (moIt.hasNext()) {
					MaintenanceOperation mo = moIt.next();
					moProvider.destroy(mo);
				}
				componentOperations.remove(operator);
			}

			synchronized (scheduleOperations) {

				// Cleanup - remove operator and its operations
				SortedSet<ScheduleCheck> sos = scheduleOperations.get(operator);
				Iterator<ScheduleCheck> soIt = sos.iterator();
				while (soIt.hasNext()) {
					ScheduleCheck mo = soIt.next();
					moProvider.destroy(mo);
				}
				scheduleOperations.remove(operator);
			}
		}
	}

	static class MaintenanceOperationComparator implements Comparator<MaintenanceOperation> {

		@Override
		public int compare(MaintenanceOperation mo1, MaintenanceOperation mo2) {

			int equals = 0;
			int lessThan = -1;
			int greaterThan = 1;

			// Preserve equality and avoid duplicate operations in set
			if (mo1.equals(mo2))
				return equals;

			int val = mo1.maxSeverity().getOrder() - mo2.maxSeverity().getOrder();
			if (val < 0)
				return lessThan;
			if (val > 0)
				return greaterThan;

			val = mo1.getResult().getStatus().getOrder() - mo2.getResult().getStatus().getOrder();
			if (val < 0)
				return lessThan;
			if (val > 0)
				return greaterThan;

			val = mo1.getResult().getSeverity().getOrder() - mo2.getResult().getSeverity().getOrder();
			if (val < 0)
				return lessThan;
			if (val > 0)
				return greaterThan;

			if (!mo1.opName().equals(mo2.opName())) {
				return mo1.opName().compareTo(mo2.opName());
			}
			return mo1.getOperator().getId().compareTo(mo2.getOperator().getId());

		}
	}
}
