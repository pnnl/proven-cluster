package gov.pnnl.proven.cluster.module.member;

import javax.ejb.Singleton;

import fish.payara.cluster.Clustered;


/**
 *  Provides a Request Registry for Cluster level.  
 * 
 * @author d3j766
 *
 */
@Singleton
@Clustered
public class ClusterRequestRegistry implements RequestRegistry {

	
}
