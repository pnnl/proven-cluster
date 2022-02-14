package gov.pnnl.proven.cluster.lib.pipeline.gridappsd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class GridAppsdMsgConvertor implements Serializable {

	
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


	public JsonObjectBuilder processNestedObject (JsonObject iObject ) {
		JsonObjectBuilder ret = Json.createObjectBuilder();
		Set<String> iokeys = iObject.keySet();
		Iterator<String> ioit = iokeys.iterator();


		while (ioit.hasNext()) 	{
			String iokey = ioit.next();

			if (iObject.get(iokey).getValueType() == ValueType.STRING) {
				ret.add(iokey, iObject.get(iokey));
			} else if (iObject.get(iokey).getValueType() != ValueType.NUMBER)  {
				ret.add(iokey, iObject.get(iokey));
			} else 	if  (iObject.get(iokey) instanceof JsonObject) {
				JsonObject i2Object = (JsonObject) iObject.get(iokey);
				ret = processNestedObject (i2Object);
			}
		}

		return ret;

	}

	//
	// How do we take responses coming deep inside pipeline to add to the response? 
	// Prototype idea of adding a JSON message to the content of the JSON being built.
	//
	public JsonObject transformationResponse (String reason, int code, String response ) {
		JsonObjectBuilder  responseObj = Json.createObjectBuilder();
		JsonObjectBuilder  responseMsg = Json.createObjectBuilder();
		responseObj.add("reason", reason);
		responseObj.add("code", code);
		responseObj.add("response", response);
		responseMsg.add("response_message", responseObj);
		return responseMsg.build();

	}

	public JsonObjectBuilder createMsgHeader(Long timestamp, String measurement) {
		JsonObjectBuilder message = Json.createObjectBuilder();
		JsonArrayBuilder header = Json.createArrayBuilder();	

		message.add("timestamp",  timestamp);
		message.add("measurement",measurement);	


		// {
		//			"timestamp": 1357048800,
		//			"measurement": "simulation",

		header.add("is_metadata");
		header.add("label");
		header.add("value");
		header.add("value_type");
		message.add("header", header.build());

		return message;

	}


	public JsonArrayBuilder processDifferenceArray (String direction, String simulationId, JsonArray differenceArray, JsonArrayBuilder values) {
		Iterator<JsonValue> diterator = differenceArray.iterator();
		JsonArrayBuilder record = Json.createArrayBuilder();
		JsonObjectBuilder valueObject = Json.createObjectBuilder();
		while (diterator.hasNext()) {
			JsonObject dobject = (JsonObject) diterator.next();
			Set<String> dkeys = dobject.keySet();
			Iterator<String> dit = dkeys.iterator();


			record.add("TRUE,hasSimulationMessageType,INPUT,STRING");
			record.add("TRUE,hasMeasurementDifference," + direction + ",STRING");			
			record.add("TRUE,simulation_id," + simulationId + ",STRING");	
			while (dit.hasNext()) {
				String rdkey = dit.next();
				if (dobject.get(rdkey).getValueType() == ValueType.STRING) {
					if (rdkey.equalsIgnoreCase("object")) {
						record.add("TRUE," + rdkey + "," + dobject.getJsonString(rdkey) + ",STRING");
					} else {
						record.add("FALSE," + rdkey + "," + dobject.getJsonString(rdkey) + ",STRING");
					}
				} else if (dobject.get(rdkey).getValueType() == ValueType.NUMBER) {
					record.add("FALSE," + rdkey + "," + dobject.getJsonNumber(rdkey) + ",NUMBER");
				} else 	if  (dobject.get(rdkey) instanceof JsonObject) {
					JsonObject iObject = dobject.getJsonObject(rdkey);
					record.add(processNestedObject(iObject).build());					

				}			


			}

			valueObject.add("record", record.build());
			values.add(valueObject);
			//			values.add(valueObject.build());

		}		
		return values;
		//		return values;
	}


	public JsonObject alarm2ProvenMeasurement (JsonObject messageObject) {


		JsonObject ret = null;	
		Long timestamp = -1L;
		JsonObjectBuilder metrics = Json.createObjectBuilder();

		JsonArray alarmArray  = null;	


		if (messageObject.containsKey("message")) {
			alarmArray = messageObject.getJsonArray("message");
		} else {
			ret = transformationResponse(
					"Invalid or missing message content type.  Json message object missing.",
					Status.BAD_REQUEST.getStatusCode(),
					"{ \"ERROR\": \"Bad request made to time-series database.\" }");
			return ret;

		}

		JsonObjectBuilder message = createMsgHeader(timestamp, "alarm");

		JsonArrayBuilder record = Json.createArrayBuilder();
		JsonArrayBuilder values = Json.createArrayBuilder();    
		for (JsonValue alarmValue : alarmArray) {

			// System.out.println(alarmValue);
			JsonObject alarmObject = (JsonObject)alarmValue;
			Set<String> fieldKeys =  alarmObject.keySet();
			JsonObjectBuilder valueObject = Json.createObjectBuilder();
			for (String fieldKey: fieldKeys)  {
				System.out.println("record: " +fieldKey);
				if (alarmObject.get(fieldKey).getValueType() == ValueType.NUMBER) {
					System.out.print("key= " + fieldKey + " ,NUMBER, " + alarmObject.getJsonNumber(fieldKey));
					record.add("FALSE," + fieldKey + "," +  alarmObject.getJsonNumber(fieldKey)  + ",NUMBER");
				} else if (alarmObject.get(fieldKey).getValueType() == ValueType.STRING) {
					System.out.print("key= " + fieldKey + " ,STRING, " + alarmObject.getString(fieldKey));	
					record.add("FALSE," + fieldKey + "," +  alarmObject.getString(fieldKey) + ",STRING");
				}
			}
			valueObject.add("record", record.build());
			values.add(valueObject.build());
		}
		metrics.add("metrics", values.build());
		message.add("message", metrics.build());
		ret = message.build();
		System.out.println(ret.toString());


		return ret;	        	

	}

	public JsonObject simulationOutput2ProvenMeasurement (JsonObject simulationOutput) {

		JsonObject ret = null;
		long timestamp = (long) -1;
		JsonObjectBuilder metrics = Json.createObjectBuilder();			
		String simulationId = "";


		if (simulationOutput.containsKey("message") == false) {

			ret = transformationResponse(
					"Invalid or missing message content type.  Json message object missing.",
					Status.BAD_REQUEST.getStatusCode(),
					"{ \"ERROR\": \"Bad request made to time-series database.\" }");
			return ret;

		}


		JsonObject messageContentObject = simulationOutput.getJsonObject("message");

		if (messageContentObject.containsKey("simulation_id")) {
			simulationId = messageContentObject.get("simulation_id").toString();
		} else {
			JsonObjectBuilder measurement = Json.createObjectBuilder();
			ret = transformationResponse(
					"Invalid or missing message content type.  Simulation id missing.",
					Status.BAD_REQUEST.getStatusCode(),
					"{ \"ERROR\": \"Bad request made to time-series database.\" }");
			return ret;
		}




		messageContentObject = messageContentObject.getJsonObject("message");
		Set<String> messageContent_keys = messageContentObject.keySet();


		//	
		if (messageContentObject.containsKey("timestamp") == true) {
			if (messageContentObject.get("timestamp").getValueType() == ValueType.NUMBER)  {
				timestamp = messageContentObject.getJsonNumber("timestamp").longValue();		
			} else if (messageContentObject.get("timestamp").getValueType() == ValueType.STRING) {		
				String timestamp_str = messageContentObject.getJsonString("timestamp").getString();
				if (timestamp_str.matches("-?\\d+(.\\d+)?")) {
					Long timestampL = Long.parseLong(timestamp_str);
					timestamp = timestampL.longValue();
				}


			} 
		}


		if (timestamp == -1) {
			ret  = transformationResponse(
					"Invalid or missing message content type.  Invalid (non-numeric epoch value) or missing measurement timestamp.",
					Status.BAD_REQUEST.getStatusCode(),
					"{ \"ERROR\": \"Bad request made to time-series database.\" }");

			return ret;
		}



		//  
		// 	"header": ["is_metadata", "label", "value", "value_type"],
		//



		if (messageContentObject.containsKey("measurements") == false) {
			ret = transformationResponse(
					"Invalid or missing message content type.  Simulation id missing.",
					Status.BAD_REQUEST.getStatusCode(),
					"{ \"ERROR\": \"Bad request made to time-series database.\" }");
			return ret;
		}


		JsonObjectBuilder message = createMsgHeader(timestamp, "simulation");
		JsonObject measurementsObject = messageContentObject.getJsonObject("measurements");
		Set<String> measurementKeys = new HashSet<String>();
		measurementKeys = measurementsObject.keySet();
		JsonArrayBuilder record = Json.createArrayBuilder();
		JsonArrayBuilder values = Json.createArrayBuilder();
		for (String key: measurementKeys)  {
			JsonObjectBuilder valueObject = Json.createObjectBuilder();
			record.add("True," + "simulation_id" + "," + simulationId  + ",STRING");
			record.add("True,hasSimulationMessageType,OUTPUT,STRING");
			record.add("True," + "object" + "," + key  + ",STRING");
			System.out.println("record: " + key);
			JsonObject measurementRecord = (JsonObject)measurementsObject.get(key);
			Set<String> fieldKeys = new HashSet<String>();
			fieldKeys = measurementRecord.keySet();
			for (String fieldKey: fieldKeys)  {
				if (measurementRecord.get(fieldKey).getValueType() == ValueType.NUMBER) {
					System.out.print("key= " + fieldKey + " ,NUMBER, " + measurementRecord.getJsonNumber(fieldKey));
					record.add("FALSE," + fieldKey + "," +  measurementRecord.getJsonNumber(fieldKey)  + ",NUMBER");
				} else if (measurementRecord.get(fieldKey).getValueType() == ValueType.STRING) {
					System.out.print("key= " + fieldKey + " ,STRING, " + measurementRecord.getString(fieldKey));	
					record.add("FALSE," + fieldKey + "," +  measurementRecord.getString(fieldKey) + ",STRING");
				}
			}

			valueObject.add("record", record.build());
			//
			//	[{
			//		"record": ["True,hasSimulationMessageType,INPUT,STRING", "True,hasMeasurementDifference,FORWARD,STRING", "FALSE,object,\"61A547FB-9F68-5635-BB4C-F7F537FD824C\",STRING", "FALSE,attribute,\"ShuntCompensator.sections\",STRING", "FALSE,value,\"0\",STRING"]
			//	},
			//
			values.add(valueObject.build());

		}  
		metrics.add("metrics", values.build());
		message.add("message", metrics.build());
		ret = message.build();
		System.out.println(ret.toString());
		return ret;
	}

	public JsonObject simulationInput2ProvenMeasurement (JsonObject messageObject) {
		//		private ProvenMessageResponse influxWriteSimulationInput(JsonObject commandObject, InfluxDB influxDB,
		//				String measurementName, String instanceId) {
		//			ProvenMessageResponse ret = null;

		
		JsonObject ret = null;			
		long timestamp = (long) -1;
		JsonArray forwardDifferenceArray = null;
		JsonArray reverseDifferenceArray = null;

		JsonObject inputMessageObject  = null;	
		JsonObject inputObject = null;
		String simulationId = "";

		if (messageObject.containsKey("message")) {
			inputMessageObject = messageObject.getJsonObject("message");
		} else {
			ret = transformationResponse(
					"Invalid or missing message content type.  Json message object missing.",
					Status.BAD_REQUEST.getStatusCode(),
					"{ \"ERROR\": \"Bad request made to time-series database.\" }");
			return ret;

		}

		if (inputMessageObject.containsKey("input")) {
			inputObject = inputMessageObject.getJsonObject("input");
		} else {
			ret = transformationResponse(
					"Invalid or missing message content type.  Json input message object missing.",
					Status.BAD_REQUEST.getStatusCode(),
					"{ \"ERROR\": \"Bad request made to time-series database.\" }");
			return ret;	        	

		}




		//			
		// Check for simulation_id
		//
		if (inputObject.containsKey("simulation_id")) {
			simulationId = inputObject.get("simulation_id").toString();
		} else {

			ret = transformationResponse(
					"Invalid or missing message content type.  Simulation id missing.",
					Status.BAD_REQUEST.getStatusCode(),
					"{ \"ERROR\": \"Bad request made to time-series database.\" }");
			return ret;
		}



		JsonObject object = (JsonObject) inputObject.get("message");
		Set<String> keys = object.keySet();
		Iterator<String> it = keys.iterator();

		//
		// Extract the timestamp if possible
		//
		if (object.containsKey("timestamp")) {
			if ((object.get("timestamp").getValueType() == ValueType.NUMBER) || (object.get("timestamp").getValueType() == ValueType.STRING) ) {
				timestamp = object.getJsonNumber("timestamp").longValue();		
			}
		}
		if (timestamp == -1) {
			ret  = transformationResponse(
					"Invalid or missing message content type.  Invalid (non-numeric epoch value) or missing measurement timestamp.",
					Status.BAD_REQUEST.getStatusCode(),
					"{ \"ERROR\": \"Bad request made to time-series database.\" }");

			return ret;
		}


		//
		//  Initialize outer object and array builders
		//
		//  {message :
		//        metrics : {
		//				header:[],
		//				values:[
		//					{record:[]},
		//				    {record:[]}
		//			    ]
		//     	  }
		//	}
		JsonObjectBuilder message = createMsgHeader(timestamp, "simulation");
		JsonObjectBuilder metrics = Json.createObjectBuilder();
		JsonArrayBuilder values = Json.createArrayBuilder();

		//
		//  Collect reverse and difference metrics
		//		
		while (it.hasNext()) {
			String key = it.next();
			if (key.equalsIgnoreCase("difference_mrid")) {
			}
			if  ((object.get(key).getValueType() ==  ValueType.ARRAY) && (key.equalsIgnoreCase("forward_differences"))) {
				forwardDifferenceArray = (JsonArray) object.get(key);
			}
			if ((object.get(key).getValueType() == ValueType.ARRAY) && (key.equalsIgnoreCase("reverse_differences"))) {
				reverseDifferenceArray = (JsonArray) object.get(key);
			}

		}
		//
		//	[{
		//		"record": ["True,hasSimulationMessageType,INPUT,STRING", "True,hasMeasurementDifference,FORWARD,STRING", "FALSE,object,\"61A547FB-9F68-5635-BB4C-F7F537FD824C\",STRING", "FALSE,attribute,\"ShuntCompensator.sections\",STRING", "FALSE,value,\"0\",STRING"]
		//	},
		//


		if ((forwardDifferenceArray == null) && (reverseDifferenceArray == null)) {
			ret = transformationResponse(
					"Invalid or missing message content type. The forwarDifferenceArray or reverseDifferenceArray measurements are missing.",
					Status.BAD_REQUEST.getStatusCode(),
					"{ \"ERROR\": \"Bad request made to time-series database.\" }");

			return ret;
		}
		JsonObjectBuilder valueObject = Json.createObjectBuilder();
		if (forwardDifferenceArray != null)  {
			values = processDifferenceArray("FORWARD", simulationId, forwardDifferenceArray,values);
		}
		if (reverseDifferenceArray != null ) {
			values = processDifferenceArray("REVERSE", simulationId, reverseDifferenceArray,values);	
		}
		metrics.add("metrics", values.build());
		message.add("message", metrics.build());
		ret = message.build();
		System.out.println(ret.toString());
		return ret;

	}


	public JsonObject retrieveQueryFilter (JsonObject messageObject) {

		JsonObject ret = null;			

		return ret;

	}	

	public JsonObject retrieveNativeMeasurement (JsonObject messageObject) {

		JsonObject ret = null;			

		return ret;

	}	

	//
	//  Detect the type of measurement contained in disclosure message
	//

	public String detectObjectType(JsonObject messageObject) {
		String objectType = "";

		//		Object innerMessageObject = messageObject.getJsonObject("message");

		if (messageObject.get("message").getValueType() == ValueType.ARRAY  )  {
			objectType = "A";
			return objectType;
		}  else if (messageObject.get("message").getValueType() == ValueType.OBJECT) {
			JsonObject innerMessageObject = messageObject.getJsonObject("message");
			boolean isContains = innerMessageObject.containsKey("command");
			if (isContains) {
				objectType = "I";
				return objectType;
			}

			isContains = innerMessageObject.containsKey("message");
			if (isContains) {
				objectType = "O";
				return objectType;
			}

			isContains = innerMessageObject.containsKey("queryFilter");
			if (isContains) {
				objectType = "Q";
				return objectType;
			}			

			isContains = innerMessageObject.containsKey("metrics");
			if (isContains) {
				objectType = "N";
				return objectType;
			}			

		}
		return objectType;

	}

	//
	// Utility used to pad an epoch number string with zeros
	//
	public static String padRightZeros(String s, int n) {
		s = String.format("%1$-" + n + "s", s);
		s = s.replace(' ', '0');
		return s;
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

	private String timeFilterStatement(String key, String val) {
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
			// Nanoseconds - 19 digits
			//
			// Microseconds - 13 digits
			//
			// Seconds - 10 digits

			// 16 digit nanosecond padding
			//		statement = padRightZeros(val, 7);
			statement = padRightZeros(val, 19);

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

	//	private Map<String, Integer> countFilterFields(JsonObject filters) {
	//		Map<String, Integer> filterFieldCounter = new HashMap();
	//		filters.keySet().size();
	//		
	//		if (filters != null) {
	//			int index = 0;			
	//			while (index < filters.size()) {
	//				ProvenQueryFilter filter = filters.get(index);
	//				String localKey = filters.get(index).getField();
	//				if (!filterFieldCounter.containsKey(localKey)) {
	//					filterFieldCounter.put(localKey, 1);
	//				} else {
	//					Integer currentCount = filterFieldCounter.get(localKey);
	//					filterFieldCounter.replace(localKey, currentCount + 1);
	//				}
	//				index = index + 1;
	//			}
	//		}
	//		return filterFieldCounter;
	//	}

	private String formatFilterCriteria(String keyName, JsonValue entry) {
		String statement = "";
		String space = " ";
		String eq = "=";
		String quote = "'";


		if (entry.getValueType() ==  ValueType.NUMBER) {
			JsonNumber num = (JsonNumber) entry;
			if (num.isIntegral()) {
				Long lnum = num.longValue();
				statement = keyName + space + eq + lnum.toString() ;	
			} else {
				BigDecimal dnum =  num.bigDecimalValue();
				statement = keyName + space + eq + dnum.toString();
			}
		} else if (entry.getValueType() ==  ValueType.STRING) {
			statement = keyName + space + eq + space + quote + entry.toString() + quote;
		}
		return statement;

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

	private String assembleUnionFilterStatement(JsonArray filters, String fieldName) {
		String unionStatement = "";
		int index = 0;
		if (filters.size() == 1) {
			unionStatement = formatFilterCriteria(fieldName, filters.get(index));
			return unionStatement;
		}

		while (index < filters.size()) {
			if (unionStatement.length() == 0) {
				unionStatement = " ( " + formatFilterCriteria(fieldName, filters.get(index));
			} else {
				unionStatement = unionStatement + " or " + formatFilterCriteria(fieldName, filters.get(index));
			}

			index = index + 1;

		}
		if (unionStatement.length() != 0) {
			unionStatement = unionStatement + " ) ";
		}
		return unionStatement;

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

	private List<String> assembleFilterStatements(JsonObject filters) {

		List<String> assembledFilterStatements = new ArrayList<String>();

		Set<String> keys = filters.keySet();
		keys.forEach((key) -> {
			if (filters.get(key).getValueType() ==  ValueType.ARRAY) {
				assembledFilterStatements.add(assembleUnionFilterStatement(filters.getJsonArray(key),key));
			} else {

				if (key.contains("starttime") || key.contains("endtime")) {
					assembledFilterStatements.add(timeFilterStatement(key, filters.getJsonString(key).getString()));
				} else {

					assembledFilterStatements.add(formatFilterCriteria(key, filters.get(key)));					
				}


			}
		});


		return assembledFilterStatements;
	}	



	public String constructQuery(JsonObject message, String measurement) {
		JsonObject innerMessage  = message.getJsonObject("message");
		JsonObject filters = innerMessage.getJsonObject("queryFilter");

		String filter_statement = "";
		String space = " ";
		String eq = "=";
		String quote = "'";
		String doublequote = "\"";
		String queryStatement = "select * from ";

		if (filters != null) {
			List <String> assembledFilterStatements = assembleFilterStatements(filters);
			queryStatement = queryStatement + space + "where" + space;
			boolean flag = true;
			int index = 0;
			while (index < assembledFilterStatements.size()) {
				//
				// Set up filter
				//


				if (index == 0) {
					queryStatement = queryStatement + space + assembledFilterStatements.get(index) + space;
					flag = false;

				} else {

					// If not a time filter, treat it as a field.
					//
					queryStatement = queryStatement + space + "and" + space + assembledFilterStatements.get(index) + space;

				}
				index = index + 1;
			}
		}
		return queryStatement;
	}

}


