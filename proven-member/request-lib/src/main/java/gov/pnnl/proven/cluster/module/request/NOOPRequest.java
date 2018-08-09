package gov.pnnl.proven.cluster.module.request;

public class NOOPRequest extends ModuleRequest<String> {

	String message;
	
	public NOOPRequest() {
		this.message = "NOOPRequest"; 
	}

	@Override
	public ModuleService getServiceProvider() {
		return super.getServiceProvider();
	}
		
}
