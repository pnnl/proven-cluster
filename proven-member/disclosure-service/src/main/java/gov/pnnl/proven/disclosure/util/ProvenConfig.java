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

package gov.pnnl.proven.disclosure.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Manages B2S configuration properties. All property settings are read from
 * environment variable and/or system properties and is performed at application
 * startup. If a required property is missing the application will fail to
 * start. Default values are provided when possible.
 */
public class ProvenConfig {

	// Logger
	private final Logger log = LoggerFactory.getLogger(ProvenConfig.class);

	private static ProvenConfig instance;
		
	private ProvenConfig() {
		log.debug("Loading B2S configuration settings...");
		loadEnvProperties();
	}

	static {
		try {
			instance = new ProvenConfig();
		} catch (ProvenConfigurationException ex) {
			throw ex;
		}
	}

	private Map<String, String> props = new HashMap<String, String>();

	private class ProvenConfigurationException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public ProvenConfigurationException(String message) {
			super(message);
		}
	}

	public static ProvenConfig getB2SConfig() {
		return instance;
	}
	
	public enum ProvenEnvProp {

		PROVEN_SERVICES_PORT("proven.psPort", true, "8080"),
		PROVEN_SWAGGER_HOST_PORT("proven.swaggerHostPort", true, "localhost:28080"),
		PROVEN_USE_IDB("proven.useIdb", false, "true"),
		PROVEN_IDB_URL("proven.idbUrl", false, "http://localhost:8086"),
		PROVEN_IDB_DB("proven.idbDB", false, "proven"),
		PROVEN_IDB_RP("proven.idbRP", false, "autogen"),
		PROVEN_IDB_USERNAME("proven.idbUsername", false, "root"),
		PROVEN_IDB_PASSWORD("proven.idbPassword", false, "root"),
		PROVEN_T3DIR("proven.t3Dir", true, "/tmp");
		
		private String envVarKey;
		private String sysPropKey;
		private boolean required;
		private String defaultValue;

		private ProvenEnvProp(String key, boolean required, String defaultValue) {
			setEnvVarKey(this.toString());
			setSysPropKey(key);
			setRequired(required);
			setDefaultValue(defaultValue);
		}

		public String getEnvVarKey() {
			return envVarKey;
		}

		public void setEnvVarKey(String envVarKey) {
			this.envVarKey = envVarKey;
		}

		public String getSysPropKey() {
			return sysPropKey;
		}

		public void setSysPropKey(String sysPropKey) {
			this.sysPropKey = sysPropKey;
		}

		public boolean isRequired() {
			return required;
		}

		public void setRequired(boolean required) {
			this.required = required;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}

	}

	private void loadEnvProperties() {

		Map<String, String> env = System.getenv();
		Properties sysProps = System.getProperties();

		for (ProvenEnvProp prop : ProvenEnvProp.values()) {

			// First check environment variables
			String key = prop.getEnvVarKey();
			String value = env.get(key);

			// If no value from environment, try java system property
			if (null == value) {
				key = prop.getSysPropKey();
				value = sysProps.getProperty(key);
			}

			// If no value, use default if provided
			if (null == value) {
				value = prop.getDefaultValue();
			}

			if ((null == value) && (prop.isRequired())) {
				throw new ProvenConfigurationException(key);
			} else {
				props.put(prop.toString(), value);
			}
		}
				
	}

	public String getPropValue(ProvenEnvProp prop) {
		return props.get(prop.toString());    
	}
		
}		
		
		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		private String key;
//		private boolean required;
//		private String defaultValue;
//
//		private ProvenEnvProp(String key, boolean required, String defaultValue) {
//			setKey(key);
//			setRequired(required);
//			setDefaultValue(defaultValue);
//		}
//
//		public String getKey() {
//			return key;
//		}
//
//		public void setKey(String key) {
//			this.key = key;
//		}
//
//		public boolean isRequired() {
//			return required;
//		}
//
//		public void setRequired(boolean required) {
//			this.required = required;
//		}
//
//		public String getDefaultValue() {
//			return defaultValue;
//		}
//
//		public void setDefaultValue(String defaultValue) {
//			this.defaultValue = defaultValue;
//		}
//
//	}
//
//	private void loadEnvProperties() {
//
//		Map<String, String> env = System.getenv();
//
//		for (ProvenEnvProp prop : ProvenEnvProp.values()) {
//
//			// First check environment variables
//			String key = prop.getKey();
//			String value = env.get(key);
//
//			// If no value, use default if provided
//			if (null == value) {
//				value = prop.getDefaultValue();
//			}
//
//			if ((null == value) && (prop.isRequired())) {
//				throw new ProvenConfigurationException("Missing configuration setting: " + key);
//			} else {
//				props.put(key, value);
//			}
//		}
//	}
//
//	public String getPropValue(ProvenEnvProp prop) {
//		return props.get(prop.getKey());
//	}
//}
