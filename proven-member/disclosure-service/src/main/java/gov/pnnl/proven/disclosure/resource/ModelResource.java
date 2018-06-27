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

package gov.pnnl.proven.disclosure.resource;

import static gov.pnnl.proven.disclosure.concept.ConceptUtil.*;
import static gov.pnnl.proven.disclosure.resource.ResourceConsts.*;
import static javax.ws.rs.core.MediaType.*;

import java.io.File;
import java.net.URI;
//import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gov.pnnl.proven.disclosure.concept.DomainModel;
import gov.pnnl.proven.disclosure.concept.KnowledgeModel;
import gov.pnnl.proven.disclosure.concept.NativeSource;
import gov.pnnl.proven.disclosure.concept.Ontology;
import gov.pnnl.proven.disclosure.manager.StoreManager;
import gov.pnnl.proven.disclosure.service.ConceptService;
import gov.pnnl.proven.disclosure.service.ModelService;
import gov.pnnl.proven.disclosure.util.Consts;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.openrdf.model.Resource;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class KnowledgeModelResource
 */
@Api(value = "Domain Model")
@Stateless
@LocalBean
@Path(RR_DOMAIN_PATH)
public class ModelResource {

	private final Logger log = LoggerFactory.getLogger(ModelResource.class);

	@EJB
	private StoreManager sm;

	@EJB
	private ModelService ms;

	@EJB
	private ConceptService cs;

	@GET
	@Path("{kmName}/" + R_ONTOLOGIES)
	@Produces(APPLICATION_XML)
	//@formatter:off
	@ApiOperation(value="TESTING",
	              hidden=true)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfull operation")})
	//@formatter:on
	public Response listOntologies(@PathParam("kmName") String kmName) {

		Response response;
		List<Ontology> ontologies = new ArrayList<Ontology>();

		try {
			KnowledgeModel km = cs.findConceptByName(KnowledgeModel.class, kmName);
			ontologies.addAll(km.getOntologies());
			GenericEntity<List<Ontology>> entity = new GenericEntity<List<Ontology>>(ontologies) {
			};
			response = Response.ok().entity(entity).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = Response.serverError().entity(e.getMessage()).build();
		}

		return response;

	}

	@PUT
	@Path("{kmName}/" + R_STRUCTURE)
	@Consumes(TEXT_PLAIN)
	@Produces(APPLICATION_XML)
	//@formatter:off
	@ApiOperation(value="TESTING",
	              hidden=true)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfull operation")})
	//@formatter:on
	public Response addStructure(String source, @PathParam("kmName") String kmName) {

		Response response;

		try {

			cs.begin();

			KnowledgeModel km = cs.findConceptByName(KnowledgeModel.class, kmName);
			java.net.URI sourceUri = new java.net.URI(source);
			File sourceFile = new File(sourceUri);
			Ontology sourceOntology = ms.createModelOntology(sourceFile);
			km.getOntologies().add(sourceOntology);
			ms.loadStructure(km, sourceOntology);
			cs.addBlobs(sourceOntology.getRepresentations());

			cs.commit();

			response = Response.noContent().build();

		} catch (Exception e) {
			cs.rollback();
			log.error(e.getMessage(), e);
			response = Response.serverError().entity(e.getMessage()).build();
		}

		return response;

	}

	@POST
	@Path("{domain}/" + R_CONTENT + "/{source}")
	//@formatter:off
	@ApiOperation(value="Add content to an existing domain" ,
                  notes= "Provide a URI location of an instance ontology to add to the specified domain.",
                 hidden=false)
	//@formatter:on
	public Response addDomainContent(@ApiParam(value = "Name of domain context") @PathParam("domain") String dmName,
			@ApiParam(value = "Source of content (URI)") @PathParam("source") String source) {

		Response response;

		try {

			cs.begin();

			DomainModel dm = cs.findConceptByName(DomainModel.class, dmName);
			Resource context = ms.getDomainContextContent(dmName);

			if (null != dm) {
				URI sourceUri = new URI(source);
				ms.loadContext(dm, sourceUri, context);
			} else {
				throw new RepositoryException("Domain not found");
			}

			cs.commit();

			response = Response.noContent().build();

		} catch (

		Exception e) {
			cs.rollback();
			log.error(e.getMessage(), e);
			response = Response.serverError().entity(e.getMessage()).build();
		}

		return response;
	}

	
	@POST
	@Path("{domain}/" + R_STRUCTURE + "/{source}")
	//@formatter:off
	@ApiOperation(value="Add structure to an existing domain" ,
                  notes= "Provide a URI location of an ontology to add to the specified domain.",
                 hidden=false)
	//@formatter:on
	public Response addDomainStructure(@ApiParam(value = "Name of domain context") @PathParam("domain") String dmName,
			@ApiParam(value = "Source of content (URI)") @PathParam("source") String source) {

		Response response;

		try {

			cs.begin();
			
			URI sourceURI = new URI(source);
			DomainModel dm = cs.findConceptByName(DomainModel.class, dmName);

			if (null != dm) {
				Ontology ont = ms.createModelOntology(sourceURI);
				cs.addConcept(ont);
				dm.getOntologies().add(ont);
				Resource context = toResource(ont.getContext().getContextUri());
				ms.loadContext(dm, sourceURI, context);

			} else {
				throw new RepositoryException("Domain not found");
			}

			cs.commit();

			response = Response.noContent().build();

		} catch (

		Exception e) {
			cs.rollback();
			log.error(e.getMessage(), e);
			response = Response.serverError().entity(e.getMessage()).build();
		}

		return response;
	}

	
	
	@DELETE
	@Path("{domain}/" + R_CONTENT)
	//@formatter:off
	@ApiOperation(value="Clear domain content" ,
                  notes= "Only domain content is removed; structure is retained.",
              response = String.class,
                 hidden=false)
	//@formatter:on	
	public Response removeContent(@PathParam("domain") String dmName) {

		Response response;
		Resource context;

		try {

			cs.begin();

			if (dmName.equals(Consts.PROVEN_PREFIX)) {
				throw new Exception("Cannot remove Proven content");
			}

			context = ms.getDomainContextContent(dmName);
			if (null != context) {
				cs.removeDomainContext(context);
			}

			cs.commit();
			response = Response.noContent().build();

		} catch (Exception e) {
			cs.rollback();
			log.error(e.getMessage(), e);
			response = Response.serverError().entity(e.getMessage()).build();
		}

		return response;
	}

	@DELETE
	@Path("{domain}/")
	//@formatter:off
	@ApiOperation(value="Clear domain" ,
                  notes= "Removes all content and structure from domain.",
              response = String.class,
                 hidden=false)
	//@formatter:on	
	public Response removeContentAll(@PathParam("domain") String dmName) {

		Response response;
		List<Resource> contexts;

		try {

			cs.begin();

			if (dmName.equals(Consts.PROVEN_PREFIX)) {
				throw new Exception("Cannot remove Proven content");
			}

			contexts = ms.getDomainContextsAll(dmName);

			if (!contexts.isEmpty()) {
				for (Resource context : contexts) {
					cs.removeDomainContext(context);
				}
			}

			cs.commit();
			response = Response.noContent().build();

		} catch (Exception e) {
			cs.rollback();
			log.error(e.getMessage(), e);
			response = Response.serverError().entity(e.getMessage()).build();
		}

		return response;
	}

	@GET
	@Path("{domain}/" + R_NATIVE_SOURCES)
	@Produces(TEXT_PLAIN)
	//@formatter:off
	@ApiOperation(value="Native source listing" ,
                  notes= "Provide a csv listing of domain specific native sources.",
              response = String.class,
                 hidden=true)
	//@formatter:on	
	public List<NativeSource> listNativeSources(@PathParam("domain") String dmName) {

		Response response;
		List<NativeSource> nativeSources;

		try {
			DomainModel dm = cs.findConceptByName(DomainModel.class, dmName);
			if (null == dm) {
				throw new RepositoryException("Domain not found");
			}
			nativeSources = cs.findNativeSourcesByDomain(dm);
			return nativeSources;
			// GenericEntity<List<NativeSource>> entity = new
			// GenericEntity<List<NativeSource>>(nativeSources) {
			// };
			// response = Response.ok().entity(entity).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			response = Response.serverError().entity(e.getMessage()).build();
		}
		// return response;
		return null;

	}

	@POST
	@Path("{domain}/")
	//@formatter:off
	@ApiOperation(value="Create new domain" ,
                  notes= "New domain created w/o content or structure.",
                  hidden=false)
	//@formatter:on
	public Response createDomain(@ApiParam(value = "Name of domain context") @PathParam("domain") String dmName) {

		Response response;

		try {

			cs.begin();

			DomainModel dm = cs.findConceptByName(DomainModel.class, dmName);

			if (null == dm) {
				dm = new DomainModel(dmName);
				cs.addConcept(dm);
			}

			cs.commit();

			response = Response.noContent().build();

		} catch (Exception e) {
			cs.rollback();
			log.error(e.getMessage(), e);
			response = Response.serverError().entity(e.getMessage()).build();
		}

		return response;
	}

}
