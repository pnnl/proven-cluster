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

package gov.pnnl.proven.utils;


public class Consts {

	// Bootstrap constants
	public static final String PROVEN_INF_PATH = "PROVEN-INF/";
	public static final String PRE_BOOTSTRAP = "PRE-BOOT:: ";
	public static final String POST_BOOTSTRAP = "POST-BOOT:: ";
	public static final String BOOTSTRAP_SERVICES_PATH = PROVEN_INF_PATH + "services/";
	public static final String BOOTSTRAP_PY_PATH = PROVEN_INF_PATH + "payara/";
	public static final String BOOTSTRAP_HZ_PATH = PROVEN_INF_PATH + "hazelcast/";

	// Default configuration files
	public static final String DEFAULT_PY_DOMAIN_FILE = "proven_domain.xml";
	public static final String DEFAULT_PY_DOMAIN_FILE_PATH = BOOTSTRAP_PY_PATH + DEFAULT_PY_DOMAIN_FILE;
	
	
	// ///////////////////////////////////////////////////////////////////
	// Proven constants
	public static final String FILE_SEP = System.getProperty("file.separator");
	public static final String NL = System.getProperty("line.separator");
	public final static String PSEP = System.getProperty("path.separator");
	public static final String PROVEN_MODULE_NAME = "ProvEn";
	public static final String PROVEN_VERSION = "0.1";
	public static final String PROVEN_OBJECT_REPO_ID = "ProvEnObjectStore";
	public static final String PROVEN_PROVENANCE_REPO_ID = "ProvEnProvenanceStore";
	public static final String PROVEN_OBJECT_REPO_TITLE = "ProvEn Object Repository";
	public static final String PROVEN_PROVENANCE_REPO_TITLE = "ProvEn Provenance Repository";
	public static final String PROVEN_PROVENANCE_REPO_JOURNAL = "provenance.jnl";
	public static final String PROVEN_PROVENANCE_REPO_PROPERTIES = "provenanceStore.properties";
	public static final String PROVEN_REPO_MANAGER_DIR = "PROVEN";
	public static final String PROVEN_REPO_MANAGER_REPO_DIR = "repositories";
	public static final String PROVEN_PREFIX = "proven";
	public static final String PROVEN_NS = "http://provenance.pnnl.gov/ns/proven#";
	public static final String PROVEN_BLOB_NS = "http://provenance.pnnl.gov/ns/provenBlob#";
	public static final String PROVEN_CONTEXT = PROVEN_NS + "provenContext";
	public static final String PROVEN_DEFAULT_BASE_DIR = System.getProperty("user.home");
	public static final String PROVEN_RESOURCE_DIR = "resources";
	public static final String PROVEN_FO_DIR = PROVEN_RESOURCE_DIR + "/foundation_ontologies";
	public static final String PROVEN_DO_DIR = PROVEN_RESOURCE_DIR + "/domain_ontologies";
	public static final String PROVEN_PROVENANCE_REPO_PROPERTIES_DIR = PROVEN_RESOURCE_DIR + FILE_SEP + PROVEN_PROVENANCE_REPO_PROPERTIES;
	public static final String PROVEN_JSON_LD_CONTEXT_DIR = PROVEN_RESOURCE_DIR
			+ "/META-INF/json-ld-contexts";
	public static final String PROVEN_BLOBSTORE_DIR = "BLOBSTORE";
	public static final String OPENRDF_SYSTEM_REPO_DIR = "SYSTEM";
	
	
	// ///////////////////////////////////////////////////////////////////
	// Proven TS Constants
	public static final String PROVENANCE_METRICS = "hasProvenanceMetric";
	public static final String MEASUREMENT_NAME = "measurementName";
	public static final String METRIC_NAME = "metricName";
	public static final String IS_METADATA = "isMetadata";
	

	/**
	 * Timeout in minutes for the response to each JMX client request. If the timeout is exceeded
	 * the connection will be terminated. This is the JMX environment parameter
	 * jmx.remote.x.request.waiting.timeout
	 */
	public static final int MAX_REQUEST_TIMEOUT_MINUTES = 30;

	/**
	 * Scheduled maintenance status values
	 */
	public enum MaintenanceStatus {
		ON, OFF, FAIL, COMPLETE;
	}

	public static final int MAX_SCHEDULED_MAINTENANCE_ATTEMPTS = 5;
	public static final int MAX_MAINTENANCE_INTERVAL_HOURS = 2;


	// ///////////////////////////////////////////////////////////////////
	// Foundation and Domain Model
	public static final String PROVEN_FM_NAME = "provenFM";
	public static final String PROVEN_FM_NS = "http://provenance.pnnl.gov/ns/provenFoundationModel#";
	public static final String PROVEN_DM_NS = "http://provenance.pnnl.gov/ns/provenDomainModel#";
	public static final String PROVEN_FM_PROV_O = PROVEN_RESOURCE_DIR
			+ "/ontology/prov-20120724.owl";

	// ///////////////////////////////////////////////////////////////////
	// Object connection pool Settings
	public static final int POOL_MAX_ACTIVE = -1;
	public static final int POOL_MAX_IDLE = -1;
	public static final int POOL_MIN_IDLE = 0;
	public static final boolean POOL_TEST_ON_BORROW = true;
	public static final boolean POOL_TEST_ON_RETURN = true;
	public static final int POOL_WHEN_EXHAUSTED_ACTION = 0;

	// ///////////////////////////////////////////////////////////////////
	// Repository Config
	public static final String NATIVE_TRIPLE_INDEXES = "spoc,posc";
	public static final boolean KEYWORD_ENABLED = true;
	public static final boolean OPTIMISTIC_READ_SNAPSHOT = true;
	public static final boolean OPTIMISTIC_SERIALIZABLE = false;
	public static final boolean OPTIMISTIC_SNAPSHOT = false;

}
