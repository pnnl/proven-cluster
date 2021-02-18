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
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.json.stream.JsonParsingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IQueue;

import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.exception.CsvParsingException;
import gov.pnnl.proven.cluster.lib.disclosure.exception.JSONDataValidationException;
import gov.pnnl.proven.cluster.lib.disclosure.exception.UnsupportedDisclosureType;
import gov.pnnl.proven.cluster.lib.disclosure.item.DisclosureItem;
import gov.pnnl.proven.cluster.lib.disclosure.item.DisclosureItemState;
import gov.pnnl.proven.cluster.lib.module.component.annotation.Scalable;
import gov.pnnl.proven.cluster.lib.module.component.maintenance.ComponentMaintenance;
import gov.pnnl.proven.cluster.lib.module.exchange.exception.DisclosureEntryInterruptedException;
import gov.pnnl.proven.cluster.lib.module.exchange.maintenance.DisclosureQueueCheck;
import gov.pnnl.proven.cluster.lib.module.exchange.maintenance.ProvenDisclosureMap;
import gov.pnnl.proven.cluster.lib.module.manager.ExchangeManager;

/**
 * Wraps a Hazelcast {@link IQueue} distributed data structure for
 * storing/processing disclosed items from internal or external sources.
 * Processed items may be exchanged with another {@code Exchanger}(s).
 * 
 * @author d3j766
 * 
 * @see DisclosureItem
 *
 */
@Scalable(maxCount = 5)
public class DisclosureQueue extends ExchangeComponent {

	static Logger log = LoggerFactory.getLogger(DisclosureQueue.class);

	public static final String EXTERNAL_DISCLOSURE_QUEUE_NAME = "gov.pnnl.proven.external.disclosure";
	public static final String INTERNAL_LARGE_ITEM_QUEUE_NAME = "gov.pnnl.proven.internal.disclosure";
	public static final int MAX_DISCLOSURE_RETRIES = 10;

	IQueue<String> itemQueue;
	IQueue<String> largeItemQueue;
	CompletableFuture<Void> reader = CompletableFuture.completedFuture(null);
	CompletableFuture<Void> largeItemReader = CompletableFuture.completedFuture(null);

	DisclosureBuffer localDisclosure;

	@Resource(lookup = ExchangeManager.EXCHANGE_EXECUTOR_SERVICE)
	ManagedExecutorService mes;

	@PostConstruct
	public void init() {
		log.debug("Post construct for DisclosureEntries");

		// TODO remove hardcoded name until multiple exchanges are supported
		// bufferSource = hzi.getQueue(getDistributedName());
		itemQueue = hzi.getQueue(EXTERNAL_DISCLOSURE_QUEUE_NAME);
		largeItemQueue = hzi.getQueue(INTERNAL_LARGE_ITEM_QUEUE_NAME);
		// startReader(false);
		// startLargeMessageReader(false);
	}

	@PreDestroy
	public void destroy() {
		log.debug("Pre destroy for DisclosureEntries");
		cancelReader();
		cancelLargeItemReader();
	}

	@Inject
	public DisclosureQueue() {
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
				reader = CompletableFuture.runAsync(this::runReader, mes).exceptionally(this::readerException);
			}
		}

	}

	private void startLargeItemReader(boolean replace) {

		synchronized (largeItemReader) {

			boolean hasReader = ((null != largeItemReader) && (!largeItemReader.isDone()));

			// Remove existing reader if replace reader is requested
			if (hasReader && replace) {
				largeItemReader.cancel(true);
				hasReader = false;
			}

			// Add new reader if one doesn't exist
			if (!hasReader) {
				largeItemReader = CompletableFuture.runAsync(this::runLargeItemReader, mes)
						.exceptionally(this::largeItemReaderException);
			}
		}

	}

	// TODO provide exp backoff if disclosure buffer is full
	protected void runReader() {

		while (true) {

			List<DisclosureItem> entries;

			// TODO this should be changed to a registry request for an
			// available disclosure buffer (registry will give preference to
			// local)
			// Should not continue if local disclosure has no free space - the
			// entries will be lost
			if ((null != localDisclosure) && (localDisclosure.hasFreeSpace(DisclosureItemState.New))) {

				try {
					// Will block if queue is empty
					// entry = getEntry();
					entries = getItems();

				} catch (InterruptedException e) {
					e.printStackTrace();
					Thread.currentThread().interrupt();
					throw new DisclosureEntryInterruptedException("Disclosure entires' queue reader interruped", e);
				}

				// Items are no longer in IMDG (queue reads are destructive)
				log.debug("ENTRIES BEING ADDED TO DISCLOSURE BUFFER: " + entries.size());

				boolean entriesAdded = true;
				if ((null != entries) && (!entries.isEmpty())) {
					DisclosureItemState state = entries.get(0).getItemState();
					entriesAdded = localDisclosure.addItems(entries, state);
				}

				log.debug("ENTRY ADDED TO DISCLOSURE BUFFER");
				log.debug("Queue size :: " + itemQueue.size());

				// TODO implement fall backs instead of simply logging an error
				// message, or these entries will be lost.
				if (!entriesAdded) {
					// Overwrite failure - Disclosure Buffer is full.
					log.error("FAILED TO ADD DISCLOSURE ENTRIES TO DISCLOSURE BUFFER- ENTRIES LOST");
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

	protected void runLargeItemReader() {

		while (true) {

			List<DisclosureItem> entries = new ArrayList<>();
			String disclosedItem = null;

			// Should not continue if local disclosure has no free space - the
			// entries will be lost
			if ((null != localDisclosure) && (localDisclosure.hasFreeSpace(DisclosureItemState.New))) {

				try {
					// Will block if queue is empty
					disclosedItem = largeItemQueue.take();
					entries = new ItemParser(disclosedItem).parse();

				} catch (JsonParsingException | JSONDataValidationException | CsvParsingException e) {
					log.error("Failed to process large disclosure entry", e);
					log.error("Entry discarded");
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
					Thread.currentThread().interrupt();
					throw new DisclosureEntryInterruptedException("Disclosure entires' queue reader interruped", e);
				}

				// Items are no longer in IMDG (queue reads are destructive)
				log.debug("ENTRIES BEING ADDED TO DISCLOSURE BUFFER: " + entries.size());

				boolean entriesAdded = true;
				if ((null != entries) && (!entries.isEmpty())) {
					DisclosureItemState state = entries.get(0).getItemState();
					entriesAdded = localDisclosure.addItems(entries, state);
				}

				log.debug("ENTRY ADDED TO DISCLOSURE BUFFER");
				log.debug("Queue size :: " + itemQueue.size());

				// TODO implement fall backs instead of simply logging an error
				// message, or these entries will be lost.
				if (!entriesAdded) {
					// Overwrite failure - Disclosure Buffer is full.
					log.error("FAILED TO ADD DISCLOSURE ENTRIES TO DISCLOSURE BUFFER- ENTRIES LOST");
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
	protected Void largeItemReaderException(Throwable readerException) {

		Void ret = null;

		boolean isInterrupted = (readerException instanceof DisclosureEntryInterruptedException);
		boolean isCancelled = (readerException instanceof CancellationException);

		// Interrupted exception - indicates the reader has failed due to thread
		// interruption, attempts to restart should be made.
		if (isInterrupted) {
			log.info("Disclosure entry reader was interrupted.");
			startLargeItemReader(true);
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

	public void cancelLargeItemReader() {

		if (largeItemReader != null) {
			synchronized (largeItemReader) {
				if ((null != largeItemReader) && (!largeItemReader.isDone())) {
					largeItemReader.cancel(true);
				}
			}
		}
	}

	public List<DisclosureItem> getItems() throws InterruptedException {

		List<DisclosureItem> entries = new ArrayList<>();

		// TODO - create a service that returns the initial disclosure state
		int max = localDisclosure.getMaxBatchSize(DisclosureItemState.New);
		boolean done = false;
		boolean initialCheck = true;
		while (!done) {
			// Block on empty queue at startup
			if (initialCheck) {
				addItem(entries, Optional.ofNullable(null));
				initialCheck = false;
			}

			// Poll for next item - timeout will flush up to maximum queue
			// entries.
			int eSize = entries.size();
			if ((eSize) < max) {
				String entry = itemQueue.poll(250, TimeUnit.MILLISECONDS);
				// Timeout occurred - return if entries are there
				if (null == entry) {
					if (!entries.isEmpty()) {
						done = true;
					}
				} else {
					addItem(entries, Optional.ofNullable(entry));
				}
			}

			// Return max entries
			else {
				done = true;
			}
		}
		return entries;
	}

	/**
	 * Checks size of item and if size is greater then the internal limit, then
	 * entry is forwarded to the large item queue. Otherwise, the item is
	 * converted into a {@code DisclosureItem} and added to the list of items
	 * for return. This method will block on queue take call, if no items are
	 * present.
	 * 
	 * TODO Resolve loss of large disclosure items on offer failures.
	 * 
	 * @param items
	 *            list of disclosure entries
	 * @param optItem
	 *            the item to include in items or be forwarded to to large item
	 *            queue if it violates internal sizing restrictions. If item is
	 *            not present, method will attempt to take next item from queue.
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void addItem(List<DisclosureItem> items, Optional<String> optItem) throws InterruptedException {
		String item = (optItem.isPresent()) ? optItem.get() : itemQueue.take();
		if (item.length() > ItemParser.MAX_INTERNAL_ITEM_SIZE_CHARS) {
			if (!largeItemQueue.offer(item)) {
				log.error("Large message entry offer failed - message has been lost");
			}
		} else {

			try {
				items.add(new DisclosureItem(item));
			} catch (JsonParsingException | CsvParsingException | JSONDataValidationException
					| UnsupportedDisclosureType e) {
				log.error("Failed to process a new disclosure item", e);
				log.error("Item discarded:");
				log.error(item);
			}

		}
	}

	public void addLocalDisclosure(DisclosureBuffer db) {
		localDisclosure = db;
	}

	public int getRemainingCapacity() {
		return itemQueue.remainingCapacity();
	}

	@Override
	public ExchangeType exchangeType() {
		return ExchangeType.DisclosureQueue;
	}

	@Override
	public ComponentMaintenance scheduledMaintenance() {
		return new ComponentMaintenance(this, DisclosureQueueCheck.class, ProvenDisclosureMap.class);
	}

}
