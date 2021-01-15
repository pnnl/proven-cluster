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
package gov.pnnl.proven.cluster.lib.disclosure.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureDomain;
import gov.pnnl.proven.cluster.lib.disclosure.exception.InvalidDisclosureDomainException;
import gov.pnnl.proven.cluster.lib.disclosure.message.exception.CsvParsingException;

/**
 * Accepts Proven disclosure data represented in a CSV format. CSV data is
 * checked for correctness at construction and converted into the platform's
 * internal JSON format.
 * 
 * @author d3j766
 *
 * @see DisclosureMessage, ProvenMessage
 * 
 */
public class CsvDisclosure {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(CsvDisclosure.class);

	private static final String MESSAGE_OBJECT = "message";

	// TODO should consolidate to general Enum that includes all top level
	// message fields. These below are a subset, used for CSV disclsoure.
	public enum MessageField {

		DOMAIN_FIELD("domain"),
		DISCLOSURE_ID_FIELD("disclosureId"),
		REQUESTOR_ID_FIELD("requestorId");

		private String fieldName;

		MessageField(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldName() {
			return fieldName;
		}

		@Override
		public String toString() {
			return fieldName;
		}

		public static MessageField getMessageField(String name) {

			name = name.trim();
			MessageField ret = null;
			for (MessageField field : MessageField.values()) {
				if (name.equals(field.fieldName)) {
					ret = field;
					break;
				}
			}

			return ret;
		}
	}

	// Supported headers
	public static final String CONCEPT_HEADER = "concept";
	public static final String ID_HEADER = "id";
	public static final String SUBJECT_ID_HEADER = "subjectId";
	public static final String PREDICATE_HEADER = "predicate";
	public static final String OBJECT_ID_HEADER = "objectId";
	public static final String SUBJECT_CONCEPT_HEADER = "subjectConcept";
	public static final String OBJECT_CONCEPT_HEADER = "objectConcept";

	// Concept Definition - these headers must be included. Other user defined
	// headers may exist
	public static String[] conceptHeaders = { CONCEPT_HEADER, ID_HEADER };
	private static Set<String> conceptDef = Collections
			.unmodifiableSet(new HashSet<String>(Arrays.asList(conceptHeaders)));

	// Intra-relationships definition - must match exactly
	public static String[] intraHeaders = { CONCEPT_HEADER, SUBJECT_ID_HEADER, PREDICATE_HEADER, OBJECT_ID_HEADER };
	private static Set<String> intraDef = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(intraHeaders)));

	// Inter-relationships definition - must match exactly
	public static String[] interHeaders = { SUBJECT_CONCEPT_HEADER, OBJECT_CONCEPT_HEADER, SUBJECT_ID_HEADER,
			PREDICATE_HEADER, OBJECT_ID_HEADER };
	private static Set<String> interDef = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(interHeaders)));

	public enum HeaderDefinition {
		CONCEPT("conceptDefinitions"),
		INTRA("intraConceptRelationships"),
		INTER("interConceptRelationships");

		private String fieldName;

		HeaderDefinition(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldName() {
			return fieldName;
		}

		@Override
		public String toString() {
			return fieldName;
		}

	}

	public CsvDisclosure() {
	}
	
	public static JsonObject toJsonObject(String json) throws CsvParsingException {

		// root object
		JsonObject ret = null;

		// root object builder
		JsonObjectBuilder rob = Json.createObjectBuilder();

		// array builder for CSV record objects
		JsonArrayBuilder ab = Json.createArrayBuilder();

		// message fields
		Map<MessageField, String> fields = new HashMap<>();

		// header definition
		HeaderDefinition headerDef = null;

		// Extract proven message property fields, if any
		try (BufferedReader inFields = new BufferedReader(new StringReader(json))) {
			Pattern p = Pattern.compile("#?([^=]+)\\=(.*)");
			inFields.lines().filter((line) -> line.startsWith("#")).forEach((line) -> {
				Matcher m = p.matcher(line);
				if (m.find()) {
					MessageField field = MessageField.getMessageField(m.group(1));

					if (null != field) {
						fields.put(field, m.group(2).trim());
						log.debug("Message field extracted from CSV :: " + m.group(1) + "=" + m.group(2));
					}
				}
			});
		} catch (IOException ex) {
			log.error("An IOException was caught: " + ex.getMessage());
			ex.printStackTrace();
		}

		// Confirm header definition and add record objects to array
		Iterable<CSVRecord> records;
		try (BufferedReader in = new BufferedReader(new StringReader(json))) {

			Set<String> headers = null;
			records = CSVFormat.EXCEL.withFirstRecordAsHeader().withTrim().withCommentMarker('#').withIgnoreEmptyLines()
					.parse(in);
			for (CSVRecord record : records) {

				if (null == headers) {
					headers = record.toMap().keySet();
				}

				log.debug("CSV headers: " + headers.toString());

				// Verify a compatible definition being provided
				if (record.getRecordNumber() == 1) {

					if (headers.equals(intraDef)) {
						log.debug("CSV INTRA definition");
						headerDef = HeaderDefinition.INTRA;
					} else if (headers.equals(interDef)) {
						log.debug("CSV INTER definition");
						headerDef = HeaderDefinition.INTER;
					} else if (headers.containsAll(conceptDef)) {
						log.debug("CSV CONCEPT definition");
						headerDef = HeaderDefinition.CONCEPT;
					} else {
						log.error("Incomatible CSV definition provided.");
						throw new CsvParsingException("Invalid CSV header configuration");
					}
				}

				// Build record object and add to array
				JsonObjectBuilder b = Json.createObjectBuilder();
				for (String header : headers) {
					b.add(header, record.get(header));
				}
				JsonObject ob = b.build();
				ab.add(ob);

			}

		} catch (IOException ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}

		// No records
		if (null == headerDef) {
			throw new CsvParsingException("Missing CSV records");
		}

		// Build root json object message and return
		JsonArray recordObjects = ab.build();
		for (MessageField field : fields.keySet()) {

			String fieldName = field.getFieldName();
			String value = fields.get(field);

			// verify domain
			if (MessageField.DOMAIN_FIELD == field) {
				if (!DisclosureDomain.isValidDomain(value)) {
					throw new InvalidDisclosureDomainException("CSV entry contains an invaid domain");
				}
			}
			rob.add(fieldName, value);

		}
		JsonObjectBuilder mob = Json.createObjectBuilder();
		mob.add(headerDef.getFieldName(), recordObjects);
		rob.add(MESSAGE_OBJECT, mob);
		ret = rob.build();

		return ret;
	}

}
