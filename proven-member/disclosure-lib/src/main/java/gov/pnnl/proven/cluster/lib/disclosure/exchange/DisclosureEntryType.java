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
package gov.pnnl.proven.cluster.lib.disclosure.exchange;

import javax.json.stream.JsonParsingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;

import gov.pnnl.proven.cluster.lib.disclosure.exception.UnsupportedDisclosureEntryType;
import gov.pnnl.proven.cluster.lib.disclosure.message.CsvDisclosure;
import gov.pnnl.proven.cluster.lib.disclosure.message.DisclosureMessage;
import gov.pnnl.proven.cluster.lib.disclosure.message.JsonDisclosure;
import gov.pnnl.proven.cluster.lib.disclosure.message.exception.CsvParsingException;

/**
 * Indicates the disclosure entry type for entries originating from an external
 * source. Each type has a "magic" regular expression that is used to determine
 * if an entry is of that type.
 * 
 * @author d3j766
 *
 */
public enum DisclosureEntryType {

	/**
	 * CSV formatted value, representing message content types in
	 * {@code MessageContentGroup#Knowledge}.
	 */
	CSV(MagicRegex.CSV_REGEX),

	/**
	 * JSON formatted value representation of all message content types.
	 */
	JSON(MagicRegex.JSON_REGEX);

	public class MagicRegex {
		public static final String CSV_REGEX = "(?s)^#CSV\\s*\\R+.*";
		public static final String JSON_REGEX = "(?s)^\\s*\\{.*\\}\\s*$";
	}

	static Logger log = LoggerFactory.getLogger(DisclosureEntryType.class);

	private String regex;

	DisclosureEntryType(String regex) {
		this.regex = regex;
	}

	public String getRegex() {
		return regex;
	}

	/**
	 * Determines an external disclosure entrie's type.
	 * 
	 * @param entry
	 *            the disclosure entry
	 * @throws UnsupportedDisclosureEntryType
	 *             if the entry type could not be determined.
	 * @throws JsonParsingException
	 *             if parsing failed for a JSON entry type
	 * @throws CsvParsingException
	 *             if parsing failed for a CSV entry type
	 * 
	 * @return the {@code DisclosureEntryType}
	 */
	public static DisclosureEntryType getEntryType(String entry) throws UnsupportedDisclosureEntryType {

		DisclosureEntryType ret = null;

		if (entry.matches(CSV.getRegex())) {
			ret = DisclosureEntryType.CSV;
		}

		if (entry.matches(JSON.getRegex())) {
			ret = DisclosureEntryType.JSON;
		}

		if (null == ret) {
			throw new UnsupportedDisclosureEntryType();
		}

		return ret;
	}

	/**
	 * Creates and returns a {@code DisclosureMessage} for an external
	 * disclosure entry.
	 * 
	 * @param entry
	 *            the disclosure entry
	 * @throws UnsupportedDisclosureEntryType
	 *             if the entry type could not be determined.
	 * @throws JsonParsingException
	 *             if parsing failed for a JSON entry type
	 * @throws CsvParsingException
	 *             if parsing failed for a CSV entry type
	 * 
	 * @return the {@code DisclosureEntryType}
	 */
	public static DisclosureMessage getEntryMessage(String entry) throws UnsupportedDisclosureEntryType {

		DisclosureMessage ret = null;

		if (entry.matches(CSV.getRegex())) {
			ret = new DisclosureMessage(CsvDisclosure.toJsonObject(entry));
		}

		if (entry.matches(JSON.getRegex())) {
			ret = new DisclosureMessage(JsonDisclosure.toJsonObject(entry));
		}

		if (null == ret) {
			throw new UnsupportedDisclosureEntryType();
		}

		return ret;
	}
}
