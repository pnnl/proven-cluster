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

import static gov.pnnl.proven.cluster.lib.disclosure.item.MeasurementRecord.IS_METADATA_HEADER;
import static gov.pnnl.proven.cluster.lib.disclosure.item.MeasurementRecord.IS_METADATA_IDX;
import static gov.pnnl.proven.cluster.lib.disclosure.item.MeasurementRecord.LABEL_HEADER;
import static gov.pnnl.proven.cluster.lib.disclosure.item.MeasurementRecord.LABEL_IDX;
import static gov.pnnl.proven.cluster.lib.disclosure.item.MeasurementRecord.METRIC_LENGTH;
import static gov.pnnl.proven.cluster.lib.disclosure.item.MeasurementRecord.VALUE_HEADER;
import static gov.pnnl.proven.cluster.lib.disclosure.item.MeasurementRecord.VALUE_IDX;
import static gov.pnnl.proven.cluster.lib.disclosure.item.MeasurementRecord.VALUE_TYPE_HEADER;
import static gov.pnnl.proven.cluster.lib.disclosure.item.MeasurementRecord.VALUE_TYPE_IDX;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

import org.leadpony.justify.api.InstanceType;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidatingException;
import org.leadpony.justify.api.Problem;

import gov.pnnl.proven.cluster.lib.disclosure.MessageContent;
import gov.pnnl.proven.cluster.lib.disclosure.exception.ValidatableBuildException;

/**
 * Immutable class representing time-series measurement data. A measurement item
 * is composed of a measurement name, a timestamp, and one or more record values
 * each of which contains time-series metrics.
 * 
 * @author d3j766
 * 
 * @see MeasurementRecord
 *
 */
public class MeasurementItem implements MessageItem {

	static final int MIN_VALUES = 1;

	// Jsonb property names
	static final String MEASUREMENT_PROP = "measurement";
	static final String TIMESTAMP_PROP = "timestamp";
	static final String HEADER_PROP = "header";
	static final String VALUES_PROP = "values";

	private String measurement;
	private Long timestamp;
	private String[] header;
	private List<MeasurementRecord> values = new ArrayList<>();

	// HZ Serialization
	public MeasurementItem() {
	}

	@JsonbCreator
	public static MeasurementItem createMeasurementItem(@JsonbProperty(MEASUREMENT_PROP) String measurement,
			@JsonbProperty(TIMESTAMP_PROP) Long timestamp, @JsonbProperty(HEADER_PROP) String[] header,
			@JsonbProperty(VALUES_PROP) List<MeasurementRecord> values) {
		return MeasurementItem.newBuilder().withMeasurement(measurement).withTimestamp(timestamp).withValues(values)
				.build(true);
	}

	private MeasurementItem(Builder b) {
		header = getHeader();
		this.measurement = b.measurement;
		this.timestamp = b.timestamp;
		this.values = b.values;
	}

	@JsonbProperty(MEASUREMENT_PROP)
	public String getMeasurement() {
		return measurement;
	}

	@JsonbProperty(TIMESTAMP_PROP)
	public Long getTimestamp() {
		return timestamp;
	}

	@JsonbProperty(HEADER_PROP)
	public String[] getHeader() {

		String[] ret = this.header;
		if (null == ret) {
			ret = new String[METRIC_LENGTH];
			ret[IS_METADATA_IDX] = IS_METADATA_HEADER;
			ret[LABEL_IDX] = LABEL_HEADER;
			ret[VALUE_IDX] = VALUE_HEADER;
			ret[VALUE_TYPE_IDX] = VALUE_TYPE_HEADER;
			this.header = ret;
		}
		return this.header;
	}

	@JsonbProperty(VALUES_PROP)
	public List<MeasurementRecord> getValues() {
		return values;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {

		private String measurement;
		private Long timestamp;
		private List<MeasurementRecord> values = new ArrayList<>();

		private Builder() {
		}

		public Builder withMeasurement(String measurement) {
			this.measurement = measurement;
			return this;
		}

		public Builder withTimestamp(Long timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public Builder withValue(MeasurementRecord value) {
			this.values.add(value);
			return this;
		}

		public Builder withValues(List<MeasurementRecord> values) {
			this.values = values;
			return this;
		}

		/**
		 * Builds new instance. Instance is validated post construction.
		 * 
		 * @return new instance
		 * 
		 * @throws JsonValidatingException
		 *             if created instance fails JSON-SCHEMA validation.
		 * 
		 */
		public MeasurementItem build() {
			return build(false);
		}

		private MeasurementItem build(boolean trustedBuilder) {

			MeasurementItem ret = new MeasurementItem(this);

			if (!trustedBuilder) {
				List<Problem> problems = ret.validate();
				if (!problems.isEmpty()) {
					throw new ValidatableBuildException("Builder failure", new JsonValidatingException(problems));
				}
			}

			return ret;
		}

	}

	@Override
	public MessageContent messageContent() {
		return MessageContent.Measurement;
	}

	@Override
	public String messageName() {
		return "Measurement message";
	}

	private JsonArray getHeaderArray() {
		JsonArrayBuilder ab = Json.createArrayBuilder();
		String[] header = getHeader();
		for (int i = 0; i < METRIC_LENGTH; i++) {
			ab.add(header[i]);
		}
		return ab.build();
	}

	@Override
	public JsonSchema toSchema() {

		JsonSchema ret;

		//@formatter:off
		ret = sbf.createBuilder()

			.withId(Validatable.schemaId(this.getClass()))
			
			.withSchema(Validatable.schemaDialect())
			
			.withTitle("Measurement item message schema")

			.withDescription("Proven's time-series measurement data representation. A measurement item is "
						+ "composed of a measurement name, a timestamp, and one or more record values, each of which "
						+ "contain the metric values. A pre-defined 'header' value is described as a const in this schema. "
						+ "The 'header' describes content and order of fields for a metric's comma delimited string. "
						+ "A metric must adhere to this header's definition.")

			.withType(InstanceType.OBJECT)

			.withProperty(MEASUREMENT_PROP,
				sbf.createBuilder()
				.withType(InstanceType.STRING)
				.withPattern("^[a-zA-Z]([A-Za-z0-9-_]){1,64}$")
				.build())
			
			.withProperty(TIMESTAMP_PROP, sbf.createBuilder()
				.withDescription("Timestamp for measurement data. "
						+ "This is a timestamp formatted as epoch time in milliseconds.")
				.withType(InstanceType.INTEGER)
				.withMinimum(0)
				.withMaximum(Long.MAX_VALUE)
				.build())					

			.withProperty(HEADER_PROP, sbf.createBuilder()
				.withDescription("Const value for the header.")
				.withType(InstanceType.ARRAY)
				.withConst(getHeaderArray())
				.build())
			
			.withProperty(VALUES_PROP, sbf.createBuilder()
				.withType(InstanceType.ARRAY)
				.withItems(Validatable.retrieveSchema(MeasurementRecord.class))
				.withMinItems(MIN_VALUES)
				.build())
									
			.withRequired(MEASUREMENT_PROP, TIMESTAMP_PROP, HEADER_PROP, VALUES_PROP)
			
			.build();
		
		//@formatter:on

		return ret;
	}

}
