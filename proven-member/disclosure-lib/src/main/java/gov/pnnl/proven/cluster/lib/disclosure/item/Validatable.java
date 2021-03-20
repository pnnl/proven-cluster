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

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParsingException;

import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonSchemaBuilderFactory;
import org.leadpony.justify.api.JsonValidatingException;
import org.leadpony.justify.api.JsonValidationService;
import org.leadpony.justify.api.Problem;
import org.leadpony.justify.api.ProblemHandler;
import org.leadpony.justify.api.ValidationConfig;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;

import gov.pnnl.proven.cluster.lib.disclosure.DomainProvider;
import gov.pnnl.proven.cluster.lib.disclosure.exception.ValidatableBuildException;
import gov.pnnl.proven.cluster.lib.disclosure.item.adapter.DisclosureDomainAdapter;
import gov.pnnl.proven.cluster.lib.disclosure.item.adapter.MessageContentAdapter;
import gov.pnnl.proven.cluster.lib.disclosure.item.adapter.MessageItemTypeAdapter;

/**
 * Represents a JSON-SCHEMA validatable object.
 * 
 * @author d3j766
 *
 */
public interface Validatable {

	static final Logger log = LoggerFactory.getLogger(Validatable.class);

	static final String JSON_SCHEMA_SUFFIX = ".schema.json";
	static final String SCHEMA_RESOURCE_DIR = "/schema";
	static final String DRAFT_07_DIALECT = "http://json-schema.org/draft-07/schema#";
	static final String DRAFT_07_SCHEMA_RESOURCE = SCHEMA_RESOURCE_DIR + "/" + "draft-07.schema.json";
	static final String JSONLD_SCHEMA_RESOURCE = SCHEMA_RESOURCE_DIR + "/" + "jsonld.schema.json";

	static final ConcurrentHashMap<URI, JsonSchema> catalog = new ConcurrentHashMap<>();
	static final JsonValidationService service = JsonValidationService.newInstance();
	static final JsonSchemaBuilderFactory sbf = service.createSchemaBuilderFactory();
	static final JsonbConfig config = new JsonbConfig().withFormatting(true).withAdapters(new MessageItemTypeAdapter(),
			new DisclosureDomainAdapter(), new MessageContentAdapter());
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
	 * Convenience method to transfer collection of strings to JSON values.
	 */
	static Set<JsonValue> toJsonValues(Collection<String> source) {
		return source.stream().map(s -> Json.createValue(s)).collect(Collectors.toSet());
	}

	static String prettyPrint(String json) {

		StringWriter sw = new StringWriter();
		try (JsonReader jr = Json.createReader(new StringReader(json))) {
			JsonValue jobj = jr.readObject();
			Map<String, Object> properties = new HashMap<>(1);
			properties.put(JsonGenerator.PRETTY_PRINTING, true);
			JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
			try (JsonWriter jsonWriter = writerFactory.createWriter(sw)) {
				jsonWriter.write(jobj);
				jsonWriter.close();
			}
		}
		return sw.toString();
	}

	/**
	 * Returns current JSON-SCHEMA URI used to define Validatable schemas.
	 * 
	 * @return current schema dialect being used.
	 * 
	 * @throws RuntimeException
	 *             if the URI syntax was entered incorrectly.
	 */
	static URI schemaDialect() {
		URI dialect = null;
		try {
			dialect = new URI(DRAFT_07_DIALECT);
		} catch (URISyntaxException e) {
			throw new RuntimeException("JSON SCHEMA dialect URI is invalid", e);
		}

		return dialect;
	}

	/**
	 * Returns the JSON-SCHEMA resource name.
	 * 
	 * @return the name of the schema resource
	 */
	static String schemaResource(Class<? extends Validatable> v) {
		return v.getSimpleName() + ".schema.json";
	}

	/**
	 * Generates and returns the schema identifier for a Validatable.
	 * 
	 * @return the JSON-SCHEMA $id for this Validatable.
	 */
	static URI schemaId(Class<? extends Validatable> v) {
		URI id = null;
		try {
			id = new URI("http://" + DomainProvider.PROVEN_DOMAIN + "/" + "schemas" + "/" + schemaResource(v));
		} catch (URISyntaxException e) {
			throw new RuntimeException("JSON SCHEMA dialect URI is invalid", e);
		}

		return id;
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
	static <T extends Validatable> JsonSchema retrieveSchema(Class<T> clazz) {
		JsonSchema ret;
		URI id = schemaId(clazz);
		ret = catalog.get(id);
		if (null == ret) {
			try {
				ret = clazz.newInstance().toSchema();
				catalog.put(id, ret);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException("Could not instantiate a new Validatable for schema generation", e);
			}
		}
		return ret;
	}

	/**
	 * Scans Package containing Validatable types and returns list of
	 * implementations.
	 * 
	 * Note: This assumes following design decisions: (1) All implementations of
	 * Validatable and the Validatable interface itself are contained in the
	 * same package - gov.pnnl.proven.cluster.lib.disclosure.item (2)
	 * MessageItem extends Validatable and it has implementations representing
	 * the payload messages available to external clients
	 * 
	 * @param messageItemsOnly
	 *            if true, only return the MessageItem implementations
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
	static <T extends Validatable> List<Class<T>> getValidatables(boolean messageItemsOnly) {

		ArrayList<Class<T>> names = new ArrayList<>();
		@SuppressWarnings("rawtypes")
		Class rootClass = Validatable.class;
		if (messageItemsOnly)
			rootClass = MessageItem.class;
		Reflections reflections = new Reflections("gov.pnnl.proven.cluster.lib.disclosure.item", new SubTypesScanner());

		Set<Class<? extends Validatable>> classSet = reflections.getSubTypesOf(rootClass);
		for (Class<? extends Validatable> vClass : classSet) {

			if (Modifier.isAbstract(vClass.getModifiers()) || vClass.isInterface())
				continue;

			names.add((Class<T>) vClass);

			// if ((!vClass.equals(Validatable.class)) &&
			// (Validatable.class.isAssignableFrom(vClass))) {
			// if ((messageItemsOnly &&
			// (Validatable.class.isAssignableFrom(vClass))) ||
			// (!messageItemsOnly)) {
			// names.add((Class<T>) vClass);
			// }
			// }
		}
		return names;
	}

	static <T extends Validatable> List<Class<T>> getValidatables() {
		return getValidatables(false);
	}

	/**
	 * Validates provided JSON string using JSON-SCHEMA associated with the
	 * provided Validatable. Default values are filled in for missing
	 * properties.
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
		JsonSchema schema = retrieveSchema(clazz);
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
	static <T extends Validatable> List<Problem> validate(Class<T> clazz, String jsonStr) {
		JsonSchema schema = retrieveSchema(clazz);
		return validate(schema, jsonStr);
	}

	/**
	 * Validates JSON string using JSON-SCHEMA for the provided Validatable
	 * type.
	 * 
	 * @param schema
	 *            the schema to validate the provided JSON string with.
	 * @param jsonStr
	 *            the JSON string to validate
	 * @return a list of problems, if any, encountered during the validation
	 *         process. Empty list indicates the validation was successful.
	 * 
	 * @throws IllegalAccessException
	 *             if no access to Validatable definition
	 * @throws InstantiationException
	 *             could not instantiate a new Validatable
	 */
	static List<Problem> validate(JsonSchema schema, String jsonStr) {
		List<Problem> problems = new ArrayList<>();
		Reader reader = new StringReader(jsonStr);
		ProblemHandler handler = ProblemHandler.collectingTo(problems);

		try (JsonParser parser = service.createParser(reader, schema, handler)) {
			while (parser.hasNext()) {
				JsonParser.Event event = parser.next();
				log.info(event.toString());
			}
		}
		return problems;
	}

	/**
	 * Determines if the provided Validatable generates a valid JSON-Schema.
	 * 
	 * @param clazz
	 *            the Validatable
	 * @return true if schema is valid, false otherwise
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	static <T extends Validatable> boolean hasValidSchema(Class<T> clazz) {
		String jsonStr = retrieveSchema(clazz).toString();
		JsonSchema schema = retrieveSchema(JsonMetaSchema.class);
		log.debug("Validataing Schema: " + schemaResource(clazz));
		log.debug(schema.toString());
		return validate(schema, jsonStr).isEmpty();
	}

	/**
	 * Determines if the provided JSON-LD is syntacticly valid.
	 * 
	 * @param json
	 *            provided JSON object or array
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	static <T extends Validatable> boolean isValidJsonLD(JsonStructure json)
			throws InstantiationException, IllegalAccessException {
		String jsonStr = json.toString();
		JsonSchema schema = service.readSchema(Validatable.class.getResourceAsStream(JSONLD_SCHEMA_RESOURCE));
		return validate(schema, jsonStr).isEmpty();
	}

	/**
	 * Build a Validatable from provided JSON String.
	 * 
	 * @param clazz
	 *            the type of Validatable to build
	 * @param jsonStr
	 *            the JSON string to build from
	 * 
	 * @param skipValidation
	 *            if true do not perform validation on provided JSON.
	 * @return a new Validatable for the provided type.
	 *
	 * @throws ValidatableBuildException
	 *             if exception encountered during build process
	 */
	static <T extends Validatable> T toValidatable(Class<T> clazz, String jsonStr, boolean skipValidation) {

		T ret;
		String jsonToValidate = jsonStr;

		try {

			if (!skipValidation) {
				AbstractMap.SimpleEntry<JsonValue, List<Problem>> result = validateWithDefaults(clazz, jsonStr);
				if (!result.getValue().isEmpty()) {
					throw new JsonValidatingException(result.getValue());
				}
				jsonToValidate = result.getKey().toString();
			}

			try (InputStream ins = new ByteArrayInputStream(jsonToValidate.getBytes())) {
				ret = jsonb.fromJson(ins, clazz);
			}

		} catch (Exception e) {
			throw new ValidatableBuildException("JSON to Validatable failed", e);
		}

		return ret;
	}

	static <T extends Validatable> T toValidatable(Class<T> clazz, String jsonStr) {
		return toValidatable(clazz, jsonStr, false);
	}

	/**
	 * Write wrapper
	 */
	static <T, U extends ObjectDataOutput, E> BiConsumer<T, U> ww(
			ThrowingWriteNullable<T, ObjectDataOutput, IOException> twn) {
		return (v, o) -> {
			try {
				twn.accept(v, o);
			} catch (IOException ex) {
				throw new UncheckedIOException(ex);
			}
		};
	}

	/**
	 * Convenience method that checks for null value during serialization.
	 * 
	 * @param val
	 *            provided value
	 * @param out
	 *            ObjectDataOutput to write to
	 * @param writeFunc
	 *            function to perform write
	 * @throws IOException
	 */
	static <T, U extends ObjectDataOutput> void writeNullable(T val, U out, BiConsumer<T, U> writeFunc)
			throws IOException {
		boolean hasVal = (null != val);
		out.writeBoolean(hasVal);
		if (hasVal) {
			writeFunc.accept(val, out);
		}
	}

	/**
	 * Read wrapper
	 */
	static <T extends ObjectDataInput, R, E> Function<T, R> rw(
			ThrowingReadNullable<ObjectDataInput, R, IOException> trn) {
		return (i) -> {
			try {
				return trn.apply(i);
			} catch (IOException ex) {
				throw new UncheckedIOException(ex);
			}
		};
	}

	/**
	 * Convenience method that checks for null value during deserialization.
	 * 
	 * @param val
	 *            provided value
	 * @param out
	 *            ObjectDataInput to read from
	 * @param writeFunc
	 *            function to perform read
	 * @throws IOException
	 */
	static <T extends ObjectDataInput, R> R readNullable(T in, Function<T, R> readFunc) throws IOException {
		R ret = null;
		if (in.readBoolean()) {
			ret = readFunc.apply(in);
		}
		return ret;
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
	 * Determines if the instance is valid.
	 * 
	 * @return a list of problems, if any, encountered during the validation
	 *         process. Empty list indicates the validation was successful.
	 */
	default List<Problem> validate() {
		return validate(this.getClass(), this.toJson().toString());
	}

	/**
	 * JSON-SCHEMA generation.
	 * 
	 * @return a JSON-SCHEMA object for the Validatable.
	 */
	JsonSchema toSchema();

	/**
	 * Creates schemas for DisclosureItem and all MessageItem implementations.
	 * Schemas are stored in provided path provided in args.
	 * 
	 * @param args
	 *            0 - target directory path to store generated schemas.
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	static void main(String[] args) throws IOException {
		String schemaDir = args[0];

		// Disclosure Item and MessageItems
		for (Class<Validatable> v : getValidatables()) {
			if ((v.equals(DisclosureItem.class)) || (MessageItem.class.isAssignableFrom(v))) {
				String schemaPath = schemaDir + "/" + schemaResource(v);
				String schema = prettyPrint(retrieveSchema(v).toString());
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(schemaPath))) {
					writer.append(schema);
				}
			}
		}
	}
}

/**
 * Wrapper for meta-schema
 */
class JsonMetaSchema implements Validatable {

	@Override
	public JsonSchema toSchema() {
		return service.readSchema(Validatable.class.getResourceAsStream(DRAFT_07_SCHEMA_RESOURCE));
	}
}

/**
 * Wrapper for JSON-LD schema
 */
class JsonLdSchema implements Validatable {

	@Override
	public JsonSchema toSchema() {
		return service.readSchema(Validatable.class.getResourceAsStream(JSONLD_SCHEMA_RESOURCE));
	}
}
