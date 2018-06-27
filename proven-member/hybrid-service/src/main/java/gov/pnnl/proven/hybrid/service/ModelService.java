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
import static gov.pnnl.proven.hybrid.util.Consts.*;
import static gov.pnnl.proven.hybrid.util.Utils.*;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.core.MediaType;

import org.openrdf.model.Resource;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.hybrid.concept.DomainModel;
import gov.pnnl.proven.hybrid.concept.FoundationModel;
import gov.pnnl.proven.hybrid.concept.KnowledgeModel;
import gov.pnnl.proven.hybrid.concept.Ontology;
import gov.pnnl.proven.hybrid.concept.Representation;

/**
 * Session Bean implementation class DomainService
 * 
 * Provides management services for ProvEn domains.
 * 
 */
@Stateless
@LocalBean
public class ModelService {

	private final Logger log = LoggerFactory.getLogger(ModelService.class);

	@EJB
	private ConceptService cs;

	@PostConstruct
	public void postConstruct() {
		log.debug("ModelService constructed ...");
	}

	@PreDestroy
	public void preDestroy() {
		log.debug("ModelService destroyed ...");
	}

	/**
	 * Add foundation and domain models to object store, as well as the proven
	 * namespace to both object and provenance stores. This assumes both stores
	 * have been initialized and are ready for use.
	 * 
	 * @throws Exception
	 *             if stores were not ready/initialized.
	 */
	public void addModels() throws Exception {

		// Add proven namespace to both object and provenance stores
		addProvenNamespace();

		addProvenDomainModel();

		// Load foundation model ontologies
		loadFoundationModel(PROVEN_FO_DIR);

		// Load any domain model's provided with proven
		loadProvidedDomainModels(PROVEN_DO_DIR);

	}

	public void addProvenNamespace() throws RepositoryException {
		cs.getObjectConnection().setNamespace(PROVEN_PREFIX, PROVEN_NS);
	}

	/**
	 * Creates the default Foundation Model
	 * 
	 * @param storeManager
	 * 
	 */
	public void loadFoundationModel(String fmDir) throws Exception {

		FoundationModel fm;

		try {

			cs.begin();

			fm = cs.findConceptByName(FoundationModel.class, PROVEN_FM_NAME);

			// Doesn't exist, so create it...
			// This should only need to be done once per repository
			if (null == fm) {
				log.debug("Did not find ProvEn's Foundation Model in repository, loading...");
				fm = new FoundationModel(PROVEN_FM_NAME, PROVEN_FM_NS);
				fm.setOntologies(createModelOntologies(getCpResources(PROVEN_FO_DIR)));

				// Commit Foundation model, including it's ontologies and
				// representations
				cs.addConcept(fm);

				// Load structure information, each ontology will be loaded
				// into it's own context
				for (Ontology o : fm.getOntologies()) {
					loadStructure(fm, o);
				}
			}

			cs.commit();

		} catch (Exception e) {
			cs.rollback();
			log.error(e.getMessage(), e);
		}

	}

	public void addProvenDomainModel() {

		try {

			cs.begin();

			DomainModel dm;
			String domainName = PROVEN_PREFIX;
			dm = cs.findConceptByName(DomainModel.class, domainName);

			// Doesn't exist, so create it...
			// This should only need to be done once per repository
			if (null == dm) {

				log.debug("Did not find Domain Model for :: " + domainName);
				dm = new DomainModel(domainName);

				// Override default EC context - use predefined constant value
				dm.getExplicitContent().setContextUri(PROVEN_CONTEXT);

				// Commit Domain model, including it's ontologies and
				// representations
				cs.addConcept(dm);

			}

			cs.commit();

		} catch (Exception e) {
			cs.rollback();
			log.error("Adding proven domain model failed");
		}

	}

	public void loadProvidedDomainModels(String domainDir) throws Exception {

		File[] domains = getCpResource(domainDir).listFiles();

		try {

			cs.begin();

			for (File domain : domains) {

				DomainModel dm;
				String domainName = domain.getName();
				log.debug("Loading domain :: " + domain.getPath());

				dm = cs.findConceptByName(DomainModel.class, domainName);

				// Doesn't exist, so create it...
				// This should only need to be done once per repository
				if (null == dm) {
					log.debug("Did not find Domain Model for :: " + domainName);
					dm = new DomainModel(domainName);
					dm.setOntologies(createModelOntologies(domain.listFiles()));

					if (dm.getOntologies().size() == 0) {
						log.debug("Zero ontology files for domain :: " + domainName);
						throw new Exception("Loading domain " + domainName
								+ " failed.  Must have at least one ontology to define a domain model");
					}

					// Commit Domain model, including it's ontologies and
					// representations
					cs.addConcept(dm);

					// Load structure information, each ontology will be loaded
					// into it's own context
					for (Ontology o : dm.getOntologies()) {
						loadStructure(dm, o);
					}
				}

			}

			cs.commit();

		} catch (Exception e) {
			cs.rollback();
			log.error("Loading domain models failed");
		}

	}

	public void loadStructure(KnowledgeModel km, Ontology source) throws Exception {

		try {
			cs.begin();

			URL location = source.getLocation().toURL();
			String baseUri = km.getBaseUri();
			RDFFormat format = source.getRdfFormat();
			Resource context = toResource(source.getContext().getContextUri());
			log.debug("Loading structure :: " + location);
			cs.getObjectConnection().add(location, baseUri, format, context);

			cs.commit();

		} catch (Exception e) {
			cs.rollback();
			log.error("Loading km structure failed");
			log.error("km base uri: " + km.getBaseUri());
			log.error("ontology source locaiton: " + source.getLocation().toString());
		}

	}

	// TODO Check for inferred structure, and add it to the inferred structure
	// context if any found.
	// TODO Add support for remote resources
	public void loadContext(DomainModel dm, URI source, Resource context) throws Exception {

		try {
			cs.begin();
			
			String sourceName = source.toString();
			URL location = source.toURL();
			String baseUri = dm.getBaseUri();
			RDFFormat format = RDFFormat.forFileName(sourceName, RDFFormat.TURTLE);
			log.debug("Loading content :: " + location);
			cs.getObjectConnection().add(location, baseUri, format, context);
			
			cs.commit();
			
		} catch (Exception e) {
			cs.rollback();
			log.error("Loading dm content failed");
		}

	}

	private Set<Ontology> createModelOntologies(File[] ontologyFiles) throws Exception {

		Set<Ontology> ret = new HashSet<Ontology>();

		for (File ontologyFile : ontologyFiles) {
			ret.add(createModelOntology(ontologyFile));
		}

		return ret;
	}

	public Ontology createModelOntology(File ontologyFile) throws Exception {

		String name = ontologyFile.getName();
		java.net.URI location = ontologyFile.toURI();
		RDFFormat rf = RDFFormat.forFileName(name, RDFFormat.TURTLE);
		MediaType mt = MediaType.valueOf(rf.getDefaultMIMEType());
		Representation rep = new Representation(name, mt, location);
		cs.addBlob(rep);

		return new Ontology(name, location, rf, rep);

	}

	public Ontology createModelOntology(URI ontologyURI) throws Exception {
		String name = ontologyURI.toString();
		//java.net.URI location = ontologyFile.toURI();
		RDFFormat rf = RDFFormat.forFileName(name, RDFFormat.TURTLE);
		MediaType mt = MediaType.valueOf(rf.getDefaultMIMEType());
		Representation rep = new Representation(name, mt, ontologyURI);
		cs.addBlob(rep);
		return new Ontology(name, ontologyURI, rf, rep);
	}
	
	public List<Resource> getDomainContextsAll(String domain) {

		List<Resource> ret = getDomainContextStructure(domain);
		Resource ec = getDomainContextContent(domain);
		if (null != ec) {
			ret.add(ec);
		}
		return ret;
	}

	public Resource getDomainContextContent(String domain) {

		Resource ret = null;
		try {
			DomainModel dm = cs.findConceptByName(DomainModel.class, domain);

			if (null != dm) {
				ret = toResource(dm.getExplicitContent().getContextUri());
			}

		} catch (Exception e) {
			ret = null;
		}

		return ret;
	}

	public List<Resource> getDomainContextStructure(String domain) {

		List<Resource> ret = new ArrayList<Resource>();

		try {

			DomainModel dm = cs.findConceptByName(DomainModel.class, domain);

			if (null != dm) {

				Set<Ontology> onts = dm.getOntologies();
				for (Ontology ont : onts) {
					Resource r = toResource(ont.getContext().getContextUri());
					ret.add(r);
				}

			}
			
		} catch (Exception e) {
			ret = new ArrayList<Resource>();
		}

		return ret;

	}

}
