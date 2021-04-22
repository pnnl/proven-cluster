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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

import org.apache.commons.lang3.StringUtils;
import org.leadpony.justify.api.InstanceType;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidatingException;
import org.leadpony.justify.api.Problem;

import gov.pnnl.proven.cluster.lib.disclosure.exception.ValidatableBuildException;

/**
 * Immutable class representing a measurement record. The measurement record is
 * a grouping of metrics or series of measurement data.
 * 
 * @author d3j766
 * 
 * @see MeasurementItem
 *
 */
public class MeasurementRecord implements Validatable {

	/**
	 * The supported metric value types. The {@link #Derive} type represents a
	 * value type that will be determined by the platform post-disclosure.
	 * Default type will be String if no other suitable type can be determined.
	 */
	public enum MetricValueType {

		//@formatter:off
		IntegerType("Integer", (s) -> { Integer.valueOf(s); return true; } ),
		LongType("Long", (s) -> { Long.valueOf(s); return true; } ),
		FloatType("Float", (s) -> { Float.valueOf(s); return true; } ),
		DoubleType("Double", (s) -> { Double.valueOf(s); return true; } ),
		BooleanType("Boolean", (s) -> { return ((s != null) && (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false"))); } ),
		StringType("String", (s) -> { return true; } ),
		Derive("Derive", null);
		//@formatter:on

		private String name;
		private Function<String, Boolean> checkType;

		MetricValueType(String name, Function<String, Boolean> checkType) {
			this.name = name;
			this.checkType = checkType;
		}

		public String getName() {
			return name;
		}

		private static String getNamesRegex() {

			String ret = "";
			for (MetricValueType v : MetricValueType.values()) {
				ret = ret + v.getName() + "|";
			}
			ret = StringUtils.stripEnd(ret, " |");

			return ret;
		}

		public static MetricValueType deriveValueType(String value) {

			MetricValueType ret = StringType;

			for (MetricValueType v : MetricValueType.values()) {

				try {

					if (v.checkType.apply(value)) {
						ret = v;
					}

				} catch (Exception e) {
					// function exception, move on to next type check.
				}

			}
			return ret;
		}
	}

	/**
	 * A metric is a single series value and is represented in the measurement
	 * record as a 4 element comma separated String. Following describes the
	 * order and content type for the elements that makeup the metric string.
	 * 
	 * @see #deriveValueType(String)
	 */
	static final String IS_METADATA_HEADER = "is_metadata";
	static final int IS_METADATA_IDX = 0;
	static final String LABEL_HEADER = "label";
	static final int LABEL_IDX = 1;
	static final String VALUE_HEADER = "value";
	static final int VALUE_IDX = 2;
	static final String VALUE_TYPE_HEADER = "value_type";
	static final int VALUE_TYPE_IDX = 3;
	static final int METRIC_LENGTH = 4;
	static final String METRIC_DELIMETER = ",";
	static final int MIN_METRICS = 1;

	static final String RECORD_PROP = "record";

	private List<String> record = new ArrayList<>();

	public MeasurementRecord() {
	}

	@JsonbCreator
	public static MeasurementRecord createMeasurementRecord(@JsonbProperty(RECORD_PROP) List<String> record) {
		return MeasurementRecord.newBuilder().withRecord(record).build(true);
	}

	private MeasurementRecord(Builder b) {
		this.record = b.record;
	}

	@JsonbProperty(RECORD_PROP)
	public List<String> getRecord() {
		return record;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {

		private List<String> record = new ArrayList<>();

		public Builder withMetric(String label, String value, Boolean isMetaData, MetricValueType valueType) {

			String[] metric = new String[METRIC_LENGTH];
			metric[IS_METADATA_IDX] = isMetaData.toString();
			metric[LABEL_IDX] = label;
			metric[VALUE_IDX] = value;
			metric[VALUE_TYPE_IDX] = valueType.getName();
			record.add(String.join(METRIC_DELIMETER, metric));
			return this;
		}

		public Builder withRecord(List<String> record) {
			this.record = record;
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
		public MeasurementRecord build() {
			return build(false);
		}

		private MeasurementRecord build(boolean trustedBuilder) {

			MeasurementRecord ret = new MeasurementRecord(this);

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
	public JsonSchema toSchema() {

		JsonSchema ret;

		//@formatter:off
		
		ret = sbf.createBuilder()

				.withId(Validatable.schemaId(this.getClass()))
				
				.withSchema(Validatable.schemaDialect())
				
				.withTitle("Measurement record schema")

				.withDescription("The measurement record is an array of metrics representing a series of "
						 + "measurement data.  A metric is a single series value and is represented in the "
				         + "measurement record as a comma delimited string that adheres to the 'header' "
						 + "property defined in the measurement item's schema")

				.withType(InstanceType.OBJECT)

				.withProperty(RECORD_PROP, sbf.createBuilder()
					.withType(InstanceType.ARRAY)
					.withItems(sbf.createBuilder()
							.withType(InstanceType.STRING)
							.withMinItems(MIN_METRICS)
							.withPattern("^((true|false)),([a-zA-Z0-9.\\-_Ee+]+,){2}(" + MetricValueType.getNamesRegex() + ")$")
							.build())
					
				.withRequired(RECORD_PROP)
				.build())
			
				.build();
			
		//@formatter:on

		return ret;

	}

}
