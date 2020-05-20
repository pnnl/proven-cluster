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

import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.json.stream.JsonParsingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.DistributedObjectUtil;
import com.hazelcast.ringbuffer.ReadResultSet;

import gov.pnnl.proven.cluster.lib.disclosure.exception.InvalidDisclosureDomainException;
import gov.pnnl.proven.cluster.lib.disclosure.exception.UnsupportedDisclosureType;
import gov.pnnl.proven.cluster.lib.disclosure.exchange.BufferedItemState;
import gov.pnnl.proven.cluster.lib.disclosure.exchange.DisclosureItem;
import gov.pnnl.proven.cluster.lib.disclosure.message.DisclosureMessage;
import gov.pnnl.proven.cluster.lib.disclosure.message.exception.CsvParsingException;
import gov.pnnl.proven.cluster.lib.module.component.CreationRequest;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Scalable;
import gov.pnnl.proven.cluster.lib.module.manager.StreamManager;
import gov.pnnl.proven.cluster.lib.module.messenger.annotation.Manager;
import gov.pnnl.proven.cluster.lib.module.stream.MessageStreamProxy;
import gov.pnnl.proven.cluster.lib.module.stream.MessageStreamType;
import gov.pnnl.proven.cluster.lib.module.stream.exception.UnsupportedMessageContentException;

/**
 * A managed component supporting the collection and processing of
 * {@code DisclosureItem}s. 
 * 
 * @author d3j766
 * 
 * @see DisclosureItem
 *
 */
@Scalable
public class DisclosureBuffer extends ExchangeBuffer<DisclosureItem> {

	static Logger log = LoggerFactory.getLogger(DisclosureBuffer.class);

	private static final BufferedItemState[] SUPPORTED_ITEM_STATES = { BufferedItemState.New };
	private RequestBuffer localExchange;
	private CompletableFuture<Void> bufferSourceReader;

	String doId;
	DisclosureQueue de;

	@Inject
	@Manager
	StreamManager sm;

	@PostConstruct
	void init() {

		log.debug("Post construct for DisclosureBuffer");

		doId = entryIdentifier().getReverseDomain();
		de = create(new CreationRequest<DisclosureQueue>(DisclosureQueue.class)).get();
		
		// Create buffer instance
		buffer = hzi.getRingbuffer(doId);
		log.debug("Disclosure Buffer created. DO-ID:: " + DistributedObjectUtil.getName(buffer));

		// Start buffer readers
		startReaders();

		// Add this as the local receiver of disclosure entries from its source
		// buffer
		de.addLocalDisclosure(this);

	}

	@PreDestroy
	void destroy() {
		log.debug("Pre Destroy for DisclosureBuffer");
		log.debug("Destroying DisclosureBuffer :: " + entryIdentifier());
		log.debug("Destroying readers");
		cancelReaders();
	}

	@Inject
	public DisclosureBuffer() {
		super(SUPPORTED_ITEM_STATES);
		log.debug("DefaultConstructer for DisclosureBuffer");
	}

	@Override
	protected void itemProcessor(ReadResultSet<DisclosureItem> items) {

		if (items.size() >= 0) {

			BufferedItemState state = items.get(0).getItemState();

			switch (state) {

			case New:

				// TODO Add a response message to "general" stream to record an
				// error event. Right now these errors are not being reported,
				// other than to the console.

				log.debug("Item processor for NEW");

				MessageStreamType mst = MessageStreamType.Disclosure;

				items.forEach((item) -> {
					try {
						DisclosureMessage dm = item.getDisclosureMessage();
						MessageStreamProxy msp = sm.getMessageStreamProxy(dm.getDomain(), mst);
						msp.addMessage(dm);
						log.debug("Added Disclosure messsage to stream :: " + dm.getMessageKey());
					} catch (UnsupportedMessageContentException | UnsupportedDisclosureType | JsonParsingException
							| InvalidDisclosureDomainException | CsvParsingException e) {
						log.error("Failed to create and add new disclosure message to stream", e);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				break;

			// TODO - implement transfer to request buffer for state Disclosed
			// case Disclosed:
			//
			// log.debug("Item processor for DISCLOSED");
			//
			// // TODO - If it's a request forward to request buffer as a
			// // RequestProxy. Any failure, resubmit as Fail.
			// break;

			default:
				log.error("No item processor for :: " + state);
				break;
			}

		}

		Runtime rt = Runtime.getRuntime();
		log.debug("FREE MEMORY AFTER ITEM PROCESSOR :: " + rt.freeMemory());

	}

	void addLocalExchange(RequestBuffer rb) {
		localExchange = rb;
	}

}
