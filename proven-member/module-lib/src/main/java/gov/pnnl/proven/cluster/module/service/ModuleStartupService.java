package gov.pnnl.proven.cluster.module.service;

import java.sql.Date;

import com.hazelcast.core.HazelcastInstance;

import gov.pnnl.proven.cluster.member.component.ComponentState;
import gov.pnnl.proven.cluster.member.component.ComponentType;
import gov.pnnl.proven.cluster.module.NOOPRequest;
import gov.pnnl.proven.cluster.module.request.ModuleService;

public class ModuleStartupService extends ModuleService<NOOPRequest<Date>> {

	
	
	
	@Override
	public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
		// TODO Auto-generated method stub
	}

	@Override
	public Object call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ComponentType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ComponentState getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setState(ComponentState state) {
		// TODO Auto-generated method stub

	}

}
