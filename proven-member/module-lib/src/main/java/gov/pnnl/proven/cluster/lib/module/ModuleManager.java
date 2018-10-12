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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hazelcast.console.SimulateLoadTask;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.ringbuffer.impl.Ringbuffer;

import gov.pnnl.proven.cluster.lib.module.component.DisclosureBuffer;
import gov.pnnl.proven.cluster.lib.module.component.manager.RequestManager;
import gov.pnnl.proven.cluster.lib.module.event.ModuleStartup;
import gov.pnnl.proven.cluster.lib.module.exception.ModuleStartupException;
import gov.pnnl.proven.cluster.lib.module.exception.MultipleModuleImplementationException;
import gov.pnnl.proven.cluster.lib.module.exception.NoModuleImplementationException;
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
	
	private static Logger logger = LogManager.getLogger(ModuleManager.class);

	@Inject
	BeanManager beanManager;
	
	@Inject
	Event<ModuleStartup> mse;

	@PostConstruct
	public void initialize() throws ModuleStartupException {
		
		logger.debug("Enter PostConstruct for " + this.getClass().getSimpleName());
		sendStartupMessage();		
		logger.debug("Leave PostConstruct for " + this.getClass().getSimpleName());
		
	}

	public void sendStartupMessage() throws ModuleStartupException {
		
		ModuleStartup ms = new ModuleStartup();
		Set<ObserverMethod<? super ModuleStartup>> observers = beanManager.resolveObserverMethods(ms);
		if (observers.isEmpty()) {
			logger.info("Module implementation was not provided");
			throw new NoModuleImplementationException();
		} else if (observers.size() > 1) {
			logger.info("Multiple module implementations provided");
			throw new MultipleModuleImplementationException();
		} else {
			mse.fire(ms);
		}
	}

}
