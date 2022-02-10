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

import java.util.Date;
import java.util.UUID;

import javax.json.JsonValue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.cluster.lib.disclosure.exception.ValidatableBuildException;
import gov.pnnl.proven.cluster.lib.disclosure.item.AdministrativeItem;
import gov.pnnl.proven.cluster.lib.disclosure.item.DisclosureItem;
import gov.pnnl.proven.cluster.lib.disclosure.item.ExplicitItem;
import gov.pnnl.proven.cluster.lib.disclosure.item.MessageContext;
import gov.pnnl.proven.cluster.lib.disclosure.item.Validatable;

public class DisclosureItemTest {

	static Logger log = LoggerFactory.getLogger(DisclosureItemTest.class);

	ExplicitItem ei;
	MessageContext mc;
	DisclosureItem di;

	@Rule
	public ExpectedException exceptionGrabber = ExpectedException.none();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");

		mc = MessageContext.newBuilder().withItem(ExplicitItem.class).withName("TEST NAME")
				.withRequestor("TEST REQUESTOR").withTags("TEST TAG1", "TEST TAG2").build();

		di = DisclosureItem.newBuilder().withSourceMessageId(UUID.randomUUID())
				//.withApplicationSentTime(new Date().getTime())
				//.withApplicationSentTime(null)
				.withAuthToken(
						"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ")
				.withContext(mc).withMessage(JsonValue.EMPTY_JSON_OBJECT).withMessageSchema(JsonValue.EMPTY_JSON_OBJECT)
				.withIsTransient(false).withIsLinkedData(false).build();

		ei = ExplicitItem.newBuilder().withMessage(mc.toJson()).build();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSchema_toSchema_passValidate() {
		assertThat("DisclosureItem generates a valid schema", Validatable.hasValidSchema(DisclosureItem.class));
	}

	@Test
	public void testCreate_builder_validJson() {
		String jsonStr = di.toJson().toString();
		assertThat("Valid JSON produced for DisclosureItem builder",
				Validatable.validate(DisclosureItem.class, jsonStr).isEmpty());
	}

	@Test
	public void testRoundTrip_builder_diObjectsNotEqual() {
		String jsonStr = di.toJson().toString();
		DisclosureItem di2 = Validatable.toValidatable(DisclosureItem.class, jsonStr);
		assertThat("Valid round trip", !di.equals(di2));
	}

	@Test
	public void testLinkedDataRule_builder_throwValidatableBuildException() {

		/**
		 * Schema rule under test : If is linked data then message item must be
		 * [Explicit | Implicit]
		 * 
		 * Exception should be thrown because message item is Administrative.
		 */
		mc = MessageContext.newBuilder().withItem(AdministrativeItem.class).withName("TEST NAME")
				.withRequestor("TEST REQUESTOR").withTags("TEST TAG1", "TEST TAG2").build();

		exceptionGrabber.expect(ValidatableBuildException.class);
		di = DisclosureItem.newBuilder().withSourceMessageId(UUID.randomUUID())
				.withApplicationSentTime(new Date().getTime())
				.withAuthToken(
						"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ")
				.withContext(mc).withMessage(JsonValue.EMPTY_JSON_OBJECT).withMessageSchema(JsonValue.EMPTY_JSON_OBJECT)
				.withIsTransient(true).withIsLinkedData(true).build();
	}

	@Test
	public void createFromDisclosureItem_construct_createdWithNewIdentifier() {
		DisclosureItem di2 = DisclosureItem.createFromDisclosureItem(di, ei);
		assertThat("New identifier assigned", di2.getMessageId() != di.getMessageId());
	}

}
