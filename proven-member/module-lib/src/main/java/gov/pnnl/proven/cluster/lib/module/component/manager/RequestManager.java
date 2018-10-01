package gov.pnnl.proven.cluster.lib.module.component.manager;

import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;

import gov.pnnl.proven.cluster.lib.module.request.ModuleRequest;

@ApplicationScoped
public class RequestManager {

	public <T> void registerRequest(Class<T> mr) {
		
		System.out.println(mr.getName());
		
		System.out.println(mr.isAssignableFrom(ModuleRequest.class));

		System.out.println(ModuleRequest.class.isAssignableFrom(mr));
		
		Method[] methods = mr.getMethods();

		System.out.println(methods.toString());
		
		
		
	}
	
	
}
