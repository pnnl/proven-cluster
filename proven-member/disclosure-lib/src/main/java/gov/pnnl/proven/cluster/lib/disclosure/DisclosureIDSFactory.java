package gov.pnnl.proven.cluster.lib.disclosure;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.DisclosureMessage;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.DisclosureResponse;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.KnowledgeMessage;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.MessageProperties;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.ProvenMeasurement;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.ProvenMessageOriginal;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.ProvenMetric;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.ProvenQueryFilter;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.ProvenQueryTimeSeries;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.ProvenStatement;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.RequestMessage;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.ResponseMessage;
import gov.pnnl.proven.cluster.lib.disclosure.item.DisclosureItem;
import gov.pnnl.proven.cluster.lib.disclosure.item.MessageContext;
import gov.pnnl.proven.cluster.lib.member.IDSFactory;

/**
 * 
 * Hazelcast DataSerializableFactory factory for disclosure-lib.
 * 
 * @author d3j766
 *
 */
public class DisclosureIDSFactory implements DataSerializableFactory {

	public DisclosureIDSFactory() {
	}

	// Factory
	public static final int FACTORY_ID = IDSFactory.DISCLOSURE.getFactoryId();

	// Serializable types
	public static final int DISCLOSURE_DOMAIN_TYPE = 1;
	public static final int DISCLOSURE_ITEM_TYPE = 2;
	public static final int MESSAGE_CONTEXT_TYPE = 3;
	
	@Override
	public IdentifiedDataSerializable create(int typeId) {

		switch (typeId) {
		
		case (DISCLOSURE_DOMAIN_TYPE):
			return new DisclosureDomain();
		case (DISCLOSURE_ITEM_TYPE):
			return new DisclosureItem();
		case (MESSAGE_CONTEXT_TYPE):
			return new MessageContext();
		default:
			return null;
		}
	}

}
