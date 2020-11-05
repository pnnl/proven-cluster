package gov.pnnl.cluster.lib.pipeline;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.cluster.lib.pipeline.response.MessageContentRoutingResponse;
import gov.pnnl.cluster.lib.pipeline.response.TsResponse;
import gov.pnnl.proven.cluster.lib.member.IDSFactory;

/**
 * 
 * Hazelcast DataSerializableFactory factory for disclosure-lib classes.
 * 
 * @author d3j766
 *
 */
public class PipelineIDSFactory implements DataSerializableFactory {

	public PipelineIDSFactory() {
	}

	// Factory
	public static final int FACTORY_ID = IDSFactory.PIPELINE.getFactoryId();

	// Serializable types
	public static final int T3_RESPONSE_TYPE = 0;
	public static final int TS_RESPONSE_TYPE = 1;
	public static final int MCR_RESPONSE_TYPE = 2;
	
	@Override
	public IdentifiedDataSerializable create(int typeId) {

		switch (typeId) {
		case (T3_RESPONSE_TYPE):
			return new MessageContentRoutingResponse();
		case (TS_RESPONSE_TYPE):
			return new TsResponse();
		case (MCR_RESPONSE_TYPE):
			return new MessageContentRoutingResponse();
		default:
			return null;
		}
	}

}
