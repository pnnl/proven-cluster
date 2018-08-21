package gov.pnnl.proven.cluster.module.service;

import java.io.Serializable;
import java.util.concurrent.Callable;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IExecutorService;

import gov.pnnl.proven.cluster.member.component.ProvenComponent;
import gov.pnnl.proven.cluster.module.request.ModuleRequest;

/**
 * A module service is responsible for servicing module requests.
 * 
 * @author d3j766
 *
 */
public abstract class ModuleService<T extends ModuleRequest<?>>
		implements Serializable, HazelcastInstanceAware, Callable<ProxyRequest<?>>, ProvenComponent {

	private static final long serialVersionUID = 1L;

	T t;

	private transient HazelcastInstance hazelcastInstance;

	public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
	}

	public ModuleService(T t) {
		this.t = t;
		
		IExecutorService
		
	}

}
