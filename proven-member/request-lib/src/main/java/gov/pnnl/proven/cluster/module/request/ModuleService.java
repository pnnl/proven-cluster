package gov.pnnl.proven.cluster.module.request;

import java.util.concurrent.Callable;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;

import gov.pnnl.proven.cluster.member.component.ProvenComponent;

/**
 * Root module service class.  Module services a
 * 
 * @author d3j766
 *
 */
public abstract class ModuleService<T extends ModuleRequest<?>>
		implements HazelcastInstanceAware, Callable<ModuleRequest<?>>, ProvenComponent {

	T t;
	
}
