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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

import gov.pnnl.proven.cluster.lib.module.component.ManagedComponent;
import gov.pnnl.proven.cluster.lib.module.component.exception.InvalidMaintenanceOperationException;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.ComponentMaintenance;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.MaintenanceOperation;

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
public class MaintenanceRegistry {

	public static final int COMPONENT_TYPE_MAINTENANCE_EVENTS_LIMIT = 10000;
	public static final int OPERATION_MAINTENANCE_EVENTS_LIMIT = 10000;

	// Component maintenance operations
	Map<ManagedComponent, SortedSet<MaintenanceOperation>> componentOperations = new HashMap<>();

	// Component Type's historical maintenance event data
	// TODO - Add this to IMDG?
	CircularFifoBuffer componentTypeEventData = new CircularFifoBuffer(COMPONENT_TYPE_MAINTENANCE_EVENTS_LIMIT);

	// Maintenance Operation historical maintenance event data
	// TODO - Add this to IMDG?
	CircularFifoBuffer operationEventData = new CircularFifoBuffer(OPERATION_MAINTENANCE_EVENTS_LIMIT);

	public MaintenanceRegistry() {
	}

	/**
	 * Registers {@code ComponentMaintenance} with the registry. The provided
	 * maintenance will replace an existing registration, if any.
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
	public void register(ComponentMaintenance cm) {

		ManagedComponent operator = cm.getOperator();

		// First check to ensure all maintenance operations are valid
		for (Class<?> clazz : cm.getMaintenanceOps()) {
			if (!clazz.getSuperclass().equals(MaintenanceOperation.class)) {
				throw new InvalidMaintenanceOperationException(
						clazz.getSimpleName() + " is not a MaintenanceOperation");
			}

		}

		// Create sorted set of the maintenance operations
		// Sorted by severity from high to low
		SortedSet<MaintenanceOperation> ops = new TreeSet<>((mo1, mo2) -> {

			int equals = 0;
			int lessThan = -1;
			int greaterThan = 1;

			// Preserve equality - avoid duplicate operations in set
			if (mo1.equals(mo2))
				return equals;

			//
			// LessThan or GreaterThan			
			int val = mo1.getSeverity().ordinal() - mo2.getSeverity().ordinal();
			if (val < 0)
				return lessThan;
			if (val > 0)
				return greaterThan;
			
			val = mo1.getStatus().ordinal() - mo2.getStatus().ordinal();
			if (val < 0)
				return lessThan;
			if (val > 0)
				return greaterThan;
			
			if (!mo1.getName().equals(mo2.getName())) {
				return mo1.getName().compareTo(mo2.getName());
			}
			return mo1.getOperator().getId().compareTo(mo2.getOperator().getId());

		});
		for (Class<?> opClazz : cm.getMaintenanceOps()) {
			ops.add(createOp(operator, opClazz));
		}

		// Add to map (replace if it exists)
		componentOperations.put(operator, ops);

	}

	/**
	 * Constructs and returns a new MaintenanceOperation instance.
	 * 
	 * It is assumed that the provided class reference is a
	 * MaintenanceOperation. Any problems with construction should throw
	 * InvalidMaintenanceOperationException.
	 * 
	 * All concrete maintenance operation classes should expose a constructor
	 * with a single parameter of {@code Managed Component}.
	 * 
	 * @param operator
	 *            the managed component responsible for performing the
	 *            maintenance operation.
	 * @param opClazz
	 *            the class object used to construct a new instance of the
	 *            maintenance operation.
	 * @return the new {@code MaintenanceOperation} instance.
	 * @throws InvalidMaintenanceOperationException
	 *             if construction fails
	 */
	private MaintenanceOperation createOp(ManagedComponent operator, Class<?> opClazz) {

		MaintenanceOperation op;

		try {
			op = MaintenanceOperation.class.cast(opClazz.getConstructor(ManagedComponent.class).newInstance(operator));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassCastException e) {
			throw new InvalidMaintenanceOperationException(
					"Construction failed for MaintenanceOperation: " + opClazz.getSimpleName(), e);
		}

		return op;
	}

	/**
	 * Returns the {@code SortedSet} of {@code MaintenanceOperation}s for a given
	 * ManagedComponent. The set is sorted by {@code MaintenanceSeverity} of the
	 * operation, from high to low severity.
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

}
