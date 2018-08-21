package gov.pnnl.proven.cluster.module.request;

import java.sql.Date;

import gov.pnnl.proven.cluster.module.service.ModuleService;


public class NOOPRequest extends ModuleRequest<Date> {
	
	public NOOPRequest(Date date) {
		super(date);
	}

	@Override
	public ModuleService<ModuleRequest<Date>> getServiceProvider() {
		ModuleService<ModuleRequest<Date>> ret = NOOPService.create(t);
		return ret;
	}

}
