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
package gov.pnnl.proven.cluster.lib.module.component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Represents the different component groups. Marks each group with their group
 * reporting type.
 * 
 * @author d3j766
 *
 */
public enum ComponentGroup {
	
	Module(GroupLabel.MODULE_GROUP),

	Registry(GroupLabel.REGISTRY_GROUP),

	Manager(GroupLabel.MANAGER_GROUP),

	Managed(GroupLabel.MANAGED_GROUP),

	Disclosure(GroupLabel.DISCLOSURE_GROUP),

	Exchange(GroupLabel.EXCHANGE_GROUP),

	Request(GroupLabel.REQUEST_GROUP),

	Stream(GroupLabel.STREAM_GROUP);

	private class GroupLabel {
		private static final String MODULE_GROUP = "module";
		private static final String REGISTRY_GROUP = "registry";
		private static final String MANAGER_GROUP = "manager";
		private static final String MANAGED_GROUP = "managed";
		private static final String DISCLOSURE_GROUP = "disclosure";
		private static final String EXCHANGE_GROUP = "exchange";
		private static final String REQUEST_GROUP = "request";
		private static final String STREAM_GROUP = "stream";
	}

	static Logger log = LoggerFactory.getLogger(ComponentGroup.class);

	private String groupLabel;

	ComponentGroup(String groupLabel) {
		this.groupLabel = groupLabel;
	}

	/**
	 * Provides the group's label.
	 * 
	 * @return a group label as a String.
	 * 
	 */
	public String getGroupLabel() {
		return groupLabel;
	}

	public List<Annotation> getQualifiers() {
		Field field;
		List<Annotation> ret = new ArrayList<>();
		try {
			field = this.getClass().getField(this.name());
			ret = Arrays.asList(field.getAnnotations());
		} catch (NoSuchFieldException | SecurityException e) {
			log.error("Invaid field name in ComponentGroup");
			e.printStackTrace();
		}

		return ret;
	}
}
