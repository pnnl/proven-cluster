package gov.pnnl.proven.hybrid.manager;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import gov.pnnl.proven.cluster.lib.module.ProvenModuleSave;



@ApplicationScoped
public class DisclosureModule extends ProvenModuleSave {

	
	@PostConstruct
	public void init() {
		
		System.out.println("Hellow there from disclosure module!!!!!!!!!!!!!!!!!!");
		System.out.println("Hellow there from disclosure module!!!!!!!!!!!!!!!!!!");
		System.out.println("Hellow there from disclosure module!!!!!!!!!!!!!!!!!!");
		System.out.println("Hellow there from disclosure module!!!!!!!!!!!!!!!!!!");
		System.out.println("Hellow there from disclosure module!!!!!!!!!!!!!!!!!!");
		
		
	}
	
	public void writeDMMessage(String message) {
		System.out.println(message);
	}

	
}
