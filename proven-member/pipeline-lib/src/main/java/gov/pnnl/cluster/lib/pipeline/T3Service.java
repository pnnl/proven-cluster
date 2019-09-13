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
package gov.pnnl.cluster.lib.pipeline;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.core.Response;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.system.JenaSystem;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;

import com.bigdata.rdf.sail.webapp.client.RemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;
import com.hazelcast.jet.pipeline.ContextFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.cluster.lib.disclosure.message.MessageUtils;
import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenMessage;
import gov.pnnl.proven.cluster.lib.disclosure.message.ResponseMessage;


/**
 * Provides services to triple store (T3).
 * 
 * @author d3j766
 *
 */
public class T3Service {

	private static Logger log = LoggerFactory.getLogger(T3Service.class);
	
	private static final String T3_SERVICE_URL_PROP = "proven.hybrid.t3.serviceUrl";

	private RemoteRepositoryManager repoManager;
	private RemoteRepository repo;
	private final RDFFormat addFormat = RDFFormat.RDFXML;
	private Jsonb jsonb = JsonbBuilder.create();

	/**
	 * Summary response information for a T3 storage request. This is included
	 * in a {@code ResponseMessage} as it's message content.
	 * 
	 * @author d3j766
	 *
	 */
	private class T3Response implements Serializable {

		private static final long serialVersionUID = 1L;

		int statusCode;
		String statusReason;
		long count;
		String message;

		T3Response() {
		}

		T3Response(Response.Status status, long count) {
			this.statusCode = status.getStatusCode();
			this.statusReason = status.getReasonPhrase();
			this.count = count;
			this.message = "";
		}
	}


	/**
	 * Returns {@code ContextFactory} for Jet processing pipelines that require T3 services.
	 * 
	 * @return {@link ContextFactory}
	 */
	public static ContextFactory<T3Service> t3Service() {
		String serviceUrl = System.getProperty(T3_SERVICE_URL_PROP);
		return ContextFactory.withCreateFn(x -> T3Service.newT3Service(serviceUrl)).toNonCooperative().withLocalSharing();
	}

		
	/**
	 * Creates a new T3Service with default settings.
	 * 
	 * @param serviceUrl
	 *            identifies SPARQL endpoint
	 * 
	 * @throws RepositoryException
	 */
	public static T3Service newT3Service(String serviceUrl) throws RepositoryException {
		T3Service t3s = new T3Service(serviceUrl);
		return t3s;
	}

	private T3Service(String serviceUrl) throws RepositoryException {
		repoManager = new RemoteRepositoryManager(serviceUrl, false);
		repo = repoManager.getRepositoryForDefaultNamespace();
	}

	// @Override
	public ResponseMessage add(ProvenMessage sourceMessage) {

		ResponseMessage ret = null;
		T3Response loadResponse = null;

		try {
	
			// Construct initial data model
			String message = MessageUtils.prependContext(sourceMessage.getDomain(),
					sourceMessage.getMessage().toString());
			Model dataModel = MessageUtils.createMessageDataModel(sourceMessage, message);

			// SHACL rule processing to produce final message data model
			dataModel = MessageUtils.addShaclRuleResults(sourceMessage.getDomain(), dataModel);

			// Load message into T3 store and return response
			loadResponse = loadMessageData(dataModel, sourceMessage);
			JsonReader reader = Json.createReader(new StringReader(jsonb.toJson(loadResponse)));
			//JsonReader reader = Json.createReader(new StringReader(""));
			JsonObject loadResponseObject = reader.readObject();
			ret = new ResponseMessage(Response.Status.fromStatusCode(loadResponse.statusCode), sourceMessage,
					loadResponseObject);

		} catch (Exception ex) {

			ex.printStackTrace();

			// Create an error response message based on T3Resposne
			if (null != loadResponse) {
				ret = createResponseMessage(loadResponse, sourceMessage);

			}
			// Create a general error response
			else {
				T3Response errorResponse = new T3Response(Response.Status.INTERNAL_SERVER_ERROR, 0);
				errorResponse.message = "T3 storage failure : " + ex.getMessage();
				ret = createResponseMessage(errorResponse, sourceMessage);
			}
		}
		return ret;
	}

	private ResponseMessage createResponseMessage(T3Response t3Response, ProvenMessage sourceMessage) {
		JsonReader reader = Json.createReader(new StringReader(jsonb.toJson(t3Response)));
		//JsonReader reader = Json.createReader(new StringReader(""));
		JsonObject loadResponseObject = reader.readObject();
		return new ResponseMessage(Response.Status.fromStatusCode(t3Response.statusCode), sourceMessage,
				loadResponseObject);
	}

	/*
	 * Load/Store T3 data
	 */
	private T3Response loadMessageData(Model dataModel, ProvenMessage sourceMessage) throws Exception {

		T3Response ret = null;

		// Data streams
		PipedOutputStream pos = new PipedOutputStream();
		PipedInputStream pis = new PipedInputStream();
		pis.connect(pos);
		
		try {
			// Push data to output stream Thread will terminate when run()
			// completes (i.e. after message is pushed to output pipe)
			new Thread(new Runnable() {
				public void run() {
					//dataModel.write(pos, jenaFormat.toString());
					dataModel.write(pos);
					try {
						pos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
			ValueFactoryImpl vf = ValueFactoryImpl.getInstance();
			URI context = vf.createURI("http://" + sourceMessage.getDomain().getDomain());
			RemoteRepository.AddOp operation = new RemoteRepository.AddOp(pis, addFormat);
			operation.setContext(context);
			long t3Count = repo.add(operation);
			pis.close();

			// Create OK response
			ret = new T3Response(Response.Status.OK, t3Count);

		} catch (Exception ex) {
			log.error("T3 add failure:");
			ex.printStackTrace();
			// Create error response
			ret = new T3Response(Response.Status.INTERNAL_SERVER_ERROR, 0);
			ret.message = ex.getMessage();
		} finally {
			pos.close();
			pis.close();
		}

		return ret;
	}
}
