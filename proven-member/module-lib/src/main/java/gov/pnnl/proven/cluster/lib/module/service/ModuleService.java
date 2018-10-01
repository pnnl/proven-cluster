package gov.pnnl.proven.cluster.lib.module.service;

import java.io.Serializable;
import java.util.concurrent.Callable;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.internal.nearcache.impl.SampleableNearCacheRecordMap;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.impl.AbstractJob;

import gov.pnnl.proven.cluster.lib.module.component.ProvenComponent;
import gov.pnnl.proven.cluster.lib.module.request.ModuleRequest;
import gov.pnnl.proven.cluster.lib.module.request.ProxyRequest;

/**
 * Root class for all Module Services. A Module service is used to service a
 * Module Request.
 * 
 * @author d3j766
 * @param <T>
 *            the request's input type
 *
 */
public abstract class ModuleService<T> implements Serializable, HazelcastInstanceAware,
		Callable<ProxyRequest<?>> {

	/**
	 * Service request
	 */
	T t;

	private static final long serialVersionUID = 1L;

	private transient HazelcastInstance hazelcastInstance;

	public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
	}

	public ModuleService(T t) {
		this.t = t;
	}

	public T getInput() {
		return t;
	}

}
