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

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReaderFactory;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import javax.json.stream.JsonParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.cluster.lib.disclosure.exception.JSONDataValidationException;
import gov.pnnl.proven.cluster.lib.disclosure.exchange.DisclosureItem;
import gov.pnnl.proven.cluster.lib.disclosure.message.MessageUtils;
import gov.pnnl.proven.cluster.lib.module.exchange.exception.EntryParserException;

/**
 * 
 * 
 * @author d3j766
 *
 */
public class ItemParser {

	static Logger log = LoggerFactory.getLogger(ItemParser.class);

	private static final JsonParserFactory pFactory = Json.createParserFactory(null);
	private static final JsonReaderFactory rFactory = Json.createReaderFactory(null);
	private static final JsonBuilderFactory bFactory = Json.createBuilderFactory(null);

	// Message constants
	private static final String MESSAGE_KEY = "message";

	// Linked data constants
	private static final String LD_ID = "@id";

	// 20 internal messages per max external message (WS not included)
	public static final int MAX_INTERNAL_ITEM_SIZE_CHARS = 250000; // 250K
	public static final int MAX_EXERNAL_ITEM_SIZE_CHARS = 5000000; // 5M

	// entry builder types
	private enum ItemBuilderType {
		OBJECT,
		ARRAY;
	}

	private JsonObject itemContainer;
	private Optional<JsonObject> message = Optional.empty();
	private Optional<JsonParser> messageParser = Optional.empty();
	private Stack<ItemBuilder> unfinishedBuilders;

	private class ItemBuilder {

		private boolean isRoot;
		private ItemBuilderType ebType;
		private JsonObjectBuilder ob;
		private JsonArrayBuilder ab;
		private String oId;
		private String oUri;
		private int size;
		private String lastKey;

		ItemBuilder(ItemBuilderType bType, int size) {
			this(bType, size, false);
		}

		ItemBuilder(ItemBuilderType ebType, int size, boolean isRoot) {

			// Create builder based on provided type
			this.isRoot = isRoot;
			this.ebType = ebType;
			if (ebType == ItemBuilderType.OBJECT) {
				ob = bFactory.createObjectBuilder();
				addId();
			} else {
				ab = bFactory.createArrayBuilder();
			}
			this.size = size;
			this.lastKey = null;
		}

		private void addId() {
			if (null == oId) {
				oId = UUID.randomUUID().toString();
				if (isRoot) {
					oUri = MessageUtils.PROVEN_MESSAGE_CONTENT_NODE_RES + "_" + oId;
				} else {
					oUri = MessageUtils.PROVEN_MESSAGE_CONTENT_CHILD_NODE_RES + "_" + oId;
				}
			}
			ob.add(LD_ID, oUri);
		}

		private void add(ItemBuilder uBuilder) {

			if (this.ebType == ItemBuilderType.OBJECT) {
				if (uBuilder.ebType == ItemBuilderType.OBJECT) {
					this.ob.add(this.lastKey, uBuilder.ob);
				} else {
					this.ob.add(this.lastKey, uBuilder.ab);
				}
			} else {
				if (uBuilder.ebType == ItemBuilderType.OBJECT) {
					this.ab.add(uBuilder.ob);
				} else {
					this.ab.add(uBuilder.ob);
				}
			}
		}

		private void reset() {

			size = MAX_INTERNAL_ITEM_SIZE_CHARS;
			// lastKey = null;
			if (ebType == ItemBuilderType.OBJECT) {
				ob = bFactory.createObjectBuilder();
				addId();
			} else {
				ab = bFactory.createArrayBuilder();
			}
		}

	}

	public ItemParser(String entry) {

		unfinishedBuilders = new Stack<>();

		try (JsonParser initParser = pFactory.createParser(new StringReader(entry))) {

			JsonObjectBuilder ecb = bFactory.createObjectBuilder();
			String lastKey = null;
			boolean isFirstEvent = true;

			while (initParser.hasNext()) {

				Event event = initParser.next();

				switch (event) {

				case START_OBJECT:
					if (isFirstEvent) {
						isFirstEvent = false;
						String oUri = MessageUtils.PROVEN_MESSAGE_RES + "_" + UUID.randomUUID().toString();
						ecb.add(LD_ID, oUri);
					} else {

						if ((null != lastKey) && (lastKey.equals(MESSAGE_KEY))) {
							message = Optional.of(initParser.getObject());
						} else {
							ecb.add(lastKey, initParser.getObject());
						}
					}
					break;

				case START_ARRAY:
					ecb.add(lastKey, initParser.getArray());
					break;

				case END_OBJECT:
					itemContainer = ecb.build();
					break;

				case KEY_NAME:
					lastKey = initParser.getString();
					break;

				case VALUE_STRING:
					String strVal = initParser.getString();
					ecb.add(lastKey, strVal);
					break;

				case VALUE_NUMBER:
					if (initParser.isIntegralNumber()) {
						long lVal = initParser.getLong();
						ecb.add(lastKey, lVal);
					} else {
						BigDecimal bdVal = initParser.getBigDecimal();
						ecb.add(lastKey, bdVal);
					}
					break;

				case VALUE_TRUE:
					ecb.add(lastKey, true);
					break;

				case VALUE_FALSE:
					ecb.add(lastKey, false);
					break;

				case VALUE_NULL:
					ecb.addNull(lastKey);
					break;

				default:
					throw new EntryParserException("Unknown structure processing event: " + event);
				}
			}

		} catch (Exception ex) {
			throw new EntryParserException("Error occurred parsing disclosure entry.", ex);
		}

		// Create message parser
		if (message.isPresent()) {
			messageParser = Optional.of(pFactory.createParser(message.get()));
		}

	}

	public List<DisclosureItem> parse() throws JSONDataValidationException {

		log.debug("PARSER STARTED");
		List<DisclosureItem> ret = parse(null);
		for (DisclosureItem di : ret) {
			log.debug("START CHUNKED MESSAGE################");
			log.debug(di.getMessage().toString());
			log.debug("END CHUNKED MESSAGE  ################");
		}

		log.debug("PARSER COMPLETED - " + ret.size());
		return ret;
	}

	private List<DisclosureItem> parse(ItemBuilder eBuilder) throws JSONDataValidationException {

		List<DisclosureItem> ret = new ArrayList<>();
		boolean isRoot = false;
		boolean isFirstEvent = true;

		// root builder
		if (null == eBuilder) {
			isRoot = true;
			eBuilder = new ItemBuilder(ItemBuilderType.OBJECT, MAX_INTERNAL_ITEM_SIZE_CHARS, true);
		}

		// Add to stack
		unfinishedBuilders.push(eBuilder);

		// There will only be a parser if the message is present, so no need
		// to test for message existence.
		if (messageParser.isPresent()) {

			try (JsonParser parser = messageParser.get()) {

				while (parser.hasNext()) {

					Event event = parser.next();

					switch (event) {

					case START_OBJECT:
					case START_ARRAY:
						if (isRoot && isFirstEvent) {
							isFirstEvent = false;
						} else {
							if (event == Event.START_OBJECT) {
								ret.addAll(parse(new ItemBuilder(ItemBuilderType.OBJECT, eBuilder.size)));
							} else {
								ret.addAll(parse(new ItemBuilder(ItemBuilderType.ARRAY, eBuilder.size)));
							}

							// Child builder has finished. Add it's finished
							// structure and remaining size count to this
							// builder and then remove it from the unfinished
							// stack. Stack should never be empty.
							ItemBuilder uBuilder = unfinishedBuilders.pop();
							eBuilder.add(uBuilder);
							eBuilder.size = uBuilder.size;
						}
						break;

					case END_OBJECT:
						if (isRoot) {
							ret.add(buildMessage());
						}
					case END_ARRAY:
						return ret;

					case KEY_NAME:
						eBuilder.lastKey = parser.getString();
						eBuilder.size = eBuilder.size - eBuilder.lastKey.length();
						break;

					case VALUE_STRING:
						String strVal = parser.getString();
						eBuilder.size = eBuilder.size - eBuilder.lastKey.length() - strVal.length();
						if (eBuilder.ebType == ItemBuilderType.OBJECT) {
							eBuilder.ob.add(eBuilder.lastKey, strVal);
						} else { // array
							eBuilder.ab.add(strVal);
						}
						break;

					case VALUE_NUMBER:
						if (parser.isIntegralNumber()) {
							long lVal = parser.getLong();
							long temp = lVal;
							int count = 0;
							if (lVal == 0)
								count = 1;
							else {
								while (temp != 0) {
									temp = temp / 10;
									++count;
								}
							}
							eBuilder.size = eBuilder.size - eBuilder.lastKey.length() - count;
							if (eBuilder.ebType == ItemBuilderType.OBJECT) {
								eBuilder.ob.add(eBuilder.lastKey, lVal);
							} else { // array
								eBuilder.ab.add(lVal);
							}
						} else {
							BigDecimal bdVal = parser.getBigDecimal();
							eBuilder.size = eBuilder.size - eBuilder.lastKey.length() - bdVal.toString().length();
							if (eBuilder.ebType == ItemBuilderType.OBJECT) {
								eBuilder.ob.add(eBuilder.lastKey, bdVal);
							} else { // array
								eBuilder.ab.add(bdVal);
							}
						}
						break;

					case VALUE_TRUE:
						eBuilder.size = eBuilder.size - eBuilder.lastKey.length() - 4;
						if (eBuilder.ebType == ItemBuilderType.OBJECT) {
							eBuilder.ob.add(eBuilder.lastKey, true);
						} else { // array
							eBuilder.ab.add(true);
						}
						break;

					case VALUE_FALSE:
						eBuilder.size = eBuilder.size - eBuilder.lastKey.length() - 4;
						if (eBuilder.ebType == ItemBuilderType.OBJECT) {
							eBuilder.ob.add(eBuilder.lastKey, false);
						} else { // array
							eBuilder.ab.add(false);
						}
						break;

					case VALUE_NULL:
						eBuilder.size = eBuilder.size - 4;
						if (eBuilder.ebType == ItemBuilderType.OBJECT) {
							eBuilder.ob.addNull(eBuilder.lastKey);
						} else { // array
							eBuilder.ab.addNull();
						}
						break;

					default:
						throw new EntryParserException("Unknown structure processing event: " + event);
					}

					// Check size if exceeded, build current message and add it
					// to
					// the Set of return values. Must not end on KEY_NAME event,
					// as
					// it would split key from value.
					if ((eBuilder.size < 0) && (event != Event.KEY_NAME)) {
						ret.add(buildMessage());
					}
				}

			} catch (Exception ex) {
				throw new EntryParserException("Error occurred parsing disclosure entry.", ex);
			}

		} else {
			// create new entry - no message content provided
			ret.add(new DisclosureItem(itemContainer));
		}

		return ret;
	}

	private DisclosureItem buildMessage() throws JSONDataValidationException {

		DisclosureItem ret = null;
		ItemBuilder temp;
		Stack<ItemBuilder> processedBuilders = new Stack<>();

		// Get current builder off stack - should never be null.
		ItemBuilder cBuilder = unfinishedBuilders.pop();

		// Get previous builder off stack - may be null.
		ItemBuilder pBuilder = (unfinishedBuilders.isEmpty()) ? (null) : (unfinishedBuilders.pop());

		boolean done = false;
		while (!done) {

			// root object
			if (null == pBuilder) {
				JsonObjectBuilder messageBuilder = bFactory.createObjectBuilder(itemContainer);
				ret = new DisclosureItem(messageBuilder.add(MESSAGE_KEY, cBuilder.ob).build());
				cBuilder.reset();
				processedBuilders.push(cBuilder);
				done = true;
			} else {
				pBuilder.add(cBuilder);
				temp = cBuilder;
				temp.reset();
				processedBuilders.push(temp);
				cBuilder = pBuilder;
				pBuilder = (unfinishedBuilders.isEmpty()) ? (null) : (unfinishedBuilders.pop());
			}
		}

		// Move back to unfinished stack
		ItemBuilder builder = (processedBuilders.isEmpty()) ? (null) : (processedBuilders.pop());
		while (null != builder) {
			unfinishedBuilders.push(builder);
			builder = (processedBuilders.isEmpty()) ? (null) : (processedBuilders.pop());
		}

		return ret;
	}

}
