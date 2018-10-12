package gov.pnnl.proven.cluster.lib.module;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import gov.pnnl.proven.cluster.lib.module.component.ExchangeBuffer;
import gov.pnnl.proven.cluster.lib.module.member.MemberRequestRegistry;
import gov.pnnl.proven.cluster.lib.module.member.ProvenMember;


@ApplicationScoped
public abstract class ProvenModuleSave {

//	@Inject
//	ProvenMember pm;
//
//	@Inject
//	MemberRequestRegistry mrr;
//	
//	@Inject
//	ExchangeBuffer eb;
	
	/**
	 * Module initialization. Responsible for registering the proven requests
	 * and components common to all modules. The module specific requests and
	 * components are registered in the implementation.
	 */
	@PostConstruct
	public void initialize() {

		// Request registrations
		
		// Component registration
		
		//eb = new ExchangeBuffer(); 
//		eb.writeMessage("Helllllllllllllllllllllllllllloooooooooooooooooooooooooooooooooo");
//		eb.writeMessage("Helllllllllllllllllllllllllllloooooooooooooooooooooooooooooooooo");
//		eb.writeMessage("Helllllllllllllllllllllllllllloooooooooooooooooooooooooooooooooo");
//		eb.writeMessage("Helllllllllllllllllllllllllllloooooooooooooooooooooooooooooooooo");
//		eb.writeMessage("Helllllllllllllllllllllllllllloooooooooooooooooooooooooooooooooo");
		

	}
	
	public void writePMMessage(String message) {
		System.out.println(message);
	}

}
