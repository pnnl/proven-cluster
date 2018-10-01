package gov.pnnl.proven.cluster.lib.module;

import java.sql.Date;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.inject.Inject;

import com.hazelcast.console.SimulateLoadTask;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.ringbuffer.impl.Ringbuffer;

import gov.pnnl.proven.cluster.lib.module.component.DisclosureBuffer;
import gov.pnnl.proven.cluster.lib.module.component.manager.RequestManager;
import gov.pnnl.proven.cluster.lib.module.event.ModuleStartup;
import gov.pnnl.proven.cluster.lib.module.request.NoopRequest;

/**
 * Startup bean for web module application. On application startup, a startup
 * message is sent to the {@link ProvenModule} implementation. Only a single
 * {@code ProvenModule} implementation per application is supported. The module
 * is required to observe this startup message in order for module activation to
 * take place. An unsuccessful startup will be logged to the container.
 * 
 * @author d3j766
 *
 */
@Singleton
@Startup
public class ModuleManager {

	@Inject
	BeanManager beanManager;
	
	@Inject RequestManager rm;

	HazelcastInstanceAware ha;
	SimulateLoadTask slt;

	@Inject
	Event<ModuleStartup> mse;

	@PostConstruct
	public void initialize() {
		// TODO Use Logger
		System.out.println("Enter PostConstruct for " + this.getClass().getSimpleName());
		
		Date date = new Date(new java.util.Date().getDate());
		NoopRequest<Date> nor = new NoopRequest<Date>(date);
		
		rm.registerRequest(NoopRequest.class);
		
		sendStartupMessage();
	}

	public void sendStartupMessage() {
		
		// TODO Use logger
		ModuleStartup ms = new ModuleStartup();
		Set<ObserverMethod<? super ModuleStartup>> observers = beanManager.resolveObserverMethods(ms);
		if (observers.isEmpty()) {
			System.out.println("MUST PROVIDE A MODULE IMPLEMENTATION");
		} else if (observers.size() > 1) {
			System.out.println("ONLY SINGLE MODULE IMPLEMENTATIONS ARE SUPPORTED AT THIS TIME");
		} else {
			mse.fire(ms);
		}
	}

}
