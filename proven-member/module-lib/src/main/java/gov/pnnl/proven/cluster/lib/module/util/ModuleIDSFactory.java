package gov.pnnl.proven.cluster.lib.module.util;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.disclosure.deprecated.exchange.ResponseItem;
import gov.pnnl.proven.cluster.lib.member.IDSFactory;
import gov.pnnl.proven.cluster.lib.module.messenger.event.StatusOperationEvent;
import gov.pnnl.proven.cluster.lib.module.registry.ComponentEntry;
import gov.pnnl.proven.cluster.lib.module.registry.EntryIdentifier;
import gov.pnnl.proven.cluster.lib.module.registry.EntryLocation;
import gov.pnnl.proven.cluster.lib.module.registry.EntryProperties;
import gov.pnnl.proven.cluster.lib.module.registry.EntryProperty;
import gov.pnnl.proven.cluster.lib.module.registry.MaintenanceOperationResultEntry;

/**
 * 
 * Hazelcast DataSerializableFactory for module-lib classes.
 * 
 * @author d3j766
 *
 */
public class ModuleIDSFactory implements DataSerializableFactory {

	public ModuleIDSFactory() {
	}

	// Factory
	public static final int FACTORY_ID = IDSFactory.MODULE.getFactoryId();

	// Serializable types
	public static final int ENTRY_IDENTIFIER_TYPE = 0;
	public static final int ENTRY_PROPERTY_TYPE = 1;
	public static final int ENTRY_PROPERTIES_TYPE = 2;
	public static final int ENTRY_LOCATION_TYPE = 3;
	public static final int COMPONENT_ENTRY_TYPE = 4;
	public static final int MAINTENENACE_OPERATION_RESULT_ENTRY_TYPE = 5;
	public static final int MAINTENANCE_RESULT_ENTRY_TYPE = 6;
	public static final int STATUS_OPERATION_EVENT_TYPE = 7;
	public static final int RESPONSE_ITEM_TYPE = 8;
	
	
	@Override
	public IdentifiedDataSerializable create(int typeId) {

		switch (typeId) {
		case (ENTRY_IDENTIFIER_TYPE):
			return new EntryIdentifier();
		case (ENTRY_PROPERTY_TYPE):
			return new EntryProperty();
		case (ENTRY_PROPERTIES_TYPE):
			return new EntryProperties();
		case (ENTRY_LOCATION_TYPE):
			return new EntryLocation();
		case (COMPONENT_ENTRY_TYPE):
			return new ComponentEntry();
		case (MAINTENENACE_OPERATION_RESULT_ENTRY_TYPE):
			return new MaintenanceOperationResultEntry();
		case (MAINTENANCE_RESULT_ENTRY_TYPE):
			return new MaintenanceOperationResultEntry();
		case (STATUS_OPERATION_EVENT_TYPE):
			return new StatusOperationEvent();
		case (RESPONSE_ITEM_TYPE):
			return new ResponseItem();
		default:
			return null;
		}
	}

}
