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

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.ringbuffer.OverflowPolicy;
import com.hazelcast.ringbuffer.ReadResultSet;
import com.hazelcast.ringbuffer.Ringbuffer;

import gov.pnnl.proven.cluster.lib.disclosure.exchange.BufferedItem;
import gov.pnnl.proven.cluster.lib.disclosure.exchange.BufferedItemState;
import gov.pnnl.proven.cluster.lib.module.exchange.exception.BufferReaderInterruptedException;

/**
 * Represents a data structure for storing {@code BufferItem}s for future
 * processing. The stored items may be exchanged with other like buffer(s)
 * within a module, member, or cluster depending on the processing requirements.
 * 
 * @author d3j766
 *
 */
public abstract class ExchangeBuffer<T extends BufferedItem> extends ExchangeComponent {

	static Logger log = LoggerFactory.getLogger(ExchangeBuffer.class);

	/**
	 * Items are read/written to buffer in batches. These values represent the
	 * minimum and maximum batches for a read or write operation. TODO - make
	 * configurable
	 */
	public static final Integer BATCH_MIN = 25;
	public static final Integer BATCH_MAX = 50;

	public Integer getMinBatchSize(BufferedItemState state) {
		return minMaxBatchSizeByState.get(state).getKey();
	}

	public Integer getMaxBatchSize(BufferedItemState state) {
		return minMaxBatchSizeByState.get(state).getValue();
	}

	protected BufferedItemState[] supportedItemStates;
	protected Map<BufferedItemState, Long> lastReadItemByState;
	protected BufferedItemState headState = null;
	protected Map<BufferedItemState, CompletableFuture<Void>> bufferReaders;
	protected Map<BufferedItemState, SimpleEntry<Integer, Integer>> minMaxBatchSizeByState;
	protected Ringbuffer<T> buffer;

	@Resource(lookup = RequestExchange.RE_EXECUTOR_SERVICE)
	ManagedExecutorService mes;

	/**
	 * Created a new exchange buffer. The exchange is configured to support the
	 * list of provided states.
	 * 
	 * @param states
	 */
	public ExchangeBuffer(BufferedItemState[] states) {
		super();
		supportedItemStates = states;
		bufferReaders = new HashMap<BufferedItemState, CompletableFuture<Void>>();
		lastReadItemByState = new HashMap<BufferedItemState, Long>();
		minMaxBatchSizeByState = new HashMap<BufferedItemState, SimpleEntry<Integer, Integer>>();
		for (BufferedItemState state : states) {
			lastReadItemByState.put(state, -1L);
			AbstractMap.SimpleEntry<Integer, Integer> entry = new AbstractMap.SimpleEntry<>(BATCH_MIN, BATCH_MAX);
			minMaxBatchSizeByState.put(state, entry);
		}
	}

	protected abstract void itemProcessor(ReadResultSet<T> item);

	protected void startReaders() {
		for (BufferedItemState state : supportedItemStates) {
			log.debug("Starting exchange buffer (" + this.getClass().getSimpleName() + ") reader for state : " + state);
			startReader(state, false);
		}
	}

	private synchronized void startReader(BufferedItemState state, boolean replaceReader) {

		synchronized (bufferReaders) {

			CompletableFuture<Void> cf = bufferReaders.get(state);
			boolean hasReader = ((null != cf) && (!cf.isDone()));

			// Remove existing reader if replace reader is requested
			if (hasReader && replaceReader) {
				cf.cancel(true);
				hasReader = false;
			}

			// Add new reader if one doesn't exist
			if (!hasReader) {
				cf = CompletableFuture.runAsync(() -> {
					runReader(state);
				}, mes).exceptionally(this::readerException);
				bufferReaders.put(state, cf);
			}
		}
	}

	protected void runReader(BufferedItemState state) {

		while (true) {

			log.debug("--");
			log.debug(moduleName + ":: BUFFER TAIL SEQUENCE: " + buffer.tailSequence());
			log.debug("--");

			log.debug(moduleName + ":: Item processor for :: " + state);

			ReadResultSet<T> bufferedItems = readItem(state);

			// TODO - item processor is now on it's own thread, and any
			// transfers should be taken care of in this thread.
			CompletableFuture.runAsync(() -> {
				itemProcessor(bufferedItems);
			}, mes).exceptionally(this::itemProcessorException);

			log.debug("Item processor invoved for :: " + state);

		}

	}

	/**
	 * Callback for buffer reader that has ended exceptionally.
	 * 
	 * @param readerException
	 *            the exception thrown from the reader.
	 */
	protected Void readerException(Throwable readerException) {

		Void ret = null;

		boolean isInterrupted = (readerException instanceof BufferReaderInterruptedException);
		boolean isCancelled = (readerException instanceof CancellationException);

		// Interrupted exception - indicates the reader has failed due to thread
		// interruption, attempts to restart should be made.
		if (isInterrupted) {
			log.info("Exchange buffer reader was interrupted.");
			BufferReaderInterruptedException bre = (BufferReaderInterruptedException) readerException;
			startReader(bre.getState(), true);
		}

		// Cancelled exception - indicates the reader has been cancelled. This
		// is a controlled event, attempts to restart the reader should not be
		// made.
		else if (isCancelled) {
			log.info("Exchange buffer reader was cancelled.");
		}

		// Other exceptions
		else {
			// TODO Call failure() to record the internal failure event and set
			// status to either failedOnelineRetry or failed depending on
			// severity -> If unchecked or error then failed, else
			// failedOnlineRetry.
			log.error("Exchange buffer reader was completed exceptionally :: " + readerException.getMessage());
			readerException.printStackTrace();
		}

		return ret;
	}

	/**
	 * Callback for item processor that has ended exceptionally.
	 * 
	 * @param processorException
	 *            the exception thrown from the reader.
	 */
	protected Void itemProcessorException(Throwable processorException) {

		Void ret = null;
		log.error("ITEM PROCESSOR FAILED");
		processorException.printStackTrace();

		return ret;
	}

	public void cancelReaders() {

		synchronized (bufferReaders) {

			for (BufferedItemState state : supportedItemStates) {
				CompletableFuture<Void> cf = bufferReaders.get(state);
				boolean hasReader = ((null != cf) && (!cf.isDone()));
				if (hasReader) {
					cf.cancel(true);
				}
			}
		}
	}

	/**
	 * Indicates if the exchange buffer has the free space to accept addition of
	 * new buffered items.
	 * 
	 * @return true if new buffer items may be added, false otherwise.
	 */
	public boolean hasFreeSpace(BufferedItemState state) {
		return freeSpaceCount(state) > minMaxBatchSizeByState.get(state).getValue();
	}

	/**
	 * Returns the count of already processed buffered items representing the
	 * buffer's free space.
	 * 
	 * @return count of processed buffer items.
	 */
	protected synchronized long freeSpaceCount(BufferedItemState state) {
		log.debug("Calculating ringbuffer free space");

		Long h = buffer.headSequence();
		Long t = buffer.tailSequence();
		Long c = buffer.capacity();
		Long cSeq = h + c - 1;
		Long r = lastReadItemByState.get(state);
		Long freeSpace = ((r - h) + 1) + (cSeq - t);

		log.debug("\th: " + h);
		log.debug("\tt: " + t);
		log.debug("\tc: " + c);
		log.debug("\tcSeq: " + cSeq);
		log.debug("\tr: " + r);
		log.debug("\tfreeSpace: " + freeSpace);

		return freeSpace;
	}

	private long getUnprocessedItemCount() {

		// This is a point in time view and may not be exact be depending on
		// buffer activity during call.

		Long ret;
		boolean validReaders = allReadersActive();
		Long c = buffer.capacity();
		Long h = buffer.headSequence();
		Long t = buffer.tailSequence();
		Long r = Collections.min(lastReadItemByState.values());

		// All readers must be active to determine capacity correctly. If one or
		// more readers are not active then it's assumed max capacity has been
		// reached.
		if (!validReaders) {
			ret = c;
		} else {
			if (r == -1L) {
				if (t == -1L) {
					ret = 0L;
				} else {
					ret = t - h;
				}
			} else {
				ret = t - r;
			}
		}

		return ret;
	}

	private boolean allReadersActive() {

		boolean ret = true;

		for (CompletableFuture<Void> cf : bufferReaders.values()) {
			if ((null == cf) || (cf.isDone())) {
				ret = false;
				break;
			}
		}

		return ret;
	}

	/**
	 * Adds items to the exchange buffer.
	 * 
	 * @param items
	 *            the items to add
	 * 
	 * @return true if the items were added. False is returned if there was not
	 *         enough free space in the buffer to support the new items.
	 */
	public boolean addItems(Collection<T> items, BufferedItemState state) {

		// Assume write succeeds. Either all items are added or no items
		// are added.
		boolean ret = true;

		Runtime rt = Runtime.getRuntime();
		log.debug("ADDING ITEMS TO DISCLOSURE BUFFER- COUNT :: " + items.size());

		if ((null != items) && (!items.isEmpty())) {

			log.debug("B####################################B");
			Long fsp = freeSpaceCount(state);
			log.debug("FREE SPACE COUNT :: " + fsp);
			if (items.size() <= fsp) {

				try {
					buffer.addAllAsync(items, OverflowPolicy.OVERWRITE).get();
				} catch (InterruptedException | ExecutionException e) {
					ret = false;
					e.printStackTrace();
				}

			} else {
				log.error("ITEMS COUND NOT BE ADDED NOT ENOUGH FREE SPACE");
				ret = false;
			}
			freeSpaceCount(state);
			log.debug("E####################################E");

		}
		log.debug("FREE MEMORY AFTER ADD ITEMS :: " + rt.freeMemory());
		return ret;
	}

	/**
	 * Reads the next unprocessed item from the exchange buffer that has the
	 * specified {@code BufferedItemState}.
	 * 
	 * @param {@code BufferedItemState} of the next item to read.
	 * @return the items read. This method will block if there are no unprocessed
	 *         items for the state provided.
	 * @throws BufferReaderInterruptedException
	 *             if the thread was interrupted during the read operation.
	 */
	protected ReadResultSet<T> readItem(BufferedItemState state) throws BufferReaderInterruptedException {

		log.debug("READ STREAM ITEMS BEGIN :: " + Calendar.getInstance().getTime().toString());

		long t = buffer.tailSequence();
		long r = lastReadItemByState.get(state);
		int startSeq = (int) r + 1;
		int minBatch = getMinBatchSize(state);
		int maxBatch = getMaxBatchSize(state);
		int u = (int) (t - r);
		int minCount = (u <= 0) ? (1) : (Math.min(u, minBatch));
		int maxCount = maxBatch;
		ReadResultSet<T> rs;

		log.debug(moduleName + " :: BATCH READ STARTING FOR STATE :: " + state);
		log.debug("start sequence :: " + startSeq);
		log.debug("min count :: " + minCount);
		log.debug("max count :: " + maxCount);

		try {

			// Batch read
			ICompletableFuture<ReadResultSet<T>> icf = buffer.readManyAsync(startSeq, minCount, maxCount, (bi) -> {
				return (bi.getItemState().equals(state));
			});
			rs = icf.get();
			log.debug("NUMBER OF ITEMS READ FROM DISCLOSURE BUFFER :: " + rs.size());
			log.debug("BATCH READ COMPLETED FOR STATE :: " + state);

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			throw new BufferReaderInterruptedException(
					"Disclosure buffer reader interrupted for state :: " + state.toString(), state, e);
		}

		long rc = rs.readCount();
		lastReadItemByState.put(state, r + rc);

		log.debug("READ STREAM ITEMS END :: " + Calendar.getInstance().getTime().toString());
		return rs;
	}

}
