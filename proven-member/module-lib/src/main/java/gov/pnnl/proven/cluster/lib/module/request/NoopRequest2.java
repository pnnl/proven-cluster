package gov.pnnl.proven.cluster.lib.module.request;

import java.sql.Date;

import gov.pnnl.proven.cluster.lib.module.exception.UnsupportedRequestException;
import gov.pnnl.proven.cluster.lib.module.service.ModuleService;
import gov.pnnl.proven.cluster.lib.module.service.NoopService;


public class NoopRequest2 extends ModuleRequest<Date> {
	
	public NoopRequest2(Date date) {
		super(date);
	}

	@Override
	public ModuleService<Date> getServiceProvider() throws UnsupportedRequestException {
		// TODO Auto-generated method stub
		return super.getServiceProvider();
	}


}
