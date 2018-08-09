package gov.pnnl.proven.cluster.module;

import com.hazelcast.internal.diagnostics.DiagnosticsLogWriter;

public class DisclosureResponseRequest extends ModuleRequest<

> {

	ProvenMessage inProvenMessage;
	

	@Override
	public  DisclosureResponseService getServiceProvider() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void create(ProvenMessage t) {
		
		inProvenMessage = t;
	
	}

}
