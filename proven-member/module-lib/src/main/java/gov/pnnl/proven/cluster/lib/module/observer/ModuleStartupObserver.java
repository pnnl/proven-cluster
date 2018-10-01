package gov.pnnl.proven.cluster.lib.module.observer;

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;

import gov.pnnl.proven.cluster.lib.module.event.ModuleShutdown;
import gov.pnnl.proven.cluster.lib.module.event.ModuleStartup;

public interface ModuleStartupObserver {

	public void observeModuleStartup(@Observes (notifyObserver=Reception.ALWAYS) ModuleStartup moduleStartup);
	
	public void observeModuleShutdown(@Observes (notifyObserver=Reception.ALWAYS) ModuleShutdown moduleShutdown);
	
}
