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
package gov.pnnl.proven.cluster.lib.model;

import java.io.Serializable;
import java.net.URI;
import java.util.Set;

import gov.pnnl.proven.cluster.lib.disclosure.item.DisclosureItem;
import gov.pnnl.proven.cluster.lib.disclosure.item.MessageContext;
import gov.pnnl.proven.cluster.lib.disclosure.item.operation.OperationContext;
import gov.pnnl.proven.cluster.lib.disclosure.item.response.ResponseItem;

/**
 * A composition of semantic graphs that can be included in model operation
 * processing of disclosure messages.
 * 
 * @author d3j766
 *
 * @see ModelOperation
 * 
 */
public class MessageModel implements ReferenceModel, Serializable {

	private static final long serialVersionUID = 1098699408171596662L;

	private URI modelName;
	private Set<URI> artifactModels;
	private MessageContext messageContext;
	private ModelOperation operation;
	private OperationContext operationContext;
	private SemanticModel graph;

	/**
	 * TODO Use hashing to support message model lookups.
	 * 
	 * This represents the hash value of the MessageContext. This will be
	 * compared to a disclosed item's MessageContext hash value, and if equal,
	 * another equality comparison will be made for the MessageContext's to
	 * verify the match (i.e. avoid false positives). A match means the message
	 * model can be used for operation processing of the matched message.
	 */
	private Long messageContextHashValue;

	public MessageModel() {
	}

	ResponseItem filter(DisclosureItem disclosureItem) {
		// TODO
		return null;
	}

	ResponseItem transform(DisclosureItem disclosureItem) {
		// TODO
		return null;
	}

	ResponseItem validate(DisclosureItem disclosureItem) {
		// TODO
		return null;
	}

	ResponseItem inference(DisclosureItem disclosureItem) {
		// TODO
		return null;
	}

	ResponseItem provenance(DisclosureItem disclosureItem) {
		return null;
	}

	@Override
	public URI modelName() {
		return modelName;
	}

	public Set<URI> getArtifactModels() {
		return artifactModels;
	}

	public MessageContext getMessageContext() {
		return messageContext;
	}

	public ModelOperation getOperation() {
		return operation;
	}

	public OperationContext getOperationContext() {
		return operationContext;
	}

	public SemanticModel getGraph() {
		return graph;
	}

	public Long getMessageContextHashValue() {
		return messageContextHashValue;
	}

}