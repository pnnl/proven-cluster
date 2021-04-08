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
package gov.pnnl.proven.cluster.lib.disclosure.deprecated.exchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
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
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.topbraid.shacl.validation.ValidationUtil;

public class TesetRegex {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		//ValidationUtil.createValidationEngine(dataModel, shapesModel, validateShapes)
		
		
		// Supported headers
		String CONCEPT_HEADER = "concept";
		String ID_HEADER = "id";
		String SUBJECT_ID_HEADER = "subjectId";
		String PREDICATE_HEADER = "predicate";
		String OBJECT_ID_HEADER = "objectId";
		String SUBJECT_CONCEPT_HEADER = "subjectConcept";
		String OBJECT_CONCEPT_HEADER = "objectConcept";

		// Concept Definition - these headers must be included. Other user defined
		// headers may exist
		String[] conceptHeaders = { CONCEPT_HEADER, ID_HEADER };
		Set<String> conceptDef = Collections
				.unmodifiableSet(new HashSet<String>(Arrays.asList(conceptHeaders)));

		// Intra-relationships definition - must match exactly
		String[] intraHeaders = { CONCEPT_HEADER, SUBJECT_ID_HEADER, PREDICATE_HEADER, OBJECT_ID_HEADER };
		Set<String> intraDef = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(intraHeaders)));

		// Inter-relationships definition - must match exactly
		String[] interHeaders = { SUBJECT_CONCEPT_HEADER, OBJECT_CONCEPT_HEADER, SUBJECT_ID_HEADER,
				PREDICATE_HEADER, OBJECT_ID_HEADER };
		Set<String> interDef = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(interHeaders)));
		
		
		// REGEX

		// String entry = " { } } } } } x \n } ";
		String entry = "#CSV\nfsfd }  ";
		String regexTry = "(?s)^#CSV\\s*\\R+.*";
		String regexGood = "(?s)^\\s*\\{.*\\}\\s*$";

		if (entry.matches(regexTry)) {
			System.out.println("YES");
		} else {
			System.out.println("NO");
		}

		// CSV

		//@formatter:off
		String csv = "#CSV \n" +
		             "#domain = tmbr.pnnl.gov \n" +
		             "#disclosureId = dId \n" +
		             "#requestorId = rId \n" +
		             "concept, subjectId, predicate, objectId \n" +
				     " A, 1, composedOf, null \n " + 
	                 " B, 2, composedOf, null \n";
		//@formatter:on

		Iterable<CSVRecord> records;
		BufferedReader in = new BufferedReader(new StringReader(csv));
		//records = CSVFormat.DEFAULT.withIgnoreEmptyLines(true).withCommentMarker('#').withTrim().withNullString("null").withHeader().withSkipHeaderRecord().parse(in);
		records = CSVFormat.EXCEL.withFirstRecordAsHeader().withTrim().withCommentMarker('#').parse(in);
//		Set<String> headers = records.iterator().next().toMap().keySet();
//		for (String header : headers) {
//			System.out.println(header);
//		}
		
		// Root object
		JsonObjectBuilder ob = Json.createObjectBuilder();
		
		// Array containing CSV objects
		JsonArrayBuilder ab = Json.createArrayBuilder();
		
		for (CSVRecord record : records) {

			Set<String> headers = record.toMap().keySet();
			
			if (record.getRecordNumber() == 1) {

				System.out.println(headers);
				if (headers.equals(intraDef)) {
					System.out.println("INTRA");					
				}
				else if (headers.equals(interDef)) {
					System.out.println("INTER");
				}
				else if (headers.contains(conceptDef)) {
					System.out.println("CONCEPT");
				}
				else {
					System.out.println("NONE");
				}
				
			}
			
			// addRecord(definition, ab, record);
		
			JsonObjectBuilder rob = Json.createObjectBuilder();
			for (String header : headers) {
				rob.add(header, record.get(header));					
			}
			JsonObject ro = rob.build();
			ab.add(ro);

			
			
//			JsonObjectBuilder rob = Json.createObjectBuilder();
//			rob.add("concept", record.get("concept"));
//			rob.add("subjectId", record.get("subjectId"));
//			rob.add("predicate", record.get("predicate"));
//			JsonObject ro = rob.build();
//			ab.add(ro);
			
			System.out.println();
		}
		
		JsonArray ja = ab.build();

		JsonObjectBuilder mob = Json.createObjectBuilder();
		mob.add("intraRelationships",ja);
		JsonObject mo = mob.build();
		
		ob.add("message", mo);
		JsonObject jo = ob.build();
		
		
		Map<String, Boolean> config = new HashMap<>();
		 
		config.put(JsonGenerator.PRETTY_PRINTING, true);
		         
		JsonWriterFactory writerFactory = Json.createWriterFactory(config);
		
		String jsonString;
		try(Writer writer = new StringWriter()) {
		    writerFactory.createWriter(writer).write(jo);
		    jsonString = writer.toString();
		    System.out.print(jsonString);
		}
		

		String s = "T=Junior Developer, DNQ=13346057, SURNAME=Doe, GIVENNAME=John, SERIALNUMBER=UK";
		//@formatter:off
		String props = "#CSV \n " +
	                   " \n\n\n\n" +
	                   "# domain=tmbr.pnnl.gov \n" +
	                   "# disclosureId = dId \n" +
	                   "# requestorId = rId \n" +
	                   "concept, subjectId, predicate, objectId \n" + 
			           " A, 1, composedOf, 2 ";
		//@formatter:on

		// Matcher m = Pattern.compile("(?:,
		// )?([^=]+)\\=([^,]+)").matcher(props);
		Pattern pOrig = Pattern.compile("(?:, )?([^=]+)\\=([^,]+)");
		Pattern p = Pattern.compile("#?([^=]+)\\=(.*)");
		//Map<DisclosureType, String> results = new HashMap<>();
		// in.lines().filter((line) -> line.startsWith("#")).forEach((line) -> {
		// Matcher m = p.matcher(line); if (m.find()) {
		// System.out.println(m.group(1) + " - " + m.group(2)); } } );
		in.lines().filter((line) -> line.startsWith("#")).forEach((line) -> {
			Matcher m = p.matcher(line);
			if (m.find()) {
				
				System.out.println(m.group(1) + " - " + m.group(2));
			}
		});
		// while (m.find()) {
		// System.out.println(m.group(1) + " - " + m.group(2));
		// }

	}
	
	
}
