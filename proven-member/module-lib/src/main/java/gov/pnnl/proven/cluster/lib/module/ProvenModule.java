package gov.pnnl.proven.cluster.lib.module;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;

import gov.pnnl.proven.cluster.lib.module.event.ModuleShutdown;
import gov.pnnl.proven.cluster.lib.module.event.ModuleStartup;
import gov.pnnl.proven.cluster.lib.module.observer.ModuleStartupObserver;

//@ApplicationScoped
public abstract class ProvenModule implements ModuleStartupObserver {

	public void observeModuleStartup(@Observes(notifyObserver = Reception.ALWAYS) ModuleStartup moduleStartup) {

		System.out.println("ProvenModule startup message observed...");

		// Activate managers

		// Log startup message

	}

	public void observeModuleShutdown(@Observes(notifyObserver = Reception.ALWAYS) ModuleShutdown moduleShutdown) {

		System.out.println("ProvenModule shutdown message observed...");

		// Deactivate managers

		// Log shutdown message

	}

}
