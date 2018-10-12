package gov.pnnl.proven.cluster.lib.module.request;

import java.sql.Date;

import gov.pnnl.proven.cluster.lib.module.exception.UnsupportedRequestException;
import gov.pnnl.proven.cluster.lib.module.service.ModuleService;


public class NoopRequest<T extends Date> extends ModuleRequest<T> {
	
	Date noopinput;
	
	public NoopRequest(T date) {
		super(date);
		noopinput = date;
	}

	
	@Override
	public ModuleService<T> getServiceProvider() throws UnsupportedRequestException {
		// TODO Auto-generated method stub
		
		return super.getServiceProvider();
	}


}
