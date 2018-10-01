package gov.pnnl.proven.module.disclosure;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import gov.pnnl.proven.cluster.lib.module.ProvenModule;

@ApplicationScoped
public class DisclosureModule extends ProvenModule {

	@Inject 
	private BeanManager beanManager;
	
	@PostConstruct
	public void initialize() {
		//TODO Use Logger
		System.out.println("Enter PostConstruct for " + this.getClass().getSimpleName());
	}

}
