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

package gov.pnnl.proven.api.exchange;

import gov.pnnl.proven.message.ProvenMessage;
import gov.pnnl.proven.message.ProvenMessageResponse;
import gov.pnnl.proven.api.producer.ProvenResponse;
import gov.pnnl.proven.api.producer.SessionInfo;
import java.net.URI;

//import java.util.Base64;
//import java.util.Base64.Encoder;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Provides implementation for REST based Exchange and an ExchangeServer.
 * 
 * @see Exchange
 * 
 * @see ExchangeServer
 * 
 * @author d3j766
 *
 */
class RestExchange implements Exchange {



	/**
	 * @see gov.pnnl.proven.api.exchange.Exchange#addProvenance()
	 */
	@Override
	public boolean addProvenData(ExchangeInfo exchangeInfo, List<ProvenMessage> messages) {
		throw new UnsupportedOperationException();	
	}

	/**
	 * Adds a new provenance message to a REST based exchange. A JSON-LD message is first
	 * generated from the provided provenance message and then POSTed to the exchange. This method
	 * will return true if the POST response is an HTTP Success 2xx code, indicating the message was
	 * added.
	 * 
	 * @see gov.pnnl.proven.api.exchange.Exchange#addProvenance()
	 */
	@Override

	public ProvenResponse addProvenData(ExchangeInfo exchangeInfo, final ProvenMessage message, final SessionInfo sessionInfo, String requestId) {

				
//		final String ADD_SERVICE_PATH = "/provenance/" + provenanceInfo.getContext();
//		URI addService = URI.create(exchangeInfo.getServicesUri() + ADD_SERVICE_PATH);
//		//URI exchangeService = URI.create(exchangeInfo.getServicesUri() + ADD_SERVICE_PATH);
//		Client client = null;
		
		//String servicePath = "/" + provenInfo.getContext() + "/" + message.getName();
		//servicePath = servicePath.replace(" ",  "%20");
		ProvenResponse pr = new ProvenResponse();
	
		try {
			//URL url = new URL("http://192.101.107.229/proven/rest/v1/repository/message/client/{domain}/{message name}");
			//URL url = new URL(exchangeInfo.getServicesUri() + servicePath);

			URI uri = new URI(exchangeInfo.getServicesUri());
	        Client client = ClientBuilder.newClient();
	        
	        ProvenMessageResponse response = client.target(uri).
	          request(MediaType.APPLICATION_JSON).
	          accept(MediaType.APPLICATION_JSON).
	          post(Entity.entity(message, MediaType.APPLICATION_JSON), ProvenMessageResponse.class);
	        System.out.println(response.getCode());

	        pr.code = response.getCode();
	        pr.status = response.getStatus();
	        pr.data = response.getResponse();

	        pr.error = response.getReason();
	        
	        pr.responsecomplete = true;
	    
						
						
			/*Client client = ClientBuilder.newClient();
			URI uri = new URI(exchangeInfo.getServicesUri());
			Invocation.Builder builder = client.target(uri).request();
			builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
			builder.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
			Response response = builder.post(Entity
					.entity(message, MediaType.APPLICATION_JSON));
			System.out.println(response.readEntity(String.class));*/
			
			
			/*URL url = new URL(exchangeInfo.getServicesUri());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("accept", "application/json");
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			ObjectOutputStream objOut = new ObjectOutputStream(connection.getOutputStream());
			objOut.writeObject(message);
	        objOut.flush();
	        objOut.close();
			//OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
			//out.write(message);
			//out.close();

			System.out.println(connection.getResponseCode());
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			System.out.println("REST Service Invoked Successfully..");
			in.close();
			System.out.println(response.toString());*/
		} catch (Exception e) {
			System.out.println("\nError while calling REST Service");
			System.out.println(e);
		}
		return pr;
		
		

		/*try {
			
			client = ClientFactory(true);
			
			// Encode the provenance message
			//Encoder encoder = Base64.getEncoder();
			byte[] j7message = message.generateJsonLd().getBytes("UTF-8");
			byte[] encodedMessage = DatatypeConverter.parseBase64Binary(message.generateJsonLd());
			//byte[] encodedMessage = encoder.encode(message.generateJsonLd().getBytes());

			// POST encoded message
			Invocation.Builder builder = client.target(addService).request();
			builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM);
			Future<String> res = builder.async().post(Entity.entity(encodedMessage,
					MediaType.APPLICATION_OCTET_STREAM), new InvocationCallback<String>() {
						
			            @Override
			            public void completed(String response) {
			                System.out.println("Response status code "
			                        + response + " received.");			                
			                sessionInfo.setMessageCount(sessionInfo.getMessageCount() + 1L);
			            }
			 
			            @Override
			            public void failed(Throwable throwable) {
			                System.out.println("Invocation failed.");
			                throwable.printStackTrace();			                
			                sessionInfo.setErrorCount(sessionInfo.getErrorCount() + 1L);
			                if (!(sessionInfo.getErrorCache().containsKey(message)))
							{
			                	sessionInfo.setErrorCache(message, null);
							}
			            }
			        } );
			res.get();
			//Response respo = res.get();
			//Response response = responseFuture.get();
			// Set return value based on HTTP response code
			//if (response.getStatusInfo().getFamily().equals(Status.Family.SUCCESSFUL)) {
			//}
			
		} catch (Exception e) {
			log.error("Error sending Provenance.");
			sessionInfo.setErrorCache(message, e.getMessage());
			// Catch all, currently not providing error information to caller
			
		}*/
		
		
	}

	/*private String getExchange(ExchangeInfo exchangeInfo) {
		// TODO Auto-generated method stub
		List<String> exchanges = exchangeInfo.getExchangeUrls();
		//load balancing
		//return one url
		return null;
	}*/

	
	
	/**
	 * Attempts to connect a provenance server using the server's provided REST service.
	 * 
	 * @see gov.pnnl.proven.api.exchange.ExchangeServer#connect()
	 */
	/*
	@SuppressWarnings("unchecked")
	@Override
	public List<ExchangeInfo> getExchanges(ProvenInfo provenanceInfo) {

		final String CONNECT_SERVICE_PATH = "/exchanges/" + provenanceInfo.getDomain();

		// Represents a disconnected session and will be default return value if service is
		// unavailable.
		List<ExchangeInfo> exchangeInfo = new ArrayList<ExchangeInfo>();
		

		try {

			URI connectService = new URI(serverInfo.getExchangeServer().toString()
					+ CONNECT_SERVICE_PATH);

			// POST new session connection
			Client client = ClientFactory();
			Invocation.Builder builder = client.target(connectService).request();
			builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
			builder.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
			Response response = builder.post(Entity
					.entity(serverInfo, MediaType.APPLICATION_JSON));

			// Set return value based on HTTP response code
			if (response.getStatusInfo().getFamily().equals(Status.Family.SUCCESSFUL)) {
				if (response.hasEntity()) {
					//String output = response.readEntity(String.class);
					exchangeInfo = response.readEntity(List.class);
					System.out.println("");
					//sessionInfo = (SessionInfo) response.getEntity();
				}
			}
		} catch (Exception e) {
			log.warn("Cannot get exchanges from ProvEn Server.");
			// Catch all, currently not providing error information to caller
			// Return default, disconnected session information on error
			//exchangeInfo = new ExchangeInfo();
		}
		

		return exchangeInfo;
	} */



}
