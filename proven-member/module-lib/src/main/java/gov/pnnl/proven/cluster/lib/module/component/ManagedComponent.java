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

import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.CheckedOffline;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Failed;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Offline;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Online;
import static gov.pnnl.proven.cluster.lib.module.component.ManagedStatus.Unknown;
import static gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationSeverity.Available;
import static gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationStatus.FAILED;
import static gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationStatus.PASSED;
import static gov.pnnl.proven.cluster.lib.module.util.LoggerResource.currentThreadLog;

import java.lang.annotation.Annotation;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;

import fish.payara.micro.PayaraMicro;
import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.member.MemberProperties;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Eager;
import gov.pnnl.proven.cluster.lib.module.component.annotation.LockedStatusOperation;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Managed;
import gov.pnnl.proven.cluster.lib.module.component.annotation.ManagedAnnotationLiteral;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Scalable;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Scheduler;
import gov.pnnl.proven.cluster.lib.module.component.exception.StatusLockException;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.ComponentMaintenance;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.MaintenanceSchedule;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.ScheduledMaintenance;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperation;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.MaintenanceOperationResult;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.operation.SchedulerCheck;
import gov.pnnl.proven.cluster.lib.module.manager.ManagerComponent;
import gov.pnnl.proven.cluster.lib.module.messenger.StatusMessages;
import gov.pnnl.proven.cluster.lib.module.messenger.StatusOperationMessage;
import gov.pnnl.proven.cluster.lib.module.messenger.StatusSchedule;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperation.Operation;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.StatusOperationAnnotationLiteral;
import gov.pnnl.proven.cluster.lib.module.messenger.event.StatusEvent;
import gov.pnnl.proven.cluster.lib.module.messenger.event.StatusOperationEvent;
import gov.pnnl.proven.cluster.lib.module.messenger.observer.ManagedObserver;
import gov.pnnl.proven.cluster.lib.module.module.ProvenModule;
import gov.pnnl.proven.cluster.lib.module.registry.MemberComponentRegistry;

/**
 * 
 * Base module component. Represents managed components within a Proven
 * application module, this includes the {@code ProvenModule} component itself.
 * Each component performs specific tasks to support a module's operation. These
 * components are registered with both their local and clustered registries. A
 * {@code ManagedComponent} may share its resources (compute and data) across
 * the cluster.
 * 
 * @author d3j766
 *
 * @see ProvenModule, RegistryComponent
 * 
 */
@Managed
public abstract class ManagedComponent implements ManagedStatusOperation, ScheduledMaintenance {

	@Inject
	Logger log;

	private static final String BASE_NAME = "component.proven.pnnl.gov";

	@Inject
	protected MemberProperties mp;

	@Inject
	protected HazelcastInstance hzi;

	@Inject
	@Eager
	protected MemberComponentRegistry mcr;

	@Inject
	@Managed
	protected Instance<ManagedComponent> componentProvider;

	@Inject
	@Scheduler(activateOnStartup = false)
	protected StatusSchedule statusSchedule;

	@Inject
	@Scheduler(delay = 10, activateOnStartup = false)
	protected MaintenanceSchedule maintenanceSchedule;

	@Inject
	@Eager
	protected ManagedObserver opObserver;

	// Identifier properties
	protected UUID id;
	protected String doId;
	protected UUID managerId;
	protected UUID creatorId;
	protected String memberId;
	protected UUID moduleId;

	// Address properties
	protected String clusterGroup;
	protected String host;
	protected String containerName;
	protected String moduleName;
	protected Set<ComponentGroup> group;

	// Status properties
	protected ManagedStatus status = Unknown;
	protected ReentrantLock statusLock = new ReentrantLock();

	// Scalable properties
	protected boolean scalable = false;
	protected int allowedScalePerComponent;
	protected int scaleAttempts;
	protected int minScaleCount;
	protected int maxScaleCount;

	/**
	 * Created (i.e. child) components. The Map contains the component's ID as
	 * the key and object as value.
	 */
	protected Map<UUID, ManagedComponent> createdComponents;

	public ManagedComponent() {
		containerName = PayaraMicro.getInstance().getInstanceName();
		id = UUID.randomUUID();
		group = new HashSet<>();
		moduleId = ProvenModule.retrieveModuleId();
		moduleName = ProvenModule.retrieveModuleName();
		if (getComponentType() == ComponentType.ProvenModule) {
			this.id = moduleId;
			this.creatorId = moduleId;
			this.managerId = moduleId;
			this.group.add(ComponentGroup.Module);
		}
		group.add(ComponentGroup.Managed);
		createdComponents = new HashMap<>();

		// Indicates component is being created - status is set to Ready in
		// PostConstruct callback
		setStatus(ManagedStatus.Creating);
	}

	@PostConstruct
	public void managedComponentInit() {

		// Member properties dependent on other injection members
		doId = new DisclosureDomain(BASE_NAME).getReverseDomain() + "." + id + "_" + getComponentType().toString();
		clusterGroup = hzi.getConfig().getGroupConfig().getName();
		host = hzi.getCluster().getLocalMember().getAddress().getHost();
		memberId = hzi.getCluster().getLocalMember().getUuid();

		// Register with the observer
		opObserver.register(this);

		// Get/set scalable properties
		if (this.getClass().isAnnotationPresent(Scalable.class)) {
			Scalable scalableAnnotation = this.getClass().getAnnotation(Scalable.class);
			scalable = true;
			allowedScalePerComponent = scalableAnnotation.alowedPerComponent();
			scaleAttempts = 0;
			minScaleCount = scalableAnnotation.minCount();
			maxScaleCount = scalableAnnotation.maxCount();
		}

		// Register with status scheduler
		statusSchedule.register(this);

		// Register default maintenance
		maintenanceSchedule.register(this);

		// Starting status for a managed component
		setStatus(ManagedStatus.Ready);
	}

	@PreDestroy
	public void managedComponentDestroy() {
		log.debug("ProvenComponent PreDestroy..." + this.getClass().getSimpleName());
	}

	public boolean acquireStatusLockNoWait() {
		log.debug(currentThreadLog("ACQUIRING LOCK NO WAIT"));
		return statusLock.tryLock();
	}

	public void acquireStatusLockWait() {
		log.debug(currentThreadLog("ACQUIRING LOCK WAIT"));
		statusLock.lock();
	}

	public boolean acquireStatusLockWaitTime(Long time, TimeUnit unit) {
		log.debug(currentThreadLog("ACQUIRING LOCK WAIT TIME"));
		boolean ret = false;
		try {
			ret = statusLock.tryLock(time, unit);
		} catch (InterruptedException e) {
			ret = false;
		}

		return ret;
	}

	public void releaseStatusLock() {
		log.debug(currentThreadLog("RELEASE LOCK"));
		statusLock.unlock();
	}

	public String getClusterGroup() {
		return clusterGroup;
	}

	public String getHost() {
		return host;
	}

	public String getMemberId() {
		return memberId;
	}

	public String getContainerName() {
		return containerName;
	}

	public UUID getModuleId() {
		return moduleId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public UUID getId() {
		return id;
	}

	public Set<ComponentGroup> getComponentGroups() {
		return group;
	}

	public abstract ComponentType getComponentType();

	public String getDoId() {
		return doId;
	}

	public boolean isScalable() {
		return this.getClass().isAnnotationPresent(Scalable.class);
	}

	public StatusSchedule getStatusSchedule() {
		return statusSchedule;
	}

	public MaintenanceSchedule getMaintenanceSchedule() {
		return maintenanceSchedule;
	}

	public <T extends ManagedComponent> T createComponent(Class<T> subtype, Annotation... qualifiers) {
		// Get component
		T mc = componentProvider.select(subtype, qualifiers).get();
		createdComponents.put(mc.getId(), mc);
		log.debug("NEW COMPONENT created - " + mc.getComponentType().toString());
		enableComponent(mc);
		log.debug("NEW COMPONENT enabled - " + mc.getComponentType().toString());
		return mc;
	}

	public <T extends ManagedComponent> List<T> createComponents(Class<T> subtype, Annotation... qualifiers) {
		List<T> ret = new ArrayList<>();
		Iterator<T> mcItr = componentProvider.select(subtype, qualifiers).iterator();
		while (mcItr.hasNext()) {
			T mc = mcItr.next();
			createdComponents.put(mc.getId(), mc);
			enableComponent(mc);
			ret.add(mc);
		}
		return ret;
	}

	private void enableComponent(ManagedComponent mc) {

		// If the caller is a manager component, then use its identifier,
		// otherwise pass through the manager identifier for the caller to the
		// new component.
		if (this instanceof ManagerComponent) {
			mc.setManagerId(getId());
		} else {
			mc.setManagerId(getManagerId());
		}

		// Creator is always the caller's identifier
		mc.setCreatorId(getId());

		// Activate scheduled messenger
		mc.getStatusSchedule().start();

		// Activate scheduled maintenance
		mc.getMaintenanceSchedule().start();
	}

	public UUID getManagerId() {
		return managerId;
	}

	protected void setManagerId(UUID managerId) {
		this.managerId = managerId;
	}

	public UUID getCreatorId() {
		return creatorId;
	}

	protected void setCreatorId(UUID creatorId) {
		this.creatorId = creatorId;
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

	public ManagedStatus getStatus() {
		return status;
	}

	public void setStatus(ManagedStatus status) throws StatusLockException {
		if (statusLock.tryLock()) {
			try {
				this.status = status;
			} finally {
				statusLock.unlock();
			}
		} else {
			throw new StatusLockException("Failed to acquire status lock when setting status for: " + getDoId());
		}

	}

	/**
	 * @see ManagedStatusOperation#activate()
	 */
	@Override
	@LockedStatusOperation
	public boolean activate() {
		log.debug("No activation operation implementation for :: " + this.getComponentType());
		return true;
	}

	/**
	 * @see ManagedStatusOperation#scale()
	 */
	@Override
	public void scale(UUID scaled) {
		log.warn("No scale operation implementation for a scalable component provided :: " + this.getComponentType());
	}

	/**
	 * @see ManagedStatusOperation#deactivate()
	 */
	@Override
	@LockedStatusOperation
	public boolean deactivate() {
		log.debug("No deactivate operation implementation for :: " + this.getComponentType());
		return true;
	}

	/**
	 * @see ManagedStatusOperation#remove()
	 */
	@Override
	@LockedStatusOperation
	public void remove() {
		log.debug("No remove operation implementation for :: " + this.getComponentType());
	}

	/**
	 * @see ManagedStatusOperation#shutdown()
	 */
	@Override
	@LockedStatusOperation
	public void shutdown() {
		log.debug("No shutdown operation implementation for :: " + this.getComponentType());
	}

	/**
	 * @see ManagedStatusOperation#fail()
	 */
	@Override
	@LockedStatusOperation
	public void fail() {
		log.debug("No fail operation implementation for :: " + this.getComponentType());
	}

	/**
	 * @see ManagedStatusOperation#check(ComponentMaintenance)
	 */
	@Override
	@LockedStatusOperation
	public MaintenanceOperationResult check(SortedSet<MaintenanceOperation> ops) {

		// Assume all checks pass
		MaintenanceOperationResult ret = new MaintenanceOperationResult(PASSED, Available, Optional.empty());

		// Record any failed operations.
		Set<MaintenanceOperation> opFailures = new HashSet<>();

		for (MaintenanceOperation mo : ops) {

			// Perform the check
			MaintenanceOperationResult opResult = mo.checkAndRepair();

			// Set as return value if it's a higher severity.
			if (opResult.getSeverity().isHigherSeverity(ret.getSeverity())) {
				ret = opResult;
			}

			// Did not pass maintenance check, add to failures list
			if (opResult.getStatus() == FAILED) {
				opFailures.add(mo);

				// Break if max severity for failure
				if (opResult.getSeverity() == mo.maximumSeverity()) {
					break;
				}
			}
		}

		// Update sorted list for a failed operation. Remove followed by add to
		// re-sort. This will ensure the failed operation is performed before
		// other operations having equal or lower severity level during the next
		// scheduled maintenance check. At any one time, there can be at most
		// one failed operation.
		if (!opFailures.isEmpty()) {
			ops.removeAll(opFailures);
			ops.addAll(opFailures);
		}

		return ret;
	}

	/**
	 * @see ManagedStatusOperation#schedulerCheck(SchedulerCheck)
	 */
	@Override
	@LockedStatusOperation
	public MaintenanceOperationResult schedulerCheck(SchedulerCheck op) {
		SortedSet<MaintenanceOperation> ops = new TreeSet<>();
		ops.add(op);
		return check(ops);
	}

	/**
	 * Optionally creates and returns {@code StatusEvent}
	 * {@code ScheduledMessages}. The component's status messages, if any, are
	 * qualified by the {@code StatusOperation.Operation}, indicating to the
	 * compnent's child observer what status operation to invoke. Messages are
	 * also directed to the component's registry, informing the registry of it's
	 * current status.
	 * 
	 * @return optional {@code ScheduledMessages}, it may be empty if
	 *         implementation determines the report is unnecessary (e.g. no
	 *         change since last report to registry and/or no child candidates
	 *         for a required status operation).
	 */
	@SuppressWarnings("serial")
	public StatusMessages reportStatus() {

		log.debug("REPORTING STATUS:: " + getStatus() + " FOR:: " + getDoId());

		StatusEvent reportingStatus = new StatusEvent(this);
		ManagedStatus currentStatus = reportingStatus.getRequestorStatus();

		// This is not a locked status operation. Therefore, status may change
		// during the method and/or before observance. The operation will be
		// re-verified on observer end to account for this possibility.
		StatusMessages sms = new StatusMessages(reportingStatus);
		Stack<SimpleEntry<Operation, StatusOperationEvent>> ops = new Stack<>();

		// Only report on non-transitional status values
		if (!currentStatus.isTransition()) {

			// Create operation messages
			for (Operation op : Operation.values()) {
				for (UUID candidate : statusEventOperationCandidates(op, currentStatus)) {
					ops.push(new SimpleEntry<Operation, StatusOperationEvent>(op,
							new StatusOperationEvent(this, candidate)));
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

	private List<UUID> statusEventOperationCandidates(Operation op, ManagedStatus status) {

		List<UUID> ret = new ArrayList<UUID>();

		switch (op) {

		case Activate:
			if (Online == status) {
				ret = hasCreatedCandidates(op);
			}
			break;

		case Scale:
			if (Online == status) {
				ret = hasCreatedCandidates(op);
			}
			break;

		case Deactivate:
			if ((Offline == status) || (CheckedOffline == status)) {
				ret = hasCreatedCandidates(op);
			}
			break;

		case Fail:
			if (Failed == status) {
				ret = hasCreatedCandidates(op);
			}
			break;

		case Remove:
			ret = hasCreatedCandidates(op);
			break;

		case Shutdown:
			// Not triggered by a status message
			break;

		case Check:
			// Not triggered by a status message
			break;
			
		case SchedulerCheck:
			// Not triggered by a status message
			break;

		default:
			log.error("Unknown Creator operation type requested in report status");
			break;
		}

		return ret;
	}

	private List<UUID> hasCreatedCandidates(Operation op) {

		List<UUID> ret = new ArrayList<>();
		for (ManagedComponent mc : createdComponents.values()) {
			if (op.verifyOperation(mc.getStatus())) {
				ret.add(mc.getId());
			}
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
