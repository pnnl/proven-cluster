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
import static gov.pnnl.proven.disclosure.util.Utils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import gov.pnnl.proven.disclosure.concept.Concept;
import gov.pnnl.proven.disclosure.concept.ConceptUtil;
import gov.pnnl.proven.disclosure.concept.DomainModel;
import gov.pnnl.proven.disclosure.concept.NativeSource;
import gov.pnnl.proven.disclosure.concept.Representation;
import gov.pnnl.proven.disclosure.concept.ConceptUtil.BlobStatus;
import gov.pnnl.proven.disclosure.manager.StoreManager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.result.NoResultException;
import org.openrdf.store.blob.BlobObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class ProvenanceService
 * 
 * Provides management and persistent services for provenance store.
 * 
 */
@Stateless
@LocalBean
public class ProvenanceService {

	private final Logger log = LoggerFactory.getLogger(ProvenanceService.class);

	@EJB
	private StoreManager sm;

	private RepositoryConnection pCon = null;

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
			log.debug("GETTING A NEW PROVENANCE STORE CONNECTION!!!");
			//this.pCon = sm.getProvenanceStoreConnection();
		} catch (Exception e) {
			e.printStackTrace();
			throw new EJBException("Borrow connection failed in construction of ProvenanceService");
		}
		log.debug("ProvenanceService constructed ...");

	}

	@PreDestroy
	public void preDestroy() {
		try {
			// TODO should uncommitted changes just be lost?
			// force a close and commit changes
			pCon.commit();
			pCon.close();
			log.debug("ProvenanceService destroyed ...");
		} catch (RepositoryException e) {
			// Swallow it, not need to throw an ejb exception at this point
			e.printStackTrace();
		}
		log.debug("ProvenanceService destroyed ...");
	}

	/**
	 * Commits all changes and creates a new connection to the repository
	 * 
	 * @throws Exception
	 */
	public void flush() {

//		try {
//			if (isValidConnection()) {
//				pCon.commit();
//				pCon.close();
//			}
//			pCon = sm.getProvenanceStoreConnection();
//
//		} catch (Exception e) {
//			log.error("FLUSH FAILURE");
//			e.printStackTrace();
//		}
	}

	public void begin() {

		try {
			if (!pCon.isActive()) {
				pCon.begin();
			}

		} catch (Exception e) {
			log.error("BEGIN FAILURE");
			e.printStackTrace();
		}

	}

	public void commit() {

		try {
			if (pCon.isActive()) {
				pCon.commit();
			}

		} catch (Exception e) {
			log.error("COMMIT FAILURE");
			e.printStackTrace();
		}

	}

	public void rollback() {

		try {
			if (pCon.isActive()) {
				pCon.rollback();
			}

		} catch (Exception e) {
			log.error("ROLLBACK FAILURE");
			e.printStackTrace();
		}

	}

	public RepositoryConnection getProvenanceConnection() {
		return pCon;
	}


	public void addStatements(Collection<Statement> statements, Resource... resources)
			throws Exception {
		pCon.add(statements, resources);
	}

	public RepositoryResult<Statement> getAllStatements() throws Exception {

		RepositoryResult<Statement> ret = null;
		ret = pCon.getStatements(null, null, null, false);
		return ret;

	}

	public RepositoryResult<Statement> getDomainStatements(String domain) throws Exception {

		RepositoryResult<Statement> ret = null;
		ret = pCon.getStatements(null, null, null, false, ConceptUtil.toResource(domain));
		return ret;
	}
	
	
	public void removeDomainContext(Resource context) throws RepositoryException {
		pCon.clear(context);
		pCon.commit();
	}

	public void removeAll() throws RepositoryException {
		pCon.clear();
		pCon.commit();
	}

	@AroundInvoke
	public Object checkObjectConnection(InvocationContext ic) throws Exception {

		try {
			if (!isValidConnection()) {
				log.debug("GETTING A NEW PROVENANCE STORE CONNECTION!!!");
				//pCon = sm.getProvenanceStoreConnection();
			}
		} catch (Exception e) {
			throw new EJBException("Return/Borrow new provenance store connection failed");
		}

		return ic.proceed();
	}

	private boolean isValidConnection() {

		boolean ret = true;

//		try {
//
//			if ((!pCon.getRepository().isInitialized()) || (!pCon.isOpen())) {
//				ret = false;
//				// Force a close, may cause an exception.
//				pCon.close();
//			}
//		} catch (Exception e) {
//			ret = false;
//		}
//
		return ret;
	}

}
