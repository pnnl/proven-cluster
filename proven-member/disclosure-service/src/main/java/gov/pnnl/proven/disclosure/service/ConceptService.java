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

package gov.pnnl.proven.disclosure.service;

import static gov.pnnl.proven.disclosure.concept.ConceptUtil.*;
import static gov.pnnl.proven.disclosure.concept.ProvenConceptSchema.*;
import static gov.pnnl.proven.disclosure.util.Consts.*;
import static gov.pnnl.proven.disclosure.util.ProvenConfig.ProvenEnvProp.*;
import static gov.pnnl.proven.disclosure.util.Utils.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import gov.pnnl.proven.disclosure.concept.Concept;
import gov.pnnl.proven.disclosure.concept.ConceptUtil;
import gov.pnnl.proven.disclosure.concept.DomainModel;
import gov.pnnl.proven.disclosure.concept.NativeSource;
import gov.pnnl.proven.disclosure.concept.Ontology;
import gov.pnnl.proven.disclosure.concept.Representation;
import gov.pnnl.proven.disclosure.concept.ConceptUtil.BlobStatus;
import gov.pnnl.proven.disclosure.manager.StoreManager;
import gov.pnnl.proven.disclosure.resource.RepositoryResource.ProvenanceMetric;
import gov.pnnl.proven.disclosure.util.ProvenConfig;
import info.aduna.iteration.Iterations;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.query.resultio.TupleQueryResultWriterRegistry;
import org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.result.NoResultException;
import org.openrdf.store.blob.BlobObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class ConcepService
 * 
 * Provides management and persistent services for proven concepts. By default, the proven context
 * and base uri are used.
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

	// //////////////////////////////////////////////////////////
	// Named queries

	// @formatter:off
	
	private static final String findSingleByName = 
			"SELECT ?s WHERE {?s " + toIri(HAS_NAME_PROP) + " ?name }";
	
	
	private static final String findNativeSourcesByDomainName =
			"SELECT ?s WHERE { ?s " + toIri(HAS_DOMAIN_MODEL_PROP) + " ?domain  . " +
	        "                  ?domain " + toIri(HAS_NAME_PROP) + " ?name " +
	        "                } ";

	private static final String findNativeSourcesByDomainName2 =
			"SELECT ?s WHERE {?s  ?p  ?o }";

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

	public void addStatements(Collection<Statement> statements, Resource... resources)
			throws Exception {
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
	 * Finds a concept instance by name. Assumes a single result, will throw an exception if
	 * multiple instances discovered.
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

			//TupleQueryResultWriterRegistry
			
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
			ret = "TupleQueryResultHandlerException :: " + e.getCause().toString() + " :: "
					+ e.getMessage();
			;
		} catch (QueryEvaluationException e) {
			ret = "QueryEvaluationException :: " + e.getCause().toString() + " :: "
					+ e.getMessage();
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

	public String influxQuery(String queryString) {

		String ret = "";

		if (useIdb) {
			InfluxDB influxDB = InfluxDBFactory.connect(idbUrl, idbUsername, idbPassword);
			String dbName = idbDB;
			// Query query = new Query("select time_idle from cpu limit 10", dbName);
			Query query = new Query(queryString, dbName);
			QueryResult qr = influxDB.query(query);
			if (qr.hasError()) {
				ret = qr.getError();
			} else {
				ret = qr.getResults().toString();
				// ret = qr.toString();
			}
		} else {
			ret = "{ \"INFO\": \"idb server disabled in Proven configuration\" }";
		}
		return ret;

	}

	public void influxWriteMeasurements(Map<String, Set<ProvenanceMetric>> measurements) {

		if (useIdb) {

			Long startTime = System.currentTimeMillis();
			InfluxDB influxDB = InfluxDBFactory.connect(idbUrl, idbUsername, idbPassword);

			for (String measurement : measurements.keySet()) {

				Set<ProvenanceMetric> pms = measurements.get(measurement);

				Point.Builder builder = Point.measurement(measurement).time(startTime,
						TimeUnit.MILLISECONDS);

				for (ProvenanceMetric pm : pms) {

					if (pm.isMetadata()) {

						builder.tag(pm.getLocalMetricName(), pm.getLabelMetricValue());

						// System.out.println("TAG");
						// System.out.println("------------------------------");
						// System.out.println(pm.getLocalMetricName());
						// System.out.println(pm.getLabelMetricValue());
						// System.out.println("------------------------------");

					} else {

						builder.field(pm.getLocalMetricName(), pm.getLabelMetricValue());

						// System.out.println("FIELD");
						// System.out.println("------------------------------");
						// System.out.println(pm.getLocalMetricName());
						// System.out.println(pm.getLabelMetricValue());
						// System.out.println("------------------------------");

					}

				}

				// System.out.println("------------------------------");
				// System.out.println("------------------------------");

				influxDB.write(idbDB, idbRP, builder.build());
			}

		}

	}

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

}
