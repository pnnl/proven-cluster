/**
 * 
 */
package gov.pnnl.proven.cluster.module.request;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import gov.pnnl.proven.cluster.member.component.ComponentType;
import gov.pnnl.proven.cluster.member.registry.RequestRegistry;
import gov.pnnl.proven.cluster.module.ProvenModule;

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

	public ModuleService getServiceProvider() {
		return null;
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
