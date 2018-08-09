package gov.pnnl.proven.cluster.module;


public class DisclosureResponseService implements ModuleService<DisclosureResponseRequest, NOOPRequest> {
	
	@Override
	public NOOPRequest submit(DisclosureResponseRequest t) {
		System.out.println("Inside submit NOOPRequest");
		return null;
	}
}
