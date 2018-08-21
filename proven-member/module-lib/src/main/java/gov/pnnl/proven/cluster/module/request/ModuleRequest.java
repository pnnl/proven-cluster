/**
 * 
 */
package gov.pnnl.proven.cluster.module.request;

import gov.pnnl.proven.cluster.module.ProvenModule;
import gov.pnnl.proven.cluster.module.exception.UnsupportedRequestException;
import gov.pnnl.proven.cluster.module.service.ModuleService;

/**
 * Represents a request that may be serviced by a {@link ProvenModule}
 * 
 * @author d3j766
 *
 */
public abstract class ModuleRequest<T> {

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
	public ModuleRequest(T t) {
		this.t = t;
	}

	/**
	 * Provided the {@link ModuleService} that will service the request.
	 * 
	 * @return the ModuleService for this request
	 * @throws UnsupportedRequestException
	 *             if the request does not have an associated ModuleService
	 */
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
