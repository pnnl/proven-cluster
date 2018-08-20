/**
 * 
 */
package gov.pnnl.proven.cluster.module.request;

import gov.pnnl.proven.cluster.module.ProvenModule;
import gov.pnnl.proven.cluster.module.request.exception.UnsupportedRequestException;

/**
 * Represents a request that may be serviced by a {@link ProvenModule}
 * 
 * @author d3j766
 *
 */
public abstract class ModuleRequest<T> {

	T t;

	private int retries;
	private int ttl;
	private RequestPriority priority;
	private RequestScope scope;

	public ModuleRequest(T t) {
		this.t = t;
	}

	public ModuleService<ModuleRequest<T>> getServiceProvider() throws UnsupportedRequestException {
		throw new UnsupportedRequestException();
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
