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
package gov.pnnl.proven.cluster.lib.item;

import static org.hamcrest.MatcherAssert.assertThat;

import javax.json.Json;
import javax.json.JsonPatch;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.serializer.DeserializationContext;

import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.cluster.lib.disclosure.DomainProvider;
import gov.pnnl.proven.cluster.lib.disclosure.item.ExplicitItem;
import gov.pnnl.proven.cluster.lib.disclosure.item.MessageContext;
import gov.pnnl.proven.cluster.lib.disclosure.item.Validatable;

public class ExplicitItemTest {

	static Logger log = LoggerFactory.getLogger(ExplicitItemTest.class);

	ExplicitItem ei;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");

		// Use JSON from another item for test
		ei = new ExplicitItem(MessageContext.newBuilder().withDomain(DomainProvider.PROVEN_DISCLOSURE_DOMAIN)
				.withItem(ExplicitItem.class).withName("TEST NAME").withRequestor("TEST REQUESTOR")
				.withTags("TEST TAG1", "TEST TAG2").build().toJson());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSchema_toSchema_passValidate() {
		MatcherAssert.assertThat("ExplicitItem generates a valid schema",
				Validatable.hasValidSchema(ExplicitItem.class));
	}

	@Test
	public void testCreate_builder_validJson() {
		String jsonStr = ei.toJson().toString();
		assertThat("Valid JSON produced for ExplicitItem builder",
				Validatable.validate(ExplicitItem.class, jsonStr).isEmpty());
	}

	@Test
	public void testRoundTrip_builder_mcObjectsEqual() {

		String jsonStr = ei.toJson().toString();
		ExplicitItem ei2 = Validatable.toValidatable(ExplicitItem.class, jsonStr);
		JsonPatch jp = Json.createDiff(ei.getMessage(), ei2.getMessage());
		MatcherAssert.assertThat("Valid round trip", jp.toJsonArray().size() == 0);
	}
}
