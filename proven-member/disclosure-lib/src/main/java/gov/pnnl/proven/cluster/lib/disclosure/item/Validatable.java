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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import javax.json.stream.JsonParsingException;

import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonSchemaBuilderFactory;
import org.leadpony.justify.api.JsonValidatingException;
import org.leadpony.justify.api.JsonValidationService;
import org.leadpony.justify.api.Problem;
import org.leadpony.justify.api.ProblemHandler;
import org.leadpony.justify.api.ValidationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a JSON-SCHEMA validatable object.
 * 
 * @author d3j766
 *
 */
public interface Validatable {

	static Logger log = LoggerFactory.getLogger(Validatable.class);

	static final String SCHEMA_RESOURCE_DIR = "message-validation";
	static final String JSON_SCHEMA_SUFFIX = ".schema.json";

	static final JsonParserFactory pFactory = Json.createParserFactory(null);
	static final JsonValidationService service = JsonValidationService.newInstance();
	static final JsonSchemaBuilderFactory sbf = service.createSchemaBuilderFactory();
	static final JsonbConfig config = new JsonbConfig().withFormatting(true);
	static final Jsonb jsonb = JsonbBuilder.create(config);

	/**
	 * Converts string to a JsonStructure (Object or Array).
	 * 
	 * @param jsonStr
	 *            JSON string to be converted
	 * @return JSON structure for the provided String
	 * 
	 * @throws JsonParsingException
	 *             if provided string is invalid JSON.
	 * @throws NullPointerException
	 *             if provided JSON string is null.
	 */
	static JsonStructure toJsonFromString(String jsonStr) {

		JsonStructure ret;
		try (JsonReader reader = Json.createReader(new StringReader(jsonStr))) {
			ret = reader.readObject();
		}
		return ret;
	}

	/**
	 * Returns the JSON-SCHEMA resource name for a Validatable type.
	 * 
	 * @param clazz
	 *            the Validatable
	 * @return the name of the schema resource
	 */
	static <T extends Validatable> String getSchemaName(Class<T> clazz) {
		return clazz.getSimpleName() + ".schema.json";
	}

	/**
	 * Retrieves the schema resource for a Validatable.
	 * 
	 * @param clazz
	 *            the Validatable
	 * @return a JSON-SCHEMA object
	 * 
	 * @throws IllegalAccessException
	 *             if no access to Validatable definition
	 * @throws InstantiationException
	 *             could not instantiate a new Validatable
	 */
	static <T extends Validatable> JsonSchema retrieveSchema(Class<T> clazz)
			throws InstantiationException, IllegalAccessException {
		return clazz.newInstance().toSchema();
	}

	/**
	 * Scans Package containing Validatable types and returns list of
	 * implementations.
	 * 
	 * Note: This assumes all implementations of Validatable and the Validatable
	 * interface itself are contained in the same package.
	 * 
	 * Note: This is meant to be called at build-time by Gradle, does not
	 * support JAR scanning.
	 * 
	 * @return list of Validatable implementations.
	 * 
	 * @throws ClassNotFoundException
	 * @throws URISyntaxException
	 * @throws UnsupportedOperationException
	 *             if JAR is the package protocol. This is intended to be called
	 *             at build time.
	 */
	@SuppressWarnings("unchecked")
	static <T extends Validatable> List<Class<T>> getValidatables() throws URISyntaxException, ClassNotFoundException {

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		ArrayList<Class<T>> names = new ArrayList<>();
		String packageName = Validatable.class.getPackage().getName();
		String packagePath = packageName.replace(".", "/");
		URL packageURL = classLoader.getResource(packagePath);

		if (packageURL.getProtocol().equals("jar")) {
			throw new UnsupportedOperationException();
		} else {
			URI uri = packageURL.toURI();
			File folder = new File(uri.getPath());
			File[] vEntries = folder.listFiles();
			String vName;
			for (File vEntry : vEntries) {
				vName = vEntry.getName();
				String vQName = packageName + "." + vName.substring(0, vName.lastIndexOf('.'));
				Class<?> vClass = Class.forName(vQName);
				if ((!vClass.equals(Validatable.class)) && (Validatable.class.isAssignableFrom(vClass))) {
					names.add((Class<T>) vClass);
				}
			}
		}
		return names;
	}

	/**
	 * Validates provided JSON string using JSON-SCHEMA associated with the
	 * provided Validatable. Default values are filled in for missing
	 * properties. Used by {@link #toValidatable(Class, JsonObject)} before
	 * deserialization.
	 * 
	 * @param validatable
	 *            the Validatable type
	 * @param jsonStr
	 *            the JSON string to validate
	 * @return a SimpleEntry containing the JsonValue representation of the
	 *         string (key) and the list of problems (value), if any,
	 *         encountered during the validation process. An empty list
	 *         indicates the validation was successful. JsonValue should be
	 *         either a JSON Array or Object.
	 * @throws JsonParsingException
	 *             if invalid JSON provided
	 * @throws IllegalAccessException
	 *             if no access to Validatable definition
	 * @throws InstantiationException
	 *             could not instantiate a new Validatable
	 */
	static <T extends Validatable> AbstractMap.SimpleEntry<JsonValue, List<Problem>> validateWithDefaults(
			Class<T> clazz, String jsonStr) throws InstantiationException, IllegalAccessException {

		AbstractMap.SimpleEntry<JsonValue, List<Problem>> ret;
		List<Problem> problems = new ArrayList<>();
		JsonSchema schema = clazz.newInstance().toSchema();
		ProblemHandler handler = ProblemHandler.collectingTo(problems);
		ValidationConfig config = service.createValidationConfig();
		config.withSchema(schema).withProblemHandler(handler).withDefaultValues(true);
		JsonReaderFactory readerFactory = service.createReaderFactory(config.getAsMap());
		try (JsonReader reader = readerFactory.createReader(new StringReader(jsonStr))) {
			JsonValue value = reader.readValue();
			ret = new AbstractMap.SimpleEntry<>(value, problems);
		}
		return ret;
	}

	/**
	 * Validates JSON string using JSON-SCHEMA for the provided Validatable
	 * type.
	 * 
	 * @param clazz
	 *            the Validatable type
	 * @param json
	 *            the JSON string to validate
	 * @return a list of problems, if any, encountered during the validation
	 *         process. Empty list indicates the validation was successful.
	 * 
	 * @throws IllegalAccessException
	 *             if no access to Validatable definition
	 * @throws InstantiationException
	 *             could not instantiate a new Validatable
	 */
	static <T extends Validatable> List<Problem> validate(Class<T> clazz, String jsonStr)
			throws InstantiationException, IllegalAccessException {

		List<Problem> problems = new ArrayList<>();
		Reader reader = new StringReader(jsonStr);
		JsonSchema schema = retrieveSchema(clazz);
		ProblemHandler handler = ProblemHandler.collectingTo(problems);

		try (JsonParser parser = service.createParser(reader, schema, handler)) {
			while (parser.hasNext()) {
				JsonParser.Event event = parser.next();
				System.out.println(event);
			}
		}
		return problems;
	}

	/**
	 * JSON-B serialization of this Validatable
	 * 
	 * @return a JSON object representing the serialization of this Validatable.
	 */
	default JsonObject toJson() {
		return (JsonObject) toJsonFromString(jsonb.toJson(this));
	}

	/**
	 * Build a Validatable from provided JSON String.
	 * 
	 * @param clazz
	 *            the type of Validatable to build
	 * @param jsonStr
	 *            the JSON string to build from
	 * @return a new Validatable for the provided type.
	 *
	 * @throws IllegalAccessException
	 *             if no access to Validatable definition
	 * @throws InstantiationException
	 *             could not instantiate a new Validatable
	 * @throws JsonbException
	 *             if JSON-B deserialization fails
	 * @throws JsonValidatingException
	 *             if schema validation fails
	 */
	static <T extends Validatable> T toValidatable(Class<T> clazz, String jsonStr)
			throws IOException, InstantiationException, IllegalAccessException {

		T ret;

		AbstractMap.SimpleEntry<JsonValue, List<Problem>> result = validateWithDefaults(clazz, jsonStr);
		if (!result.getValue().isEmpty()) {
			throw new JsonValidatingException(result.getValue());
		}

		try (InputStream ins = new ByteArrayInputStream(result.getKey().toString().getBytes())) {
			ret = jsonb.fromJson(ins, clazz);
		}

		return ret;
	}

	/**
	 * JSON-SCHEMA generation.
	 * 
	 * @return a JSON-SCHEMA object for the Validatable.
	 */
	JsonSchema toSchema();

}
