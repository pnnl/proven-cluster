package gov.pnnl.proven.cluster.lib.module;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gov.pnnl.proven.cluster.lib.module.event.ModuleShutdown;
import gov.pnnl.proven.cluster.lib.module.event.ModuleStartup;
import gov.pnnl.proven.cluster.lib.module.observer.ModuleStartupEventObserver;

public abstract class ProvenModule implements ModuleStartupEventObserver {

	private static Logger logger = LogManager.getLogger(ProvenModule.class);

	
	public void observeModuleStartup(@Observes(notifyObserver = Reception.ALWAYS) ModuleStartup moduleStartup) {

		logger.debug("ProvenModule startup message observed");

		// Activate managers

		// Log startup message
		logger.info("ProvenModule startup completed.");

	}

	public void observeModuleShutdown(@Observes(notifyObserver = Reception.ALWAYS) ModuleShutdown moduleShutdown) {

		logger.debug("ProvenModule shutdown message observed");

		// Deactivate managers

		// Log shutdown message
		logger.info("ProvenModule shutdown completed.");

	}

}
