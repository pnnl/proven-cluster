package gov.pnnl.proven.cluster.lib.module.member;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fish.payara.cluster.Clustered;
import gov.pnnl.proven.cluster.lib.module.component.ExchangeBuffer;

/**
 * Provides a Request Registry for Member level.
 * 
 * @author d3j766
 *
 */
@Singleton
@ApplicationScoped
public class MemberRequestRegistry  {


	public String getName() {
		return "Hello World";
	}
	
	
}
