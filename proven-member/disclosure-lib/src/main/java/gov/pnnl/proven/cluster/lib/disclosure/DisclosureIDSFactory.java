package gov.pnnl.proven.cluster.lib.disclosure;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.DisclosureMessage;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.DisclosureResponse;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.KnowledgeMessage;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.MessageProperties;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.ProvenMeasurement;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.ProvenMessageOriginal;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.ProvenQueryFilter;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.ProvenQueryTimeSeries;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.ProvenStatement;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.RequestMessage;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.ResponseMessage;
import gov.pnnl.proven.cluster.lib.disclosure.item.DisclosureItem;
import gov.pnnl.proven.cluster.lib.disclosure.item.ExplicitItem;
import gov.pnnl.proven.cluster.lib.disclosure.item.MessageContext;
import gov.pnnl.proven.cluster.lib.member.IDSFactory;

/**
 * 
 * Hazelcast DataSerializableFactory factory for disclosure-lib.
 * 
 * @author d3j766
 *
 */
public class DisclosureIDSFactory implements DataSerializableFactory {

	public DisclosureIDSFactory() {
	}

	// Factory
	public static final int FACTORY_ID = IDSFactory.DISCLOSURE.getFactoryId();

	// Serializable types
	public static final int DISCLOSURE_DOMAIN_TYPE = 1;
	public static final int DISCLOSURE_ITEM_TYPE = 2;
	public static final int MESSAGE_CONTEXT_TYPE = 3;
	
	@Override
	public IdentifiedDataSerializable create(int typeId) {

		switch (typeId) {
		
		case (DISCLOSURE_DOMAIN_TYPE):
			return new DisclosureDomain();
		case (DISCLOSURE_ITEM_TYPE):
			return new DisclosureItem();
		case (MESSAGE_CONTEXT_TYPE):
			return new MessageContext();
		default:
			return null;
		}
	}

	
	public static JsonValue jsonValueIn(byte[] content) throws IOException {

		JsonValue ret = null;

		if (content.length > 0) {
			try (ByteArrayInputStream bais = new ByteArrayInputStream(content);
					JsonReader reader = Json.createReader(bais)) {
				ret = reader.readValue();
			}
		}
		return ret;
	}

	public static byte[] jsonValueOut(JsonValue value) throws IOException {

		byte[] ret = new byte[0];

		if (null != value) {
			try (ByteArrayOutputStream oos = new ByteArrayOutputStream(); JsonWriter writer = Json.createWriter(oos)) {
				writer.write(value);
				writer.close();
				oos.flush();
				ret = oos.toByteArray();
			}
		}
		return ret;
	}
	
	
}
