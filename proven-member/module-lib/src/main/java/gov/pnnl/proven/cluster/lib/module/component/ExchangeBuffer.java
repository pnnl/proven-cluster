package gov.pnnl.proven.cluster.lib.module.component;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import gov.pnnl.proven.cluster.lib.module.member.MemberRequestRegistry;

@Dependent
public class ExchangeBuffer {

	@Inject
	private MemberRequestRegistry mrr;
	
	@PostConstruct 
	public void init() {
		System.out.println(mrr.getName() + " JERSEY");
	}
		
	public ExchangeBuffer() {
		System.out.println(mrr.getName() + " JERSEY SHORE");
	}
	
	public void writeMessage(String message) {
		System.out.println(message);
	}
	
}
