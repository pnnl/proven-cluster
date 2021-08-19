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
package gov.pnnl.proven.cluster.lib.disclosure.item.operation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.cluster.lib.disclosure.item.ArtifactModelItem;

/**
 * Represents the pre-defined item processing operations. Operations are
 * assigned a name and an operation context type. The context provides
 * additional operation specific data, if any, supporting an operations's
 * execution.  
 * 
 * @see OperationContext
 * 
 * @author d3j766
 *
 */
public enum ItemOperation {

	FILTER(ItemOperationName.FILTER_NAME, FilterContext.class),
	TRANSFORM(ItemOperationName.TRANSFORM_NAME, TransformContext.class),
	VALIDATE(ItemOperationName.VALIDATE_NAME, ValidateContext.class),
	INFERENCE(ItemOperationName.INFERENCE_NAME, InferContext.class),
	PROVENANCE(ItemOperationName.PROVENANCE_NAME, ProvenanceContext.class);

	public class ItemOperationName {
		public static final String FILTER_NAME = "Filter";
		public static final String TRANSFORM_NAME = "Transform";
		public static final String VALIDATE_NAME = "Validate";
		public static final String INFERENCE_NAME = "Inference";
		public static final String PROVENANCE_NAME = "Provenance";
	}

	private static Logger log = LoggerFactory.getLogger(ItemOperation.class);

	private String opName;
	private Class<? extends OperationContext> opContext;

	ItemOperation(String opName, Class<? extends OperationContext> opContext) {
		this.opName = opName;
		this.opContext = opContext;
	}

	public String getOpName() {
		return opName;
	}

	public Class<? extends OperationContext> getOpContext() {
		return opContext;
	}

	/**
	 * Returns operation associated with the provided operation name.
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
	 * Provides list of all operation names.
	 */
	public static List<String> getOperationNames() {

		List<String> ret = new ArrayList<>();

		for (ItemOperation op : values()) {
			ret.add(op.getOpName());
		}

		return ret;
	}

}
