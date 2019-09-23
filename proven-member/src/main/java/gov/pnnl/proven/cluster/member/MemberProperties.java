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
package gov.pnnl.proven.cluster.member;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.pnnl.proven.cluster.member.exception.MemberConfigurationException;

/**
 * 
 * Stores properties for a proven cluster member. For each module startup
 * process, a module's startup bean depends on this bean to successfully acquire
 * the member properties. These properties are defined in the
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
@Singleton
public class MemberProperties {

	static Logger log = LoggerFactory.getLogger(MemberProperties.class);

	private static final char PROPERTY_LIST_DELIMITER = ',';

	// member
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

	// hazelcast
	private static final String HAZELCAST_MEMBERS_PROP = "proven.hazelcast.members";
	private static final String HAZELCAST_MEMBER_PORT_PROP = "proven.hazelcast.member.port";

	// hazelcast jet
	private static final String JET_INSTANCE_NETWORK_ADDRESSES = "proven.jet.instance.network.addresses";
	private static final String JET_INSTANCE_TEST_PORT = "proven.jet.instance.test.port";
	private static final String JET_CONFIG_GROUP = "proven.jet.config.group";

	// hybrid
	private static final String HYBRID_T3_SERVICE_URL = "proven.hybrid.t3.serviceUrl";

	private String installRoot;

	@PostConstruct
	public void init() throws RuntimeException {

		installRoot = System.getProperty(MEMBER_INSTALL_ROOT_PROP);
		if (null == installRoot) {
			throw new MemberConfigurationException("Missing " + MEMBER_INSTALL_ROOT_PROP + " value");
		}
		installRoot.trim();
		if (!installRoot.endsWith("/")) {
			installRoot += "/";
		}
	}

	// Member property methods
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

	public File getPipelineRequestLibsDir() {
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

	/**
	 * Returns the property value for the provided type.
	 * 
	 * @param key
	 *            property key
	 * @param propertyType
	 *            type of property. String and Integer are supported.
	 * @throws MemberConfigurationException
	 *             if property type not supported.
	 * @return the property value. null if the key does not exist.
	 */
	private <T> T getPropertyValue(String key, Class<T> propertyType) throws MemberConfigurationException {

		T ret = null;
		String value = System.getProperty(key);
		if (null != value) {

			if (propertyType.equals(String.class)) {
				ret = (T) value;
			}

			else if (propertyType.equals(Integer.class)) {
				ret = ((T) Integer.valueOf(value));
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
	 * @throws MemberConfigurationException
	 *             if property type not supported.
	 * @return the property value. null if the key does not exist.
	 */
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

	// Hazelcast property methods
	public List<String> getHazelcastMembers() {
		return getPropertyValues(HAZELCAST_MEMBERS_PROP, String.class);
	}

	public Integer getHazelcastMemberPort() {
		return getPropertyValue(HAZELCAST_MEMBER_PORT_PROP, Integer.class);
	}

	// Hazelcast Jet property methods
	public List<String> getJetInstanceAddresses() {
		return getPropertyValues(JET_INSTANCE_NETWORK_ADDRESSES, String.class);
	}

	public Integer getJetInstanceTestPort() {
		return getPropertyValue(JET_INSTANCE_TEST_PORT, Integer.class);
	}

	public String getJetConfigGroup() {
		return getPropertyValue(JET_CONFIG_GROUP, String.class);
	}

	// Hybrid module property methods
	public String getHybridT3ServiceUrl() {
		return getPropertyValue(HYBRID_T3_SERVICE_URL, String.class);
	}

}
