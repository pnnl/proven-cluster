package gov.pnnl.proven.cluster.lib.module.component;

import java.io.Serializable;

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
public class ClusterComponentRegistry implements Serializable {

	
}
