package gov.pnnl.proven.cluster.module;

public interface ModuleService<T extends ModuleRequest<?>, S extends ModuleRequest<?> > {
	
	public S submit(T t);
		
}
