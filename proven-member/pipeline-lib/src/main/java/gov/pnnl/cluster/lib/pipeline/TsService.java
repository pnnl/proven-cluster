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
package gov.pnnl.cluster.lib.pipeline;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue.ValueType;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.core.Response;

import org.apache.commons.csv.CSVPrinter;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.influxdb.impl.TimeUtil;


import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.jet.pipeline.ContextFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.cluster.lib.disclosure.exchange.DisclosureItem;
import gov.pnnl.proven.cluster.lib.disclosure.message.KnowledgeMessage;
import gov.pnnl.proven.cluster.lib.disclosure.message.MessageUtils;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessage;
import gov.pnnl.proven.cluster.lib.disclosure.message.ResponseMessage;
import gov.pnnl.proven.cluster.lib.member.MemberProperties;
import gov.pnnl.cluster.lib.pipeline.gridappsd.GridAppsdMsgConvertor;
import gov.pnnl.cluster.lib.pipeline.gridappsd.Measurement;
import gov.pnnl.cluster.lib.pipeline.gridappsd.Mpoint;
import gov.pnnl.cluster.lib.pipeline.gridappsd.Results;


//import com.opencsv.CSVWriter;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Provides services to triple store (T3).
 * 
 * @author d3j766
 *
 */
public class TsService  {

	private static Logger log = LoggerFactory.getLogger(TsService.class);


	private Long startTime = System.currentTimeMillis();
	private InfluxDB influxDB;
	private String serviceUrl = "http://127.0.0.1:8086";
	private Boolean useIdb = true;
	String idbDB = "proven";
	String idbRP = "autogen";
	String idbUsername =   "root";
	String idbPassword =  "root";


	private Jsonb jsonb = JsonbBuilder.create();



	public class GridappsdMsgConvertResponse implements Serializable {

		private static final long serialVersionUID = 1962L;

		int statusCode;
		String statusReason;
		long count;
		String message;

		GridappsdMsgConvertResponse() {
		}

		public GridappsdMsgConvertResponse(Response.Status status, long count) {
			this.statusCode = status.getStatusCode();
			this.statusReason = status.getReasonPhrase();
			this.count = count;
			this.message = "";
		}

		public GridappsdMsgConvertResponse(int statusCode, String reason, String message) {
			this.statusCode = statusCode;
			this.statusReason = reason;	
			this.count = 0;
			this.message = message;
		}
	}


	/**
	 * Summary response information for a T3 storage request. This is included
	 * in a {@code ResponseMessage} as it's message content.
	 * 
	 * @author d3j766
	 *
	 */
	public class TsResponse implements Serializable {

		private static final long serialVersionUID = 1L;

		int statusCode;
		String statusReason;
		String statusMessage;
		String results;

		public String getResults() {
			return results;
		}

		public void setReturnMessage(String results) {
			this.results = results;
		}

		TsResponse() {
			this.results = null;
		}

		TsResponse(Response.Status status, String statusMessage) {
			this.statusCode = status.getStatusCode();
			this.statusReason = status.getReasonPhrase();
			this.statusMessage = statusMessage;
			this.results = null;
		}
	}


	/**
	 * Returns {@code ContextFactory} for Jet processing pipelines that require TS services.
	 * 
	 * @return {@link ContextFactory}
	 */
	public static ContextFactory<TsService> tsService() {

		return ContextFactory.withCreateFn(x -> TsService.newTsService()).toNonCooperative().withLocalSharing();

	}


	/**
	 * Creates a new T3Service with default settings.
	 * 
	 * @param serviceUrl
	 *            identifies SPARQL endpoint
	 * 
	 * @throws RepositoryException
	 */
	public static TsService newTsService()  {
		TsService tsS = new TsService();

		return tsS;
	}

	private TsService() {

		//	    serviceUrl = props.getHybridTsServiceUrl();
		//		useIdb = props.getHybridTsUseIdb();
		//		idbDB = props.getHybridTsIdbDb(); 
		//		idbRP = props.getHybridTsIdbRp();
		//		idbUsername =   props.getHybridTsIdbUsername(); 
		//		idbPassword =  props.getHybridTsIdbPassword();

	}

	private TsResponse writeMeasurements(JsonObject transformationMsg, ProvenMessage sourceMessage) {
		TsResponse ret = null;
		JsonReader reader =  null;
		if (useIdb) {

			influxDB = InfluxDBFactory.connect(serviceUrl, idbUsername, idbPassword);
			influxDB.enableBatch(20000, 20, TimeUnit.SECONDS);

			long timestamp  = transformationMsg.getJsonNumber("timestamp").longValue();
			String measurement = transformationMsg.getJsonString("measurement").getString();
			JsonObject messageObject = transformationMsg.getJsonObject("message");
			JsonArray metricsArray = messageObject.getJsonArray("metrics");
			int metricsSize = metricsArray.size();
			Point.Builder builder = Point.measurement(measurement).time(timestamp, TimeUnit.SECONDS);
			for (int i = 0; i < metricsSize; i++) {

				System.out.println("BEGIN POINT");
				JsonObject recordsObject = (JsonObject) metricsArray.getJsonObject(i);
				JsonArray recordValues = (JsonArray) recordsObject.getJsonArray("record");
				int valuesSize = recordValues.size();
				for (int vi = 0 ; vi < valuesSize ; vi++)  {
					String[] arrStr = recordValues.getString(vi).split(",");
					int x = vi;
					if (arrStr[0].equalsIgnoreCase("TRUE")) {
						builder.tag(arrStr[1], arrStr[2]);
						System.out.println("Tag Name= " + arrStr[1] + " Value= " + arrStr[2] + " Data Type= " +arrStr[3]);

					} else {
						System.out.println("Field Name= " + arrStr[1] + " Value= " + arrStr[2] + " Data Type= " +arrStr[3]);

						if (arrStr[3].equalsIgnoreCase("STRING")) {
							builder.addField(arrStr[1], arrStr[2]);
						}  else if (arrStr[3].equalsIgnoreCase("FLOAT")) {
							builder.addField(arrStr[1], Float.parseFloat(arrStr[2]));
						}  else if (arrStr[3].equalsIgnoreCase("INTEGER")) {
							builder.addField(arrStr[1], Integer.parseInt(arrStr[2]));
						}  else if (arrStr[3].equalsIgnoreCase("BOOLEAN")) {
							builder.addField(arrStr[1], Boolean.parseBoolean(arrStr[2]));									
						}  else if (arrStr[3].equalsIgnoreCase("LONG")) {
							builder.addField(arrStr[1], Long.parseLong(arrStr[2]));									
						}  else if (arrStr[3].equalsIgnoreCase("DOUBLE")) {
							builder.addField(arrStr[1], Double.parseDouble(arrStr[2]));									
						}

					}


					try {

						System.out.println("END POINT");
						influxDB.write(idbDB, idbRP, builder.build());
						influxDB.flush();
						ret = new TsResponse(Response.Status.OK, "Time-series measurements successfully created.");

					} catch (Exception e) {
						ret = new TsResponse(Response.Status.BAD_REQUEST, "Error interpreting measurement, possibly malformed JSON or no fields in measurement, output not recorded.");
						return ret;
					}
				}
			}
		}
		return ret;
	}

	// @Override
	public ResponseMessage add(ProvenMessage sourceMessage) {

		ResponseMessage ret = null;		
		GridappsdMsgConvertResponse loadResponse = null;

		//
		// Define transformMsg is an internal Proven measurement format.
		// If problems occur during transformation a "response_message" object
		// is created that captures details  on the transformation error.
		//
		// The GridAppsdMsgConvertor inherits from ResponseMessage and augments
		// state information to support custom messages.
		//
		JsonObject transformMsg = null;
		JsonObject mObject = sourceMessage.getDisclosureItem().getMessage();
		GridAppsdMsgConvertor gmc =  new GridAppsdMsgConvertor();
		TsResponse tsResponse = null;


        //
		// Detect a message type. If non-native GridAppsd message type is detected transform it 
		// into a Proven measurement.  If the transformation is successful a measurement is created.
		// If a problem occurs "response_message" is included.
		//
		String objectType = gmc.detectObjectType(mObject);
		if (objectType.equalsIgnoreCase("I")) {
			transformMsg = gmc.simulationInput2ProvenMeasurement (sourceMessage.getDisclosureItem().getMessage()); 		        	
		} else if (objectType.equalsIgnoreCase("O")) {
			transformMsg = gmc.simulationOutput2ProvenMeasurement (sourceMessage.getDisclosureItem().getMessage()); 		        		
		} else if (objectType.equalsIgnoreCase("A")) {
			transformMsg = gmc.alarm2ProvenMeasurement (sourceMessage.getDisclosureItem().getMessage()); 
		} else if (objectType.equalsIgnoreCase("Q")) {
			transformMsg = gmc.retrieveQueryFilter (sourceMessage.getDisclosureItem().getMessage());
		} else if (objectType.equalsIgnoreCase("N")) {
			transformMsg = gmc.retrieveNativeMeasurement (sourceMessage.getDisclosureItem().getMessage());				
	    } else {
	    	
	    	
		  tsResponse = new TsResponse(Response.Status.BAD_REQUEST, "Error interpreting measurement, possibly malformed JSON or no fields in measurement, output not recorded.");
		  return ret;
	      	
	    }
		
		//
		// The transformMsg is either native measurements for writing,  a query filter
		// or an error response when a transformation  could not be performed.  
		//
		// If a "response_message" key is present, an error response needs to be post 
		// processed and returned.
		//
		//
		
		

		
		if (transformMsg != null) {
			if (transformMsg.containsKey("response_message")) {

				JsonObject statusObj = transformMsg.getJsonObject("response_message");
				loadResponse = new GridappsdMsgConvertResponse(statusObj.getInt("code"), statusObj.getString("reason"), statusObj.getString("response"));
			} else {

				if (objectType.equalsIgnoreCase("Q")) {
					tsResponse =  writeMeasurements(transformMsg, sourceMessage);					
				} else {
					tsResponse = search(transformMsg, "measurement", false, false, false, sourceMessage);
				}

			}


		} else {
			tsResponse = new TsResponse(Response.Status.BAD_REQUEST, "Error interpreting measurement, possibly malformed JSON or no fields in measurement, output not recorded.");
			ret = createResponseMessage(tsResponse, sourceMessage) ;
			return ret;
		}
		
		
		return ret;
		




	}

	
	private ResponseMessage createResponseMessage(TsResponse tsResponse, ProvenMessage sourceMessage) {
		JsonReader reader = Json.createReader(new StringReader(jsonb.toJson(tsResponse)));
		JsonObject loadResponseObject = reader.readObject();
		return new ResponseMessage(Response.Status.fromStatusCode(tsResponse.statusCode), loadResponseObject,
				sourceMessage);
	}


	/*
	 * 
{ 
        "domain": "gridappsd.pnnl.gov",
        "name": "SimulationInput",
        "content": "measurement",
        "disclosureId": "abc12434",
        "requestorId": "xyz356436",
        "isStatic": false,
        "isTransient": false,
"message":
{"queryType": "time-series",
 "queryMeasurement": "simulation",
 "queryFilter":
 {"simulation_id": "687235791",
 "startTime":"1248159974",
 "endTime":"1248159999"},
"responseFormat": "JSON"}}
	 */

	public TsResponse search(JsonObject query, String measurementName, boolean returnCsvFlag, boolean returnQueryStatement, boolean pingQuery, ProvenMessage sourceMessage) {
		TsResponse ret = null;

		String queryStatement = "";

		JsonObject tsquery = query.getJsonObject("message");
		if (tsquery == null) {
			ret = new TsResponse(Response.Status.BAD_REQUEST, "JSON message object not found.  Unable to perform query");
            return ret;
		}
		GridAppsdMsgConvertor gmc = new GridAppsdMsgConvertor();


		queryStatement  = gmc.constructQuery(tsquery, measurementName);

		String response = "";
		String reason = "";

		if (!useIdb) {

			ret = new TsResponse(Response.Status.SERVICE_UNAVAILABLE, "Time-series database unavailable or database adapter is disabled. InfluxDB server disabled in Proven configuration");
            return ret;
		} else {
			InfluxDB influxDB = InfluxDBFactory.connect(serviceUrl, idbUsername, idbPassword);
			String dbName = idbDB;
			influxDB.setRetentionPolicy(idbRP);

			Query influxQuery = null;
			if (pingQuery) {
				queryStatement = queryStatement + " LIMIT 1";
				influxQuery = new Query(queryStatement, dbName);
			} else
				influxQuery = new Query(queryStatement, dbName);
			//
			// Unless TimeUnit parameter is used in query, Epoch time will not
			// be returned.
			//
			System.out.println(influxQuery);



			//	       if (transformationMsg != null) {
			//	       	if (transformationMsg.containsKey("response_message")) {
			//	       		JsonObject statusObj = transformationMsg.getJsonObject("response_message");
			//	       		loadResponse = new GridappsdMsgConvertResponse(statusObj.getInt("code"), statusObj.getString("reason"), statusObj.getString("response"));
			//	       		
			//	       	}		        	
			//	       	
			//	       }


			QueryResult qr = null;
			try {

				qr = influxDB.query(influxQuery, TimeUnit.SECONDS);

			} catch (OutOfMemoryError e) {
				ret = new TsResponse(Response.Status.BAD_REQUEST, "Query results are to large to return.  Out of memory");

				return ret;
			}




			try { 

				if (returnCsvFlag) {
					if (returnQueryStatement) {
					    ret = new TsResponse(Response.Status.OK, "Results successfully retrieved" + "Query statement: " + queryStatement);
					} else {
						ret = new TsResponse(Response.Status.OK, "Results successfully retrieved");
					}
					ret.setReturnMessage(convertResults2Csv2(qr));
                    return ret;
				} else {
					ret = new TsResponse(Response.Status.OK, "Results successfully retrieved");
                    ret.setReturnMessage(convertResults2Json(qr));
                    return ret;
				}

			} catch (OutOfMemoryError e) {


				ret = new TsResponse(Response.Status.BAD_REQUEST, "Query results are to large to return.  Out of memory");

				return ret;

			}




		}



	}

	@SuppressWarnings("unused")
	public String convertResults2Json(QueryResult qr) {

		//
		// Declare Json Objects to be built
		//
		String jsonresults = "";
        JsonArrayBuilder measurementBuilder = Json.createArrayBuilder();

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
				JsonObjectBuilder rowObject = Json.createObjectBuilder();

				//
				// Process Rows for a given Series
				//
				for (List<Object> row : table) {

					int cellIndex = 0;
					//
					// Process Cells for a given Row
					//
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
								rowObject.add(colNames.get(cellIndex), lval);
							} else if (cell instanceof Integer) {
								rowObject.add(colNames.get(cellIndex), (Integer)cell);						    
							} else if (cell instanceof Double) {
								rowObject.add(colNames.get(cellIndex), (Double)cell);						    
							}else {
								String buff = cell.toString();
								//
								// If a cell is a quoted string, remove the
								// surrounding double quotes.
								//
								buff = buff.replaceAll("\"", "");
								//								point.putRow(colNames.get(cellIndex), buff);
								//							    rowResults.put(colNames.get(cellIndex), buff);
								rowObject.add(colNames.get(cellIndex), buff);
							}

							//
							// Add each cell Json Object to a row Object.
							//
						}
						cellIndex++;

					} // end adding to row
					//					tableResults.put(rowIndex, rowResults);
					
					measurementBuilder.add(rowObject.build());
					rowObject = Json.createObjectBuilder();
					rowIndex++;

					//
					// Add each row O
					//
				} // end adding to table
				seriesIndex++;
			} // end adding series

			resultIndex++;
		}
		; // end adding results
		jsonresults = measurementBuilder.build().toString();
		return jsonresults;

	}


	public String convertResults2Csv2(QueryResult qr) {

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



	//	private ResponseMessage createResponseMessage(TsResponse tsResponse, ProvenMessage sourceMessage) {
	//		JsonReader reader = Json.createReader(new StringReader(jsonb.toJson(tsResponse)));
	//		//JsonReader reader = Json.createReader(new StringReader(""));
	//		JsonObject loadResponseObject = reader.readObject();
	//		return new ResponseMessage(Response.Status.fromStatusCode(tsResponse.statusCode), sourceMessage,
	//				loadResponseObject);
	//	}


}
