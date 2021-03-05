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
package gov.pnnl.proven.cluster.lib.disclosure.item;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.IsNot;
import org.junit.Test;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.Problem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisclosureItemTest {

	static Logger log = LoggerFactory.getLogger(DisclosureItemTest.class);

	@Test
	public void test() throws FileNotFoundException, URISyntaxException, IOException, InstantiationException,
			IllegalAccessException {

		JsonSchema schema = new DisclosureItem().toSchema();
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		JsonObject jo;
		String jsonStr;
		try (InputStream ins = classloader.getResourceAsStream("message_schema/DisclosureItemTest.json");
				InputStreamReader insr = new InputStreamReader(ins);
				BufferedReader br = new BufferedReader(insr)) {
			jsonStr = br.lines().collect(Collectors.joining());
		}

		AbstractMap.SimpleEntry<JsonValue, List<Problem>> result = Validatable.validateWithDefaults(DisclosureItem.class,
				jsonStr);
		MatcherAssert.assertThat(result.getKey(), IsInstanceOf.instanceOf(JsonStructure.class));
		MatcherAssert.assertThat(result.getValue(), empty());
		
		DisclosureItem di = Validatable.toValidatable(DisclosureItem.class, result.getKey().toString());
		MatcherAssert.assertThat(di, notNullValue());
		
		
		// classloader.getResource("message-validation/DisclosureItem.json")
		//
		// Files.lines()
		//
		// String jsonStr = new
		// String(Files.readAllBytes(classloader.getResource("message-validation/DisclosureItem.json").toURI().getPath()));
		//
		//
		//
		// DisclosureItem di = (DisclosureItem)
		// Validatable.toValidatable(DisclosureItem.class, jo);
		// // DisclosureItem di = jsonb.fromJson(ins, DisclosureItem.class);
		//
		// MatcherAssert.assertThat("DisclosureItem not assigned a value",
		// di.getMessageId(), notNullValue());
		// MatcherAssert.assertThat("Domain value", di.getDisclosureDomain(),
		// is(equalTo("proven.pnnl.gov")));
		//
		// fail("Not yet implemented");
	}

}
