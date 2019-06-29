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

package gov.pnnl.proven.cluster.module.hybrid.resource;

import static gov.pnnl.proven.cluster.module.hybrid.concept.ConceptUtil.*;
import static gov.pnnl.proven.cluster.module.hybrid.concept.ProvenConceptSchema.HAS_MESSAGE_ID;
import static gov.pnnl.proven.cluster.module.hybrid.concept.ProvenConceptSchema.HAS_MESSAGE_NAME;
import static gov.pnnl.proven.cluster.module.hybrid.concept.ProvenConceptSchema.HAS_NAME_PROP;
import static gov.pnnl.proven.cluster.module.hybrid.concept.ProvenConceptSchema.PART_OF_PROVEN_MESSAGE_PROP;
import static gov.pnnl.proven.cluster.module.hybrid.concept.ProvenConceptSchema.PROVENANCE_MESSAGE_CLASS;
import static gov.pnnl.proven.cluster.module.hybrid.concept.ProvenConceptSchema.RDF_TYPE_PROP;
import static gov.pnnl.proven.cluster.module.hybrid.resource.HybridResourceConsts.*;
import static gov.pnnl.proven.cluster.module.hybrid.util.Consts.*;
import static javax.ws.rs.core.MediaType.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.helpers.StatementCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.sesame.SesameTripleCallback;
import com.github.jsonldjava.utils.JsonUtils;
//import com.google.gson.Gson;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import gov.pnnl.proven.cluster.lib.disclosure.message.MessageContent;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessage;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessageOriginal;
import gov.pnnl.proven.cluster.module.hybrid.concept.ConceptUtil;
import gov.pnnl.proven.cluster.module.hybrid.concept.DomainModel;
import gov.pnnl.proven.cluster.module.hybrid.concept.dto.ProvenState;
import gov.pnnl.proven.cluster.module.hybrid.manager.StoreManager;
import gov.pnnl.proven.cluster.module.hybrid.service.ConceptService;
import gov.pnnl.proven.cluster.module.hybrid.util.Consts;
import gov.pnnl.proven.cluster.lib.disclosure.message.DisclosureResponse;

/**
 * Session Bean implementation class RepositoryResource
 * 
 * Services supporting administration of ProvEn's repositories.
 */
@Stateless
@LocalBean
@Api(value = "Repository")
@Path(RR_REPOSITORY_PATH)
public class RepositoryResource {

	private final Logger log = LoggerFactory.getLogger(RepositoryResource.class);

	public class ProvenanceMetric {

		URI metricName;
		boolean isMetadata;
		Literal metricValue;

		ProvenanceMetric(URI metricName, boolean isMetadata, Literal metricValue) {
			this.metricName = metricName;
			this.isMetadata = isMetadata;
			this.metricValue = metricValue;
		}

		public URI getMetricName() {
			return metricName;
		}

		public String getLocalMetricName() {
			return metricName.getLocalName();
		}

		public void setMetricName(URI metricName) {
			this.metricName = metricName;
		}

		public boolean isMetadata() {
			return isMetadata;
		}

		public void setMetadata(boolean isMetadata) {
			this.isMetadata = isMetadata;
		}

		public Literal getMetricValue() {
			return metricValue;
		}

		public String getLabelMetricValue() {
			return metricValue.getLabel();
		}

		public void setMetricValue(Literal metricValue) {
			this.metricValue = metricValue;
		}

	}

	private final static String SPARQL_EXAMPLE =
	//@formatter:off
			"SELECT ?subject ?predicate ?object  " 
    		+ Consts.NL + "WHERE {  "
			+ Consts.NL + "  GRAPH <http://provenance.pnnl.gov/ns/proven#provenTesting>  " + Consts.NL 
			            + "  {  "
			+ Consts.NL + "    ?subject ?predicate ?object  " + Consts.NL + "  }  " 
			            + Consts.NL + "}  ";
	//@formatter:on

	@EJB
	private StoreManager sm;

	@EJB
	private ConceptService cs;

	@Inject
	HazelcastInstance hzMemberInstance;

	@GET
	@Path(R_REPOSITORY_STATE)
	@Produces(APPLICATION_JSON)
	//@formatter:off
	@ApiOperation(value="State of Proven hybrid store",
	              notes = "Checks if Object Repository has been enabled and is currently "
	                    + "accepting conections.",
	              response = ProvenState.class,
			      hidden=false)
	//@formatter:on
	public Response getRepositoryState() {

		Response response;
		ProvenState ps = new ProvenState(true);

		try {

			if (!sm.isReady()) {
				ps.setIsEnabled(false);
				// response = Response.ok(R_REPOSITORY_STATE_ENABLED).build();
			}
			response = Response.ok(ps).build();
			;

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = Response.serverError().entity(e.getMessage()).build();
		}

		return response;
	}

	@GET
	@Path(R_CONCEPTS + "/type/{domain}")
	@Produces(APPLICATION_SPARQL_RESULTS)
	//@formatter:off
	@ApiOperation(value="Domain concept types and count of instances by domain" ,
	              notes= "<i><b> \"" + APPLICATION_SPARQL_RESULTS + "\"</i></b>" 
	                     + " MIME type result is produced by the service, " 
	            		 + "projected variables in bindings array, include: <br><br>" 
	                     + "<b>Concept</b> - the concept type <br>"  
	            		 + "<b>Count</b> - the number of instances for the type <br><br>",
			      hidden=false)
	//@ApiResponses( value = { @ApiResponse(code = 200, message = "Successfull operation")})
	//@formatter:on
	public Response getDomainConceptTypes(
			@ApiParam(value = "Name of domain context") @PathParam("domain") String domain) {

		Response response;
		String result = null;

		try {

			DomainModel dm = cs.findConceptByName(DomainModel.class, domain);

			if (dm != null) {
				String dGraph = toIri(dm.getExplicitContent().getContextUri()).toString();

				//@formatter:off
				String sQuery =
				" PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>  " +
				//" PREFIX proven:<http://provenance.pnnl.gov/ns/proven#>  " +
                " PREFIX dGraph:" + dGraph +
				" SELECT ?Concept (count(distinct ?ConceptId) as ?Count)  " +
				"  WHERE {    " +
				//"  GRAPH proven:" + domain + "  {  " +
                "  GRAPH dGraph:  {  " +				
				"    ?ConceptId rdf:type ?Concept .  " +
				"  }  " +
				" } GROUP BY ?Concept ";
				//@formatter:on

				log.debug(sQuery);
				result = cs.sparqlQuery(sQuery);
			}

			if (null == result) {
				response = Response.noContent().build();
			} else {
				response = Response.ok(result).build();
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = Response.serverError().entity(e.getMessage()).build();
		}

		return response;
	}

	@GET
	@Path(R_CONCEPTS + "/{domain}" + "/{pattern}")
	// @Produces(APPLICATION_JSON)
	@Produces(APPLICATION_SPARQL_RESULTS)
	//@formatter:off
	@ApiOperation(value="Domain concepts for a specified regex search pattern" ,
	              notes= "<i><b> \"" + APPLICATION_SPARQL_RESULTS + "\"</i></b>" + " MIME type result is produced by the service, " +
	                     "projected variables in bindings array, include: <br><br>" +
	            		 "<b>ConceptId</b> - the concept identifier (i.e. class instance) <br>"  +
	            		 "<b>Concept</b> - the rdf type (i.e. class) <br>"  +
	            		 "<b>Term</b> - the predicate <br>"  +
	            		 "<b>Value</b> - the value <br><br>"  +
	                     "Search pattern is case-insensitve, and a limit value less then or equal to 0 will return all results.",
	    	      response = String.class, 
	    	      produces = APPLICATION_SPARQL_RESULTS,
			      hidden=false)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfull operation")})
	//@formatter:on
	public Response getDomainConcepts(@ApiParam(value = "Name of domain context") @PathParam("domain") String domain,
			@ApiParam(value = "Search pattern") @PathParam("pattern") String pattern,
			@ApiParam(value = "limit number of results") @QueryParam("limit") Integer limit) {

		Response response;
		String result = null;

		try {

			DomainModel dm = cs.findConceptByName(DomainModel.class, domain);

			if (null != dm) {

				String dGraph = toIri(dm.getExplicitContent().getContextUri()).toString();

				//@formatter:off
				String sQuery =
				" PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>  " +
//				" PREFIX proven:<http://provenance.pnnl.gov/ns/proven#>  " +
                " PREFIX dGraph:" + dGraph +						
				" SELECT ?ConceptId ?Concept ?Term ?Value  " +
				"  WHERE {    " +
//				"  GRAPH proven:" + domain + "  {  " +
                "  GRAPH dGraph:  {  " +				
				"    ?ConceptId rdf:type ?Concept .  " +
				"    ?ConceptId ?Term ?Value .  " +
				"    filter( regex(str(?Concept), " + "\"" + pattern + "\"," + " \"i\" ) ||  " +
				"            regex(str(?ConceptId), " + "\"" + pattern + "\"," + " \"i\" ) ||  " +
				"            regex(str(?Term), " + "\"" + pattern + "\"," + " \"i\" ) ||  " +
				"            regex(str(?Value), " + "\"" + pattern + "\"," + " \"i\" ) ) .  " +			
				"  }  " + 
				" } " ;
				//@formatter:on

				if ((null != limit) && (limit > 0)) {
					sQuery += " LIMIT " + limit.toString();
				}

				log.debug(sQuery);

				result = cs.sparqlQuery(sQuery);

			}

			if (null == result) {
				response = Response.noContent().build();
			} else {
				response = Response.ok(result).build();
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = Response.serverError().entity(e.getMessage()).build();
		}

		return response;
	}

	@GET
	@Path(R_MESSAGES + "/{domain}" + "/{conceptId}")
	@Produces(APPLICATION_SPARQL_RESULTS)
	//@formatter:off
	@ApiOperation(value="Knowledge(s) data containing provided concept identifier" ,
            notes= "<i><b> \"" + APPLICATION_SPARQL_RESULTS + "\"</b></i>" + " MIME type result is produced by the service, " +
                    "projected variables in bindings array, include: <br><br>" +
           		 "<b>MessageId</b> - the message identifier <br>"  +
           		 "<b>Subject</b><br>"  +
           		 "<b>Predicate</b><br>"  +
           		 "<b>Object</b><br><br>"  +
                    "Provides a triple-listing (S,P,O) of the message(s) that contains the provided concept identifier.",
	    	      response = String.class, 
	    	      produces = APPLICATION_SPARQL_RESULTS,
			      hidden=false)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfull operation")})
	//@formatter:on
	public Response getDomainMessagesForConceptId(
			@ApiParam(value = "Name of domain context") @PathParam("domain") String domain,
			@ApiParam(value = "Concept Identifier (IRI format)") @PathParam("conceptId") String conceptId) {

		Response response;
		String result = null;

		try {

			DomainModel dm = cs.findConceptByName(DomainModel.class, domain);

			if (null != dm) {

				String dGraph = toIri(dm.getExplicitContent().getContextUri()).toString();

				//@formatter:off
				String sQuery =
				" PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>  " +
				" PREFIX proven:<http://provenance.pnnl.gov/ns/proven#>  " +
						
				" SELECT ?MessageId ?Subject ?Predicate ?Object  " +
				"  WHERE {    " +
				"  GRAPH proven:" + domain + "  {  " +
				
				"   " + conceptId  + " proven:partOfProvenMessage ?MessageId .  " +
				"   ?Subject proven:partOfProvenMessage ?MessageId .  " +
				"   ?Subject ?Predicate ?Object .  " +		
				"  }  " + 
				" } " ;
				//@formatter:on

				log.debug(sQuery);

				result = cs.sparqlQuery(sQuery);

			}

			if (null == result) {
				response = Response.noContent().build();
			} else {
				response = Response.ok(result).build();
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = Response.serverError().entity(e.getMessage()).build();
		}

		return response;
	}

	@PUT
	@Path(R_REPOSITORY_STATE + "/" + R_REPOSITORY_STATE_ENABLED)
	//@formatter:off
	@ApiOperation(value="Enables Proven Hybrid Store",
			      hidden=true)
	//@formatter:on
	public Response enableRepository() {

		Response response = Response.noContent().build();

		try {

			sm.start();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = Response.serverError().entity(e.getMessage()).build();
		}

		return response;
	}

	@PUT
	@Path(R_REPOSITORY_STATE + "/" + R_REPOSITORY_STATE_DISABLED)
	//@formatter:off
	@ApiOperation(value="Disables Proven Hybrid Store",
			      hidden=true)
	//@formatter:on		
	public Response disableRepository() {

		Response response = Response.noContent().build();

		try {

			sm.stop();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = Response.serverError().entity(e.getMessage()).build();
		}

		return response;
	}

	@DELETE
	@Path(R_STATEMENTS)
	//@formatter:off
	@ApiOperation(value="Removes all statement triples from Proven Semantic store",
			      hidden=true)
	//@formatter:on
	public Response removeAllStatements() {

		Response response = Response.noContent().build();

		try {

			cs.removeAll();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = Response.serverError().entity(e.getMessage()).build();
		}

		return response;

	}

	@DELETE
	@Path(R_STATEMENTS + "/{domain}")
	//@formatter:off
	@ApiOperation(value="Removes all statement triples from provided domain",
			      hidden=true,
			      notes="Cannot be undone; use with caution")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successfull operation") })
	//@formatter:on
	public Response removeDomainStatements(
			@ApiParam(required = true, value = "Name of domain context") @PathParam("domain") String domain) {

		log.debug("Getting Statements for :: " + domain + "  .....");

		Response response = Response.noContent().build();
		Resource domainResource = toContext(domain);

		try {

			cs.removeDomainContext(domainResource);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = Response.serverError().entity(e.getMessage()).build();
		}

		return response;

	}

	@GET
	@Path(R_STATEMENTS)
	@Produces(TEXT_PLAIN)
	//@formatter:off
	@ApiOperation(value="Simple view of all semantic statments",
			      hidden=false,
            	  notes = "The semantic statments are listed in SPOC (Subject:Predicate:Object:Context) order")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successfull operation") 
    })
	//@formatter:on
	public Response getAllStatements() {

		Response response;
		String ret = "STATEMENTS..." + NL;

		try {

			RepositoryResult<Statement> results = cs.getAllStatements();
			List<Statement> statements = results.asList();
			ret = ret + "Size: " + statements.size() + NL;
			for (Statement statement : statements) {
				ret = ret + statement.getSubject().toString() + NL + statement.getPredicate().toString() + NL
						+ statement.getObject().toString() + NL;

				if (statement.getContext() != null) {
					ret = ret + statement.getContext().toString() + NL;
				}
				ret = ret + "---------------------------------------------------------------------------" + NL;
			}

			response = Response.ok(ret).build();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = Response.serverError().entity(e.getMessage()).build();
		}

		return response;

	}

	@GET
	@Path(R_STATEMENTS + "/{domain}")
	@Produces(TEXT_PLAIN)
	//@formatter:off
	@ApiOperation(value = "Simple view of all semantic statments for a given domain (content + structure)", 
	              hidden = false, 
	              notes = "The semantic statments are listed in SPOC (Subject:Predicate:Object:Context) order", 
	              response = String.class, 
	              produces = "text/plain")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successfull operation") 
			              })
	//@formatter:on
	// public Response getDomainStatements(
	public String getDomainStatements(
			@ApiParam(required = true, value = "Name of domain context") @PathParam("domain") String domain) {

		log.debug("GETTING DOMAIN STATEMENTS...");

		Response response;
		String ret = domain + " STATEMENTS..." + NL;

		try {

			RepositoryResult<Statement> results = cs.getDomainStatements(domain);
			if (null != results) {

				List<Statement> statements = results.asList();
				ret = ret + "Size: " + statements.size() + NL;
				for (Statement statement : statements) {
					ret = ret + statement.getSubject().toString() + NL + statement.getPredicate().toString() + NL
							+ statement.getObject().toString() + NL + statement.getContext().toString() + NL
							+ "---------------------------------------------------------------------------" + NL;
				}
			}

			response = Response.ok(ret).build();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = Response.serverError().entity(e.getMessage()).build();
		}

		// return response;
		return ret;
	}

	@POST
	@Path(R_MESSAGE + "/client" + "/{domain}" + "/{messageName}")
	// @Consumes(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	//@formatter:off
	@ApiOperation(value = "Adds provenance message to domain context", 
	              notes = "Provided provenance mesage must be valid <a href='https://www.w3.org/TR/json-ld/'>JSON-LD.</a>  "
			            + "Object identifiers for Subject/Object statement values are responsibility of caller.  "
	            		+ "Proven's Describe Anything Provenance Interface (DAPI) library, if used, will manage identifiers for the user.  "
	            		+ "Default insert behavior is overwrite for identical triple statements.  "
	            		+ "Any Provenance Metrics specified in message will be added to time-series store, if enabled.  "
	            		+ "Invalid JSON-LD will return error and message will not be added to store.")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Successfull operation") 
		                  })
	//@formatter:on
	public Response addClientMessage(String jsonLd,
			@ApiParam(value = "Name of domain context") @PathParam("domain") String domain,
			@ApiParam(value = "Name of provenance message") @PathParam("messageName") String messageName) {

		Response response = Response.noContent().build();
		// Resource context = toResource(PROVEN_CONTEXT);
		// Resource context = toResource(PROVEN_CONTEXT + "/" + domain);
		// Resource context = toContext(domain);

		try {

			cs.begin();

			DomainModel dm = cs.findConceptByName(DomainModel.class, domain);

			if ((null == dm) || (jsonLd == null)) {
				throw new RepositoryException("Domain and/or JSON-LD missing");
			}

			String dmContent = dm.getExplicitContent().getContextUri();
			Resource context = toResource(dmContent);

			// Resource contextUri =
			// toResource(schedule.getNativeSource().getDomainModel()
			// .getContent().getContextUri());
			SesameTripleCallback stc = new SesameTripleCallback();
			Map<URI, Literal> po = new HashMap<URI, Literal>();

			// Returns a sesame StatementCollector, containing statements to add
			// to repository.
			StatementCollector statementCollector = (StatementCollector) JsonLdProcessor
					.toRDF(JsonUtils.fromString(jsonLd), stc);

			// Find ProvenanceMessage Subject and link to ExchangeSession
			Collection<Statement> statements = statementCollector.getStatements();
			Statement nameStatement = null;
			Resource messageSubject = null;
			for (Statement statement : statements) {
				// TODO - Remove

				// Does not support empty messages
				// if
				// (statement.getPredicate().toString().equals(HAS_PROVENANCE_PROP))
				// {
				// nameStatement = new
				// ContextStatementImpl(statement.getSubject(),
				// toUri(HAS_NAME_PROP),
				// toLiteral(messageName), context);
				// messageSubject = statement.getSubject();
				// }

				// Supports empty messages
				if (statement.getPredicate().toString().equals(RDF_TYPE_PROP)
						&& statement.getObject().toString().equals(PROVENANCE_MESSAGE_CLASS)) {
					nameStatement = new ContextStatementImpl(statement.getSubject(), toUri(HAS_NAME_PROP),
							toLiteral(messageName), context);
					messageSubject = statement.getSubject();
				}
				po.put(statement.getPredicate(), toLiteral(statement.getObject()));
			}

			if (null != nameStatement) {
				statements.add(nameStatement);
			} else {
				log.warn("Failed to link message with its exchange session");
			}

			// ////
			// Collect ProvenanceMetrics for Time Series Store
			Map<String, Set<ProvenanceMetric>> measurements = new HashMap<String, Set<ProvenanceMetric>>();
			List<ProvenanceMetric> pmList = new ArrayList<ProvenanceMetric>();
			JsonArray jarray = Json.createReader(new StringReader(jsonLd)).readObject()
					.getJsonArray(PROVENANCE_METRICS);

			// if (null != jarray) {

			for (int i = 0, size = jarray.size(); i < size; i++) {

				JsonObject jobject = jarray.getJsonObject(i);
				String measurementName = jobject.getString(MEASUREMENT_NAME);

				URI metricName;
				String jMetricName = jobject.getString(METRIC_NAME);
				try {
					metricName = toUri(jMetricName);
				} catch (IllegalArgumentException e) {
					log.warn("Metric Name not found, skipping... : " + jMetricName.toString());
					continue;
				}

				Boolean isMetadata = jobject.getBoolean(IS_METADATA);
				boolean missingMeasurementName = ((null == measurementName) || (measurementName.isEmpty()));
				boolean missingIsMetadata = (null == isMetadata);

				if ((missingMeasurementName) || (missingIsMetadata)) {
					throw new RepositoryException(
							"Invalid provenance metric format, nissing measurement and/or IsMetadata");
				}

				Literal metricValue = po.get(metricName);

				ProvenanceMetric pm;
				if (null != metricValue) {
					pm = new ProvenanceMetric(metricName, isMetadata, metricValue);
					pmList.add(pm);

					// Add provenance metric
					if (measurements.containsKey(measurementName)) {
						measurements.get(measurementName).add(pm);
					} else {
						Set<ProvenanceMetric> pms = new HashSet<ProvenanceMetric>();
						pms.add(pm);
						measurements.put(measurementName, pms);
					}
				}
			}
			// }

			// Remove TS data from RDF message (i.e. statements)
			// Get Knowledge name and ID information; use to directly link metrics
			// with semantic data
			String pmMessageName = null;
			String pmMessageId = null;
			Iterator<Statement> itr = statements.iterator();
			Statement statement = null;
			while (itr.hasNext()) {

				statement = itr.next();
				for (ProvenanceMetric pm : pmList) {

					if (pm.metricName.equals(statement.getPredicate())) {
						itr.remove();
						break;
					}

				}

				// Get name and id
				if (statement.getSubject().toString().endsWith("ProvenanceMessage")) {

					if (statement.getPredicate().toString().endsWith("hasProvenance")) {

						pmMessageId = statement.getSubject().toString();
						pmMessageName = "unknown";
						String localName = toUri(statement.getObject().toString()).getLocalName();

						int idxSplit = localName.indexOf('_');
						if ((idxSplit != -1) && (localName.length() > 3)) {
							pmMessageName = localName.substring(idxSplit + 1);
						}

					}

				}

			}

			if ((null != pmMessageName) && (null != pmMessageId)) {
				for (String measurement : measurements.keySet()) {

					measurements.get(measurement)
							.add(new ProvenanceMetric(toUri(HAS_MESSAGE_NAME), true, toLiteral(pmMessageName)));
					measurements.get(measurement)
							.add(new ProvenanceMetric(toUri(HAS_MESSAGE_ID), true, toLiteral(pmMessageId)));

				}
			}

			// Add :partOfProvenMessage relationship for each statement subject
			if (null == messageSubject)
				log.error("MESSAGE does not have an identifier!");
			Statement messageStatement = null;
			Collection<Statement> messageStatements = new ArrayList<Statement>();
			for (Statement s : statements) {
				if (!(s.getSubject().equals(messageSubject))) {
					messageStatement = new ContextStatementImpl(s.getSubject(), toUri(PART_OF_PROVEN_MESSAGE_PROP),
							messageSubject, context);
				}
				// Include the message itself to support message queries
				else {
					messageStatement = new ContextStatementImpl(messageSubject, toUri(PART_OF_PROVEN_MESSAGE_PROP),
							messageSubject, context);
				}
				log.debug(messageStatement.toString());
				messageStatements.add(messageStatement);

			}

			statements.addAll(messageStatements);
			cs.addStatements(statements, context);
			cs.influxWriteMeasurements(measurements);
			cs.commit();

		} catch (

		Exception e)

		{
			cs.rollback();
			response = Response.serverError().entity(e.getMessage()).build();
			log.error("Load client statements failed");
			if (null != jsonLd)
				log.error(jsonLd);
		}

		return response;

	}

	@POST
	@Path("sparql")
	@Consumes(TEXT_PLAIN)
	// @Produces(APPLICATION_JSON)
	@Produces(TEXT_PLAIN)
	//@formatter:off
	@ApiOperation(value = "Query semantic store using sparql query language", 
	              hidden = false, 
	              notes = "Provided sparql query will be submited to semantic store.  "
	                    + "Format of query results is in JSON, and is described here  "
	                    + "<a href='https://www.w3.org/TR/sparql11-results-json/'> SPARQL Results Format </a>.  "
	            		+ "Sparql query laguage is described here  "
	                    + "<a href='https://www.w3.org/TR/sparql11-query/'> SPARQL Language Reference </a>.  ")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successfull operation") 
                          })
	//@formatter:on
	public Response getSparql(String queryString) {
		Response response;
		String ret = cs.sparqlQuery(queryString);
		// ret = "hello world!!!";
		response = Response.ok(ret).build();
		return response;
	}

	@POST
	@Path("influxql")
	@Consumes(TEXT_PLAIN)
	@Produces(TEXT_PLAIN)
	//@formatter:off
	@ApiOperation(value = "Query influxDB time-series store using InfluxQL", 
	              hidden = false, 
	              notes = "If enabled, provided query will be submited to InfluxDB time-series store.  "
	                    + "Format of query results are in JSON, described here  "
	                    + "<a href='https://docs.influxdata.com/influxdb/v1.2/guides/querying_data/'> InfluxQL Results Format </a>.  "
	            		+ "InfluxQL query laguage is described here  "
	                    + "<a href='https://docs.influxdata.com/influxdb/v1.2/query_language/spec/'> InfluxQL Language Reference </a>.  ")
    //@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfull operation")})
	//@formatter:on	
	public Response getInflux(String queryString) {
		Response response;
		String ret = cs.influxQuery(queryString);
		response = Response.ok(ret).build();
		return response;
	}

	@POST
	@Path(R_PROVEN_MESSAGE)
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	//@formatter:off
	@ApiOperation(value = "Adds a provenance message", 
	              notes = "Provided provenance mesage must be valid \"ProvenMessage\" object")
	@ApiResponses(value =  { @ApiResponse(code = 200, message = "Successful operation.", response = DisclosureResponse.class),
			                 @ApiResponse(code = 201, message = "Created.", response = DisclosureResponse.class),	
			                 @ApiResponse(code = 403, message = "Invalid or missing message content",response = DisclosureResponse.class),
			                 @ApiResponse(code = 500, message = "Internal server error.")})		                  
	//@formatter:on
	public DisclosureResponse addProvenMessage(ProvenMessageOriginal pm) {

		DisclosureResponse pmr = null;

		try {

			cs.begin();
		
			// Add proven message to stream
			// TODO Implement MapStore
			String key = pm.getMessageKey();
			pm.getMessageProperties().setDisclosure(new Date().getTime());
			String stream = pm.getMessageContent().getName(); 
			if (null != hzMemberInstance) {
				log.warn("HZ cluster instance could not be found");
				IMap<String, ProvenMessageOriginal> pms = hzMemberInstance.getMap(stream);
				pms.set(key, pm);
			}		

			// Add statements to T3 store
			Resource[] contexts = {};
			Collection<Statement> statements = ConceptUtil.getSesameStatements(pm.getStatements());
			cs.addStatements(statements, contexts);

			
			// Query or Add measurements to TS store
			
			// Query

			if (stream.equals(MessageContent.Explicit.getName())) {
			    // Now using MapStore to write messages 8/29/2018
                // pmr = cs.influxWriteMeasurements(pm.getMeasurements());
				pmr = new DisclosureResponse();
				
				pmr.setReason("Success");
				pmr.setStatus(Status.OK);
				pmr.setCode(Status.OK.getStatusCode());
				pmr.setResponse("Measurement queued in IMDG.");
				
				

				
			}

			// Explicit
			if (stream.equals(MessageContent.Query.getName())) {
				pmr = cs.influxQuery(pm);
			}

			
			// Invalid message content
			if (null == pmr) {
				pmr = new DisclosureResponse();
				pmr.setStatus(Status.BAD_REQUEST);
				pmr.setReason("Invalid or missing message content type.");
				pmr.setCode(Status.BAD_REQUEST.getStatusCode());
				pmr.setResponse("{ \"ERROR\": \"Bad request made to time-series database.\" }");

				cs.rollback();
				
			} else {
				
				cs.commit();
			}

		} catch (Exception e) {
			cs.rollback();
			pmr = new DisclosureResponse();
			pmr.setStatus(Status.INTERNAL_SERVER_ERROR);
			pmr.setReason(e.getMessage());
			pmr.setCode(Status.INTERNAL_SERVER_ERROR.getStatusCode());
			pmr.setResponse("{ \"ERROR\": \"Bad request made to time-series database.\" }");

		}

		return pmr;
	}

}