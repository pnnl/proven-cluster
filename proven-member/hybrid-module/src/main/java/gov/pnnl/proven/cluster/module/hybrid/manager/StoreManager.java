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

package gov.pnnl.proven.cluster.module.hybrid.manager;

import static gov.pnnl.proven.cluster.module.hybrid.concept.ConceptUtil.*;
import static gov.pnnl.proven.cluster.module.hybrid.util.Consts.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.config.SailRepositoryConfig;
import org.openrdf.sail.keyword.config.KeywordConfig;
import org.openrdf.sail.nativerdf.config.NativeStoreConfig;
import org.openrdf.sail.optimistic.config.OptimisticConfig;
import org.openrdf.store.blob.BlobStore;
import org.openrdf.store.blob.BlobStoreFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.cluster.module.hybrid.service.ModelService;
import gov.pnnl.proven.hybrid.util.ProvenConfig;
import gov.pnnl.proven.hybrid.util.ProvenConfig.ProvenEnvProp;

//import com.bigdata.journal.Options;
//import com.bigdata.rdf.sail.BigdataSail;
//import com.bigdata.rdf.sail.BigdataSailRepository;
//import com.bigdata.rdf.sail.config.BigdataRepositoryFactory;
//import com.bigdata.rdf.sail.config.BigdataSailConfig;
//import com.bigdata.rdf.sail.config.BigdataSailFactory;
//import com.bigdata.util.Util;

/**
 * Session Bean implementation class StoreManager
 * 
 * Manages ProvEn's repository and connection pool. This is the startup bean for
 * ProvEn.
 * 
 */
@Singleton
@LocalBean
public class StoreManager {

	private final Logger log = LoggerFactory.getLogger(StoreManager.class);

	ProvenConfig pg = ProvenConfig.getB2SConfig();

	@EJB
	private PropertiesManager pm;

	@EJB
	private ModelService ms;

	private File baseDir;
	private LocalRepositoryManager repoManager;
	private ObjectRepository objRepo;
	private Repository provRepo;

	@PostConstruct
	public void postConstruct() {
	}

	@PreDestroy
	public void preDestroy() {
	}

	public boolean isReady() {
		//return (isObjectStoreReady() && isProvenanceStoreReady());
		return (isObjectStoreReady());
	}

	public boolean isObjectStoreReady() {

		boolean ret = false;

		try {
			if (null != repoManager) {
				if (null != objRepo) {
					ret = (objRepo.isInitialized() && objRepo.isWritable());
				}
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
			ret = false;
		}

		return ret;

	}

	public boolean isProvenanceStoreReady() {

		boolean ret = false;

		try {
			if (null != repoManager) {
				if (null != provRepo) {
					ret = (provRepo.isInitialized() && provRepo.isWritable());
				}
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
			ret = false;
		}

		return ret;

	}

	public void start() throws Exception {

		if (!isReady()) {

			// Force a shutdown
			stop();

			try {

				// Get repository manager location
				if (null == baseDir) {
					baseDir = getRepoManagerDir();
				}

				// Create repository manager, if necessary
				if (null == repoManager) {
					repoManager = new LocalRepositoryManager(baseDir);
				}

				// Initialize repository manager
				repoManager.initialize();

				// Create and initialize Proven's object and provenance
				// repositories
				objRepo = createObjectStore(repoManager);
				// provRepo = createProvenanceStore(repoManager);

				// Assign Blobstore for local Native Source content storage
				File blobStoreDir = new File(baseDir + FILE_SEP + PROVEN_BLOBSTORE_DIR);
				blobStoreDir.mkdir();
				BlobStoreFactory factory = BlobStoreFactory.newInstance();
				BlobStore store = factory.openBlobStore(blobStoreDir);
				objRepo.setBlobStore(store);

			} catch (Exception e) {
				e.printStackTrace();
				stop();
				throw e;
			}
		}
	}

	/**
	 * Forces a shutdown for the repository manager and both object and
	 * provenance repositories, if they exist.
	 */
	public void stop() {

		if (null != repoManager) {
			if (null != objRepo) {
				if (objRepo.isInitialized()) {
					try {
						objRepo.shutDown();
					} catch (RepositoryException e) {
						log.debug("Repository shutdown failed for object store");
						e.printStackTrace();
					}
				}
				objRepo = null;
			}
			if (null != provRepo) {
				if (provRepo.isInitialized()) {
					try {
						provRepo.shutDown();
					} catch (RepositoryException e) {
						log.debug("Repository shutdown failed for provenance store");
						e.printStackTrace();
					}
				}
				provRepo = null;
			}
			repoManager.shutDown();
			repoManager = null;
		}
	}

	/**
	 * Retrieves an object store connection from pool. Default settings for the
	 * connection are used:
	 * <p>
	 * <ul>
	 * <li>Auto Commit = false</li>
	 * <li>Insert Context = PROVEN CONTEXT</li>
	 * <li>Read Context = PROVEN CONTEXT</li>
	 * <li>Remove Context = PROVEN CONTEXT</li>
	 * </ul>
	 * 
	 * @return returns an object connection
	 * @throws Exception
	 *             if connection retrieval fails
	 */
	public ObjectConnection getObjectStoreConnection() throws RepositoryException {

		ObjectConnection ret;

		if (!isObjectStoreReady()) {
			throw new RepositoryException("Object Repository currently unavailable");
		}

		boolean autoCommit = false;
		URI insertContext = toUri(PROVEN_CONTEXT);
		URI[] readContexts = { toUri(PROVEN_CONTEXT) };
		URI[] removeContexts = { toUri(PROVEN_CONTEXT) };
		try {
			ret = objRepo.getConnection();
			ret.setAutoCommit(autoCommit);
			ret.setInsertContext(insertContext);
			//Entire repo by default for reads
			//ret.setReadContexts(readContexts);
			ret.setRemoveContexts(removeContexts);
		} catch (RepositoryException e) {
			log.error("Get object store connection failed");
			e.printStackTrace();
			throw e;
		}

		return ret;
	}

	/**
	 * Retrieves a provenance store connection from pool.
	 * 
	 * @return returns a repository connection
	 * @throws Exception
	 *             if connection retrieval fails
	 */
	public RepositoryConnection getProvenanceStoreConnection() throws RepositoryException {

		RepositoryConnection ret;

		// if (!isProvenanceStoreReady()) {
		// throw new RepositoryException("Object Repository currently
		// unavailable");
		// }

		try {
			ret = provRepo.getConnection();
		} catch (RepositoryException e) {
			log.error("Get provenance store connection failed");
			e.printStackTrace();
			throw e;
		}

		return ret;
	}

	/**
	 * Creates and initializes the ProvEn object store using the provided
	 * repository manager. If ProvEn store already exists (i.e. created from a
	 * previous call) then it's retrieved from the repository manager,
	 * initialized, and returned.
	 * 
	 * @return returns the initialized store
	 * @throws Exception
	 * 
	 */
	private ObjectRepository createObjectStore(LocalRepositoryManager repoManager) throws Exception {

		Repository managedRepo = null;
		ObjectRepository ret = null;

		// if repository already exists, then retrieve from repository manager,
		// The manager initializes the repository for us
		managedRepo = repoManager.getRepository(PROVEN_OBJECT_REPO_ID);

		// If repository does not exist then create it...
		if (null == managedRepo) {

			// Get proven configuration and register with repository manager
			RepositoryConfig config = getProvenObjectStoreConfig();
			// RepositoryConfig config = getProvenBlazegraphConfig();
			repoManager.addRepositoryConfig(config);

			// Get/create the new repository
			managedRepo = repoManager.getRepository(PROVEN_OBJECT_REPO_ID);

		}

		// Create object repository using the managed repository as its delegate
		ObjectRepositoryFactory objFac = new ObjectRepositoryFactory();
		ret = objFac.createRepository(managedRepo);

		return ret;
	}

	/**
	 * Creates and initializes the ProvEn provenance store using the provided
	 * repository manager. If ProvEn provenance store already exists (i.e.
	 * created from a previous call) then it's retrieved from the repository
	 * manager, initialized, and returned.
	 * 
	 * @return returns the initialized store
	 * @throws Exception
	 * 
	 */
	private Repository createProvenanceStore(LocalRepositoryManager repoManager) throws Exception {

		Repository ret = null;

		// Disable BG Store

		// if repository already exists, then retrieve from repository manager,
		// The manager initializes the repository for us
		// ret = repoManager.getRepository(PROVEN_PROVENANCE_REPO_ID);
		//
		// // If repository does not exist then create it...
		// if (null == ret) {
		//
		// // Get provenance store configuration and register with repository
		// manager
		// RepositoryConfig config =
		// getProvenProvenanceStoreConfig(repoManager);
		// repoManager.addRepositoryConfig(config);
		//
		// // Get/create the new repository
		// ret = repoManager.getRepository(PROVEN_PROVENANCE_REPO_ID);
		// ret.initialize();
		// }

		return ret;
	}

	/**
	 * Creates and returns file location of ProvEn's repository manager's base
	 * directory.
	 * 
	 * @return returns File object representing base directory for ProvEn's
	 *         repository manager
	 * 
	 * @throws IOException
	 *             if directory could not be created
	 */
	private File getRepoManagerDir() throws IOException {

		File ret;

		String repoManagerDir = FILE_SEP + PROVEN_REPO_MANAGER_DIR;
		//String baseDir = pm.getStoreBaseDir();

		repoManagerDir = pg.getPropValue(ProvenEnvProp.PROVEN_T3DIR) + repoManagerDir;

//		 Old repo dir value setting
//		 if (null == baseDir) {
//		 if ((null == T3_DIR) || (T3_DIR.isEmpty())) {
//		 repoManagerDir = T3_DIR_DEFAULT + repoManagerDir;
//		 } else {
//		 repoManagerDir = T3_DIR + repoManagerDir;
//		 }
//		 } else {
//		 repoManagerDir = baseDir + repoManagerDir;
//		 }

		ret = new File(repoManagerDir);
		if (ret.exists()) {
			if (!ret.isDirectory()) {
				throw new IOException(
						"Could not create ProvEn base directory : " + ret.getAbsolutePath() + "  File already exists.");
			}
		} else {
			if (!ret.mkdir()) {
				throw new IOException("Could not create ProvEn base directory : " + ret.getAbsolutePath());
			}
		}

		return ret;
	}

	private RepositoryConfig getProvenObjectStoreConfig() throws IOException, OpenRDFException {

		// Native Store Configuration
		NativeStoreConfig NSConfig = new NativeStoreConfig();
		NSConfig.setTripleIndexes(NATIVE_TRIPLE_INDEXES);

		// Keyword Configuration
		KeywordConfig keywordConfig = new KeywordConfig();
		keywordConfig.setEnabled(KEYWORD_ENABLED);

		// Optimistic Config
		OptimisticConfig optimisticConfig = new OptimisticConfig();
		optimisticConfig.setReadSnapshot(OPTIMISTIC_READ_SNAPSHOT);
		optimisticConfig.setSerializable(OPTIMISTIC_SERIALIZABLE);
		optimisticConfig.setSnapshot(OPTIMISTIC_SNAPSHOT);

		// Create the stack
		keywordConfig.setDelegate(NSConfig);
		optimisticConfig.setDelegate(keywordConfig);

		// Create a configuration for the sail repository
		SailRepositoryConfig sailRepositoryConfig = new SailRepositoryConfig(optimisticConfig);

		// Create and return the proven repository configuration
		return new RepositoryConfig(PROVEN_OBJECT_REPO_ID, PROVEN_OBJECT_REPO_TITLE, sailRepositoryConfig);
	}

	// private RepositoryConfig
	// getProvenProvenanceStoreConfig(LocalRepositoryManager repoManager)
	// throws IOException, OpenRDFException, URISyntaxException {
	//
	// Properties properties = new Properties();
	// InputStream is = null;
	// OutputStream os = null;
	// File propOutFile;
	//
	// try {
	//
	// File repoDir = getProvenanceStoreDir(repoManager);
	//
	// // Get default props
	// File propInFile =
	// Utils.getCpResource(PROVEN_PROVENANCE_REPO_PROPERTIES_DIR);
	// is = new FileInputStream(propInFile);
	// properties.load(is);
	//
	// // Add in location of Journal file - must be set at runtime
	// properties.put(Options.FILE,
	// repoDir.getPath() + FILE_SEP + PROVEN_PROVENANCE_REPO_JOURNAL);
	//
	// // Write properties out to repository directory
	// propOutFile = new File(
	// repoDir.getPath() + FILE_SEP + PROVEN_PROVENANCE_REPO_PROPERTIES);
	// os = new FileOutputStream(propOutFile);
	// properties.store(os, "Provenance store properties");
	//
	// } finally {
	// if (is != null)
	// is.close();
	// if (os != null)
	// os.close();
	// }
	//
	// // Get the bigdata sail config and set properties
	// BigdataSailConfig bigdataSailConfig = (BigdataSailConfig) new
	// BigdataSailFactory()
	// .getConfig();
	// bigdataSailConfig.setPropertiesFile(propOutFile.getPath().toString());
	//
	// // Create a configuration for the sail repository
	// SailRepositoryConfig sailRepositoryConfig = new
	// SailRepositoryConfig(bigdataSailConfig);
	//
	// // Create and return the provenance repository configuration
	// return new RepositoryConfig(PROVEN_PROVENANCE_REPO_ID,
	// PROVEN_PROVENANCE_REPO_TITLE,
	// sailRepositoryConfig);
	// }

	private File getProvenanceStoreDir(LocalRepositoryManager repoManager) throws IOException, URISyntaxException {

		String repoDirStr = repoManager.getLocation().toURI().getPath().toString() + FILE_SEP
				+ PROVEN_REPO_MANAGER_REPO_DIR + FILE_SEP + PROVEN_PROVENANCE_REPO_ID;
		File repoDir = new File(repoDirStr);

		if (repoDir.exists()) {
			if (!repoDir.isDirectory()) {
				throw new IOException("Could not create provencance store's base directory : "
						+ repoDir.getAbsolutePath() + "  File already exists.");
			}
		} else {
			if (!repoDir.mkdirs()) {
				throw new IOException(
						"Could not create provenance store's base directory : " + repoDir.getAbsolutePath());
			}
		}

		return repoDir;
	}
}
