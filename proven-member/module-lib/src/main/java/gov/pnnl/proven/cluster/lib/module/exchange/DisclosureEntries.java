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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hazelcast.core.IQueue;

import gov.pnnl.proven.cluster.lib.disclosure.exchange.BufferedItemState;
import gov.pnnl.proven.cluster.lib.disclosure.exchange.DisclosureProxy;
import gov.pnnl.proven.cluster.lib.module.component.ComponentStatus;
import gov.pnnl.proven.cluster.lib.module.component.ModuleComponent;
import gov.pnnl.proven.cluster.lib.module.component.annotation.ManagedComponent;
import gov.pnnl.proven.cluster.lib.module.exchange.exception.DisclosureEntryInterruptedException;

/**
 * A managed component representing a data structure to accept/store externally
 * provided disclosure messages, providing back pressure for its associated
 * {@code DisclosureBuffer}. Disclosure entries are forwarded as
 * {@code DisclosureProxy} objects to its disclosure buffer, if it has
 * processing capacity to support it.
 * 
 * @author d3j766
 * 
 * @see RequestExchange
 *
 */
@ManagedComponent
public class DisclosureEntries extends ModuleComponent {

	static Logger log = LoggerFactory.getLogger(DisclosureEntries.class);
	
	public static final String EXTERNAL_DISCLOSURE_NAME = "gov.pnnl.proven.external.disclosure";

	IQueue<String> queue;
	CompletableFuture<Void> reader = CompletableFuture.completedFuture(null);
	DisclosureBuffer localDisclosure;
	
	@Resource(lookup = RequestExchange.RE_EXECUTOR_SERVICE)
	ManagedExecutorService mes;

	@PostConstruct
	public void init() {
		log.debug("Post construct for DisclosureEntries");

		// TODO remove hardcoded name until multiple exchanges are supported
		// bufferSource = hzi.getQueue(getDistributedName());
		queue = hzi.getQueue(EXTERNAL_DISCLOSURE_NAME);
		startReader(false);
	}

	@PreDestroy
	public void destroy() {
		log.debug("Pre destroy for DisclosureEntries");
		cancelReader();
	}

	@Inject
	public DisclosureEntries(InjectionPoint ip) {
		super();
		log.debug("DefaultConstructer for DisclosureBuffer");
	}

	private void startReader(boolean replace) {

		synchronized (reader) {

			boolean hasReader = ((null != reader) && (!reader.isDone()));

			// Remove existing reader if replace reader is requested
			if (hasReader && replace) {
				reader.cancel(true);
				hasReader = false;
			}

			// Add new reader if one doesn't exist
			if (!hasReader) {
				// reader = CompletableFuture.runAsync(this::runReader, r -> new
				// Thread(r).start()).exceptionally(this::readerException);
				reader = CompletableFuture.runAsync(this::runReader, mes).exceptionally(this::readerException);
			}
		}

	}

	protected void runReader() {

		while (true) {

			List<DisclosureProxy> entries;

			// Should not continue if local disclosure has no free space - the
			// entries will be lost
			if ( (null != localDisclosure) && (localDisclosure.hasFreeSpace(BufferedItemState.New))) {

				try {
					// Will block if queue is empty
					// entry = getEntry();
					entries = getEntries();

				} catch (InterruptedException e) {
					e.printStackTrace();
					Thread.currentThread().interrupt();
					throw new DisclosureEntryInterruptedException("Disclosure entires' queue reader interruped", e);
				}

				// Item is no longer in IMDG (queue reads are destructive)
				log.debug("ENTRY ADDING TO DISCLOSURE BUFFER");

				boolean entriesAdded = true;
				if ((null != entries) && (!entries.isEmpty())) {
					BufferedItemState state = entries.get(0).getItemState();
					entriesAdded = localDisclosure.addItems(entries, state);
				}

				log.debug("ENTRY ADDED TO DISCLOSURE BUFFER");
				log.info("Queue size :: " + queue.size());

				// TODO implement fall backs instead of simply logging an error
				// message, or these entries will be lost.
				if (!entriesAdded) {
					// Overwrite failure - Disclosure Buffer is full.
					log.error("Failed to add disclosure entries to disclosure buffer- entry lost");
				}

			} else {
				log.info("Local disclosure buffer is not available");
			}
		}

	}

	/**
	 * Callback for entry reader that has ended exceptionally.
	 * 
	 * @param readerException
	 *            the exception thrown from the reader.
	 */
	protected Void readerException(Throwable readerException) {

		Void ret = null;

		boolean isInterrupted = (readerException instanceof DisclosureEntryInterruptedException);
		boolean isCancelled = (readerException instanceof CancellationException);

		// Interrupted exception - indicates the reader has failed due to thread
		// interruption, attempts to restart should be made.
		if (isInterrupted) {
			log.info("Disclosure entry reader was interrupted.");
			startReader(true);
		}

		// Cancelled exception - indicates the reader has been cancelled. This
		// is a controlled event, attempts to restart the reader should not be
		// made.
		else if (isCancelled) {
			log.info("Disclosure entry reader was cancelled.");
		}

		// Other exceptions - not managed, do not attempt to restart.
		else {
			log.info("Disclosure entry reader was completed exceptionally :: " + readerException.getMessage());
			readerException.printStackTrace();
		}

		return ret;
	}

	public void cancelReader() {

		if (reader != null) {
			synchronized (reader) {
				if ((null != reader) && (!reader.isDone())) {
					reader.cancel(true);
				}
			}
		}
	}


	public List<DisclosureProxy> getEntries() throws InterruptedException {

		List<DisclosureProxy> entries = new ArrayList<>();

		// TODO - create a service that returns the initial disclosure state
		int max = localDisclosure.getMaxBatchSize(BufferedItemState.New);
		boolean done = false;
		boolean initialCheck = true;

		while (!done) {
		
			// Block on empty queue at startup 
			if (initialCheck) {				
				entries.add(new DisclosureProxy(queue.take()));
				initialCheck = false;
			}

			// Poll for next item - timeout will flush up to maximum queue
			// entries.  TODO - also flush entries if the entry exceed a max size.
			int qSize = queue.size();
			int eSize = entries.size();
			if ((qSize + eSize) < max) {
				String entry = queue.poll(250, TimeUnit.MILLISECONDS);
				// Timeout occurred
				if (null == entry) {
					for (int i = 0; i < qSize - 1; i++) {
						entries.add(new DisclosureProxy(queue.take()));
					}
					done = true;
				} else {
					entries.add(new DisclosureProxy(entry));
				}
			}

			// Flush max entries
			else {
				for (int i = 0; i < qSize - 1; i++) {
					entries.add(new DisclosureProxy(queue.take()));
				}
				done = true;
			}

		}
		log.debug("REMOVED ENTRIES FROM QUEUE :: " + entries.size());
		return entries;
	}

	@Override
	public ComponentStatus getStatus() {
		return null;
	}

	public void addLocalDisclosure(DisclosureBuffer db) {
		localDisclosure = db;
	}

}
