/*******************************************************************************
 * Copyright (c) 2017, Battelle Memorial Institute All rights reserved.
 * Battelle Memorial Institute (hereinafter Battelle) hereby grants permission to any person or entity 
 * lawfully obtaining a copy of this software and associated documentation files (hereinafter the 
 * Software) to redistribute and use the Software in source and binary forms, with or without modification. 
 * Such person or entity may use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of 
 * the Software, and may permit others to do so, subject to the following conditions:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the 
 * following disclaimers.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Other than as used herein, neither the name Battelle Memorial Institute or Battelle may be used in any 
 * form whatsoever without the express written consent of Battelle.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * BATTELLE OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * General disclaimer for use with OSS licenses
 * 
 * This material was prepared as an account of work sponsored by an agency of the United States Government. 
 * Neither the United States Government nor the United States Department of Energy, nor Battelle, nor any 
 * of their employees, nor any jurisdiction or organization that has cooperated in the development of these 
 * materials, makes any warranty, express or implied, or assumes any legal liability or responsibility for 
 * the accuracy, completeness, or usefulness or any information, apparatus, product, software, or process 
 * disclosed, or represents that its use would not infringe privately owned rights.
 * 
 * Reference herein to any specific commercial product, process, or service by trade name, trademark, manufacturer, 
 * or otherwise does not necessarily constitute or imply its endorsement, recommendation, or favoring by the United 
 * States Government or any agency thereof, or Battelle Memorial Institute. The views and opinions of authors expressed 
 * herein do not necessarily state or reflect those of the United States Government or any agency thereof.
 * 
 * PACIFIC NORTHWEST NATIONAL LABORATORY operated by BATTELLE for the 
 * UNITED STATES DEPARTMENT OF ENERGY under Contract DE-AC05-76RL01830
 ******************************************************************************/
package gov.pnnl.proven.cluster.lib.module.exchange;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.hazelcast.collection.IQueue;

import gov.pnnl.proven.cluster.lib.module.component.ComponentGroup;
import gov.pnnl.proven.cluster.lib.module.component.ManagedComponent;
import gov.pnnl.proven.cluster.lib.module.registry.ComponentEntry;
import gov.pnnl.proven.cluster.lib.module.registry.EntryIdentifier;
import gov.pnnl.proven.cluster.lib.module.registry.EntryProperties;
import gov.pnnl.proven.cluster.lib.module.registry.EntryProperty;
import gov.pnnl.proven.cluster.lib.module.registry.EntryProperty.IntegerProp;
import gov.pnnl.proven.cluster.lib.module.registry.EntryProperty.StringProp;
import gov.pnnl.proven.cluster.lib.module.registry.EntryReporter;

/**
 * Represents an exchange component. These components are responsible for
 * exchanging disclosed items with other exchange components inside the cluster
 * for processing before distribution to IMDG.
 * 
 * @see DisclosureItem, ExchangeRequest
 * 
 * @author d3j766
 *
 */
public abstract class ExchangeComponent extends ManagedComponent {

    @Inject
    Logger log;

    public static class ExchangeProp {

	/**
	 * Exchange Queue Identifier
	 */
	public static final StringProp EQ_IDENTIFIER = new StringProp("eqIdentifier");

	/**
	 * Exchange Queue percent of maximum capacity that is being used
	 */
	public static final IntegerProp EQ_CAPACITY_PERCENT = new IntegerProp("remainingCapacityPercent");

    }

    /**
     * Exchange queue identifier
     */
    EntryIdentifier eqIdentifier;

    /**
     * Component's exchange queue. Component reads items from this queue to process.
     * Items are added to this queue via exchange requests performed by a
     * ModuleExchange or MemberExchange component.
     */
    IQueue<ExchangeRequest> exchangeQueue;

    /**
     * TODO add reader implementation for exchange queue as well as other methods
     * supporting exchange queues.
     */

    @PostConstruct
    public void initExcahngeComponent() {
	exchangeQueue = hzi.getQueue(getEQIdentifier().toString());
    }

    public ExchangeComponent() {
	super(ComponentGroup.Exchange);
	eqIdentifier = new EntryIdentifier(UUID.randomUUID(), mp.getExchangeQueueName(), getComponentGroup());
    }

    /**
     * @see EntryReporter#entry() 
     */
    @Override
    public ComponentEntry entry() {
	return new ExchangeEntry(this);
    }

    @Override
    public EntryProperties entryProperties() {
	EntryProperties eps = new EntryProperties(super.entryProperties());
	eps.add(new EntryProperty(ExchangeProp.EQ_IDENTIFIER, getEQIdentifier().toString()));
	eps.add(new EntryProperty(ExchangeProp.EQ_CAPACITY_PERCENT, getEQCapacityPercent()));
	return eps;
    }

    private EntryIdentifier getEQIdentifier() {
	return eqIdentifier;
    }

    private int getEQCapacityPercent() {
	int maxSize = mp.getExchangeQueueMaxSize();
	int rc = exchangeQueue.remainingCapacity();
	return (maxSize - rc) / maxSize;
    }
}
