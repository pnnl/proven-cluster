/**
 * 
 */
package gov.pnnl.proven.cluster.lib.module.request;

import java.io.Serializable;
import java.util.concurrent.Callable;

import com.hazelcast.ringbuffer.impl.ArrayRingbuffer;

import gov.pnnl.proven.cluster.lib.module.ProvenModuleSave;
import gov.pnnl.proven.cluster.lib.module.exception.UnsupportedRequestException;
import gov.pnnl.proven.cluster.lib.module.service.ModuleService;

/**
 * Represents a request that may be serviced by a {@link ProvenModuleSave}
 * 
 * @author d3j766
 *
 */
public class ProxyRequest<T> implements Serializable {

	/**
	 * Request input type
	 */
	T t;
	
	/**
	 * Maximum number of request retries before being sent to error stream
	 */
	private int retries;

	/**
	 * Time to live (in seconds) before being removed from a request buffer.
	 */
	private int ttl;

	/**
	 * Priority of request as defined in {@link RequestPriority}. Higher
	 * priority requests are services before lower priority requests.
	 */
	private RequestPriority priority;

	/**
	 * Scope of the reuest's service execution as defined in
	 * {@link RequestScope}
	 */
	private RequestScope scope;

	/**
	 * Request constructor. Input of request is required at time of
	 * construction.
	 * 
	 * @param t
	 *            the type of input for the request
	 */
	public ProxyRequest(T t) {
		this.t = t;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public RequestPriority getPriority() {
		return priority;
	}

	public void setPriority(RequestPriority priority) {
		this.priority = priority;
	}

	public RequestScope getScope() {
		return scope;
	}

	public void setScope(RequestScope scope) {
		this.scope = scope;
	}

}
