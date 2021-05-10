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
 * Represents pre-defined exchange processing operations. Internal operations
 * will always be performed. Non-internal operations will be performed if it has
 * a Model definition with a matching message context.
 * 
 * @see ModelItem, MessageContext
 * 
 * @author d3j766
 *
 */
public enum ItemOperation {

	Disclosure(OperationName.DISCLOSURE, true, -30),
	Contextualize(OperationName.CONTEXTUALIZE, true, -20),
	Filter(OperationName.FILTER, false, -10),
	Distribute(OperationName.DISTRIBUTE, true, 0),
	Transform(OperationName.TRANSFORM, false, 10),
	Validation(OperationName.VALIDATION, false, 20),
	Inference(OperationName.INFERENCE, false, 30);

	public class OperationName {

		public static final String DISCLOSURE = "Disclosure";
		public static final String CONTEXTUALIZE = "Contextualize";
		public static final String FILTER = "Filter";
		public static final String DISTRIBUTE = "Distribute";
		public static final String TRANSFORM = "Transform";
		public static final String VALIDATION = "Validation";
		public static final String INFERENCE = "Inference";
	}

	private static Logger log = LoggerFactory.getLogger(ItemOperation.class);

	private String opName;
	private boolean internal;
	private int priority;

	ItemOperation(String opName, boolean internal, int priority) {
		this.opName = opName;
		this.internal = internal;
		this.priority = priority;
	}

	public String getOpName() {
		return opName;
	}

	public boolean isInternal() {
		return internal;
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
	public static List<String> getOpNames() {
		return getOpNames(false);
	}

	/**
	 * Provides a list of operation names. Internal operation names may be
	 * excluded from list.
	 */
	public static List<String> getOpNames(boolean excludeInternalOperations) {

		List<String> ret = new ArrayList<>();

		for (ItemOperation op : values()) {
			if (!op.isInternal() || !excludeInternalOperations) {
				ret.add(op.getOpName());
			}
		}

		return ret;
	}

}
