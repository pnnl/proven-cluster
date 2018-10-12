package gov.pnnl.proven.module.disclosure;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.cluster.lib.module.ProvenModule;



@ApplicationScoped
public class DisclosureModule extends ProvenModule {

	private static Logger logger = LoggerFactory.getLogger("TESTING");

	
	@PostConstruct
	public void init() {
		
		System.out.println("Hello there from disclosure module!!!!!!!!!!!!!!!!!!");;
		logger.info("Hello there from disclosure module!!!!!!!!!!!!!!!!!!");
		writeDMMessage("Hello again...");
	}
	
	public void writeDMMessage(String message) {
		logger.info("Inside write!!!!!!!!!!!!!!!!!!");
		System.out.println(message);
	}

	
}
