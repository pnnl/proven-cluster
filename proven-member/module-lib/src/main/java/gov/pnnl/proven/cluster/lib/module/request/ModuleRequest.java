/**
 * 
 */
package gov.pnnl.proven.cluster.lib.module.request;

import java.io.Serializable;

import gov.pnnl.proven.cluster.lib.module.ProvenModuleSave;
import gov.pnnl.proven.cluster.lib.module.exception.UnsupportedRequestException;
import gov.pnnl.proven.cluster.lib.module.service.ModuleService;

/**
 * Represents a request that may be serviced by a {@link ProvenModuleSave}
 * 
 * @author d3j766
 *
 */
public abstract class ModuleRequest<T> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Request input
	 */
	private T t;

	/**
	 * Represents the name of request.  Used by request registry
	 */
	private String requestName;
	
	
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
	protected ModuleRequest(T t) {
		this.t = t;
	}

	/**
	 * Provides the {@link ModuleService} that will service the request.
	 * 
	 * @return the ModuleService for this request
	 * @throws UnsupportedRequestException
	 *             if the request does not have an associated ModuleService
	 */
	public ModuleService<T> getServiceProvider() throws UnsupportedRequestException {
		throw new UnsupportedRequestException();
	}
	
	public T getInput() {
		return t;
	}
	
	public String getRequestName() {
		return requestName;
	}

	public void setRequestName(String requestName) {
		this.requestName = requestName;
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
