//package gov.pnnl.proven.hybrid.resource;
//
//import javax.annotation.PostConstruct;
//import javax.ejb.LocalBean;
//import javax.ejb.Singleton;
//import javax.ejb.Startup;
//import javax.enterprise.context.ApplicationScoped;
//
//import gov.pnnl.proven.cluster.module.ProvenModule;
//
///**
// * Session Bean implementation class DisclosureModule
// */
//@ApplicationScoped
//@Singleton
//@Startup
//public class DisclosureModule extends ProvenModule {
//
//	/**
//	 * Default constructor.
//	 */
//	public DisclosureModule() {
//		super();
//		System.out.println("Inside disclosure module constructor");
//		// TODO Auto-generated constructor stub
//	}
//	
//	@PostConstruct 
//	void init() {
//		System.out.println("Inside disclosure module post construct");	
//	}
//
//	@Override
//	public void startApp() {
//		System.out.println("In startup method");
//	}
//
//}
