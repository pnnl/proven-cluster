package gov.pnnl.proven.cluster.lib.disclosure;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.disclosure.exchange.DisclosureItem;
import gov.pnnl.proven.cluster.lib.disclosure.message.DisclosureMessage;
import gov.pnnl.proven.cluster.lib.disclosure.message.DisclosureResponse;
import gov.pnnl.proven.cluster.lib.disclosure.message.KnowledgeMessage;
import gov.pnnl.proven.cluster.lib.disclosure.message.MessageProperties;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMeasurement;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessageOriginal;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMetric;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenQueryFilter;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenQueryTimeSeries;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenStatement;
import gov.pnnl.proven.cluster.lib.disclosure.message.RequestMessage;
import gov.pnnl.proven.cluster.lib.disclosure.message.ResponseMessage;
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
	public static final int PROVEN_MESSAGE_ORIGINAL_TYPE = 0;
	public static final int MESSAGE_PROPERTIES_TYPE = 1;
	public static final int PROVEN_MEASUREMENT_TYPE = 2;
	public static final int DISCLOSURE_RESPONSE_TYPE = 3;
	public static final int PROVEN_METRIC_TYPE = 4;
	public static final int PROVEN_QUERY_FILTER_TYPE = 5;
	public static final int PROVEN_QUERY_TIME_SERIES_TYPE = 6;
	public static final int PROVEN_STATEMENT_TYPE = 7;
	public static final int DISCLOSURE_MESSAGE_TYPE = 8;
	public static final int KNOWLEDGE_MESSAGE_TYPE = 9;
	public static final int REQUEST_MESSAGE_TYPE = 10;
	public static final int RESPONSE_MESSAGE_TYPE = 12;
	public static final int DISCLOSURE_DOMAIN_TYPE = 13;
	public static final int DISCLOSURE_ITEM_TYPE = 14;
	public static final int RESPONSE_ITEM_TYPE = 15;
	

	@Override
	public IdentifiedDataSerializable create(int typeId) {

		switch (typeId) {
		case (PROVEN_MESSAGE_ORIGINAL_TYPE):
			return new ProvenMessageOriginal();
		case (MESSAGE_PROPERTIES_TYPE):
			return new MessageProperties();
		case (PROVEN_MEASUREMENT_TYPE):
			return new ProvenMeasurement();
		case (DISCLOSURE_RESPONSE_TYPE):
			return new DisclosureResponse();
		case (PROVEN_METRIC_TYPE):
			return new ProvenMetric();
		case (PROVEN_QUERY_FILTER_TYPE):
			return new ProvenQueryFilter();
		case (PROVEN_QUERY_TIME_SERIES_TYPE):
			return new ProvenQueryTimeSeries();
		case (PROVEN_STATEMENT_TYPE):
			return new ProvenStatement();
		case (DISCLOSURE_MESSAGE_TYPE):
			return new DisclosureMessage();
		case (KNOWLEDGE_MESSAGE_TYPE):
			return new KnowledgeMessage();
		case (REQUEST_MESSAGE_TYPE):
			return new RequestMessage();
		case (RESPONSE_MESSAGE_TYPE):
			return new ResponseMessage();
		case (DISCLOSURE_DOMAIN_TYPE):
			return new DisclosureDomain();
		case (DISCLOSURE_ITEM_TYPE):
			return new DisclosureItem();
		case (RESPONSE_ITEM_TYPE):
			return new DisclosureItem();
		default:
			return null;
		}
	}

}
