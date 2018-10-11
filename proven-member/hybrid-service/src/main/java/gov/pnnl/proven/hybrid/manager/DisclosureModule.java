package gov.pnnl.proven.hybrid.manager;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.cluster.lib.module.ProvenModule;
import gov.pnnl.proven.cluster.lib.module.ProvenModuleSave;



@ApplicationScoped
public class DisclosureModule extends ProvenModule {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger("TESTING");

	
	@PostConstruct
	public void init() {
		
		System.out.println("Hello there from disclosure module!!!!!!!!!!!!!!!!!!");;
		logger.info("Hello there from disclosure module!!!!!!!!!!!!!!!!!!");
		writeDMMessage("Hello again...");
	}
	
	public void writeDMMessage(String message) {
		System.out.println(message);
	}

	
}
