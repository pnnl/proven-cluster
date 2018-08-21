package gov.pnnl.proven.cluster.module.component;

import javax.ejb.Singleton;

import fish.payara.cluster.Clustered;


/**
 *  Provides a Component Registry for Cluster level.  
 * 
 * @author d3j766
 *
 */
@Singleton
@Clustered
public class ClusterComponentRegistry implements ComponentRegistry {

	
}
