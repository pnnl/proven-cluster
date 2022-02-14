package gov.pnnl.proven.hybrid.util.store;

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
import static gov.pnnl.proven.hybrid.util.ProvenConfig.ProvenEnvProp.PROVEN_IDB_DB;
import static gov.pnnl.proven.hybrid.util.ProvenConfig.ProvenEnvProp.PROVEN_IDB_PASSWORD;
import static gov.pnnl.proven.hybrid.util.ProvenConfig.ProvenEnvProp.PROVEN_IDB_RP;
import static gov.pnnl.proven.hybrid.util.ProvenConfig.ProvenEnvProp.PROVEN_IDB_URL;
import static gov.pnnl.proven.hybrid.util.ProvenConfig.ProvenEnvProp.PROVEN_IDB_USERNAME;
import static gov.pnnl.proven.hybrid.util.ProvenConfig.ProvenEnvProp.PROVEN_USE_IDB;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

//import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.map.MapStore;

import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.ProvenMeasurement;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.ProvenMessageOriginal;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.ProvenMetric;
import gov.pnnl.proven.cluster.lib.disclosure.deprecated.message.ProvenMetric.MetricFragmentIdentifier.MetricValueType;
import gov.pnnl.proven.hybrid.util.ProvenConfig;

/**
 * Session Bean implementation class T3MapStore
 * 
 * Provides management and persistent services for proven concepts. By default,
 * the proven context and base uri are used.
 * 
 */
public class HybridMapStore implements MapStore<String, ProvenMessageOriginal> {

	private final Logger log = LoggerFactory.getLogger(HybridMapStore.class);

	private ProvenConfig pg;

	private boolean useIdb;
	private String idbDB;
	private String idbRP;
	private String idbUrl;
	private String idbUsername;
	private String idbPassword;
	private InfluxDB influxDB;

	HybridMapStore() {

		pg = ProvenConfig.getB2SConfig();
		useIdb = Boolean.valueOf(pg.getPropValue(PROVEN_USE_IDB));
		this.idbDB = pg.getPropValue(PROVEN_IDB_DB);
		this.idbRP = pg.getPropValue(PROVEN_IDB_RP);
		idbUrl = pg.getPropValue(PROVEN_IDB_URL);
		idbUsername = pg.getPropValue(PROVEN_IDB_USERNAME);
		idbPassword = pg.getPropValue(PROVEN_IDB_PASSWORD);
		influxDB = InfluxDBFactory.connect(idbUrl, idbUsername, idbPassword);
		// influxDB.enableBatch(BatchOptions.DEFAULT_BATCH_ACTIONS_LIMIT,
		// BatchOptions.DEFAULT_BATCH_INTERVAL_DURATION, TimeUnit.SECONDS);
		influxDB.enableBatch(20000, 10, TimeUnit.SECONDS);
	}

	@Override
	public void finalize() {
		influxDB.close();
	}

	public ProvenMessageOriginal load(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map loadAll(Collection keys) {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterable loadAllKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	public void store(String key, ProvenMessageOriginal value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeAll(Map<String, ProvenMessageOriginal> map) { // 1
		// TODO Auto-generated method stub
		if (useIdb) { // 2
			for (Map.Entry<String, ProvenMessageOriginal> entry : map.entrySet()) { // 3
				// OLD Long startTime = System.currentTimeMillis();
				// influxDB = InfluxDBFactory.connect(idbUrl, idbUsername,
				// idbPassword);
				// //
				// influxDB.enableBatch(BatchOptions.DEFAULT_BATCH_ACTIONS_LIMIT,
				// // BatchOptions.DEFAULT_BATCH_INTERVAL_DURATION,
				// TimeUnit.SECONDS);
				// influxDB.enableBatch(20000, 10, TimeUnit.SECONDS);
				ProvenMessageOriginal message = (ProvenMessageOriginal) entry.getValue();
				Collection<ProvenMeasurement> measurements = message.getMeasurements();
				for (ProvenMeasurement measurement : measurements) { // 4

					Set<ProvenMetric> pms = measurement.getMetrics();

					Point.Builder builder = Point.measurement(measurement.getMeasurementName())
							.time(measurement.getTimestamp(), TimeUnit.MILLISECONDS);

					for (ProvenMetric pm : pms) { // 5

						if (pm.isMetadata()) { // 6

							builder.tag(pm.getLabel(), pm.getValue());

							System.out.println("Mapstore TAG");
							System.out.println("------------------------------");
							System.out.println(pm.getLabel());
							System.out.println(pm.getValue());
							System.out.println("------------------------------");

						} else { // 6

							try { // 7
								if (pm.getValueType().equals(MetricValueType.Integer)) { // 8
									builder.addField(pm.getLabel(), Integer.valueOf(pm.getValue()));

								} else if (pm.getValueType().equals(MetricValueType.Long)) {
									builder.addField(pm.getLabel(), Long.valueOf(pm.getValue()));

								} else if (pm.getValueType().equals(MetricValueType.Float)) {
									builder.addField(pm.getLabel(), Float.valueOf(pm.getValue()));

								} else if (pm.getValueType().equals(MetricValueType.Double)) {
									builder.addField(pm.getLabel(), Double.valueOf(pm.getValue()));

								} else if (pm.getValueType().equals(MetricValueType.Boolean)) {
									builder.addField(pm.getLabel(), Boolean.valueOf(pm.getValue()));
								} else {
									builder.addField(pm.getLabel(), pm.getValue());
								} // 8
							} catch (NumberFormatException e) {
								builder.addField(pm.getLabel(), pm.getValue());
							} // 7

						} // 6

					} // 5 else not metadata
					influxDB.write(idbDB, idbRP, builder.build());

				} // 4 for
					// influxDB.close();
			} // 3 for

		} // 2 if

	} // 1 Method storeAll

	@Override
	public void delete(String key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll(Collection keys) {
		// TODO Auto-generated method stub

	}
}