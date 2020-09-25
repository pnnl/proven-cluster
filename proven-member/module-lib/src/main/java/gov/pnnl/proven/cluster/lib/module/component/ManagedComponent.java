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

import static gov.pnnl.proven.cluster.lib.module.component.Creator.scalable;
import static gov.pnnl.proven.cluster.lib.module.component.Creator.scaleAllowedCount;
import static gov.pnnl.proven.cluster.lib.module.component.Creator.scaleMaxCount;
import static gov.pnnl.proven.cluster.lib.module.component.Creator.scaleMinCount;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedComponent.ComponentLock.CREATED_LOCK;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedComponent.ComponentLock.STATUS_LOCK;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.CheckedOffline;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Failed;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Offline;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Online;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Unknown;
import static gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationSeverity.Available;
import static gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationSeverity.Noop;
import static gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationStatus.FAILED;
import static gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationStatus.PASSED;
import static gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation.RequestScale;

import java.lang.annotation.Annotation;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;

import gov.pnnl.proven.cluster.lib.member.MemberProperties;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Eager;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Managed;
import gov.pnnl.proven.cluster.lib.module.component.annotation.ManagedAnnotationLiteral;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Scalable;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Scheduler;
import gov.pnnl.proven.cluster.lib.module.component.exception.InvalidCreatedModificationException;
import gov.pnnl.proven.cluster.lib.module.component.exception.InvalidCreationRequestException;
import gov.pnnl.proven.cluster.lib.module.component.exception.InvalidScalableConfigurationException;
import gov.pnnl.proven.cluster.lib.module.component.exception.InvalidStatusModificationException;
import gov.pnnl.proven.cluster.lib.module.component.exception.MissingConfigurationImplementationException;
import gov.pnnl.proven.cluster.lib.module.component.interceptor.ManagedInterceptor;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.ComponentMaintenance;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.MaintenanceSchedule;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.ScheduledMaintenance;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperation;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationResult;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationSeverity;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.ScheduleCheck;
import gov.pnnl.proven.cluster.lib.module.manager.ManagerComponent;
import gov.pnnl.proven.cluster.lib.module.messenger.StatusOperationMessage;
import gov.pnnl.proven.cluster.lib.module.messenger.StatusOperationMessages;
import gov.pnnl.proven.cluster.lib.module.messenger.StatusSchedule;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperationAnnotationLiteral;
import gov.pnnl.proven.cluster.lib.module.messenger.event.StatusOperationEvent;
import gov.pnnl.proven.cluster.lib.module.messenger.observer.ManagedObserver;
import gov.pnnl.proven.cluster.lib.module.module.ProvenModule;
import gov.pnnl.proven.cluster.lib.module.registry.ComponentEntry;
import gov.pnnl.proven.cluster.lib.module.registry.EntryIdentifier;
import gov.pnnl.proven.cluster.lib.module.registry.EntryLocation;
import gov.pnnl.proven.cluster.lib.module.registry.EntryProperties;
import gov.pnnl.proven.cluster.lib.module.registry.EntryReporter;

/**
 * 
 * Base module component. Represents managed components within a Proven
 * application module, this includes the {@code ProvenModule} component itself.
 * Each component performs specific tasks to support a module's operation. These
 * components are registered with both their local, member, and cluster
 * registries. A {@code ManagedComponent} may share its resources (compute and
 * data) across the cluster.
 * 
 * @author d3j766
 *
 * @see ProvenModule, RegistryComponent
 * 
 */
@Managed
public abstract class ManagedComponent implements ManagedStatusOperation, ScheduledMaintenance, Creator, EntryReporter {

	@Inject
	Logger log;

	String test;

	public void setTest(String test) {
		this.test = test;
	}

	@Inject
	protected HazelcastInstance hzi;

	@Inject
	@Any
	protected Instance<ManagedComponent> componentProvider;

	@Inject
	@Scheduler(delay = 10, activateOnStartup = false)
	protected StatusSchedule statusSchedule;

	@Inject
	@Scheduler(delay = 10, activateOnStartup = false)
	protected MaintenanceSchedule maintenanceSchedule;

	@Inject
	@Scheduler(delay = 10, activateOnStartup = false)
	protected ScaleSchedule scaleSchedule;

	@Inject
	@Eager
	protected ManagedObserver opObserver;

	/**
	 * Member properties
	 */
	protected MemberProperties mp = MemberProperties.getInstance();

	// Component properties
	protected UUID id;
	protected Class<? extends ManagedComponent> type;
	protected String moduleName = ProvenModule.retrieveModuleName();
	protected ComponentGroup group;
	protected Long creationeTime = new Date().getTime();
	protected boolean isModule = false;
	protected boolean isManager = false;

	
	/**
	 * Component's entry identifier
	 */
	protected EntryIdentifier entryIdentifier;
		
	/**
	 * Component location, this is initialized in
	 * {@link ManagedInterceptor#verifyManagedComponent(javax.interceptor.InvocationContext)}
	 */
	protected EntryLocation location;

	// Status and lock properties
	private ManagedStatus status = Unknown;
	private ReentrantLock statusLock = new ReentrantLock();
	private ReentrantLock createdLock = new ReentrantLock();

	public enum ComponentLock {
		STATUS_LOCK,
		CREATED_LOCK;
	}

	/**
	 * Indicates number of successful scaling attempts (i.e. number of
	 * components created) triggered by this component.
	 */
	private int scaledCount = 0;

	/**
	 * Creator component. Identifies the creator of a ManagedComponent. All
	 * ManagedComponents have a creator, with the exception of ProvenModule.
	 */
	private ManagedComponent creator;

	/**
	 * Created (i.e. child) components. The Map contains the component's ID as
	 * the key and object as value.
	 */
	private Map<UUID, ManagedComponent> createdComponents = new HashMap<>();

	/**
	 * Creation queue. Contains requests for component creation. Scale scheduler
	 * is the consumer of this queue. {@code CreationQueue#removeRequest()} will
	 * block waiting for new creation requests to be added.
	 * 
	 * @see ScaleSchedule#apply()
	 * 
	 */
	private CreationQueue<ManagedComponent> creationQueue = new CreationQueue<>();

	public ManagedComponent(ComponentGroup group) {

		this.id = UUID.randomUUID();
		this.group = group;
		// Note: WELD specific; proxies are sub classes
		this.type = (Class<? extends ManagedComponent>) this.getClass().getSuperclass();
		this.isModule = ProvenModule.class.isAssignableFrom(type);
		this.isManager = ManagerComponent.class.isAssignableFrom(type);
		this.entryIdentifier = new EntryIdentifier(id, getName(), group);
		// Indicates component is being created - status is set to Ready in
		// PostConstruct callback
		setStatus(ManagedStatus.Creating);
	}

	@PostConstruct
	public void managedComponentInit() {

		// Set and verify scalable properties, if any
		if (this.getClass().isAnnotationPresent(Scalable.class)) {
			if (Creator.scalable(getType())) {
				if (!Creator.isValidScaleConfiguration(getType())) {
					throw new InvalidScalableConfigurationException(getName());
				}
			}
		}

		// Register with the managed observer
		opObserver.register(this);

		// Register with status schedule
		statusSchedule.register(this);

		// Register with maintenance schedule
		maintenanceSchedule.register(this);

		// Register with scale schedule
		scaleSchedule.register(this);

		// Starting status for a managed component
		setStatus(ManagedStatus.Ready);
	}

	@PreDestroy
	public void managedComponentDestroy() {
		log.debug("ProvenComponent PreDestroy..." + this.getClass().getSimpleName());
		opObserver.unregister(this);
	}

	public boolean acquireLockNoWait(ComponentLock lockType) {

		boolean ret;

		if (lockType == ComponentLock.STATUS_LOCK) {
			ret = statusLock.tryLock();
		} else { // CREATED_LOCK
			ret = createdLock.tryLock();
		}

		return ret;
	}

	public void acquireLockWait(ComponentLock lockType) {

		if (lockType == ComponentLock.STATUS_LOCK) {
			statusLock.lock();
		} else { // CREATED_LOCK
			createdLock.lock();
		}

	}

	public boolean acquireLockWaitTime(ComponentLock lockType, Long time, TimeUnit unit) {

		boolean ret = false;
		try {

			if (lockType == ComponentLock.STATUS_LOCK) {
				ret = statusLock.tryLock(time, unit);
			} else { // CREATED_LOCK
				ret = createdLock.tryLock(time, unit);
			}

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			ret = false;
		}

		return ret;

	}

	public void releaseLock(ComponentLock lockType) {

		if (lockType == ComponentLock.STATUS_LOCK) {
			if (statusLock.isHeldByCurrentThread()) {
				statusLock.unlock();
			}
		} else { // CREATED_LOCK
			if (createdLock.isHeldByCurrentThread()) {
				createdLock.unlock();
			}
		}

	}

	public Class<? extends ManagedComponent> getType() {
		return type;
	}

	public String getName() {
		return getType().getSimpleName();
	}

	public UUID getId() {
		return id;
	}

	public ComponentGroup getComponentGroup() {
		return group;
	}

	public String getModuleName() {
		return moduleName;
	}

	public String getGroupLabel() {
		return getComponentGroup().getGroupLabel();
	}

	public Long getCreationeTime() {
		return creationeTime;
	}

	public boolean isScalable() {
		return this.getClass().isAnnotationPresent(Scalable.class);
	}

	/**
	 * @return the scaledCount
	 */
	public int getScaledCount() {
		return scaledCount;
	}

	/**
	 * If locked it must have been previously acquired by same thread, if not
	 * throw an exception.
	 * 
	 * @param scaledCount
	 *            the number of scaled components triggered by this component.
	 * 
	 * @throws InvalidStatusModificationException
	 *             if caller does not have a status lock for the component being
	 *             modified.
	 * 
	 */
	public void setScaledCount(int scaledCount) {

		if (statusLock.tryLock()) {
			try {
				this.scaledCount = scaledCount;
			} finally {
				statusLock.unlock();
			}
		} else {
			throw new InvalidStatusModificationException("Not lock owner for component: " + entryIdentifier());
		}
	}

	public StatusSchedule getStatusSchedule() {
		return statusSchedule;
	}

	public MaintenanceSchedule getMaintenanceSchedule() {
		return maintenanceSchedule;
	}

	public ScaleSchedule getScaleSchedule() {
		return scaleSchedule;
	}

	public void stopSchedules() {
		getStatusSchedule().stop();
		getMaintenanceSchedule().stop();
		getScaleSchedule().stop();
	}

	private void enableComponent(ManagedComponent mc) {

		// Assign Creator (always the caller)
		mc.creator = this;

		// Activate scheduled messenger
		mc.getStatusSchedule().start();

		// Activate scheduled maintenance
		mc.getMaintenanceSchedule().start();

		// Activate scheduled component creation
		mc.getScaleSchedule().start();

	}

	public UUID getMemberId() {
		return location.getMemberId();
	}

	public UUID getModuleId() {
		return location.getModuleId();
	}

	public UUID getManagerId() {
		return location.getManagerId();
	}

	public UUID getCreatorId() {
		return location.getCreatorId();
	}

	public ManagedComponent getCreator() {
		return creator;
	}

	public ManagedStatus getModuleStatus() {
		if (isModule) {
			return status;
		} else {
			return creator.getModuleStatus();
		}
	}

	public ManagedStatus getManagerStatus() {
		if (isModule || isManager) {
			return status;
		} else {
			return creator.getManagerStatus();
		}
	}

	/**
	 * If locked it must have been previously acquired by same thread, if not
	 * throw an exception.
	 * 
	 * @param mc
	 *            managed component to add
	 * 
	 * @throws InvalidCreatedModificationException
	 *             if caller does not have a created lock for the component
	 *             being modified.
	 * 
	 */
	public void addCreated(ManagedComponent mc) {

		if (createdLock.tryLock()) {
			try {
				this.createdComponents.put(mc.getId(), mc);
			} finally {
				createdLock.unlock();
			}
		} else {
			throw new InvalidCreatedModificationException("Not lock owner for component: " + entryIdentifier());
		}
	}

	public Set<UUID> getCreatedIds() {
		return createdComponents.keySet();
	}

	public Optional<ManagedComponent> getCreated(UUID id) {

		Optional<ManagedComponent> ret = Optional.empty();

		ManagedComponent mc = createdComponents.get(id);
		if (null != mc) {
			ret = Optional.of(mc);
		}

		return ret;
	}

	/**
	 * Optionally returns first component of provided type in its collection of
	 * created components. If there is more then one manager per type (i.e. is
	 * scalable), then {@link #getAllCreated(Class)} should be utilized.
	 * 
	 * @param clazz
	 *            type of component to retrieve
	 * 
	 * @returns first created component found matching the provided type. If no
	 *          match found, an empty optional is returned.
	 */
	public <T extends ManagedComponent> Optional<T> getCreated(Class<T> clazz) {

		Optional<T> ret = Optional.empty();

		for (ManagedComponent mc : createdComponents.values()) {
			if (clazz.equals(mc.getType())) {
				ret = Optional.of((T) mc);
				break;
			}
		}

		return ret;

	}

	/**
	 * Optionally returns all components of the provided type in its collection
	 * of created components.
	 * 
	 * @param clazz
	 *            type of components to retrieve
	 * 
	 * @returns a list of created components that match the provided type. If no
	 *          matches found, an empty optional is returned.
	 */
	public <T extends ManagedComponent> Optional<List<T>> getAllCreated(Class<T> clazz) {

		Optional<List<T>> ret = Optional.empty();
		List<T> cList = new ArrayList<>();

		for (ManagedComponent mc : createdComponents.values()) {
			// CDI bean proxy is subclass
			if (clazz.equals(mc.getType())) {
				cList.add((T) mc);
			}
		}

		if (!cList.isEmpty()) {
			ret = Optional.of(cList);
		}

		return ret;

	}

	@SuppressWarnings("unchecked")
	public <T extends ManagedComponent> boolean submitScaleRequest(CreationRequest<T> request) {
		return creationQueue.addRequest(getId(), (CreationRequest<ManagedComponent>) request);
	}

	public CreationRequest<ManagedComponent> removeScaleRequest() throws InterruptedException {
		return creationQueue.removeRequest();
	}

	public ManagedStatus getStatus() {
		return status;
	}

	/**
	 * If locked it must have been previously acquired by same thread, if not
	 * throw an exception.
	 * 
	 * @param status
	 *            component's new status value
	 * @throws InvalidStatusModificationException
	 *             if caller does not have a status lock for the component being
	 *             modified.
	 * 
	 */
	public void setStatus(ManagedStatus status) {

		if (statusLock.tryLock()) {
			try {
				this.status = status;
			} finally {
				statusLock.unlock();
			}
		} else {
			throw new InvalidStatusModificationException("Not lock owner for component: " + entryIdentifier());
		}
	}

	/**
	 * @see ManagedStatusOperation#requestScale()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean requestScale() {

		/**
		 * New creation request for component triggering scale operation (i.e.
		 * this component)
		 */
		CreationRequest request = new CreationRequest(getType(), Optional.of(getId()), configuration());

		/**
		 * Request is added to Creator's creation queue
		 */
		return getCreator().submitScaleRequest(request);
	}

	/**
	 * @see ManagedStatusOperation#activate()
	 */
	@Override
	public boolean activate() {
		log.debug("No activation operation implementation for :: " + this.getType());
		return true;
	}

	/**
	 * @see ManagedStatusOperation#deactivate()
	 */
	@Override
	public boolean deactivate() {
		log.debug("No deactivate operation implementation for :: " + this.getType());
		return true;
	}

	/**
	 * @see ManagedStatusOperation#remove()
	 */
	@Override
	public void remove() {
		log.debug("No remove operation implementation for :: " + this.getType());
	}

	/**
	 * @see ManagedStatusOperation#shutdown()
	 */
	@Override
	public void suspend() {
		log.debug("No suspend operation implementation for :: " + this.getType());
	}

	/**
	 * @see ManagedStatusOperation#shutdown()
	 */
	@Override
	public void shutdown() {
		log.debug("No shutdown operation implementation for :: " + this.getType());
	}

	/**
	 * @see ManagedStatusOperation#fail()
	 */
	@Override
	public void fail() {
		log.debug("No fail operation implementation for :: " + this.getType());
	}

	/**
	 * @see ManagedStatusOperation#check(ComponentMaintenance)
	 */
	@Override
	public <T extends MaintenanceOperation> MaintenanceOperationResult check(SortedSet<T> ops) {

		log.debug("Check operation for :: " + this.getType());

		// Assume NOOP
		MaintenanceOperationResult ret = new MaintenanceOperationResult(PASSED, Noop, Optional.empty());

		if (!ops.isEmpty()) {

			ret = new MaintenanceOperationResult(PASSED, Available, Optional.empty());

			// Record any failed operations.
			Set<T> opFailures = new HashSet<>();

			/**
			 * Process operations. They are ordered by high to low max severity.
			 * 
			 * Processing will terminate if:
			 * 
			 * (1) All operations PASSED
			 * 
			 * (2) An operation FAILED with its max severity
			 * 
			 * (3) Current operation to be processed has a lower max severity
			 * then a previous FAILED operation.
			 */
			for (T op : ops) {

				if (ret.getSeverity().isHigherSeverity(op.maxSeverity())) {
					break; // (3)
				}

				// Perform the check
				MaintenanceOperationResult opResult = op.checkAndRepair();

				// Did not pass (i.e. failed) maintenance check
				if (opResult.getStatus() == FAILED) {

					// Get severity of failure
					MaintenanceOperationSeverity opSeverity = opResult.getSeverity();

					// Record failure
					opFailures.add(op);

					// Update return value to higher severity, if necessary
					if (opSeverity.isHigherSeverity(ret.getSeverity())) {
						ret = opResult;
					}

					// Break if max severity for operation
					if (opSeverity.isSameSeverity(op.maxSeverity())) {
						break; // (2)
					}
				}
			} // (1)

			// Update sorted list for a failed operation. Remove followed by add
			// to
			// re-sort. This will ensure the failed operation is performed
			// before
			// other operations having equal or lower severity level during the
			// next
			// scheduled maintenance check. At any one time, there can be at
			// most
			// one failed operation.
			if (!opFailures.isEmpty()) {
				ops.removeAll(opFailures);
				ops.addAll(opFailures);
			}

		}

		return ret;
	}

	/**
	 * @see ManagedStatusOperation#schedulerCheck(SortedSet)
	 */
	@Override
	public MaintenanceOperationResult schedulerCheck(SortedSet<ScheduleCheck> ops) {
		return check(ops);
	}

	/**
	 * Creates and returns {@code StatusOperationMessages}. These messages, if any,
	 * are qualified by a {@code StatusOperation}, indicating to the compnent's
	 * child observer what status operation to invoke. Messages are also
	 * directed to the component's registry, informing the registry of it's
	 * current status.
	 * 
	 * @return optional {@code ScheduledMessages}, it may be empty if
	 *         implementation determines the report is unnecessary (e.g. no
	 *         change since last report to registry and/or no child candidates
	 *         for a required status operation).
	 */
	@SuppressWarnings("serial")
	public StatusOperationMessages reportStatus() {

		log.debug("REPORTING STATUS:: " + getStatus() + " FOR:: " + entryIdentifier());

		// ComponentEntry reportingStatus = new ComponentEntry(this);
		ComponentEntry reportingStatus = entry();
		ManagedStatus currentStatus = reportingStatus.getcStatus();

		// This is not a locked operation. Therefore, status may change
		// during the method and/or before observance. The operation will be
		// re-verified on observer end to account for this possibility.
		StatusOperationMessages sms = new StatusOperationMessages(reportingStatus);
		Stack<SimpleEntry<Operation, StatusOperationEvent>> ops = new Stack<>();

		// Only report on non-transitional status values
		if (!currentStatus.isTransition()) {

			// Create operation messages
			for (Operation op : Operation.values()) {

				// Only report on observed operations
				if (op.isObserved()) {
					for (UUID candidate : statusEventOperationCandidates(this, op, currentStatus)) {
						ops.push(new SimpleEntry<Operation, StatusOperationEvent>(op,
								new StatusOperationEvent(this, candidate)));
					}
				}
			}
			do {
				List<Annotation> qualifiers = new ArrayList<>();
				Optional<SimpleEntry<Operation, StatusOperationEvent>> opEntry = Optional.empty();

				if (!ops.empty()) {
					opEntry = Optional.of(ops.pop());
				}

				if (opEntry.isPresent()) {

					Operation operation = opEntry.get().getKey();

					qualifiers.add(new ManagedAnnotationLiteral() {
					});

					qualifiers.add(new StatusOperationAnnotationLiteral() {
						@Override
						public Operation operation() {
							// TODO Auto-generated method stub
							return operation;
						}
					});
					sms.addMessage(new StatusOperationMessage(opEntry.get().getValue(), qualifiers));
				}

			} while (!ops.empty());

		}

		return sms;
	}

	private List<UUID> statusEventOperationCandidates(ManagedComponent creator, Operation op, ManagedStatus status) {

		List<UUID> ret = new ArrayList<UUID>();

		switch (op) {

		/**
		 * Activation cascades only for Online creators
		 */
		case Activate:
			if (Online == status) {
				ret = operationCandidates(op);
			}
			break;

		case RequestScale:
			ret = scaleOperationCandidates();
			break;

		case Deactivate:
			if ((Offline == status) || (CheckedOffline == status)) {
				ret = operationCandidates(op);
			}
			break;

		/**
		 * Failed status cascades only for Failed Modules or Managers.
		 */
		case Fail:
			boolean failedModule = !ManagedStatus.isRecoverable(creator.getModuleStatus());
			boolean failedManager = !ManagedStatus.isRecoverable(creator.getManagerStatus());
			if (failedModule || failedManager) {
				if (Failed == status) {
					ret = operationCandidates(op);
				}
			}
			break;

		case Remove:
			ret = operationCandidates(op);
			break;

		default:
			log.error("Unknown operation type requested in report status: " + op.name());
			break;
		}

		return ret;
	}

	private List<UUID> operationCandidates(Operation op) {

		List<UUID> ret = new ArrayList<>();
		for (ManagedComponent mc : createdComponents.values()) {
			if (op.verifyOperation(mc.getStatus())) {
				ret.add(mc.getId());
			}
		}

		return ret;
	}

	/**
	 * Return list of scale candidates. Candidate is only included if it is
	 * valid ( @see {@link #validScaleCandidate(ManagedComponent)} ) and there
	 * is remaining capacity to support its creation taking into consideration
	 * other candidates of the same type that have already been selected.
	 */
	private List<UUID> scaleOperationCandidates() {

		List<UUID> ret = new ArrayList<>();
		Map<Class<?>, Integer> typeCounts = new HashMap<>();

		for (ManagedComponent mc : createdComponents.values()) {

			if (validScaleCandidate(mc)) {

				// Update type counts
				int typeCount = 1;
				if (typeCounts.containsKey(mc.getType())) {
					typeCount = typeCounts.get(mc.getType()) + 1;
					typeCounts.put(mc.getType(), typeCount);
				} else {
					typeCounts.put(mc.getType(), typeCount);
				}

				if ((scaleCapacity(mc.getType()) + typeCount) < scaleMaxCount(mc.getType())) {
					ret.add(mc.getId());
				}
			}
		}

		return ret;
	}

	/**
	 * Returns true if the provided candidate is considered as valid for
	 * triggering a scale event.
	 */
	public boolean validScaleCandidate(ManagedComponent candidate) {

		boolean ret = false;

		ManagedStatus pStatus = getStatus();
		ManagedStatus cStatus = candidate.getStatus();

		// Must be scalable to generate a scale request
		// if (candidate.isScalable()) {
		if (Creator.scalable(candidate.getType())) {

			// Verify status
			if (RequestScale.verifyOperation(cStatus)) {

				// Online is used to ensure not below initial count
				if (cStatus == Online) {

					if ((hasScaleAttempts(candidate)) && (isBelowMinimumCapacity(candidate.getType()))) {
						ret = true;
					}

				}
				// Other status values should be non-factors in capacity
				// calculation.
				else {
					if ((hasScaleAttempts(candidate)) && (hasScaleCapacity(candidate.getType()))) {
						ret = true;
					}
				}
			}

		}

		return ret;

	}

	/**
	 * Determines if the component can be used to trigger a scaling attempt.
	 * 
	 * @param candidate
	 *            the ManagedComponent to check
	 * 
	 * @return false if component is not scalable. Else true is returned if
	 *         {@link #scaledCount} is less then the
	 *         {@link Scalable#allowedCount()}, false otherwise.
	 */
	public boolean hasScaleAttempts(ManagedComponent candidate) {

		boolean ret = false;

		if (scalable(candidate.getType())) {
			ret = ((scaleAllowedCount(candidate.getType()) - candidate.scaledCount) > 0);
		}

		return ret;
	}

	/**
	 * Represents current capacity measure for the provided type. That is, the
	 * count of component's in the collection of created components that match
	 * the provide type and are {@link ManagedStatus#Online}.
	 * 
	 * @param candidateType
	 *            the type of component to measure
	 * 
	 * @return 0 if not a scalable component type. Otherwise returns the count
	 *         of created components that are of the provide type and are
	 *         {@link ManagedStatus#Online}
	 */
	public <T extends ManagedComponent> int scaleCapacity(Class<T> candidateType) {

		int onlineCount = 0;
		if (scalable(candidateType)) {
			for (ManagedComponent mc : createdComponents.values()) {
				if (mc.getType().equals(candidateType)) {
					if (mc.getStatus() == Online) {
						onlineCount++;
					}
				}
			}
		}

		return onlineCount;
	}

	/**
	 * Represents remaining capacity measure for the provided component. That
	 * is, the max capacity minus current capacity.
	 * 
	 * @param candidateType
	 *            the component to measure
	 * 
	 * @return 0 if not a scalable component type. Otherwise returns max
	 *         capacity minus current capacity.
	 */
	public <T extends ManagedComponent> int remainingCapacity(Class<T> candidateType) {

		int remainingCapacity = 0;
		if (scalable(candidateType)) {
			int currentCapacity = scaleCapacity(candidateType);
			remainingCapacity = (scaleMaxCount(candidateType) - currentCapacity);
		}

		return remainingCapacity;
	}

	/**
	 * Returns true if the current capacity measure for the provided type is
	 * below {@link Scalable#maxCount()}
	 * 
	 * @param candidateType
	 *            the component type to check
	 * 
	 * @return false if provided component type is not scalable. Else true is
	 *         returned if current scale capacity if below maximum capacity,
	 *         false otherwise.
	 * 
	 */
	public <T extends ManagedComponent> boolean hasScaleCapacity(Class<T> candidateType) {

		boolean ret = false;
		if (scalable(candidateType)) {
			int currentCapacity = scaleCapacity(candidateType);
			ret = (currentCapacity < scaleMaxCount(candidateType));
		}

		return ret;
	}

	/**
	 * Returns true if the current capacity measure for the provided type is
	 * below {@link Scalable#minCount()}
	 * 
	 * @param candidateType
	 *            the component type to check
	 * 
	 * @return false if provide component type is not scalable. Else true is
	 *         returned if provided component type is below minimum capacity,
	 *         false otherwise.
	 * 
	 */
	public <T extends ManagedComponent> boolean isBelowMinimumCapacity(Class<T> candidateType) {

		boolean ret = false;
		if (scalable(candidateType)) {
			int currentCapacity = scaleCapacity(candidateType);
			ret = (currentCapacity < scaleMinCount(candidateType));
		}

		return ret;
	}

	/**
	 * Default is no maintenance. Override to assign component maintenance.
	 * 
	 * @see ScheduledMaintenance#scheduledMaintenance()
	 */
	@Override
	public ComponentMaintenance scheduledMaintenance() {
		return new ComponentMaintenance(this);
	}

	/**
	 * @see Creator#create()
	 */
	@Override
	public <T extends ManagedComponent> CreationResponse<T> create(CreationRequest<T> request) {

		log.debug("Create operation, creation for type :: " + request.getSubtype().getSimpleName());

		List<T> created = new ArrayList<>();

		try {

			acquireLockWait(STATUS_LOCK);
			acquireLockWait(CREATED_LOCK);

			Class<T> subtype = (Class<T>) request.getSubtype();

			// Add Managed qualifier and convert to Array
			request.getQualifiers().add(getManagedQualifier());
			Annotation[] qualifiers = request.getQualifiers().toArray(new Annotation[0]);

			// Get ManagedComponent instance
			Instance<T> instance = componentProvider.select(subtype, qualifiers);

			//@formatter:off
			log.debug("CREATE: INJECTION STATUS \n" + 
			"\t RESOLVABLE: "  + (true == instance.isResolvable() ? "YES" : "NO") + "\n" + 
			"\t AMBIGUOUS: "   + (true == instance.isAmbiguous() ? "YES" : "NO") + "\n" + 
			"\t UNSATISFIED: " + (true == instance.isUnsatisfied() ? "YES" : "NO")); 
			//@formatter:on		

			if (instance.isResolvable()) {
				created.add(instance.get());
			} else if (instance.isAmbiguous()) {
				Iterator<T> mcItr = instance.iterator();
				while (mcItr.hasNext()) {
					T mc = mcItr.next();
					created.add(mc);
				}
			} else { // InvalidRequest
				throw new InvalidCreationRequestException("Creation request injection could not be satisfied", request);
			}

			/**
			 * Initialize components and add to creator's created collection.
			 * Because the CREATED lock has already been acquired, it's not
			 * essential to use addCreated() method. However, addition of new
			 * items should always use the addCreated() method to ensure
			 * collection is locked for modification.
			 */
			for (T mc : created) {
				mc.configure(request.getConfig());
				addCreated(mc);
				enableComponent(mc);
			}

		} finally {
			releaseLock(CREATED_LOCK);
			releaseLock(STATUS_LOCK);
		}

		return new CreationResponse<T>(request, created);
	}

	@SuppressWarnings("serial")
	private ManagedAnnotationLiteral getManagedQualifier() {

		return new ManagedAnnotationLiteral() {

			@Override
			public String memberId() {
				return getMemberId().toString();
			}

			@Override
			public String moduleId() {
				return getModuleId().toString();
			}

			@Override
			public String managerId() {

				// Assume this is a manager
				String ret = getId().toString();

				// If not a manager, then pass it down the tree
				if (!isManager) {
					ret = getManagerId().toString();
				}

				return ret;
			}

			@Override
			public String creatorId() {
				return getCreatorId().toString();
			}
		};
	}

	/**
	 * 
	 * @see Creator#createAsync(CreationRequest)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ManagedComponent> void createAsync(CreationRequest<T> request) { 
	//public void createAsync(CreationRequest<ManagedComponent> request) {
		creationQueue.addRequest(getId(), (CreationRequest<ManagedComponent>)request);
	}

	/**
	 * @see Creator#scale(CreationRequest)
	 */
	@Override
	public <T extends ManagedComponent> Optional<CreationResponse<T>> scale(CreationRequest<T> request) {

		log.debug("Creator scale operation, scaling for type :: " + request.getSubtype().getSimpleName());

		// return Optional.of(create(request));
		Optional<CreationResponse<T>> response = Optional.empty();

		try {

			acquireLockWait(STATUS_LOCK);
			acquireLockWait(CREATED_LOCK);

			/**
			 * Get lock on scale candidate component.
			 * 
			 * Because the CANDIDATE_LOCK has already been acquired, the
			 * candidate cannot be removed/destroyed (if it does exists) until
			 * this lock has been released.
			 */
			if (!request.getScaleSource().isPresent()) {
				throw new InvalidCreationRequestException(
						"Creation request for scale operation was missing source candidate component identifier.");
			} else {

				// Proceed if the candidate still exists
				Optional<ManagedComponent> scaleCandidateOpt = getCreated(request.getScaleSource().get());
				if (scaleCandidateOpt.isPresent()) {

					ManagedComponent source = scaleCandidateOpt.get();

					try {

						source.acquireLockWait(STATUS_LOCK);

						if (validScaleCandidate(source)) {

							// response = mc.scale(request);
							response = Optional.of(create(request));

							/**
							 * Increment scaled count for source scale candidate
							 * by the number of components created by the scale
							 * operation provided by the response, if present.
							 */
							if (response.isPresent()) {
								source.setScaledCount(source.getScaledCount() + response.get().createdCount());
							}
						}
					} finally {
						source.releaseLock(STATUS_LOCK);
					}
				}
			}

		} finally {
			releaseLock(CREATED_LOCK);
			releaseLock(STATUS_LOCK);
		}

		return response;
	}

	/**
	 * Default implementation returning an empty list meaning the component has
	 * no configuration. However, if this component is configurable a
	 * MissingConfigurationImplementationException will be thrown.
	 * 
	 * @see Creator#configuration()
	 * 
	 */
	@Override
	public Object[] configuration() {

		if (Creator.configurable(getType())) {
			throw new MissingConfigurationImplementationException("Component type: " + getType());
		}

		return new Object[] {};
	}

	/**
	 * @see EntryReporter#entry()
	 */
	@Override
	public ComponentEntry entry() {
		return new ComponentEntry(this);
	}
	
	/**
	 * @see EntryReporter#entryIdentifier()
	 */
	@Override
	public EntryIdentifier entryIdentifier() {
		return this.entryIdentifier;
	}

	/**
	 * @see EntryReporter#entryLocation(EntryLocation)
	 */
	@Override
	public void entryLocation(EntryLocation location) {
		this.location = location;
	}

	/**
	 * @see EntryReporter#entryLocation()
	 */
	@Override
	public EntryLocation entryLocation() {
		return this.location;
	}

	/**
	 * @see EntryReporter#entryProperties()
	 * 
	 *      Default is no properties
	 */
	@Override
	public EntryProperties entryProperties() {
		return new EntryProperties();
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ManagedComponent)) {
			return false;
		}
		ManagedComponent other = (ManagedComponent) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
