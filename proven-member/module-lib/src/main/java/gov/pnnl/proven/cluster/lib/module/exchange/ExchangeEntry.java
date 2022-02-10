package gov.pnnl.proven.cluster.lib.module.exchange;

import gov.pnnl.proven.cluster.lib.module.component.ManagedComponent;
import gov.pnnl.proven.cluster.lib.module.exchange.ExchangeComponent.ExchangeProp;
import gov.pnnl.proven.cluster.lib.module.registry.ComponentEntry;

/**
 * Specialization of a component entry dor a component registry's exchange
 * processing support.
 * 
 * Note: any exchange entry specific data (beyond properties) should be here. At
 * a minimum this does allow the registry to type it's entries for exchange
 * functions.
 * 
 * @author d3j766
 *
 */
public class ExchangeEntry extends ComponentEntry {

    private static final long serialVersionUID = 1L;

    private String exchangeQueue;

    public ExchangeEntry(ManagedComponent mc) {
	super(mc);
	exchangeQueue = super.getProperties().get(ExchangeProp.EQ_IDENTIFIER);
    }

    public String getExchangeQueue() {
	return exchangeQueue;
    }

}
