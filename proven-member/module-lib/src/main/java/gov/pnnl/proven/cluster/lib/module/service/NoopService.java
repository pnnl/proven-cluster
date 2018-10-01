package gov.pnnl.proven.cluster.lib.module.service;

import java.sql.Date;

import gov.pnnl.proven.cluster.lib.module.request.NoopRequest;
import gov.pnnl.proven.cluster.lib.module.request.ProxyRequest;

public class NoopService<T extends NoopRequest<? extends Date>> extends ModuleService<T> {

	public NoopService(T t) {
		super(t);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public ProxyRequest<?> call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
