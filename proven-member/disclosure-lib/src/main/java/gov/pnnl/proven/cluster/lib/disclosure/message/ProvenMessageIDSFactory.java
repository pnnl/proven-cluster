package gov.pnnl.proven.cluster.lib.disclosure.message;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.disclosure.exchange.DisclosureProxy;

/**
 * 
 * IdentifiedDataSerializable factory for Proven Messages
 * 
 * @author d3j766
 *
 */
public class ProvenMessageIDSFactory implements DataSerializableFactory {

	public ProvenMessageIDSFactory() {
	}

	// Factory
	public static final int FACTORY_ID = 1;

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
	public static final int DISCLOSURE_PROXY_TYPE = 11;
	public static final int RESPONSE_MESSAGE_TYPE = 12;

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
		case (DISCLOSURE_PROXY_TYPE):
			return new DisclosureProxy();
		case (RESPONSE_MESSAGE_TYPE):
			return new ResponseMessage();
		default:
			return null;
		}
	}

}
