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
package gov.pnnl.proven.cluster.lib.member;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.cluster.lib.member.exception.MemberConfigurationException;

/**
 * Stores properties for a proven cluster member. For each module startup
 * process, these properties are used. The properties are defined in the
 * {@code proven-system-properties} file. Unsuccessful acquisition and/or
 * verification of these properties will short circuit a module's startup
 * process.
 * 
 * Convenience methods are provided to get the property values.
 * 
 * TODO Depending on the number of properties to manage, this may need to be
 * split up based on module type.
 * 
 * @author d3j766
 *
 */
public class MemberProperties {

    static Logger log = LoggerFactory.getLogger(MemberProperties.class);

    private static final char PROPERTY_LIST_DELIMITER = ',';

    /**
     * Proven Member properties
     */
    private static final String MEMBER_INSTALL_ROOT_PROP = "proven.member.install.root";
    private static final String MEMBER_PROVEN_INF_DIR = "PROVEN-INF/";
    private static final String MEMBER_PROVEN_INF_DEPLOY_DIR = MEMBER_PROVEN_INF_DIR + "deploy/";
    private static final String MEMBER_PROVEN_INF_DOMAIN_DIR = MEMBER_PROVEN_INF_DIR + "domain/";
    private static final String MEMBER_PROVEN_INF_LIB_DIR = MEMBER_PROVEN_INF_DIR + "lib/";
    private static final String MEMBER_PROVEN_INF_PAYARA_DIR = MEMBER_PROVEN_INF_LIB_DIR + "payara-micro/";
    private static final String MEMBER_PROVEN_INF_PIPELINE_DIR = MEMBER_PROVEN_INF_LIB_DIR + "pipeline-request/";
    private static final String MEMBER_PROVEN_INF_MODULE_DIR = MEMBER_PROVEN_INF_LIB_DIR + "module-request/";
    private static final String MEMBER_PROPERTIES_FILE_NAME = "proven-system-properties";
    private static final String MEMBER_DEFAULT_PROPERTIES_FILE = MEMBER_PROVEN_INF_DIR + MEMBER_PROPERTIES_FILE_NAME;
    private static final String MEMBER_POSTBOOT_COMMANDS_FILE_NAME = "postboot-command-file";
    private static final String MEMBER_POSTBOOT_COMMANDS_FILE = MEMBER_PROVEN_INF_DIR
	    + MEMBER_POSTBOOT_COMMANDS_FILE_NAME;
    private static final String MEMBER_HAZELCAST_CONFIG_FILE_NAME = "hazelcast-proven-data";
    private static final String MEMBER_HAZELCAST_CONFIG_FILE = MEMBER_PROVEN_INF_DIR
	    + MEMBER_HAZELCAST_CONFIG_FILE_NAME;

    /**
     * Hazelcast properties
     */
    private static final String HAZELCAST_MEMBERS_PROP = "proven.hazelcast.members";
    private static final String HAZELCAST_MEMBER_PORT_PROP = "proven.hazelcast.member.port";
    private static final String HAZELCAST_GROUP_NAME = "proven.hazelcast.group.name";

    /**
     * Hazelcast jet properties
     */
    private static final String JET_INSTANCE_NETWORK_ADDRESSES = "proven.jet.instance.network.addresses";
    private static final String JET_INSTANCE_TEST_PORT = "proven.jet.instance.test.port";
    private static final String JET_CLUSTER_NAME = "proven.jet.cluster.name";

    /**
     * Exchange properties
     */
    private static final String EXCHANGE_QUEUE_NAME = "proven.lib.module.exchange.queue.exchange.name";
    private static final String DISCLOSURE_QUEUE_NAME = "proven.lib.module.exchange.queue.disclosure.name";
    private static final String EXCHANGE_QUEUE_MAX_SIZE = "proven.lib.module.exchange.queue.exchange.max_size";
    private static final String DISCLOSURE_QUEUE_MAX_SIZE = "proven.lib.module.exchange.queue.disclosure.max_size";
    private static final String EXCHANGE_BUFFER_CAPACITY = "proven.lib.module.exchange.buffer.capacity";
    private static final String SERVICE_BUFFER_CAPACITY = "proven.lib.module.exchange.buffer.service.capacity";

    /**
     * Stream properties
     */
    private static final String SEMANTIC_ENGINE = "proven.lib.module.stream.semantic.engine";

    /**
     * Registry properties
     */
    private static final String MANAGED_COMPONENT_MAX_RETRIES = "proven.lib.module.managed_component.max_retries";
    private static final String CLUSTER_COMPONENT_REGISTRY_NAME = "proven.lib.module.registry.component.cluster.name";
    private static final String MEMBER_EXCHANGE_NAME = "proven.lib.module.registry.member_exchange.name";
    private static final String MEMBER_LOCATION_NAME = "proven.lib.module.registry.member_location.name";
    private static final String MEMBER_LOCATION_SELECTOR_ENABLED = "proven.lib.module.registry.member_location_selector.enabled";
    private static final String TASK_SCHEDULE_MAX_SKIPPED_ENTRY_REPORTS = "proven.lib.module.schedule.task.max_skipped_entry_reports";
    private static final String PROVEN_DISCLOSURE_MAP_NAME = "proven.lib.module.registry.disclosure.map.name";
    private static final String MEMBER_EXCHANGE_SELECTION_NAME = "proven.lib.module.registry.member_exchange_selection.name";

    /**
     * hybrid-module properties
     */
    private static final String HYBRID_T3_SERVICE_URL = "proven.module.hybrid.t3.serviceUrl";
    private static final String HYBRID_TS_SERVICE_URL = "proven.module.hybrid.ts.serviceUrl";
    private static final String HYBRID_TS_USE_IDB = "proven.module.hybrid.ts.useIdb";
    private static final String HYBRID_TS_IDB_DB = "proven.module.hybrid.ts.idbDB";
    private static final String HYBRID_TS_IDB_RP = "proven.module.hybrid.ts.idbRP";
    private static final String HYBRID_TS_IDB_USERNAME = "proven.module.hybrid.ts.idbUsername";
    private static final String HYBRID_TS_IDB_PASSWORD = "proven.module.hybrid.ts.idbPassword";

    /**
     * Singleton instance of MemberProperties
     */
    private static MemberProperties instance = null;

    /**
     * (Required) Installation root path must be provided.
     */
    private String installRoot;

    private MemberProperties() {

	/**
	 * Get and configure installation root path. If not provided throw exception.
	 */
	installRoot = System.getProperty(MEMBER_INSTALL_ROOT_PROP);
	if (null == installRoot) {
	    throw new MemberConfigurationException("Missing " + MEMBER_INSTALL_ROOT_PROP + " value");
	}
	installRoot.trim();
	if (!installRoot.endsWith("/")) {
	    installRoot += "/";
	}
    }

    public static MemberProperties getInstance() {

	/**
	 * Create singleton, if necessary
	 */
	if (null == instance) {
	    instance = new MemberProperties();
	}

	return instance;
    }

    /**
     * Convenience methods to retrieve property values.
     */

    //////////////////////////////////////////////////////
    // MEMBER PROPERTY METHODS

    public File getInstallRootDir() {
	return new File(installRoot);
    }

    public File getDeployDir() {
	return new File(installRoot, MEMBER_PROVEN_INF_DEPLOY_DIR);
    }

    public File getDomainDir() {
	return new File(installRoot, MEMBER_PROVEN_INF_DOMAIN_DIR);
    }

    public File getPayaraLibsDir() {
	return new File(installRoot, MEMBER_PROVEN_INF_PAYARA_DIR);
    }

    public File getPipelineServiceLibsDir() {
	return new File(installRoot, MEMBER_PROVEN_INF_PIPELINE_DIR);
    }

    public File getModuleRequestLibsDir() {
	return new File(installRoot, MEMBER_PROVEN_INF_MODULE_DIR);
    }

    public File getDefaultPropertiesFile() {
	return new File(installRoot, MEMBER_DEFAULT_PROPERTIES_FILE);
    }

    public File getPostBootCommandsFile() {
	return new File(installRoot, MEMBER_POSTBOOT_COMMANDS_FILE);
    }

    public File getHazelcastConfigFile() {
	return new File(installRoot, MEMBER_HAZELCAST_CONFIG_FILE);
    }

    //////////////////////////////////////////////////////
    // HAZELCAST PROPERTY METHODS

    public List<String> getHazelcastMembers() {
	return getPropertyValues(HAZELCAST_MEMBERS_PROP, String.class);
    }

    public String getHazelcastGroupName() {
	return getPropertyValue(HAZELCAST_GROUP_NAME, String.class);
    }

    public Integer getHazelcastMemberPort() {
	return getPropertyValue(HAZELCAST_MEMBER_PORT_PROP, Integer.class);
    }

    //////////////////////////////////////////////////////
    // HAZELCAST JET PROPERTY METHODS
    public List<String> getJetInstanceAddresses() {
	return getPropertyValues(JET_INSTANCE_NETWORK_ADDRESSES, String.class);
    }

    public Integer getJetInstanceTestPort() {
	return getPropertyValue(JET_INSTANCE_TEST_PORT, Integer.class);
    }

    public String getJetGroupName() {
	return getPropertyValue(JET_CLUSTER_NAME, String.class);
    }

    //////////////////////////////////////////////////////
    // MODULE-LIB EXCHANGE PROPERTY METHODS
    public String getExchangeQueueName() {
	return getPropertyValue(EXCHANGE_QUEUE_NAME, String.class);
    }

    public String getDisclosureQueueName() {
	return getPropertyValue(DISCLOSURE_QUEUE_NAME, String.class);
    }

    public Integer getExchangeQueueMaxSize() {
	return getPropertyValue(EXCHANGE_QUEUE_MAX_SIZE, Integer.class);
    }

    public Integer getDisclosureQueueMaxSize() {
	return getPropertyValue(DISCLOSURE_QUEUE_MAX_SIZE, Integer.class);
    }

    public Integer getExchangeBufferCapacity() {
	return getPropertyValue(EXCHANGE_BUFFER_CAPACITY, Integer.class);
    }

    public Integer getServiceBufferCapacity() {
	return getPropertyValue(SERVICE_BUFFER_CAPACITY, Integer.class);
    }

    //////////////////////////////////////////////////////
    // MODULE-LIB REGISTRY PROPERTY METHODS
    public Integer getManagedComponentMaxRetries() {
	return getPropertyValue(MANAGED_COMPONENT_MAX_RETRIES, Integer.class);
    }

    public String getClusterComponentRegistryName() {
	return getPropertyValue(CLUSTER_COMPONENT_REGISTRY_NAME, String.class);
    }

    public String getMemberExchangeName() {
	return getPropertyValue(MEMBER_EXCHANGE_NAME, String.class);
    }

    public String getMemberLocationName() {
	return getPropertyValue(MEMBER_LOCATION_NAME, String.class);
    }
    
    public Boolean isMemberLocationSelectorEnabled() {
	return getPropertyValue(MEMBER_LOCATION_SELECTOR_ENABLED, Boolean.class);
    }
    
    public Integer getTaskScheduleMaxSkippedEntryReports() {
	return getPropertyValue(TASK_SCHEDULE_MAX_SKIPPED_ENTRY_REPORTS, Integer.class);
    }

    public String getProvenDisclosureMapName() {
	return getPropertyValue(PROVEN_DISCLOSURE_MAP_NAME, String.class);
    }

    public String getMemberExchangeSelectionName() {
	return getPropertyValue(MEMBER_EXCHANGE_SELECTION_NAME, String.class);
    }
    
    //////////////////////////////////////////////////////
    // MODULE-LIB STREAM PROPERTY METHODS
//	public SemanticEngine getSemanticEngine() {
//		String ge = getPropertyValue(SEMANTIC_ENGINE, String.class);
//		return SemanticEngine.valueOf(ge);
//	}	

    //////////////////////////////////////////////////////
    // HYBRID MODULE PROPERTY METHODS
    public String getHybridT3ServiceUrl() {
	return getPropertyValue(HYBRID_T3_SERVICE_URL, String.class);
    }

    public String getHybridTsServiceUrl() {
	return getPropertyValue(HYBRID_TS_SERVICE_URL, String.class);
    }

    public Boolean getHybridTsUseIdb() {
	return getPropertyValue(HYBRID_TS_USE_IDB, Boolean.class);
    }

    public String getHybridTsIdbDb() {
	return getPropertyValue(HYBRID_TS_IDB_DB, String.class);
    }

    public String getHybridTsIdbRp() {
	return getPropertyValue(HYBRID_TS_IDB_RP, String.class);
    }

    public String getHybridTsIdbUsername() {
	return getPropertyValue(HYBRID_TS_IDB_USERNAME, String.class);
    }

    public String getHybridTsIdbPassword() {
	return getPropertyValue(HYBRID_TS_IDB_PASSWORD, String.class);
    }

    /**
     * Returns the property value for the provided type.
     * 
     * @param key
     *            property key
     * @param propertyType
     *            type of property. String and Integer are supported.
     * 
     * @throws MemberConfigurationException
     *             if property type not supported.
     * 
     * @return the property value. null if the key does not exist.
     */
    @SuppressWarnings("unchecked")
    private <T> T getPropertyValue(String key, Class<T> propertyType) throws MemberConfigurationException {

	T ret = null;
	String value = System.getProperty(key);
	if (null != value) {

	    if (propertyType.equals(String.class)) {
		ret = (T) value;
	    } else if (propertyType.equals(Integer.class)) {
		ret = ((T) Integer.valueOf(value));
	    } else if (propertyType.equals(Boolean.class)) {
		ret = ((T) Boolean.valueOf(value));
	    } else {
		throw new MemberConfigurationException("Unsupported property type");
	    }
	}

	return ret;
    }

    /**
     * Returns the property values as a list for the provided type.
     * 
     * @param key
     *            property key
     * @param propertyType
     *            type of property. String and Integer are supported.
     * 
     * @throws MemberConfigurationException
     *             if property type not supported.
     * 
     * @return the property value. null if the key does not exist.
     */
    @SuppressWarnings("unchecked")
    private <T> List<T> getPropertyValues(String key, Class<T> propertyType) {

	List<T> ret = null;
	String values = System.getProperty(key);
	if (null != values) {

	    if (propertyType.equals(String.class)) {
		ret = (List<T>) Arrays.asList(values.split(String.valueOf(PROPERTY_LIST_DELIMITER)));
	    }

	    else if (propertyType.equals(Integer.class)) {
		ret = new ArrayList<T>();
		for (String str : values.split(String.valueOf(PROPERTY_LIST_DELIMITER))) {
		    ret.add((T) Integer.valueOf(str));
		}
	    } else {
		throw new MemberConfigurationException("Unsupported property type");
	    }
	}

	return ret;
    }

}
