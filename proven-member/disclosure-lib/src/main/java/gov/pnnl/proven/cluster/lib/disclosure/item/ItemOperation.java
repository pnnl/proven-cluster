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
package gov.pnnl.proven.cluster.lib.disclosure.item;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the pre-defined item processing operations. Operations may be
 * assigned model definitions via ModelItem messages and/or may also be included
 * in SSE event response message subscriptions via an EventResponseItem message.
 * Operations are assigned a priority value which determines the order in which
 * they are performed. Priority is given to operations which exchange message
 * data to the Hybrid Store's streaming environment.
 * 
 * @see ModelItem, EventResponseItem
 * 
 * @author d3j766
 *
 */
public enum ItemOperation {

	Disclose(OperationName.DISCLOSE, false, false, 0),
	Contextualize(OperationName.CONTEXTUALIZE, false, false, 10),
	Filter(OperationName.FILTER, true, true, 20),
	Distribute(OperationName.DISTRIBUTE, false, false, 30),
	Transform(OperationName.TRANSFORM, true, true, 40),
	Validate(OperationName.VALIDATE, true, true, 50),
	Infer(OperationName.INFER, true, true, 60),
	Provenance(OperationName.PROVENANCE, true, true, 70),
	Request(OperationName.REQUEST, false, true, 80),
	Service(OperationName.SERVICE, false, true, 90);

	public class OperationName {
		public static final String DISCLOSE = "Disclose";
		public static final String CONTEXTUALIZE = "Contextualize";
		public static final String FILTER = "Filter";
		public static final String DISTRIBUTE = "Distribute";
		public static final String TRANSFORM = "Transform";
		public static final String VALIDATE = "Validate";
		public static final String INFER = "Infer";
		public static final String PROVENANCE = "Provenancex";
		public static final String REQUEST = "Request";
		public static final String SERVICE = "Service";
	}

	private static Logger log = LoggerFactory.getLogger(ItemOperation.class);

	private String opName;
	private boolean model;
	private boolean event;
	private int priority;

	ItemOperation(String opName, boolean model, boolean event, int priority) {
		this.opName = opName;
		this.model = model;
		this.event = event;
		this.priority = priority;
	}

	public String getOpName() {
		return opName;
	}

	public boolean isModel() {
		return model;
	}

	public boolean isEvent() {
		return event;
	}

	public int getPriority() {
		return priority;
	}

	/**
	 * Returns ItemOperation associated with the provided operation name.
	 * 
	 * @param operation
	 *            name
	 * @return associated ItemOperation, else null
	 */
	public static ItemOperation getItemOperation(String opName) {

		ItemOperation ret = null;

		for (ItemOperation op : values()) {
			if (opName.equals(op.getOpName())) {
				ret = op;
				break;
			}
		}
		return ret;
	}

	/**
	 * Provides a list of all operation names.
	 */
	public static List<String> getOperationNames() {

		List<String> ret = new ArrayList<>();

		for (ItemOperation op : values()) {
				ret.add(op.getOpName());
		}

		return ret;
	}
	
	/**
	 * Provides a list of operation names that may have assigned Model
	 * definitions.
	 * 
	 * @see ModelArtifactItem
	 */
	public static List<String> getModelOperationNames() {

		List<String> ret = new ArrayList<>();

		for (ItemOperation op : values()) {
			if (op.isModel()) {
				ret.add(op.getOpName());
			}
		}

		return ret;
	}

	/**
	 * Provides a list of operation names that may be included in an SSE event
	 * response subscription.
	 * 
	 * @see EventContext
	 */
	public static List<String> getEventOperationNames() {

		List<String> ret = new ArrayList<>();

		for (ItemOperation op : values()) {
			if (!op.isEvent()) {
				ret.add(op.getOpName());
			}
		}

		return ret;
	}

}
