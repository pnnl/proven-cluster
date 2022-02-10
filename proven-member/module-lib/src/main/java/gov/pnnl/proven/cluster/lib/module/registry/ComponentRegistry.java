/*******************************************************************************
 * CopFyright (c) 2017, Battelle Memorial Institute All rights reserved.
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

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.member.MemberProperties;
import gov.pnnl.proven.cluster.lib.module.component.ManagedStatus;
import gov.pnnl.proven.cluster.lib.module.exchange.Exchange;
import gov.pnnl.proven.cluster.lib.module.exchange.ExchangeComponent;
import gov.pnnl.proven.cluster.lib.module.exchange.ExchangeEntry;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.Module;
import gov.pnnl.proven.cluster.lib.module.module.ModuleStatus;
import gov.pnnl.proven.cluster.lib.module.module.ProvenModule;
import gov.pnnl.proven.cluster.lib.module.module.ProvenModule.LocationSelector;
import gov.pnnl.proven.cluster.lib.module.util.ModuleIDSFactory;

/**
 * A registry of component entries. The registry is responsible for keeping
 * current entry records for each of its module's managed components.
 * 
 * A cluster wide registry is created from the combination of all member
 * component registries inside a cluster, see {@link #clusterComponents}.
 * 
 * Note: Each Hazelcast member contains exactly one proven member-module.
 * Meaning, each Hazelcast member has a single component registry. The terms
 * member and module can be used interchangeably.
 * 
 * @author d3j766
 *
 * @see ComponentEntry, EntryLocation, EntryIdentifier
 *
 */
@ApplicationScoped
public class ComponentRegistry implements Exchange {

    @Inject
    Logger log;

    MemberProperties props = MemberProperties.getInstance();

    @Inject
    HazelcastInstance hzi;

    @Inject
    @Module
    ProvenModule pm;

    /**
     * Cluster listener key. Used to add/remove cluster listener if lead module
     * status is removed.
     * 
     * @see this{@link #leadModule()}
     */
    UUID clusterListenerKey = null;

    /**
     * Used by the scheduled {@link #localCleanup()} method to ensure only one local
     * cleanup task is running at any given time.
     */
    AtomicBoolean localClean = new AtomicBoolean(false);

    /**
     * (Local) Module Components
     * 
     * Local storage of all component entries for this Module.
     * 
     */
    TreeSet<ComponentEntry> moduleComponents = new TreeSet<ComponentEntry>();

    /**
     * (IMDG) Cluster components
     * 
     * All cluster component entries.
     * 
     */
    IMap<EntryIdentifier, ComponentEntry> clusterComponents;

    /**
     * Current set of member locations and their exchanges, including this member,
     * used to select an exchange component supporting a transfer/exchange request.
     */
    ExchangeLocations locations;

    /**
     * Default constructor
     */
    public ComponentRegistry() {
	System.out.println("Inside ComponentRegistry constructor");
    }

    @PostConstruct
    public void initialize() {
	log.debug("Inside ComponentRegistry PostConstruct");
	clusterComponents = hzi.getMap(props.getClusterComponentRegistryName());
	locations = new ExchangeLocations();
    }

    public ExchangeLocations getExchangeLocations() {
	return locations;
    }

    /**
     * Records reported ComponentEntry event made by a ManagedComponent. Following
     * are the steps performed to record entries at the module, member, and cluster
     * levels:
     * 
     * <ol>
     * <li>Add time of recording, used for overdue reporting calculations</li>
     * <li>Adds entry to local module components</li>
     * <li>Adds entry to IMDG cluster components</li>
     * <li>Updates local member exchange, and IMDG member exchange</li>
     * </ol>
     * 
     * @param entry
     *            reported Component entry to record
     */
    public void record(ComponentEntry entry) {

	// 1
	entry.setRecorded(new Date().getTime());

	// 2 (Local)
	recordModuleComponent(entry);

	// 3 (IMDG)
	recordClusterComponent(entry);

	// 4 (Local and IMDG)
	if (entry instanceof ExchangeEntry) {
	    locations.updateExchange((ExchangeEntry) entry);
	}
    }

    /**
     * A scheduled cleanup task.
     * 
     * Manages local module component entries that are overdue in reporting their
     * status.
     * 
     * If it can be determined that the component no longer exists, its status will
     * be updated to {@value ManagedStatus#Destroyed}, otherwise its status will be
     * set to {@value ManagedStatus#Unknown}.
     * 
     * Updated entries will be recorded/removed from the cluster registry for
     * Unknown and Destroyed, respectively.
     * 
     * TODO make schedule time configurable
     * 
     */
    @Schedule(minute = "*/5", hour = "*", persistent = false)
    private void localCleanup() {

	// Do work only if it's not already running
	if (!localClean.compareAndSet(false, true)) {
	    return;
	}
	try {

	    // TODO Cleanup work here
	    log.debug("LOCAL CLEANUP TASK INVOKED");

	} finally {
	    localClean.set(false);
	}

    }

    private void recordModuleComponent(ComponentEntry entry) {

	/**
	 * Create new ModuleEntry, if necessary. And add entry to module.
	 */
	synchronized (moduleComponents) {

	    if (!moduleComponents.add(entry)) {
		moduleComponents.remove(entry);
		moduleComponents.add(entry);
	    }
	}
    }

    private void recordClusterComponent(ComponentEntry entry) {
	clusterComponents.set(entry.getEntryId(), entry);
    }

    private void removeModuleComponent(ComponentEntry entry) {

	synchronized (moduleComponents) {
	    moduleComponents.remove(entry);
	}
    }

    private void removeClusterComponent(ComponentEntry entry) {
	clusterComponents.remove(entry.getEntryId());
    }

    /**
     * Provides access to current member exchanges (including this member) and their
     * locations.
     */
    public class ExchangeLocations {

	/**
	 * (Local) Member location
	 * 
	 * Local storage of this member's location.
	 * 
	 * @see MemnberExchange
	 * 
	 */
	MemberLocation localLocation = new MemberLocation();

	/**
	 * (Local) Member exchange
	 * 
	 * Local storage of available exchange components for this member.
	 * 
	 * @see MemnberExchange
	 * 
	 */
	MemberExchange localExchange = new MemberExchange();

	/**
	 * (Local) Sorted locations
	 * 
	 * Sorted member locations for the cluster. Used to get next member for exchange
	 * processing.
	 * 
	 * @see MemberLocation, LocationSelector
	 * 
	 */
	TreeSet<MemberLocation> sortedLocations;

	/**
	 * (IMDG) Member exchange
	 * 
	 * Available exchange components, by member, for the cluster. Key is the member
	 * identifier and Value contains the available exchange components for the
	 * identified Member.
	 */
	IMap<UUID, MemberExchange> remoteExchanges;

	/**
	 * (IMDG) Member location
	 * 
	 * Available member locations, by member, for the cluster. Key is the member
	 * identifier and Value contains the member's location information.
	 */
	IMap<UUID, MemberLocation> remoteLocations;

	/**
	 * Used by the scheduled {@link #updateLocation()} method to ensure only one
	 * update task is running at any given time.
	 */
	AtomicBoolean updateLocationScheduler = new AtomicBoolean(false);

	/**
	 * Initializes both local and remote location and exchange information.
	 */
	public ExchangeLocations() {

	    /**
	     * Initialize member locations, both local and remote
	     */
	    remoteLocations = hzi.getMap(props.getMemberLocationName());
	    sortedLocations = new TreeSet<MemberLocation>(new MemberLocationSelection());
	    synchronized (sortedLocations) {
		sortedLocations.add(localLocation);
		sortedLocations.addAll(remoteLocations.values());
	    }

	    /**
	     * Initialize member exchanges, both local and remote
	     */
	    remoteExchanges = hzi.getMap(props.getMemberExchangeName());
	    remoteExchanges.set(pm.getMemberId(), localExchange);
	}

	/**
	 * Updates the member's exchange, using the provided entry. Both local and
	 * remote are updated if it has changed.
	 * 
	 * @param entry
	 *            the component entry report
	 */
	public void updateExchange(ExchangeEntry entry) {

	    log.debug("Updating exchange...");
	    boolean hasComponent = localExchange.contains(entry);
	    log.debug("For entry: " + entry.getcName());
	    if (ManagedStatus.isAvailable(entry.getcStatus())) {
		if (!hasComponent) {
		    log.debug("Adding new exchange component to local exchange: " + entry.getcName());
		    localExchange.add(entry);
		}
	    } else {
		if (hasComponent) {
		    log.debug("Removing exchange component from local exchange: " + entry.getcName());
		    localExchange.remove(entry);
		}
	    }
	}

	/**
	 * A scheduled task to update this members location. Both remote and local
	 * representations will be updated, if it has changed since the last update.
	 * 
	 * Note: A change means either the status or location selector has changed.
	 * 
	 * TODO make schedule time configurable
	 */
	@Schedule(second = "*/3", minute = "*/*", hour = "*/*", persistent = false)
	private void updateLocation() {

	    log.debug("Updating location...");
	    
	    // Do work only if it's not already running
	    if (!updateLocationScheduler.compareAndSet(false, true)) {
		return;
	    }
	    try {
		log.debug("Update location task invoked");

		boolean publish = false;
		MemberLocation current = new MemberLocation();
		MemberLocation previous = localLocation;

		int changedStatus = current.getStatus().compareTo(previous.getStatus());
		if (changedStatus != 0) {
		    publish = true;
		} else {
		    int changedLocation = current.getLocationSelctor().compareTo(previous.getLocationSelctor());
		    if (changedLocation != 0)
			publish = true;
		}

		if (publish) {
		    log.debug("Publishing new exchange location");
		    localLocation = current;
		    synchronized (sortedLocations) {
			sortedLocations.remove(previous);
			sortedLocations.add(current);
		    }

		}

	    } finally {
		updateLocationScheduler.set(false);
	    }
	}

	/**
	 * Performs search for an exchange component that will accept/receive an
	 * exchange request originating from the provided fromType. The search always
	 * looks at the local registry fist before expanding search to include the other
	 * member locations.
	 * 
	 * @param fromType
	 *            the type of component requesting the exchange
	 * 
	 * @return a reference to the receiving components exchange queue. If a
	 *         component cannot be found, null is returned.
	 */
	String findTransferComponent(Class<? extends ExchangeComponent> fromType) {

	    String exchangeQueue = null;

	    // Get component name
	    String toType = fromType.getSimpleName();

	    // try local exchange first before trying remotes
	    exchangeQueue = localExchange.retrieve(toType);

	    // try remote exchanges
	    if (null == exchangeQueue) {

		// Selection comparator, used to get next location
		MemberLocationSelection mls = (MemberLocationSelection) sortedLocations.comparator();

		/**
		 * Get location to start search with. Can only loop through remote exchanges one
		 * time. Search will end if a component queue is discovered or the search has
		 * looked at all remotes without finding a component queue.
		 */
		MemberLocation location = mls.nextSelection();
		MemberLocation firstLocation = location;
		do {
		    if (location != localLocation) {
			if (location.getStatus() == ModuleStatus.Running) {
			    MemberExchange remoteExchange = remoteExchanges.get(location.getMemberId());
			    exchangeQueue = remoteExchange.retrieve(toType);
			}
		    }

		    // Get next remote
		    location = mls.nextSelection(location);

		} while ((location != firstLocation) && (null == exchangeQueue));
	    }

	    return exchangeQueue;
	}

	/**
	 * Provides comparison for member locations to support member exchange
	 * selections.
	 * 
	 * @author d3j766
	 *
	 */
	public class MemberLocationSelection implements Comparator<MemberLocation> {

	    /**
	     * Seed with local location.
	     */
	    private AtomicReference<MemberLocation> lastSelection = new AtomicReference<>(localLocation);

	    public MemberLocationSelection() {
	    }

	    public MemberLocation nextSelection() {
		while (true) {
		    MemberLocation last = lastSelection.get();
		    MemberLocation next = nextSelection(last);
		    if (lastSelection.compareAndSet(last, next)) {
			return next;
		    }
		}
	    }

	    public MemberLocation nextSelection(MemberLocation location) {
		MemberLocation next = sortedLocations.higher(location);
		if (null == location) {
		    next = sortedLocations.first();
		}
		return next;
	    }

	    @Override
	    public int compare(MemberLocation o1, MemberLocation o2) {

		int ret = o1.getMemberId().compareTo(o2.getMemberId());
		if (ret != 0) {
		    LocationSelector ls1 = o1.getLocationSelctor();
		    LocationSelector ls2 = o2.getLocationSelctor();
		    int lsCompare = ls1.compareTo(ls2);
		    if (lsCompare != 0) {
			ret = lsCompare;
		    }
		}
		return ret;
	    }
	}

	/**
	 * Comparator for member exchange entries.
	 * 
	 * Note: Using apache commons to support null values in comparison. null value
	 * is less than non null value.
	 * 
	 * @author d3j766
	 * 
	 * @see MemberExchange
	 *
	 */
	public class MemberExchangeEntrySelection implements Comparator<SimpleEntry<String, String>>, Serializable {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public int compare(SimpleEntry<String, String> o1, SimpleEntry<String, String> o2) {

		int ret = StringUtils.compare(o1.getKey(), o2.getKey());
		if (ret == 0) {
		    ret = StringUtils.compare(o1.getValue(), o2.getValue());
		}
		return ret;
	    }

	}

	/**
	 * Map listener to keep local sorted member locations synchronized.
	 * 
	 * @author d3j766
	 *
	 */
	public class MemberLocationListener
		implements EntryAddedListener<UUID, MemberLocation>, EntryRemovedListener<UUID, MemberLocation> {

	    @Override
	    public void entryAdded(EntryEvent<UUID, MemberLocation> event) {
		
		log.debug("Member Location Listener: location added for member: " + event.getValue().memberId.toString());
		synchronized (sortedLocations) {
		    boolean added = sortedLocations.add(event.getValue());
		    if (!added) {
			sortedLocations.remove(event.getValue());
			sortedLocations.add(event.getValue());
		    }
		}
	    }

	    @Override
	    public void entryRemoved(EntryEvent<UUID, MemberLocation> event) {
		log.debug("Member Location Listener: location removed for member: " + event.getValue().memberId.toString());
		synchronized (sortedLocations) {
		    sortedLocations.remove(event.getValue());
		}
	    }
	}

	/**
	 * Represents a member's available exchange components.
	 *
	 * @see ExchangeComponent
	 * 
	 * @author d3j766
	 *
	 */
	public class MemberExchange implements IdentifiedDataSerializable {

	    /**
	     * Set of pairs where Key is type of exchange component using
	     * {@link Class#getSimpleName()} and Value is name of exchange queue reference.
	     */
	    private TreeSet<SimpleEntry<String, String>> exchangeQueues;

	    /**
	     * Epoch time last update was made to exchange.
	     */
	    private long lastUpdated;

	    public MemberExchange() {
		this.exchangeQueues = new TreeSet<>(new MemberExchangeEntrySelection());
		this.lastUpdated = updateTime();
	    }

	    private long updateTime() {
		return new Date().getTime();
	    }

	    public long getLastUpdated() {
		return lastUpdated;
	    }

	    /**
	     * Returns the total number of available exchange components.
	     */
	    public int available() {
		return exchangeQueues.size();
	    }

	    public <U extends ExchangeComponent> String getComponentName(Class<U> componentType) {
		return componentType.getSimpleName();
	    }

	    /**
	     * Determines if an exchange component, provided by entry, is included in the
	     * local exchange.
	     * 
	     * @param entry
	     *            the entry to search for inside the exchange
	     * 
	     * @return true if provided entry is in exchange. false otherwise.
	     */
	    public boolean contains(ExchangeEntry entry) {
		SimpleEntry<String, String> component = new SimpleEntry<>(entry.getcName(), entry.getExchangeQueue());
		return exchangeQueues.contains(component);
	    }

	    /**
	     * Provided entry is added to exchange.
	     * 
	     * @param entry
	     *            component entry to add
	     * 
	     * @return true if the entry is not already present in the exchange, otherwise
	     *         false.
	     */
	    public boolean add(ExchangeEntry entry) {

		boolean ret = true;
		String componentName = entry.getcName();
		String exchangeQueue = entry.getExchangeQueue();
		SimpleEntry<String, String> se = new SimpleEntry<>(componentName, exchangeQueue);

		synchronized (exchangeQueues) {
		    if (!exchangeQueues.add(se)) {
			ret = false;
		    } else {
			this.lastUpdated = updateTime();
		    }
		}
		return ret;
	    }

	    /**
	     * Provided entry is removed from exchange.
	     * 
	     * @param entry
	     *            component entry to remove
	     * 
	     * @return true if the entry is present in the exchange, otherwise false.
	     */
	    public boolean remove(ExchangeEntry entry) {

		boolean ret = true;
		String componentName = entry.getcName();
		String queueName = entry.getExchangeQueue();
		SimpleEntry<String, String> se = new SimpleEntry<>(componentName, queueName);

		synchronized (exchangeQueues) {
		    if (!exchangeQueues.remove(se)) {
			ret = false;
		    } else {
			this.lastUpdated = updateTime();
		    }
		}
		return ret;
	    }

	    /**
	     * Retrieves a queue name from exchange for the provided type of component.
	     * 
	     * @param componentName
	     *            the type of component
	     * 
	     * @return an exchange queue name if exists, otherwise null is returned.
	     */
	    public String retrieve(String componentName) {

		String ret = null;
		List<String> names = new ArrayList<>();

		synchronized (exchangeQueues) {

		    SimpleEntry<String, String> fromEntry = exchangeQueues
			    .ceiling(new SimpleEntry<String, String>(componentName, null));

		    // Is their a floor for the component type
		    if ((null != fromEntry) && (fromEntry.getKey().equals(componentName))) {

			Iterator<SimpleEntry<String, String>> entryItr = exchangeQueues.tailSet(fromEntry).iterator();
			while (entryItr.hasNext()) {
			    SimpleEntry<String, String> entry = entryItr.next();
			    if (entry.getKey().equals(componentName)) {
				names.add(entry.getValue());
			    } else {
				// iterated over all entries for component name
				break;
			    }
			}
		    }
		}

		// Select a random entry from candidate names for return
		if (!names.isEmpty()) {
		    Random rndm = new Random();
		    int index = rndm.nextInt(names.size());
		    ret = names.get(index);
		}

		return ret;
	    }

	    @Override
	    public void readData(ObjectDataInput in) throws IOException {
		int numEntries = in.readInt();
		for (int i = 0; i < numEntries; i++) {
		    String key = in.readUTF();
		    String val = in.readUTF();
		    exchangeQueues.add(new SimpleEntry<String, String>(key, val));
		}
		this.lastUpdated = in.readLong();
	    }

	    @Override
	    public void writeData(ObjectDataOutput out) throws IOException {
		out.writeInt(exchangeQueues.size());
		Iterator<SimpleEntry<String, String>> itr = exchangeQueues.iterator();
		while (itr.hasNext()) {
		    SimpleEntry<String, String> se = itr.next();
		    out.writeUTF(se.getKey());
		    out.writeUTF(se.getValue());
		}
		out.writeLong(lastUpdated);
	    }

	    @Override
	    public int getFactoryId() {
		return ModuleIDSFactory.FACTORY_ID;
	    }

	    @Override
	    public int getId() {
		return ModuleIDSFactory.MEMBER_EXCHANGE_TYPE;
	    }
	}

	/**
	 * A member's "location" within the cluster. The location is used for member
	 * exchange selection for the ComponentRegistry.
	 * 
	 * @author d3j766
	 * 
	 * @see ComponentRegistry, MemberExchange
	 *
	 */
	public class MemberLocation implements IdentifiedDataSerializable {

	    private long created;
	    private UUID memberId;
	    private ModuleStatus status;
	    private LocationSelector locationSelector;

	    public MemberLocation() {
		this.created = new Date().getTime();
		this.status = pm.retrieveModuleStatus();
		this.memberId = pm.getMemberId();
		this.locationSelector = pm.new LocationSelector();
	    }

	    public long getCreated() {
		return created;
	    }

	    public UUID getMemberId() {
		return memberId;
	    }

	    public ModuleStatus getStatus() {
		return status;
	    }

	    public LocationSelector getLocationSelctor() {
		return locationSelector;
	    }

	    @Override
	    public boolean equals(Object obj) {
		if (this == obj)
		    return true;
		if (obj == null)
		    return false;
		if (getClass() != obj.getClass())
		    return false;
		MemberLocation other = (MemberLocation) obj;
		return Objects.equals(memberId, other.memberId);
	    }

	    @Override
	    public void writeData(ObjectDataOutput out) throws IOException {
		out.writeLong(created);
		out.writeUTF(memberId.toString());
		out.writeUTF(status.toString());
	    }

	    @Override
	    public void readData(ObjectDataInput in) throws IOException {
		this.created = in.readLong();
		this.memberId = UUID.fromString(in.readUTF());
		this.status = ModuleStatus.valueOf(in.readUTF());
	    }

	    @Override
	    public int getFactoryId() {
		return ModuleIDSFactory.FACTORY_ID;
	    }

	    @Override
	    public int getId() {
		return ModuleIDSFactory.MEMBER_LOCATION_TYPE;
	    }
	}

    }

}
