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

import static gov.pnnl.proven.hybrid.concept.ConceptUtil.*;
import static gov.pnnl.proven.hybrid.concept.ProvenConceptSchema.*;
import static gov.pnnl.proven.hybrid.util.Consts.*;
import static gov.pnnl.proven.hybrid.util.ProvenConfig.ProvenEnvProp.*;
import static gov.pnnl.proven.hybrid.util.Utils.*;
import static gov.pnnl.proven.message.ProvenMetric.MetricFragmentIdentifier.*;

import gov.pnnl.proven.message.*;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.lang.Long;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.codec.binary.StringUtils;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.xmlmodel.ObjectFactory;
//import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.result.NoResultException;
import org.openrdf.store.blob.BlobObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import gov.pnnl.proven.hybrid.concept.Concept;
import gov.pnnl.proven.hybrid.concept.DomainModel;
import gov.pnnl.proven.hybrid.concept.NativeSource;
import gov.pnnl.proven.hybrid.concept.Ontology;
import gov.pnnl.proven.hybrid.concept.Representation;
import gov.pnnl.proven.hybrid.concept.ConceptUtil.BlobStatus;
import gov.pnnl.proven.hybrid.manager.StoreManager;
import gov.pnnl.proven.hybrid.resource.RepositoryResource.ProvenanceMetric;
import gov.pnnl.proven.hybrid.util.ProvenConfig;
import gov.pnnl.proven.hybrid.util.Mpoint;
import gov.pnnl.proven.hybrid.util.Measurement;
import gov.pnnl.proven.hybrid.util.Results;
import gov.pnnl.proven.message.ProvenMeasurement;
import gov.pnnl.proven.message.ProvenMessage;
import gov.pnnl.proven.message.ProvenMessageResponse;
import gov.pnnl.proven.message.ProvenMetric;
import gov.pnnl.proven.message.ProvenMetric.MetricFragmentIdentifier.MetricValueType;
import gov.pnnl.proven.message.ProvenQueryFilter;
import gov.pnnl.proven.message.ProvenQueryTimeSeries;
import gov.pnnl.proven.message.MessageUtils;
import gov.pnnl.proven.message.exception.InvalidProvenMessageException;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

/**
 * Session Bean implementation class ConcepService
 * 
 * Provides management and persistent services for proven concepts. By default,
 * the proven context and base uri are used.
 * 
 */
@Stateless
@LocalBean
public class ConceptService {

	private final Logger log = LoggerFactory.getLogger(ConceptService.class);

	private ProvenConfig pg;

	@EJB
	private StoreManager sm;

	private ObjectConnection oCon = null;

	private boolean useIdb;
	private String idbDB;
	private String idbRP;
	private String idbUrl;
	private String idbUsername;
	private String idbPassword;
    private String x;
	// //////////////////////////////////////////////////////////
	// Named queries

	// @formatter:off

	private static final String findSingleByName = "SELECT ?s WHERE {?s " + toIri(HAS_NAME_PROP) + " ?name }";

	private static final String findNativeSourcesByDomainName = "SELECT ?s WHERE { ?s " + toIri(HAS_DOMAIN_MODEL_PROP)
	+ " ?domain  . " + "                  ?domain " + toIri(HAS_NAME_PROP) + " ?name " + "                } ";

	private static final String findNativeSourcesByDomainName2 = "SELECT ?s WHERE {?s  ?p  ?o }";

	// @formatter:on
	// //////////////////////////////////////////////////////////

	@PostConstruct
	public void postConstruct() {

		try {
			this.oCon = sm.getObjectStoreConnection();
		} catch (Exception e) {
			e.printStackTrace();
			throw new EJBException("Borrow connection failed in construction of ConceptService");
		}

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
		try {
			// TODO should uncommitted changes just be lost?
			// force a close and commit changes
			oCon.commit();
			oCon.close();
			log.debug("ConeptService destroyed ...");
		} catch (RepositoryException e) {
			// swallow it, not need to throw an ejb exception at this point
			e.printStackTrace();
		}
		log.debug("ConceptService destroyed ...");
	}

	/**
	 * Commits all changes and creates a new connection to the repository
	 * 
	 * @throws Exception
	 */
	public void flush() {

		try {
			if (isValidConnection()) {
				oCon.commit();
				oCon.close();
			}
			oCon = sm.getObjectStoreConnection();

		} catch (Exception e) {
			log.error("FLUSH FAILURE");
			e.printStackTrace();
		}
	}

	public void begin() {

		try {
			if (!oCon.isActive()) {
				oCon.begin();
			}

		} catch (Exception e) {
			log.error("BEGIN FAILURE");
			e.printStackTrace();
		}

	}

	public void commit() {

		try {
			if (oCon.isActive()) {
				oCon.commit();
			}

		} catch (Exception e) {
			log.error("COMMIT FAILURE");
			e.printStackTrace();
		}

	}

	public void rollback() {

		try {
			if (oCon.isActive()) {
				oCon.rollback();
			}

		} catch (Exception e) {
			log.error("ROLLBACK FAILURE");
			e.printStackTrace();
		}

	}

	public ObjectConnection getObjectConnection() {
		return oCon;
	}

	public String addConcept(Concept c) throws RepositoryException {
		String ret = oCon.addObject(c).toString();
		addBlobs(c.getRepresentations());
		return ret;
	}

	public void addConcepts() {
	}

	public <T> List<T> getConcepts(Class<T> concept) throws Exception {
		return oCon.getObjects(concept).asList();
	}

	public void addStatements(Collection<Statement> statements, Resource... resources) throws Exception {
		oCon.add(statements, resources);
	}

	public <T> void removeConcept(Concept c, Class<T> cType) throws Exception {
		oCon.removeDesignation(c, cType);
	}

	public void removeConcepts() {
	}

	public <T> T findConceptById(Class<T> conceptClass, String uri) throws Exception {
		T ret = null;
		log.debug("QUERY::" + findSingleByName);
		ret = oCon.getObject(conceptClass, uri);
		return ret;
	}

	/**
	 * Finds a concept instance by name. Assumes a single result, will throw an
	 * exception if multiple instances discovered.
	 * 
	 * @param conceptClass
	 *            the concept class to search over
	 * @param name
	 *            value of name property to search for
	 * 
	 * @return returns the concept instance. Returns null if no concept found
	 * 
	 * @throws Exception
	 *             if query failure occurs
	 */
	public <T> T findConceptByName(Class<T> conceptClass, String name) throws Exception {

		T ret = null;
		ObjectQuery q = oCon.prepareObjectQuery(findSingleByName);
		q.setObject("name", name);
		try {
			ret = q.evaluate(conceptClass).singleResult();
		} catch (NoResultException nre) {
			ret = null;
		}
		return ret;
	}

	// TODO - concept specific queries should be moved to the concept class it's
	// associated with. Only concept wide queries should be provided here. Move
	// this method to the NativeSource concept class.
	public List<NativeSource> findNativeSourcesByDomain(DomainModel dm) throws Exception {

		List<NativeSource> ret = new ArrayList<NativeSource>();
		log.debug("QUERY::" + findNativeSourcesByDomainName);

		log.debug("DOMAIN NAME :: " + dm.getName());

		// Set read to domain content area
		log.debug("CONTEXT URI :: " + toUri(dm.getExplicitContent().getContextUri()));
		oCon.setReadContexts(toUri(PROVEN_CONTEXT), toUri(dm.getExplicitContent().getContextUri()));
		ret.addAll(oCon.getObjects(NativeSource.class).asList());

		return ret;
	}

	public RepositoryResult<Statement> getAllStatements() throws Exception {

		RepositoryResult<Statement> ret = null;
		ret = oCon.getStatements(null, null, null);
		return ret;

	}

	public RepositoryResult<Statement> getDomainStatements(String domain) throws Exception {

		RepositoryResult<Statement> ret = null;
		List<Resource> contexts = new ArrayList<Resource>();

		DomainModel dm = findConceptByName(DomainModel.class, domain);

		if (null != dm) {

			Resource ec = toResource(dm.getExplicitContent().getContextUri());
			contexts.add(ec);

			Set<Ontology> onts = dm.getOntologies();
			for (Ontology ont : onts) {
				Resource r = toResource(ont.getContext().getContextUri());
				contexts.add(r);
			}

			Resource[] resources = new Resource[contexts.size()];
			ret = oCon.getStatements(null, null, null, contexts.toArray(resources));
		}

		return ret;

	}

	public void removeDomainContext(Resource context) throws RepositoryException {
		oCon.clear(context);
		oCon.commit();
	}

	public void removeAll() throws RepositoryException {
		oCon.setRemoveContexts();
		// RepositoryResult<Resource> contexts = oCon.getContextIDs();
		// List<Resource> contextList = Iterations.asList(contexts);
		// for (Resource context : contextList) {
		// if (!context.toString().equals(PROVEN_CONTEXT));
		// oCon.clear(context);
		// }
		oCon.clear();
		oCon.commit();
	}

	public void addBlobs(Set<Representation> reps) {
		for (Representation rep : reps) {

			try {
				addBlob(rep);
			} catch (RepositoryException e) {
				log.warn("Blob creation failed :: " + e.getMessage());
				// e.printStackTrace();
			}
		}

	}

	public void addBlob(Representation rep) throws RepositoryException {

		OutputStream out = null;
		FileInputStream in = null;
		rep.setBlobStatus(BlobStatus.ADD_COMPLETE);

		try {

			// For now, only add local file representations
			if (isLocalResource(rep.getLocation())) {

				// Get blob and output stream
				BlobObject bo = oCon.getBlobObject(rep.getBlobKey().toString());
				out = bo.openOutputStream();

				File source = new File(rep.getLocation());
				in = new FileInputStream(source);

				byte[] buffer = new byte[1024];
				int len = in.read(buffer);
				while (len != -1) {
					out.write(buffer, 0, len);
					len = in.read(buffer);
				}
			} else {
				rep.setBlobStatus(BlobStatus.REMOTE);
			}

		} catch (Exception e) {
			rep.setBlobStatus(BlobStatus.ADD_FAIL);
			e.printStackTrace();
			throw new RepositoryException(e.getMessage());
		} finally {
			if (null != out)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (null != in)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

	public String sparqlQuery(String queryString) {

		String ret = "";
		OutputStream outStream = null;

		try {

			// TupleQueryResultWriterRegistry

			// prepare the query
			// String queryString = "SELECT * WHERE {?s ?p ?o . }";
			TupleQuery query = oCon.prepareTupleQuery(QueryLanguage.SPARQL, queryString);

			// open a file to write the result to it in JSON format
			outStream = new ByteArrayOutputStream();
			TupleQueryResultHandler writer = new SPARQLResultsJSONWriter(outStream);

			// execute the query and write the result directly to file
			query.evaluate(writer);

			if (null != outStream) {
				ret = outStream.toString();
			}

		} catch (MalformedQueryException e) {
			ret = "MalformedQueryException :: " + e.getCause().toString() + " :: " + e.getMessage();
			e.printStackTrace();
		} catch (RepositoryException e) {
			ret = "RepositoryException :: " + e.getCause().toString() + " :: " + e.getMessage();
			;
		} catch (TupleQueryResultHandlerException e) {
			ret = "TupleQueryResultHandlerException :: " + e.getCause().toString() + " :: " + e.getMessage();
			;
		} catch (QueryEvaluationException e) {
			ret = "QueryEvaluationException :: " + e.getCause().toString() + " :: " + e.getMessage();
			;
		} finally {

			if (null != outStream) {
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return ret;

	}




	@SuppressWarnings("unused")
	public String convertResults2Json(QueryResult qr) {

		//
		// Declare Json Objects to be built
		//
		JSONArray  measurementArray = new JSONArray();
		String jsonresults = "";
		Mpoint point = new Mpoint();
		Measurement measurement = new Measurement();

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

	public String influxQuery(String queryString) {

		String ret = "";

		if (useIdb) {
			InfluxDB influxDB = InfluxDBFactory.connect(idbUrl, idbUsername, idbPassword);
			String dbName = idbDB;
			// Query query = new Query("select time_idle from cpu limit 10",
			// dbName);
			Query query = new Query(queryString, dbName);
			//
			// Unless TimeUnit parameter is used in query, Epoch time will not
			// be returned.
			//
			QueryResult qr = influxDB.query(query, TimeUnit.SECONDS);
			if (qr.hasError()) {
				ret = qr.getError();
			} else {
				ret = convertResults2Json(qr);
			}
		} else {
			ret = "{ \"INFO\": \"idb server disabled in Proven configuration\" }";
		}
		return ret;

	}
	
	public Map<String,Boolean> initSelectKeyMap(List<String> keys) {
		Map<String,Boolean> keyMap = new HashMap();
        for (String key : keys) {
        	keyMap.put(key, false);
        }
		return keyMap;
		
	}
	
	
	public Boolean isKey(List<Object> keyList, String selectKey) {
		Boolean ret = false;

			for (Object foundKey : keyList ) {
				String foundKeyStr = foundKey.toString();
				if (selectKey.equalsIgnoreCase(foundKeyStr) ) {
					ret = true;
				}
			}

		
		return ret;
		
	}

	public Map<String,Boolean> buildSelectKeyMap(String queryStr, Map<String, Boolean> selectKeys) {
		Map<String, Boolean> ret = selectKeys;

		if (useIdb) {
			InfluxDB influxDB = InfluxDBFactory.connect(idbUrl, idbUsername, idbPassword);
			String dbName = idbDB;
			// Query query = new Query("select time_idle from cpu limit 10",
			// dbName);
			Query query = new Query(queryStr, dbName);
			//
			// Unless TimeUnit parameter is used in query, Epoch time will not
			// be returned.
			//
			QueryResult qr = influxDB.query(query, TimeUnit.SECONDS);
			if (!qr.hasError()) {
				List<Result> lr = qr.getResults(); 
				for (Result l : lr) {
					List<Series> sl = l.getSeries();
					for (Series s : sl) {
						List<List<Object>> so = s.getValues();
						for (List<Object> lo : so) {
						    for (Map.Entry<String, Boolean> entry : selectKeys.entrySet()) {
						        if (isKey(lo, entry.getKey())) {
						        	entry.setValue(true);
						    }
							
//							for (Object ov : lo ) {
//								String ovs = ov.toString();
//								if (key.equalsIgnoreCase(ovs) ) {
//									ret = true;
//								}
							}

						}
					}

				}
			}
		}
		ret = selectKeys;
		return ret;
	}
	
	public String checkInfluxKeys(String measurement, List<String> selectKeys ) {

        String fields = " field ";
        String keyS = " keys ";
        String from = " from ";
        String tags = "tag";
        String query_prefix = "show " ;
        String responseString = "";
        Map <String,Boolean> selectKeyStatus = initSelectKeyMap(selectKeys);
        
        String queryString = query_prefix + fields + keyS + from + measurement;
        selectKeyStatus = buildSelectKeyMap(queryString, selectKeyStatus);
        queryString = query_prefix + tags + keyS + from + measurement;
        selectKeyStatus = buildSelectKeyMap(queryString, selectKeyStatus);        
        
//		if (useIdb) {
//			InfluxDB influxDB = InfluxDBFactory.connect(idbUrl, idbUsername, idbPassword);
//			String dbName = idbDB;
//			// Query query = new Query("select time_idle from cpu limit 10",
//			// dbName);
//			Query query = new Query(queryFieldString, dbName);
//			//
//			// Unless TimeUnit parameter is used in query, Epoch time will not
//			// be returned.
//			//
//			QueryResult qr = influxDB.query(query, TimeUnit.SECONDS);
//			if (qr.hasError()) {
//				responseString = qr.getError().toString();
//			} else {
//				List<Result> lr = qr.getResults(); 
//			    for (Result l : lr) {
//			    	List<Series> sl = l.getSeries();
//			    	for (Series s : sl) {
//			    	  List<List<Object>> so = s.getValues();
//			    	  for (List<Object> lo : so) {
//			    		  for (Object ov : lo ) {
//			    			  String ovs = ov.toString();
//			    			  if (keyname.equalsIgnoreCase(ovs) ) {
//			    				  ret = true;
//			    			  }
//			    		  }
//			    		  
//			    	  }
//			    	}
//			          
//			    }
//			}
//		}
        for (Map.Entry<String, Boolean> entry : selectKeyStatus.entrySet()) {
            if (entry.getValue() == false) {
            	if (responseString.length() == 0) {
            		responseString = "The following key(s) are not found in measurement " + measurement + ":\n";
            	}
            	responseString = responseString + entry.getKey();
            }
        }
		return responseString;

	}
	
	//
	// Utility used to pad an epoch number string with zeros
	//
	public static String padRightZeros(String s, int n) {
		s = String.format("%1$-" + n + "s", s);
		s = s.replace(' ', '0');
		return s;
	}

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

	private Map<String, Integer> countFilterFields(List<ProvenQueryFilter> filters) {
		Map<String, Integer> filterFieldCounter = new HashMap();
		if (filters != null) {
			int index = 0;			
			while (index < filters.size()) {
				ProvenQueryFilter filter = filters.get(index);
				String localKey = filters.get(index).getField();
				if (!filterFieldCounter.containsKey(localKey)) {
					filterFieldCounter.put(localKey, 1);
				} else {
					Integer currentCount = filterFieldCounter.get(localKey);
					filterFieldCounter.replace(localKey, currentCount + 1);
				}
				index = index + 1;
			}
		}
		return filterFieldCounter;
	}

	private String formatFilterCriteria(ProvenQueryFilter filter) {
		String statement = "";
		String space = " ";
		String eq = "=";
		String quote = "'";

		if (filter.getDatatype() == null) {

			statement = filter.getField() + space + eq + quote + filter.getValue() + quote;
		} else {
			if (filter.getDatatype().equalsIgnoreCase(MetricValueType.Integer.toString())) {
				statement = filter.getField() + space + eq + Integer.parseInt(filter.getValue());
			} else if (filter.getDatatype().equalsIgnoreCase(MetricValueType.Long.toString())) {
				statement = filter.getField() + space + eq + Long.parseLong(filter.getValue());
			} else if (filter.getDatatype().equalsIgnoreCase(MetricValueType.Float.toString())) {
				statement = filter.getField() + space + eq + Float.parseFloat(filter.getValue());
			} else if (filter.getDatatype().equalsIgnoreCase(MetricValueType.Double.toString())) {
				statement = filter.getField() + space + eq + Double.parseDouble(filter.getValue());
			} else
				statement = filter.getField() + space + eq + quote + filter.getValue() + quote;
		}
		return statement;

	}

	private String assembleUnionFilterStatement(List<ProvenQueryFilter> filters, String fieldName, Integer fieldCounter) {
		String unionStatement = "";
		int index = 0;
		while (index < filters.size()) {

			if (filters.get(index).getField().equalsIgnoreCase(fieldName)) {
				if (unionStatement.length() == 0) {
					unionStatement = " ( " + formatFilterCriteria(filters.get(index));
				} else {
					unionStatement = unionStatement + " or " + formatFilterCriteria(filters.get(index));
				}
			}

			index = index + 1;

		}
		if (unionStatement.length() != 0) {
			unionStatement = unionStatement + " ) ";
		}
		return unionStatement;

	}


	private List<String> assembleFilterStatements(List<ProvenQueryFilter> filters,
			Map<String, Integer> filterFieldCounter) {

		List<String> assembledFilterStatements = new ArrayList<>();

		Iterator iter = filterFieldCounter.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Integer counterIndex = (Integer)entry.getValue();
			if (counterIndex == 1) {

				int index = 0;
				String filter_statement = "";
				while (index < filters.size()) {

					if (filters.get(index).getField().equalsIgnoreCase((String)entry.getKey())) {
						if (filters.get(index).getField().toLowerCase().contains("starttime")
								|| filters.get(index).getField().toLowerCase().contains("endtime")) {
							//							assembledFilterStatements.add(timeFilterStatement(filters.get(index).getField(), filters.get(index).getValue()));
							assembledFilterStatements.add(timeFilterStatement(filters.get(index).getField(), filters.get(index).getValue()));
						} else {

							assembledFilterStatements.add(formatFilterCriteria(filters.get(index)));					
						}

						break;

					} 
					index = index + 1;
				}
			} else {
				assembledFilterStatements.add(assembleUnionFilterStatement(filters, (String) entry.getKey(), (Integer) counterIndex));
				// 
				//				int index = 0;
				//				String unionStatement = "";
				//				while (index < filters.size()) {
				//
				//					if (filters.get(index).getField().equalsIgnoreCase((String)entry.getKey())) {
				//						if (unionStatement.length() == 0) {
				//							unionStatement = " ( " + assembledFilterStatements.add(formatFilterCriteria(filters.get(index)));
				//						} else {
				//							unionStatement = unionStatement + " or " + formatFilterCriteria(filters.get(index));
				//						}
				//					}
				//					if (unionStatement.length() != 0) {
				//						assembledFilterStatements.add(unionStatement + " ) ");
				//					}
				//					index = index + 1;
				//
			}
		}

		return assembledFilterStatements;
	}


	public ProvenMessageResponse influxQuery(ProvenMessage query) throws InvalidProvenMessageException {

		ProvenMessageResponse pmr = influxQuery(query, false, false, false);
		return pmr;

	}


	public ProvenMessageResponse debugInfluxQuery (ProvenMessage query) throws InvalidProvenMessageException {
		ProvenMessageResponse pmr = influxQuery(query, false, true, false);
		return pmr;
	}

	public ProvenMessageResponse pingInfluxQuery (ProvenMessage query) throws InvalidProvenMessageException {
		ProvenMessageResponse pmr = influxQuery(query, false, true, true);
		return pmr;
	}

	
	
	public String formatComplexFilterCriteria (String measurement, JSONArray filterArray) {

		    String ge = " >= ";
		    String gt = " > ";
		    String and = " and ";
		    String eq = " == ";
		    String lt = " < ";
		    String le = " <= ";
			String filterstatement = "";
			String space = " ";
			String quote = "'";
			String key = "";
			String filterFragment = "";
			String operator = "";
			Object operatorValue = new Object();
			JSONObject filterJsonObject;
			List<String> filterList = new ArrayList();
			for (int index = 0; filterArray.size() < 1; index ++) {
				Object filterObject = filterArray.get(index);
				if (filterObject instanceof JSONObject) {
					filterJsonObject = (JSONObject) filterObject;
					Set keys = filterJsonObject.keySet();
					Iterator iter = keys.iterator();
					while (iter.hasNext()) {
						if (key.equalsIgnoreCase("key")) {
							key = (String) iter.next();
						} else if (key.equalsIgnoreCase("eq")) {
							operator = eq;
							operatorValue = (Object) filterJsonObject.get("eq");
						} else if (key.equalsIgnoreCase("ge")) {
							operator = ge;
							operatorValue = (Object) filterJsonObject.get("ge");

						} else if (key.equalsIgnoreCase("gt")) {
							operator = gt;
							operatorValue = (Object) filterJsonObject.get("gt");

						} else if (key.equalsIgnoreCase("lt")) {
							operator = lt;
							operatorValue = (Object) filterJsonObject.get("lt");

						} else if (key.equalsIgnoreCase("le")) {
							operator = le;
							operatorValue = (Object) filterJsonObject.get("le");
						}
					}
				}
				if (operatorValue.getClass() == Integer.class) {
					filterFragment = key + operator + ((Integer) operatorValue).toString();
					
				} else if (operatorValue.getClass() == Float.class) {
					filterFragment = key + operator + ((Float) operatorValue).toString();
					
				} else if (operatorValue.getClass() == Double.class) {
					filterFragment = key + operator + ((Double) operatorValue).toString();
				} else if (operatorValue.getClass() == Long.class ) {
					filterFragment = key + operator + ((Long) operatorValue).toString();
					
				} else if (operatorValue.getClass() == String.class) {
					filterFragment = key + operator + "\""+ (String) operatorValue + "\"";					
				}
				
				filterList.add(filterFragment);
				
				
			}
			int size = filterList.size();
			if (size == 0) {
				return filterstatement;
			} else if (size == 1) {
				filterstatement = filterList.get(0);
				return filterstatement;
			} else {
			
            for (int index = 0; index < filterList.size()  ; index++) {
               if (index == 0) {
   				filterstatement = filterList.get(0);            	   
               } else {
           		filterstatement = filterstatement + and + filterList.get(index);
               }
            	
            }
            }

		return filterstatement;
	}
	public ProvenMessageResponse getComplexQuery(String queryJson, String measurement, boolean returnCsvFlag, boolean returnQueryStatement, boolean pingQuery) throws InvalidProvenMessageException {

		ProvenMessageResponse ret = null;
		String ge = " >= ";
		String gt = " > ";
		String and = " and ";
		String eq = " == ";
		String lt = " < ";
		String le = " <= ";
		String comma = " , ";
		String from = " from ";
		String select = " select ";
		String selectCriteriaStr = "";


		JSONParser parser = new JSONParser();

		Object filterObject = null;
		List<String> selectCriteria = new ArrayList(); 

		try {
			filterObject = parser.parse(queryJson);

		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			ret = new ProvenMessageResponse();
			ret.setStatus(Status.BAD_REQUEST);
			ret.setReason("Invalid or missing message content type.  Could not parse JSON message.");
			ret.setCode(Status.BAD_REQUEST.getStatusCode());
			ret.setResponse("{ \"ERROR\": \"Bad request made to time-series database.\" }");
			e.printStackTrace();
			return ret;
		}    


		if (filterObject instanceof JSONObject) {

			JSONObject fObject = (JSONObject) filterObject;
			JSONArray  filterArray = (JSONArray) fObject.get("queryFilter");
			JSONArray  selectCriteriaArray = (JSONArray) fObject.get("selectCriteria");

			for (int sindex = 0; sindex < selectCriteriaArray.size(); sindex++) {
				Object keyObject = selectCriteria.get(sindex);
				selectCriteria.add((String) keyObject);
			}

			String responseCheck = checkInfluxKeys(measurement, selectCriteria );
			if (responseCheck.length() != 0) {

				ret = new ProvenMessageResponse();
				ret.setStatus(Status.BAD_REQUEST);
				ret.setReason("Keys not currently in database.    " + responseCheck);
				ret.setCode(Status.BAD_REQUEST.getStatusCode());
				ret.setResponse("{ \"ERROR\": \"Bad request made to time-series database.\" }");
				return ret;

			} else {

				int size = selectCriteria.size();
				if (size == 0) {

					selectCriteriaStr = "*";
				} else if (size == 1) {

					selectCriteriaStr = selectCriteria.get(0);
				} else {

					for (int index = 0; index < size; index++) {

						if (index == 0) {
							selectCriteriaStr = selectCriteria.get(0);
						} else {
							selectCriteriaStr = selectCriteriaStr + " , " + (String)selectCriteria.get(index);

						}

					}



				}



			}
			String filterStr = formatComplexFilterCriteria(measurement, filterArray);
			String queryStatement = selectCriteriaStr + from + measurement + filterStr;



			String response = "";
			String reason = "";

			if (useIdb) {
				InfluxDB influxDB = InfluxDBFactory.connect(idbUrl, idbUsername, idbPassword);
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

				QueryResult qr = null;
				try {

					qr = influxDB.query(influxQuery, TimeUnit.SECONDS);

				} catch (OutOfMemoryError e) {
					ret = new ProvenMessageResponse();
					ret.setReason("Out of memory");
					ret.setStatus(Status.BAD_REQUEST);
					ret.setCode(Status.BAD_REQUEST.getStatusCode());
					ret.setResponse("{ \"ERROR\": \"Query results are to large to return.  Out of memory\" } ");

				}

				if (qr.hasError()) {
					ret = new ProvenMessageResponse();
					ret.setReason("Invalid or missing content.");
					ret.setStatus(Status.BAD_REQUEST);
					ret.setCode(Status.BAD_REQUEST.getStatusCode());
					ret.setResponse("{ \"ERROR\": \"Bad request made to time-series database.\" } " + qr.getError());

				} else {
					ret = new ProvenMessageResponse();
					ret.setReason("Success");
					ret.setStatus(Status.OK);
					ret.setCode(Status.OK.getStatusCode());
					ret.setResponse("Query Statement=" + queryStatement + "\n" + ret.getResponse() );
					try { 

						if (returnCsvFlag) {
							ret.setResponse(convertResults2Csv2(qr));
						} else {
							ret.setResponse(convertResults2Json(qr));					
						}

					} catch (OutOfMemoryError e) {
						ret = new ProvenMessageResponse();
						ret.setReason("Out of memory");
						ret.setStatus(Status.BAD_REQUEST);
						ret.setCode(Status.BAD_REQUEST.getStatusCode());
						ret.setResponse("{ \"ERROR\": \"Query results successfully returned but were to large to postprocess.  Out of memory\" } ");

					}




				}
			} else {

				ret = new ProvenMessageResponse();
				ret.setReason("Time-series database unavailable or database adapter is disabled.");
				ret.setStatus(Status.SERVICE_UNAVAILABLE);
				ret.setCode(Status.SERVICE_UNAVAILABLE.getStatusCode());
				ret.setResponse("{ \"INFO\": \"idb server disabled in Proven configuration\" }");
			}
			if (returnQueryStatement) {
				ret.setResponse("Query Statement=" + queryStatement + "\n" + ret.getResponse() );

			}
			// ret.setReason(reason);
			// ret.setResponse(response);
		}
		return ret;


	}
	
	public ProvenMessageResponse deprecated_influxComplexQuery(ProvenMessage query, boolean returnCsvFlag, boolean returnQueryStatement, boolean pingQuery) throws InvalidProvenMessageException {
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
			Map<String,Integer> filterFieldCounter = countFilterFields(filters);
			List <String> assembledFilterStatements = assembleFilterStatements(filters, filterFieldCounter);
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

		String response = "";
		String reason = "";

		if (useIdb) {
			InfluxDB influxDB = InfluxDBFactory.connect(idbUrl, idbUsername, idbPassword);
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

			QueryResult qr = null;
			try {

				qr = influxDB.query(influxQuery, TimeUnit.SECONDS);

			} catch (OutOfMemoryError e) {
				ret = new ProvenMessageResponse();
				ret.setReason("Out of memory");
				ret.setStatus(Status.BAD_REQUEST);
				ret.setCode(Status.BAD_REQUEST.getStatusCode());
				ret.setResponse("{ \"ERROR\": \"Query results are to large to return.  Out of memory\" } ");

			}

			if (qr.hasError()) {
				ret = new ProvenMessageResponse();
				ret.setReason("Invalid or missing content.");
				ret.setStatus(Status.BAD_REQUEST);
				ret.setCode(Status.BAD_REQUEST.getStatusCode());
				ret.setResponse("{ \"ERROR\": \"Bad request made to time-series database.\" } " + qr.getError());

			} else {
				ret = new ProvenMessageResponse();
				ret.setReason("Success");
				ret.setStatus(Status.OK);
				ret.setCode(Status.OK.getStatusCode());
				ret.setResponse("Query Statement=" + queryStatement + "\n" + ret.getResponse() );
				try { 

					if (returnCsvFlag) {
						ret.setResponse(convertResults2Csv2(qr));
					} else {
						ret.setResponse(convertResults2Json(qr));					
					}

				} catch (OutOfMemoryError e) {
					ret = new ProvenMessageResponse();
					ret.setReason("Out of memory");
					ret.setStatus(Status.BAD_REQUEST);
					ret.setCode(Status.BAD_REQUEST.getStatusCode());
					ret.setResponse("{ \"ERROR\": \"Query results successfully returned but were to large to postprocess.  Out of memory\" } ");

				}




			}
		} else {

			ret = new ProvenMessageResponse();
			ret.setReason("Time-series database unavailable or database adapter is disabled.");
			ret.setStatus(Status.SERVICE_UNAVAILABLE);
			ret.setCode(Status.SERVICE_UNAVAILABLE.getStatusCode());
			ret.setResponse("{ \"INFO\": \"idb server disabled in Proven configuration\" }");
		}
		if (returnQueryStatement) {
			ret.setResponse("Query Statement=" + queryStatement + "\n" + ret.getResponse() );

		}
		// ret.setReason(reason);
		// ret.setResponse(response);
		return ret;

	}



	//
	// New write measurement routine
	//
	public ProvenMessageResponse influxWriteMeasurements(Collection<ProvenMeasurement> measurements) {
		ProvenMessageResponse ret = null;
		if (useIdb) {

			// OLD Long startTime = System.currentTimeMillis();
			InfluxDB influxDB = InfluxDBFactory.connect(idbUrl, idbUsername, idbPassword);
			// influxDB.enableBatch(BatchOptions.DEFAULT_BATCH_ACTIONS_LIMIT,
			// BatchOptions.DEFAULT_BATCH_INTERVAL_DURATION, TimeUnit.SECONDS);
			influxDB.enableBatch(20000, 20, TimeUnit.SECONDS);
			for (ProvenMeasurement measurement : measurements) {

				Set<ProvenMetric> pms = measurement.getMetrics();

				if (measurement.getTimestamp() == null) {
					ret = new ProvenMessageResponse();
					ret.setStatus(Status.BAD_REQUEST);
					ret.setReason("Invalid or missing message content type.  Measurement timestamp missing.");
					ret.setCode(Status.BAD_REQUEST.getStatusCode());
					ret.setResponse("{ \"ERROR\": \"Bad request made to time-series database.\" }");
					return ret;
				}
				Point.Builder builder = Point.measurement(measurement.getMeasurementName())
						.time(measurement.getTimestamp(), TimeUnit.SECONDS);

				for (ProvenMetric pm : pms) {

					if (pm.isMetadata()) {

						builder.tag(pm.getLabel(), pm.getValue());

						// System.out.println("TAG");
						// System.out.println("------------------------------");
						// System.out.println(pm.getLocalMetricName());
						// System.out.println(pm.getLabelMetricValue());
						// System.out.println("------------------------------");

					} else {

						try {
							if (pm.getValueType().equals(MetricValueType.Integer)) {
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
							}
						} catch (NumberFormatException e) {
							builder.addField(pm.getLabel(), pm.getValue());
						}

						// System.out.println("FIELD");
						// System.out.println("------------------------------");
						// System.out.println(pm.getLocalMetricName());
						// System.out.println(pm.getLabelMetricValue());
						// System.out.println("------------------------------");

					}

				}

				//
				// Add semantic links
				//
				builder.tag("hasProvenMessage", measurement.getProvenMessage().toString());
				builder.tag("hasMeasurement", measurement.getProvenMessageMeasurement().toString());

				// System.out.println("------------------------------");
				// System.out.println("------------------------------");

				influxDB.write(idbDB, idbRP, builder.build());
				ret = new ProvenMessageResponse();
				ret.setReason("success");
				ret.setStatus(Status.CREATED);
				ret.setCode(Status.CREATED.getStatusCode());
				ret.setResponse("{ \"INFO\": \"Time-series measurements successfully created.\" }");
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
	public String detectObjectType(Object messageObject) {
		String objectType = "";
		if (messageObject instanceof JSONObject) {

			JSONObject mObject = (JSONObject) messageObject;
			JSONObject object = (JSONObject) (mObject.get("message"));
			if (object != null) {
				objectType = "O";

			} else {
				object = (JSONObject) mObject.get("input");
				if (object != null) {
					objectType = "I";
				}
			}
			//
			// If it isn't a JSONObject, assume that it is a JSONArray representing Alarms
			//
		} else {
			objectType = "A";
		} 


		return objectType;

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
			
			
			
//  Replaced April 19, 2021			
//		} else if (fdobject.get(fdkey) instanceof Integer) {
//		    builder.addField( fdkey, Float.valueOf(fdobject.get(fdkey).toString()));
//    	} else if (fdobject.get(fdkey) instanceof Long) {
//		    builder.addField(fdkey, Double.valueOf(fdobject.get(fdkey).toString()));
//		} else if (fdobject.get(fdkey) instanceof Float) {
//			builder.addField(fdkey, Float.valueOf((Float) fdobject.get(fdkey)));
//		} else if (fdobject.get(fdkey) instanceof Double) {
//			builder.addField(fdkey, Double.valueOf((Double) fdobject.get(fdkey)));
//		} else if (fdobject.get(fdkey) instanceof JSONObject) {
//
//			JSONObject iObject = (JSONObject)fdobject.get(fdkey);
//			builder = processNestedObject(iObject, fdkey,  builder);
//
//		}			
			
			   

			
			if (iObject.get(iokey) instanceof String) {
				
				if (iokey.equalsIgnoreCase("object")) {
				    ret.tag(iokey, (String) iObject.get(iokey));						
				} else {
				    ret.addField(iokey, (String) iObject.get(iokey));
				}
				ret.addField(iokey, (String) iObject.get(iokey));
			} else if (iObject.get(iokey) instanceof Integer) {
				ret.addField(iokey, Float.valueOf( iObject.get(iokey).toString()));
			} else if (iObject.get(iokey) instanceof Long) {
				ret.addField(iokey, Double.valueOf(iObject.get(iokey).toString()));
			} else if (iObject.get(iokey) instanceof Float) {
				ret.addField(iokey, Float.valueOf(iObject.get(iokey).toString()));
			} else if (iObject.get(iokey) instanceof Double) {
				ret.addField(iokey, Double.valueOf(iObject.get(iokey).toString()));
			} else 	if  (iObject.get(iokey) instanceof JSONObject) {
				JSONObject i2Object = (JSONObject) iObject.get(iokey);
				ret = processNestedObject (i2Object, iokey, ret);  
			}			 
						 
			 
		}

		return ret;

	}


	private ProvenMessageResponse influxWriteSimulationInput(JSONObject commandObject, InfluxDB influxDB,
			String measurementName, String instanceId,  Long currentTime) {
		ProvenMessageResponse ret = null;
		Long timestamp = (long) -1;
		String differenceMrid = null;
		JSONArray forwardDifferenceArray = null;
		JSONArray reverseDifferenceArray = null;
		JSONObject inputObject = (JSONObject) commandObject.get("input");
		String simulationid = "";
		if (inputObject.get("simulation_id") != null) {
			simulationid = inputObject.get("simulation_id").toString();
		}

		if ((simulationid.equalsIgnoreCase(""))) {
			ret = new ProvenMessageResponse();
			ret.setStatus(Status.BAD_REQUEST);
			ret.setReason("Invalid or missing message content type.  Simulation id missing.");
			ret.setCode(Status.BAD_REQUEST.getStatusCode());
			ret.setResponse("{ \"ERROR\": \"Bad request made to time-series database.\" }");
			return ret;
		}

		JSONObject object = (JSONObject) inputObject.get("message");
		@SuppressWarnings("unchecked")
		Set<String> keys = object.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (key.equalsIgnoreCase("timestamp")) {
				if (object.get(key) instanceof Long) {
					timestamp = (Long) object.get("timestamp");					
				} else if (object.get(key) instanceof String) {
					if (((String)object.get(key)).matches("-?\\d+(.\\d+)?")) {
						timestamp = Long.parseLong((String)object.get(key));

					}

				}
				if (timestamp == -1) {
					ret = new ProvenMessageResponse();
					ret.setStatus(Status.BAD_REQUEST);
					ret.setReason("Invalid or missing message content type.  Invalid (non-numeric epoch value) or missing measurement timestamp.");
					ret.setCode(Status.BAD_REQUEST.getStatusCode());
					ret.setResponse("{ \"ERROR\": \"Bad request made to time-series database.\" }");
					return ret;
				}

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


		if ((forwardDifferenceArray == null) && (reverseDifferenceArray == null)) {
			ret = new ProvenMessageResponse();
			ret.setStatus(Status.BAD_REQUEST);
			ret.setReason("Invalid or missing message content type. The forwarDifferenceArray or reverseDifferenceArray measurements are missing.");
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
					if (fdkey.equalsIgnoreCase("object")) {
					    builder.tag(fdkey, (String) fdobject.get(fdkey));						
					} else {
					    builder.addField(fdkey, (String) fdobject.get(fdkey));
					}
//
//
//
// Changed March 30, 2021 to change any numeric field into a float or double
// 
//				} else if (fdobject.get(fdkey) instanceof Integer) {
//					builder.addField(fdkey, Integer.valueOf((Integer) fdobject.get(fdkey)));
//				} else if (fdobject.get(fdkey) instanceof Long) {
//					builder.addField(fdkey, Long.valueOf((Long) fdobject.get(fdkey)));
				

// Changed April 19, 2021 to change any numeric field into a float or double
//					} else if (fdobject.get(fdkey) instanceof Integer) {
//					builder.addField(fdkey, Float.valueOf((Float)fdobject.get(fdkey)));
//				} else if (fdobject.get(fdkey) instanceof Long) {
//					builder.addField(fdkey, Double.valueOf((Double) fdobject.get(fdkey)));
//				} else if (fdobject.get(fdkey) instanceof Float) {
//					builder.addField(fdkey, Float.valueOf((Float) fdobject.get(fdkey)));
//				} else if (fdobject.get(fdkey) instanceof Double) {
//					builder.addField(fdkey, Double.valueOf((Double) fdobject.get(fdkey)));
//				} else 	if  (fdobject.get(fdkey) instanceof JSONObject) {
//
//					JSONObject iObject = (JSONObject)fdobject.get(fdkey);
//					builder = processNestedObject(iObject, fdkey,  builder);
//
//				}
					
				} else if (fdobject.get(fdkey) instanceof Integer) {
				    builder.addField( fdkey, Float.valueOf(fdobject.get(fdkey).toString()));
		    	} else if (fdobject.get(fdkey) instanceof Long) {
				    builder.addField(fdkey, Double.valueOf(fdobject.get(fdkey).toString()));
				} else if (fdobject.get(fdkey) instanceof Float) {
					builder.addField(fdkey, Float.valueOf((Float) fdobject.get(fdkey)));
				} else if (fdobject.get(fdkey) instanceof Double) {
					builder.addField(fdkey, Double.valueOf((Double) fdobject.get(fdkey)));
				} else if (fdobject.get(fdkey) instanceof JSONObject) {

					JSONObject iObject = (JSONObject)fdobject.get(fdkey);
					builder = processNestedObject(iObject, fdkey,  builder);

				}
					


			}
			
					
			
			
			try {
				influxDB.write(idbDB, idbRP, builder.build());
			} catch (Exception e) {
				ret =  new ProvenMessageResponse();
				ret.setReason("error");
				ret.setStatus(Status.BAD_REQUEST);
				ret.setCode(Status.BAD_REQUEST.getStatusCode());
				ret.setResponse("{ \"ERROR\": \"Error interpreting measurement, possibly malformed JSON or no fields in measurement, command not recorded.\" }");
				return ret;

			}
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
			if (currentTime != null) {
			    builder.addField("realtime", currentTime);
			}
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
					
					if (rdkey.equalsIgnoreCase("object")) {
					    builder.tag(rdkey, (String) rdobject.get(rdkey));						
					} else {
					    builder.addField(rdkey, (String) rdobject.get(rdkey));
					}
					builder.addField(rdkey, (String) rdobject.get(rdkey));
				} else if (rdobject.get(rdkey) instanceof Integer) {
					builder.addField(rdkey, Float.valueOf(rdobject.get(rdkey).toString()));
				} else if (rdobject.get(rdkey) instanceof Long) {
					builder.addField(rdkey, Double.valueOf(rdobject.get(rdkey).toString()));
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

	private ProvenMessageResponse influxWriteSimulationOutput(JSONObject messageObject, InfluxDB influxDB,
			String measurementName, String instanceId, Long currentTime) {

		ProvenMessageResponse ret = null;
		Long timestamp = (long) -1;
		boolean hasMeasurementObject = false;
		//		boolean hasMeasurementArray = false;
		String measurementArrayKey = null;
		String simulationid = "";
		if (messageObject.get("simulation_id") != null) {
			simulationid = messageObject.get("simulation_id").toString();
		}

		if ((simulationid.equalsIgnoreCase(""))) {
			ret = new ProvenMessageResponse();
			ret.setStatus(Status.BAD_REQUEST);
			ret.setReason("Invalid or missing message content type.  Simulation id missing.");
			ret.setCode(Status.BAD_REQUEST.getStatusCode());
			ret.setResponse("{ \"ERROR\": \"Bad request made to time-series database.\" }");
			return ret;
		}

		JSONObject messageContentsObject = (JSONObject) messageObject.get("message");
		@SuppressWarnings("unchecked")
		Set<String> messageContent_keys = messageContentsObject.keySet();

		if (messageContentsObject.get("timestamp") instanceof Long) {
			timestamp = (Long) messageContentsObject.get("timestamp");					
		} else if (messageContentsObject.get("timestamp") instanceof String) {
			if (((String)messageContentsObject.get("timestamp")).matches("-?\\d+(.\\d+)?")) {
				timestamp = Long.parseLong((String)messageContentsObject.get("timestamp"));
			}

		} 

		if (timestamp == -1) {
			ret = new ProvenMessageResponse();
			ret.setStatus(Status.BAD_REQUEST);
			ret.setReason("Invalid or missing message content type.  Invalid (non-numeric epoch value) or missing measurement timestamp.");
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
				if (currentTime != null) {
				    builder.addField("realtime", currentTime);
				}

				// Add instanceId tag, if any
				if (null != instanceId) {
					// 02/03/2020					builder.tag("instance_id", instanceId);
					builder.addField("instance_id", instanceId);
				}

				// Add OUTPUT tag
				builder.tag("hasSimulationMessageType", "OUTPUT");

				while (record_it.hasNext()) {
					String record_key = record_it.next();

					if (record.get(record_key) instanceof String) {
						if (record_key.equalsIgnoreCase("measurement_mrid")) {
							builder.tag(record_key, (String) record.get(record_key));
							// 05/12/2020 builder.addField(record_key, (String) record.get(record_key));
						} else {
							// 02/03/2020							builder.tag(record_key, (String) record.get(record_key));
							builder.addField(record_key, (String) record.get(record_key));
						}
// Changed March 30, 2021
// Gridapps-D wanted all numeric data to be either Float or Double.
//						
//					} else if (record.get(record_key) instanceof Integer) {
//						builder.addField( record_key, Integer.valueOf((Integer) record.get(record_key)));
//					} else if (record.get(record_key) instanceof Long) {
//						builder.addField(record_key, Long.valueOf((Long) record.get(record_key)));						
					} else if (record.get(record_key) instanceof Integer) {
						builder.addField( record_key, Float.valueOf(record.get(record_key).toString()));
					} else if (record.get(record_key) instanceof Long) {
						builder.addField(record_key, Double.valueOf(record.get(record_key).toString()));
					} else if (record.get(record_key) instanceof Float) {
						builder.addField(record_key, Float.valueOf((Float) record.get(record_key)));
					} else if (record.get(record_key) instanceof Double) {
						builder.addField(record_key, Double.valueOf((Double) record.get(record_key)));
					}
				}
				try {
					influxDB.write(idbDB, idbRP, builder.build());
					ret = new ProvenMessageResponse();
					ret.setReason("success");
					ret.setStatus(Status.CREATED);
					ret.setCode(Status.CREATED.getStatusCode());
					ret.setResponse("{ \"INFO\": \"Time-series measurements successfully created.\" }");
				} catch (Exception e) {
					ret =  new ProvenMessageResponse();
					ret.setReason("error");
					ret.setStatus(Status.BAD_REQUEST);
					ret.setCode(Status.BAD_REQUEST.getStatusCode());
					ret.setResponse("{ \"ERROR\": \"Error interpreting measurement, possibly malformed JSON or no fields in measurement, output not recorded.\" }");
					return ret;

				}

			}
		}
		return ret;

	}
	

	private ProvenMessageResponse influxWriteAlarms(JSONArray messageObject, InfluxDB influxDB,
			String measurementName, String instanceId, String simulationId, Long currentTime) {

		ProvenMessageResponse ret = null;
		
		Long timestamp = -1L;  

		Iterator<JSONObject> iterator = messageObject.iterator();
		
		while (iterator.hasNext()) {
			//System.out.println(iterator.next());
			JSONObject record = (JSONObject) iterator.next();         
			Set<String> record_keys = record.keySet();
			Iterator<String> record_it = record_keys.iterator();
            Iterator<String> timestamp_it = record_keys.iterator();
            
			while (timestamp_it.hasNext()) {
				String record_key = timestamp_it.next();
                if (record_key.equalsIgnoreCase("timestamp")) {
                	if (record.get(record_key) instanceof String) {
                		
                	    String  timestamp_str = (String) record.get(record_key);
						if (timestamp_str.matches("-?\\d+(.\\d+)?")) {
							timestamp = Long.parseLong(timestamp_str);
							break;
                	    } 
                	} else if  (record.get(record_key) instanceof Long) {
                		    timestamp = Long.valueOf((Long) record.get(record_key));
                		    break;
                	}
                }
           
             }
			
			if (timestamp == -1) {
				ret = new ProvenMessageResponse();
				ret.setStatus(Status.BAD_REQUEST);
				ret.setReason("Invalid or missing message content type.  Invalid (non-numeric epoch value) or missing measurement timestamp.");
				ret.setCode(Status.BAD_REQUEST.getStatusCode());
				ret.setResponse("{ \"ERROR\": \"Bad request made to time-series database.\" }");
				return ret;
			}
			
			Point.Builder builder = Point.measurement(measurementName).time(timestamp, TimeUnit.SECONDS);
			if (currentTime != null) {
			    builder.addField("realtime", currentTime);
			}
			if (instanceId != null )  {
				
			builder.tag("instanceid", instanceId);
		    }

			if (simulationId != null )  {
				
			builder.tag("simulation_id", simulationId);
		    }

			
			while (record_it.hasNext()) {
				String record_key = record_it.next();
				if (!record_key.equalsIgnoreCase("timestamp")) {
				
				if (record.get(record_key) instanceof String) {
				builder.addField( record_key, String.valueOf((String) record.get(record_key)));	
//				} else if (record.get(record_key) instanceof Integer) {
//					builder.addField( record_key, Integer.valueOf((Integer) record.get(record_key)));
//				} else if (record.get(record_key) instanceof Long) {
//					builder.addField(record_key, Long.valueOf((Long) record.get(record_key)));
				
//  Note:  this change Feb 23, 2021 is meant to ensure that integers or longs will be changed to default decimal 
//		   otherwise if a field is created as a integer floats will be ignored.
				} else if (record.get(record_key) instanceof Integer) {
				    builder.addField( record_key, Float.valueOf(record.get(record_key).toString()));
		    	} else if (record.get(record_key) instanceof Long) {
				    builder.addField(record_key, Double.valueOf(record.get(record_key).toString()));
				} else if (record.get(record_key) instanceof Float) {
					builder.addField(record_key, Float.valueOf((Float) record.get(record_key)));
				} else if (record.get(record_key) instanceof Double) {
					builder.addField(record_key, Double.valueOf((Double) record.get(record_key)));
				}
				}
			}
			influxDB.write(idbDB, idbRP, builder.build());

		}

		ret = new ProvenMessageResponse();
		ret.setReason("success");
		ret.setStatus(Status.CREATED);
		ret.setCode(Status.CREATED.getStatusCode());
		ret.setResponse("{ \"INFO\": \"Time-series measurements successfully created.\" }");

		return ret;

	}	 

	//
	// New write measurement routine
	//
	public ProvenMessageResponse influxWriteMeasurements(String measurements, 
			String measurementName, String instanceId, String simulationId, Long realTime) {

		//		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		//		LocalDateTime now = LocalDateTime.now();
		//		System.out.println(dtf.format(now));
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

		Object messageObject = null;

		String objectType = "" ;

		try {
			messageObject = parser.parse(measurements);

		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			ret = new ProvenMessageResponse();
			ret.setStatus(Status.BAD_REQUEST);
			ret.setReason("Invalid or missing message content type.  Could not parse JSON message.");
			ret.setCode(Status.BAD_REQUEST.getStatusCode());
			ret.setResponse("{ \"ERROR\": \"Bad request made to time-series database.\" }");
			e.printStackTrace();
			return ret;
		}

		objectType = detectObjectType(messageObject);

		if (objectType.equalsIgnoreCase("O")) {
			ret = influxWriteSimulationOutput((JSONObject)messageObject, influxDB, measurementName, instanceId, realTime);
		} else if (objectType.equalsIgnoreCase("I")) {
			ret = influxWriteSimulationInput((JSONObject)messageObject, influxDB, measurementName, instanceId, realTime);
		} else if (objectType.equalsIgnoreCase("A")) {
			ret = influxWriteAlarms((JSONArray)messageObject, influxDB, measurementName, instanceId, simulationId, realTime);			
		} else {
			ret = new ProvenMessageResponse();
			ret.setStatus(Status.BAD_REQUEST);
			ret.setReason("Invalid or missing message content type.  Simulation data format irregular or not detected as input, output, or alarm.");
			ret.setCode(Status.BAD_REQUEST.getStatusCode());
			ret.setResponse("{ \"ERROR\": \"Bad request made to time-series database.\" }");
			return ret;
		}

		//		now = LocalDateTime.now();
		//		System.out.println(dtf.format(now));
		influxDB.close();
		return ret;

	}
	
//  08/20/2020 Removed because RepositoryResource.addClientMessage  does not add timestamp.
//
//	public void influxWriteMeasurements(Map<String, Set<ProvenanceMetric>> measurements) {
//
//		if (useIdb) {
//
//			Long startTime = System.currentTimeMillis();
//			InfluxDB influxDB = InfluxDBFactory.connect(idbUrl, idbUsername, idbPassword);
//
//			for (String measurement : measurements.keySet()) {
//
//				Set<ProvenanceMetric> pms = measurements.get(measurement);
//
//				Point.Builder builder = Point.measurement(measurement).time(startTime, TimeUnit.SECONDS);
//
//				for (ProvenanceMetric pm : pms) {
//
//					if (pm.isMetadata()) {
//
//						builder.tag(pm.getLocalMetricName(), pm.getLabelMetricValue());
//
//						// System.out.println("TAG");
//						// System.out.println("------------------------------");
//						// System.out.println(pm.getLocalMetricName());
//						// System.out.println(pm.getLabelMetricValue());
//						// System.out.println("------------------------------");
//
//					} else {
//
//						builder.field(pm.getLocalMetricName(), pm.getLabelMetricValue());
//
//						// System.out.println("FIELD");
//						// System.out.println("------------------------------");
//						// System.out.println(pm.getLocalMetricName());
//						// System.out.println(pm.getLabelMetricValue());
//						// System.out.println("------------------------------");
//
//					}
//
//				}
//
//				// System.out.println("------------------------------");
//				// System.out.println("------------------------------");
//
//				influxDB.write(idbDB, idbRP, builder.build());
//			}
//
//		}
//
//	}

	@AroundInvoke
	public Object checkObjectConnection(InvocationContext ic) throws Exception {

		try {
			if (!isValidConnection()) {
				log.debug("GETTING A NEW CONNECTION!!!");
				oCon = sm.getObjectStoreConnection();
			}
		} catch (Exception e) {
			throw new EJBException("Return/Borrow new connection failed");
		}

		return ic.proceed();
	}

	private boolean isValidConnection() {

		boolean ret = true;

		try {

			if ((!oCon.getRepository().isInitialized()) || (!oCon.isOpen())) {
				ret = false;
				// Force a close, may cause an exception.
				oCon.close();
			}
		} catch (Exception e) {
			ret = false;
		}

		return ret;
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
					for (String column : colNames) {
						csvRow = csvRow.concat(column);
						csvRow = csvRow.concat(",");
					}
					if (csvRow.endsWith(comma)) {
						csvRow = csvRow.substring(0, csvRow.length()-1);
						csvRow = csvRow.concat(newline);
					}
					topHeader = true;
				}

				int rowIndex = 0;
				//				rowResults = new HashMap<String, String>();
				//				JSONObject rowObject = new JSONObject();

				//
				// Process Rows for a given Series
				//
				for (List<Object> row : table) {

					int cellIndex = 0;
					row.toString();
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
								csvRow = csvRow.concat(lval.toString()).concat(comma);
							} else if (cell instanceof Integer) {
								csvRow = csvRow.concat(cell.toString()).concat(comma);
							} else if (cell instanceof Double) {	
								csvRow = csvRow.concat(cell.toString()).concat(comma);
							}else {
								String buff = cell.toString();
								//
								// If a cell is a quoted string, remove the
								// surrounding double quotes.
								//
								buff = buff.replaceAll("\"", "");
								csvRow = csvRow.concat(buff).concat(comma);
							}

							//
							// Add each cell Json Object to a row Object.
							//
						} else {

							csvRow = csvRow.concat(comma);

						}
						cellIndex++;

					} // end adding to row
					if (csvRow.endsWith(comma)) {
						csvRow = csvRow.substring(0, csvRow.length()-1);
						csvRow = csvRow.concat(newline);
					}
					csvMeasurements = csvMeasurements.concat(csvRow);
					csvRow = "";
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
		; // end adding results
		//		results.addMeasurement(measurement);
		//       rootObj.put("data", measurementArray);

		//			jsonresults = rootObj.toJSONString();
		//		} catch (JAXBException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//        System.out.println (csvMeasurements);
		return csvMeasurements;

	}

	public ProvenMessageResponse influxQuery(ProvenMessage query, boolean returnCsvFlag, boolean returnQueryStatement, boolean pingQuery) throws InvalidProvenMessageException {
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
			Map<String,Integer> filterFieldCounter = countFilterFields(filters);
			List <String> assembledFilterStatements = assembleFilterStatements(filters, filterFieldCounter);
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
	
		String response = "";
		String reason = "";
	
		if (useIdb) {
			InfluxDB influxDB = InfluxDBFactory.connect(idbUrl, idbUsername, idbPassword);
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
	
			QueryResult qr = null;
			try {
	
				qr = influxDB.query(influxQuery, TimeUnit.SECONDS);
	
			} catch (OutOfMemoryError e) {
				ret = new ProvenMessageResponse();
				ret.setReason("Out of memory");
				ret.setStatus(Status.BAD_REQUEST);
				ret.setCode(Status.BAD_REQUEST.getStatusCode());
				ret.setResponse("{ \"ERROR\": \"Query results are to large to return.  Out of memory\" } ");
	
			}
	
			if (qr.hasError()) {
				ret = new ProvenMessageResponse();
				ret.setReason("Invalid or missing content.");
				ret.setStatus(Status.BAD_REQUEST);
				ret.setCode(Status.BAD_REQUEST.getStatusCode());
				ret.setResponse("{ \"ERROR\": \"Bad request made to time-series database.\" } " + qr.getError());
	
			} else {
				ret = new ProvenMessageResponse();
				ret.setReason("Success");
				ret.setStatus(Status.OK);
				ret.setCode(Status.OK.getStatusCode());
				ret.setResponse("Query Statement=" + queryStatement + "\n" + ret.getResponse() );
				try { 
	
					if (returnCsvFlag) {
						ret.setResponse(convertResults2Csv2(qr));
					} else {
						ret.setResponse(convertResults2Json(qr));					
					}
	
				} catch (OutOfMemoryError e) {
					ret = new ProvenMessageResponse();
					ret.setReason("Out of memory");
					ret.setStatus(Status.BAD_REQUEST);
					ret.setCode(Status.BAD_REQUEST.getStatusCode());
					ret.setResponse("{ \"ERROR\": \"Query results successfully returned but were to large to postprocess.  Out of memory\" } ");
	
				}
	
	
	
	
			}
		} else {
	
			ret = new ProvenMessageResponse();
			ret.setReason("Time-series database unavailable or database adapter is disabled.");
			ret.setStatus(Status.SERVICE_UNAVAILABLE);
			ret.setCode(Status.SERVICE_UNAVAILABLE.getStatusCode());
			ret.setResponse("{ \"INFO\": \"idb server disabled in Proven configuration\" }");
		}
		if (returnQueryStatement) {
			ret.setResponse("Query Statement=" + queryStatement + "\n" + ret.getResponse() );
	
		}
		// ret.setReason(reason);
		// ret.setResponse(response);
		return ret;
	
	}

}
