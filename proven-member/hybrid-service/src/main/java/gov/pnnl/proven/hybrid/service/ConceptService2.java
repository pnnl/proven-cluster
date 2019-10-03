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

package gov.pnnl.proven.hybrid.service;
import static gov.pnnl.proven.hybrid.util.ProvenConfig.ProvenEnvProp.*;


import gov.pnnl.proven.message.*;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.lang.Long;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.core.Response.Status;



//import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import gov.pnnl.proven.hybrid.util.ProvenConfig;
import gov.pnnl.proven.hybrid.util.Mpoint;
import gov.pnnl.proven.hybrid.util.Measurement;
import gov.pnnl.proven.hybrid.util.Results;
import gov.pnnl.proven.message.ProvenMessage;
import gov.pnnl.proven.message.ProvenMessageResponse;
import gov.pnnl.proven.message.ProvenMetric.MetricFragmentIdentifier.MetricValueType;
import gov.pnnl.proven.message.ProvenQueryFilter;
import gov.pnnl.proven.message.ProvenQueryTimeSeries;
import gov.pnnl.proven.message.exception.InvalidProvenMessageException;

import com.opencsv.CSVWriter;
import java.io.Writer;

/**
 * Session Bean implementation class ConcepService
 * 
 * Provides management and persistent services for proven concepts. By default,
 * the proven context and base uri are used.
 * 
 */
@Stateless
@LocalBean
public class ConceptService2 {

	private final Logger log = LoggerFactory.getLogger(ConceptService2.class);

	private ProvenConfig pg;

//	@EJB
//	private StoreManager sm;


	private boolean useIdb;
	private String idbDB;
	private String idbRP;
	private String idbUrl;
	private String idbUsername;
	private String idbPassword;



	@PostConstruct
	public void postConstruct() {


		pg = ProvenConfig.getB2SConfig();
		useIdb = Boolean.valueOf(pg.getPropValue(PROVEN_USE_IDB));
		idbDB = pg.getPropValue(PROVEN_IDB_DB);
		idbRP = pg.getPropValue(PROVEN_IDB_RP);
		idbUrl = pg.getPropValue(PROVEN_IDB_URL);
		idbUsername = pg.getPropValue(PROVEN_IDB_USERNAME);
		idbPassword = pg.getPropValue(PROVEN_IDB_PASSWORD);

		log.debug("USE IDB  : " + useIdb);
		log.debug("IDB DB : " + idbDB);
		log.debug("IDB RP : " + idbRP);
		log.debug("IDB URL : " + idbUrl);
		log.debug("IDB Username : " + idbUsername);
		log.debug("ConceptService constructed ...");

	}

	@PreDestroy
	public void preDestroy() {

	}





	public String convertResults2Json(QueryResult qr) {

		//
		// Declare Json Objects to be built
		//
		JSONArray  measurementArray = new JSONArray();
		String jsonresults = "";
		Mpoint point = new Mpoint();
		Measurement measurement = new Measurement();
		Results results = new Results();

		List<Result> resultList = qr.getResults();
		int resultIndex = 0;

		//
		// Process Each Result
		//
		for (Result result : resultList) {

			List<Series> seriesList = ((null == result.getSeries()) ? (new ArrayList<Series>()) : result.getSeries());

			int seriesIndex = 0;

			//
			// Process Each Series
			//
			for (Series series : seriesList) {
				String seriesName = series.getName();
				List<String> colNames = series.getColumns();
				List<List<Object>> table = series.getValues();

				int rowIndex = 0;
				//				rowResults = new HashMap<String, String>();
				JSONObject rowObject = new JSONObject();

				//
				// Process Rows for a given Series
				//
				for (List<Object> row : table) {

					int cellIndex = 0;
					//
					// Process Cells for a given Row
					//
					point = new Mpoint();
					for (Object cell : row) {
						if (cell != null) {
							if (colNames.get(cellIndex).contains("time") && colNames.get(cellIndex).length() == 4) {
								//
								// Influx returns epoch time in a double format.
								// Needs to be converted to Long
								//
								Double val = new Double(cell.toString());
								Long lval = val.longValue();
								//								point.putRow(colNames.get(cellIndex), lval.toString());
								//							    rowResults.put(colNames.get(cellIndex), lval.toString());
								rowObject.put(colNames.get(cellIndex), lval);
							} else if (cell instanceof Integer) {
								rowObject.put(colNames.get(cellIndex), (Integer)cell);						    
							} else if (cell instanceof Double) {
								rowObject.put(colNames.get(cellIndex), (Double)cell);						    
							}else {
								String buff = cell.toString();
								//
								// If a cell is a quoted string, remove the
								// surrounding double quotes.
								//
								buff = buff.replaceAll("\"", "");
								//								point.putRow(colNames.get(cellIndex), buff);
								//							    rowResults.put(colNames.get(cellIndex), buff);
								rowObject.put(colNames.get(cellIndex), buff);
							}

							//
							// Add each cell Json Object to a row Object.
							//
						}
						cellIndex++;

					} // end adding to row
					measurement.addPoint(point);
					//					tableResults.put(rowIndex, rowResults);
					measurementArray.add(rowObject);
					rowObject = new JSONObject();
					rowIndex++;

					//
					// Add each row O
					//
				} // end adding to table
				measurement.setName(seriesName);
				seriesIndex++;
			} // end adding series

			resultIndex++;
		}
		; // end adding results
		jsonresults = measurementArray.toJSONString();
		return jsonresults;

	}

	public String convertResults2Csv(QueryResult qr) {

		//
		// Declare Json Objects to be built
		//
		String csvMeasurements = "";
		String csvRow = "";
		String comma = ",";
		String newline = "\n";
		Mpoint point = new Mpoint();
		Measurement measurement = new Measurement();
		Results results = new Results();
		File tempFile = null;
		Writer writer = null;
		CSVWriter csvWriter = null;

		String[] headerRecord = null;
		try {
			tempFile = File.createTempFile("proven", ".influx2csv");
			tempFile.deleteOnExit();
			writer = Files.newBufferedWriter(Paths.get(tempFile.getPath()));
			csvWriter = new CSVWriter(writer,
					CSVWriter.DEFAULT_SEPARATOR,
					CSVWriter.NO_QUOTE_CHARACTER,
					CSVWriter.DEFAULT_ESCAPE_CHARACTER,
					CSVWriter.DEFAULT_LINE_END);


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		List<Result> resultList = qr.getResults();
		qr.toString();

		int resultIndex = 0;

		//
		// Process Each Result
		//
		boolean topHeader = false;
		for (Result result : resultList) {

			List<Series> seriesList = ((null == result.getSeries()) ? (new ArrayList<Series>()) : result.getSeries());

			int seriesIndex = 0;

			//
			// Process Each Series
			//
			for (Series series : seriesList) {
				String seriesName = series.getName();
				List<String> colNames = series.getColumns();
				List<List<Object>> table = series.getValues();


				if (topHeader == false) {
					headerRecord = new String[colNames.size()];
					int colIndex = 0;
					for (String column : colNames) {
						//                	csvRow = csvRow.concat(column);
						//                 	csvRow = csvRow.concat(",");
						headerRecord[colIndex] = column;
						colIndex++;
					}
					//                if (csvRow.endsWith(comma)) {
					//                	csvRow = csvRow.substring(0, csvRow.length()-1);
					//                	csvRow = csvRow.concat(newline);
					//                }
					topHeader = true;
					csvWriter.writeNext(headerRecord);
				}

				int rowIndex = 0;
				//				rowResults = new HashMap<String, String>();
				//				JSONObject rowObject = new JSONObject();

				//
				// Process Rows for a given Series
				//
				for (List<Object> row : table) {

					int cellIndex = 0;
					String[] rowCsv = new String[colNames.size()];
					//
					// Process Cells for a given Row
					//
					point = new Mpoint();
					for (Object cell : row) {

						if (cell != null) {
							if (colNames.get(cellIndex).contains("time") && colNames.get(cellIndex).length() == 4) {
								//
								// Influx returns epoch time in a double format.
								// Needs to be converted to Long
								//
								Double val = new Double(cell.toString());
								Long lval = val.longValue();
								//							    csvRow = csvRow.concat(lval.toString()).concat(comma);
								rowCsv[cellIndex] = lval.toString();
							} else if (cell instanceof Integer) {
								//								csvRow = csvRow.concat(cell.toString()).concat(comma);
								rowCsv[cellIndex] = cell.toString();
							} else if (cell instanceof Double) {	
								//								csvRow = csvRow.concat(cell.toString()).concat(comma);
								rowCsv[cellIndex] = cell.toString();
							}else {
								String buff = cell.toString();

								//
								// If a cell is a quoted string, remove the
								// surrounding double quotes.
								//
								buff = buff.replaceAll("\"", "");
								//							    csvRow = csvRow.concat(buff).concat(comma);
								rowCsv[cellIndex] = buff;
							}

							//
							// Add each cell Json Object to a row Object.
							//
						} else {

							//							csvRow = csvRow.concat(comma);

						}
						cellIndex++;

					} // end adding to row
					csvWriter.writeNext(rowCsv);
					//	                if (csvRow.endsWith(comma)) {
					//	                	csvRow = csvRow.substring(0, csvRow.length()-1);
					//	                	csvRow = csvRow.concat(newline);
					//	                }
					//	                csvMeasurements = csvMeasurements.concat(csvRow);
					//                    csvRow = "";
					rowIndex++;
					//                   System.out.println(rowIndex + " " + csvMeasurements.length());
					//
					// Add each row O
					//
				} // end adding to table
				seriesIndex++;
			} // end adding series

			resultIndex++;
		}




		try {
			writer.close();
			FileInputStream fis = new FileInputStream(tempFile);
			byte[] data = new byte[(int) tempFile.length()];
			fis.read(data);
			fis.close();

			csvMeasurements = new String(data, "UTF-8");


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return csvMeasurements;

	}


	//
	// Utility used to pad an epoch number string with zeros
	//
	public String padTimeWithZeros(String s, int n) {
		String.format("%1$-" + n + "s", s);
		s = s.replace(' ', '0');
		return s;
	}

	private String buildTimeQueryStatement(String key, String val) {
		String statement = "";
		// SimpleDateFormat sdf = new
		// SimpleDateFormat(MessageUtils.DATE_FORMAT_1);
		try {
			// TODO remove hard coded nanosecond - should provide a converter
			// source format -> ts format
			// statement = val + "000000";
			//
			// Replaced hardcoded buffer with right pad
			// Assumption will be to use nanosecond precision
			// Nanoseconds - 16 digits
			//
			// Microseconds - 10 digits
			//
			// Seconds - 7 digits

			// 16 digit nanosecond padding
		statement = padTimeWithZeros(val, 7);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (key.toLowerCase().contains("start")) {
			statement = "time >= " + statement + " ";
		} else if (key.toLowerCase().contains("end")) {
			statement = "time <= " + statement + " ";
		}
		return statement;

	}


	public ProvenMessageResponse queryMeasurements(ProvenMessage query) throws InvalidProvenMessageException {

		ProvenMessageResponse pmr = queryMeasurements(query, false);
		return pmr;

	}



	public ProvenMessageResponse queryMeasurements(ProvenMessage query, boolean returnCsvFlag) throws InvalidProvenMessageException {
		ProvenMessageResponse ret = null;
		String space = " ";
		String eq = "=";
		String quote = "'";
		String doublequote = "\"";

		String queryStatement = "select * from ";

		ProvenQueryTimeSeries tsquery = query.getTsQuery();
		if (tsquery == null) {
			throw new InvalidProvenMessageException();
		}
		queryStatement = queryStatement + space + doublequote + tsquery.getMeasurementName() + doublequote;
		queryStatement = queryStatement + space;

		List<ProvenQueryFilter> filters = tsquery.getFilters();
		String filter_statement = "";
		if (filters != null) {
			queryStatement = queryStatement + space + "where" + space;
			boolean flag = true;
			int index = 0;
			while (index < filters.size()) {
				ProvenQueryFilter filter = filters.get(index);

				//
				// Set up filter
				//
				if (filter.getDatatype() == null) {

					filter_statement = filter.getField() + space + eq + quote + filter.getValue() + quote;
				} else {
					if (filter.getDatatype().equals(MetricValueType.Integer.toString())) {
						filter_statement = filter.getField() + space + eq + Integer.parseInt(filter.getValue());
					} else if (filter.getDatatype().equals(MetricValueType.Long.toString())) {
						filter_statement = filter.getField() + space + eq + Long.parseLong(filter.getValue());
					} else if (filter.getDatatype().equals(MetricValueType.Float.toString())) {
						filter_statement = filter.getField() + space + eq + Float.parseFloat(filter.getValue());
					} else if (filter.getDatatype().equals(MetricValueType.Double.toString())) {
						filter_statement = filter.getField() + space + eq + Double.parseDouble(filter.getValue());
					} else
						filter_statement = filter.getField() + space + eq + quote + filter.getValue() + quote;
				}


				//
				// If time oriented, override filter with StartTime and EndTime
				// are special fields used to filter the InfluxDB time.
				//
				if (filter.getField().toLowerCase().contains("starttime")
						|| filter.getField().toLowerCase().contains("endtime")) {
					filter_statement = buildTimeQueryStatement(filter.getField(), filter.getValue());
				}

				if (flag) {
					queryStatement = queryStatement + space + filter_statement + space;
					flag = false;
				} else {

					// If not a time filter, treat it as a field.
					//
					queryStatement = queryStatement + space + "and" + space + filter_statement + space;

				}
				index = index + 1;
			}
		}

		String response = "";
		String reason = "";

		if (useIdb) {
			InfluxDB influxDB = InfluxDBFactory.connect(idbUrl, idbUsername, idbPassword);
			String dbName = idbDB;
			influxDB.setRetentionPolicy(idbRP);

			// // Query query = new Query("select time_idle from cpu limit 10",
			// dbName);
			//
			//*******DEBUG statement to shorten query results.  Remove before checking in.
			//queryStatement = queryStatement + " limit 10";
			//*******DEBUG statement
			//
			

			Query influxQuery = new Query(queryStatement, dbName);
			//
			// Unless TimeUnit parameter is used in query, Epoch time will not
			// be returned.
			//
			QueryResult qr = influxDB.query(influxQuery, TimeUnit.SECONDS);
			if (qr.hasError()) {
				ret = new ProvenMessageResponse();
				ret.setReason("Invalid or missing content.");
				ret.setStatus(Status.BAD_REQUEST);
				ret.setCode(Status.BAD_REQUEST.getStatusCode());
				ret.setResponse("{ \"ERROR\": \"Bad request made to time-series database.\" }");

			} else {
				ret = new ProvenMessageResponse();
				ret.setReason("Success");
				ret.setStatus(Status.OK);
				ret.setCode(Status.OK.getStatusCode());
				if (returnCsvFlag) {
					ret.setResponse(convertResults2Csv(qr));
				} else {
					ret.setResponse(convertResults2Json(qr));					
				}


			}
		} else {

			ret = new ProvenMessageResponse();
			ret.setReason("Time-series database unavailable or database adapter is disabled.");
			ret.setStatus(Status.SERVICE_UNAVAILABLE);
			ret.setCode(Status.SERVICE_UNAVAILABLE.getStatusCode());
			ret.setResponse("{ \"INFO\": \"idb server disabled in Proven configuration\" }");
		}

		return ret;

	}



	@SuppressWarnings("unchecked")
	public char detectObjectType(JSONObject message) {
		char type = 0;
		JSONObject object = (JSONObject) message.get("message");
		if (object != null) {
			type = 'O';
		} else {
			object = (JSONObject) message.get("input");
			if (object != null) {
				type = 'I';
			}
		}

		return type;

	}

	//
	//  Warning we are using a recursive method to process the JSON object.
	//

	private Point.Builder processNestedObject (JSONObject iObject, String key, Point.Builder builder  ) {
		Point.Builder ret = builder;
		Set<String> iokeys = iObject.keySet();
		Iterator<String> ioit = iokeys.iterator();


		while (ioit.hasNext()) 	{
			String iokey = ioit.next();

			if (iObject.get(iokey) instanceof String) {
				ret.tag(iokey, (String) iObject.get(iokey));
			} else if (iObject.get(iokey) instanceof Integer) {
				ret.addField(iokey, Integer.valueOf((Integer) iObject.get(iokey)));
			} else if (iObject.get(iokey) instanceof Long) {
				ret.addField(iokey, Long.valueOf((Long) iObject.get(iokey)));
			} else if (iObject.get(iokey) instanceof Float) {
				ret.addField(iokey, Float.valueOf((Float) iObject.get(iokey)));
			} else if (iObject.get(iokey) instanceof Double) {
				ret.addField(iokey, Double.valueOf((Double) iObject.get(iokey)));
			} else 	if  (iObject.get(iokey) instanceof JSONObject) {
				JSONObject i2Object = (JSONObject) iObject.get(iokey);
				ret = processNestedObject (i2Object, iokey, ret);
			}
		}

		return ret;

	}


	private ProvenMessageResponse writeSimulationInput(JSONObject commandObject, InfluxDB influxDB,
			String measurementName, String instanceId) {
		ProvenMessageResponse ret = null;
		Long timestamp = (long) -1;
		String differenceMrid = null;
		JSONArray forwardDifferenceArray = null;
		JSONArray reverseDifferenceArray = null;
		JSONObject inputObject = (JSONObject) commandObject.get("input");
		String simulationid = inputObject.get("simulation_id").toString();
		JSONObject object = (JSONObject) inputObject.get("message");
		@SuppressWarnings("unchecked")
		Set<String> keys = object.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (key.equalsIgnoreCase("timestamp")) {
				timestamp = (Long) object.get("timestamp");

			}
			if (key.equalsIgnoreCase("difference_mrid")) {
				differenceMrid = (String) object.get(key);
			}
			if (object.get(key) instanceof JSONArray && (key.equalsIgnoreCase("forward_differences"))) {
				forwardDifferenceArray = (JSONArray) object.get(key);
			}
			if (object.get(key) instanceof JSONArray && (key.equalsIgnoreCase("reverse_differences"))) {
				reverseDifferenceArray = (JSONArray) object.get(key);
			}

		}

		if ((timestamp == -1) || (simulationid.equalsIgnoreCase(""))) {
			ret = new ProvenMessageResponse();
			ret.setStatus(Status.BAD_REQUEST);
			ret.setReason("Invalid or missing message content type.  Measurement timestamp or simulation id missing.");
			ret.setCode(Status.BAD_REQUEST.getStatusCode());
			ret.setResponse("{ \"ERROR\": \"Bad request made to time-series database.\" }");
			return ret;
		}

		if ((forwardDifferenceArray == null) && (reverseDifferenceArray == null)) {
			ret = new ProvenMessageResponse();
			ret.setStatus(Status.BAD_REQUEST);
			ret.setReason("Invalid or missing message content type.  Measurements missing.");
			ret.setCode(Status.BAD_REQUEST.getStatusCode());
			ret.setResponse("{ \"ERROR\": \"Bad request made to time-series database.\" }");
			return ret;
		}

		@SuppressWarnings("unchecked")
		Iterator<JSONObject> fditerator = forwardDifferenceArray.iterator();
		while (fditerator.hasNext()) {
			JSONObject fdobject = (JSONObject) fditerator.next();
			Set<String> fdkeys = fdobject.keySet();
			Iterator<String> fdit = fdkeys.iterator();
			Point.Builder builder = Point.measurement(measurementName).time(timestamp, TimeUnit.SECONDS);
			builder.tag("simulation_id", simulationid);
			builder.tag("difference_mrid", differenceMrid);

			// Add OUTPUT tag
			builder.tag("hasSimulationMessageType", "INPUT");
			builder.tag("hasMeasurementDifference", "FORWARD");

			while (fdit.hasNext()) {
				String fdkey = fdit.next();

				if (fdobject.get(fdkey) instanceof String) {
					builder.tag(fdkey, (String) fdobject.get(fdkey));
				} else if (fdobject.get(fdkey) instanceof Integer) {
					builder.addField(fdkey, Integer.valueOf((Integer) fdobject.get(fdkey)));
				} else if (fdobject.get(fdkey) instanceof Long) {
					builder.addField(fdkey, Long.valueOf((Long) fdobject.get(fdkey)));
				} else if (fdobject.get(fdkey) instanceof Float) {
					builder.addField(fdkey, Float.valueOf((Float) fdobject.get(fdkey)));
				} else if (fdobject.get(fdkey) instanceof Double) {
					builder.addField(fdkey, Double.valueOf((Double) fdobject.get(fdkey)));
				} else 	if  (fdobject.get(fdkey) instanceof JSONObject) {

					JSONObject iObject = (JSONObject)fdobject.get(fdkey);
					builder = processNestedObject(iObject, fdkey,  builder);

				}

			}
			influxDB.write(idbDB, idbRP, builder.build());
			ret = new ProvenMessageResponse();
			ret.setReason("success");
			ret.setStatus(Status.CREATED);
			ret.setCode(Status.CREATED.getStatusCode());
			ret.setResponse("{ \"INFO\": \"Time-series measurements successfully created.\" }");

		}

		@SuppressWarnings("unchecked")
		Iterator<JSONObject> rditerator = reverseDifferenceArray.iterator();
		while (rditerator.hasNext()) {
			JSONObject rdobject = (JSONObject) rditerator.next();
			Set<String> rdkeys = rdobject.keySet();
			Iterator<String> rdit = rdkeys.iterator();
			Point.Builder builder = Point.measurement(measurementName).time(timestamp, TimeUnit.SECONDS);
			builder.tag("simulation_id", simulationid);
			builder.tag("difference_mrid", differenceMrid);

			// Add OUTPUT tag
			builder.tag("hasSimulationMessageType", "INPUT");

			// Add DIFFERENCE tag
			builder.tag("hasMeasurementDifference", "REVERSE");

			while (rdit.hasNext()) {
				String rdkey = rdit.next();
				// if (rdobject.get(rdkey) instanceof String) {
				if (rdobject.get(rdkey) instanceof String) {
					builder.tag(rdkey, (String) rdobject.get(rdkey));
				} else if (rdobject.get(rdkey) instanceof Integer) {
					builder.addField(rdkey, Integer.valueOf((Integer) rdobject.get(rdkey)));
				} else if (rdobject.get(rdkey) instanceof Long) {
					builder.addField(rdkey, Long.valueOf((Long) rdobject.get(rdkey)));
				} else if (rdobject.get(rdkey) instanceof Float) {
					builder.addField(rdkey, Float.valueOf((Float) rdobject.get(rdkey)));
				} else if (rdobject.get(rdkey) instanceof Double) {
					builder.addField(rdkey, Double.valueOf((Double) rdobject.get(rdkey)));

				} else 	if  (rdobject.get(rdkey) instanceof JSONObject) {
					JSONObject iObject = (JSONObject)rdobject.get(rdkey);
					builder = processNestedObject(iObject, rdkey,  builder);					

				}			

			}
			influxDB.write(idbDB, idbRP, builder.build());
			ret = new ProvenMessageResponse();
			ret.setReason("success");
			ret.setStatus(Status.CREATED);
			ret.setCode(Status.CREATED.getStatusCode());
			ret.setResponse("{ \"INFO\": \"Time-series measurements successfully created.\" }");

		}
		return ret;
	}

	private ProvenMessageResponse writeSimulationOutput(JSONObject messageObject, InfluxDB influxDB,
			String measurementName, String instanceId) {

		ProvenMessageResponse ret = null;
		Long timestamp = (long) -1;
		boolean hasMeasurementObject = false;
		//		boolean hasMeasurementArray = false;
		String measurementArrayKey = null;
		String simulationid = messageObject.get("simulation_id").toString();
		JSONObject messageContentsObject = (JSONObject) messageObject.get("message");
		@SuppressWarnings("unchecked")
		Set<String> messageContent_keys = messageContentsObject.keySet();
		timestamp = (Long) messageContentsObject.get("timestamp");



		if ((timestamp == -1) || (simulationid.equalsIgnoreCase(""))) {
			ret = new ProvenMessageResponse();
			ret.setStatus(Status.BAD_REQUEST);
			ret.setReason("Invalid or missing message content type.  Measurement timestamp or simulation id missing.");
			ret.setCode(Status.BAD_REQUEST.getStatusCode());
			ret.setResponse("{ \"ERROR\": \"Bad request made to time-series database.\" }");
			return ret;
		}


		@SuppressWarnings("unchecked")

		JSONObject measurements = (JSONObject) messageContentsObject.get("measurements");
		Set<String> measurement_keys = measurements.keySet();
		Iterator<String> measurement_it = measurement_keys.iterator();
		while (measurement_it.hasNext()) {
			String measurement_key = measurement_it.next();
			if (measurements.get(measurement_key) instanceof JSONObject) {
				JSONObject record = (JSONObject)   measurements.get(measurement_key);
				Set<String> record_keys = record.keySet();
				Iterator<String> record_it = record_keys.iterator();
				Point.Builder builder = Point.measurement(measurementName).time(timestamp, TimeUnit.SECONDS);
				builder.tag("simulation_id", simulationid);

				// Add instanceId tag, if any
				if (null != instanceId) {
					builder.tag("instance_id", instanceId);
				}

				// Add OUTPUT tag
				builder.tag("hasSimulationMessageType", "OUTPUT");

				while (record_it.hasNext()) {
					String record_key = record_it.next();

					if (record.get(record_key) instanceof String) {
						if (record_key.equalsIgnoreCase("measurement_mrid")) {
							builder.tag(record_key, (String) record.get(record_key));
						} else {
							builder.tag(record_key, (String) record.get(record_key));
						}
					} else if (record.get(record_key) instanceof Integer) {
						builder.addField( record_key, Integer.valueOf((Integer) record.get(record_key)));
					} else if (record.get(record_key) instanceof Long) {
						builder.addField(record_key, Long.valueOf((Long) record.get(record_key)));
					} else if (record.get(record_key) instanceof Float) {
						builder.addField(record_key, Float.valueOf((Float) record.get(record_key)));
					} else if (record.get(record_key) instanceof Double) {
						builder.addField(record_key, Double.valueOf((Double) record.get(record_key)));
					}
				}

				influxDB.write(idbDB, idbRP, builder.build());
				ret = new ProvenMessageResponse();
				ret.setReason("success");
				ret.setStatus(Status.CREATED);
				ret.setCode(Status.CREATED.getStatusCode());
				ret.setResponse("{ \"INFO\": \"Time-series measurements successfully created.\" }");

			}
		}
		return ret;

	}

	//
	// New write measurement routine
	//
	public ProvenMessageResponse writeMeasurement(String measurements, String measurement_type,
			String measurementName, String instanceId) {

		ProvenMessageResponse ret = null;
		if (!useIdb) {

			ret = new ProvenMessageResponse();
			ret.setReason("Time-series database unavailable or database adapter is disabled.");
			ret.setStatus(Status.SERVICE_UNAVAILABLE);
			ret.setCode(Status.SERVICE_UNAVAILABLE.getStatusCode());
			ret.setResponse("{ \"INFO\": \"idb server disabled in Proven configuration\" }");
			return ret;
		}

		InfluxDB influxDB = InfluxDBFactory.connect(idbUrl, idbUsername, idbPassword);
		influxDB.enableBatch(20000, 20, TimeUnit.SECONDS);
		JSONParser parser = new JSONParser();

		JSONObject messageObject = null;
		try {
			messageObject = (JSONObject) parser.parse(measurements);
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		char type = detectObjectType(messageObject);

		if (type == 'O') {
			ret = writeSimulationOutput(messageObject, influxDB, measurementName, instanceId);
		} else if (type == 'I') {
			ret = writeSimulationInput(messageObject, influxDB, measurementName, instanceId);
		} else {
			ret = new ProvenMessageResponse();
			ret.setStatus(Status.BAD_REQUEST);
			ret.setReason("Invalid or missing message content type.  Simulation data irregular or not detected.");
			ret.setCode(Status.BAD_REQUEST.getStatusCode());
			ret.setResponse("{ \"ERROR\": \"Bad request made to time-series database.\" }");
			return ret;
		}

		//		now = LocalDateTime.now();
		//		System.out.println(dtf.format(now));
		influxDB.close();
		return ret;

	}

	

}
