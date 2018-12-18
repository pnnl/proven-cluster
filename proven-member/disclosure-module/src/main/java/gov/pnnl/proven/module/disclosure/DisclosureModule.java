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
package gov.pnnl.proven.module.disclosure;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.ringbuffer.Ringbuffer;

import fish.payara.micro.PayaraMicro;
import gov.pnnl.proven.client.lib.disclosure.ClientDisclosureMap;
import gov.pnnl.proven.client.lib.disclosure.ClientResponseMap;
import gov.pnnl.proven.client.lib.disclosure.ProxyRequest;
import gov.pnnl.proven.cluster.lib.module.ProvenModule;

@ApplicationScoped
public class DisclosureModule extends ProvenModule {

	private static Logger log = LoggerFactory.getLogger(DisclosureModule.class);

	public static final String DISCLOSURE_BUFFER = "disclosure.buffer";

	public static final String HOST_TAG = "<HOST>";
	public static final String PORT_TAG = "<PORT>";
	public static final String SESSION_TAG = "SESSION";
	public static final String RESPONSE_URL_TEMPLATE = "http://" + HOST_TAG + ":" + PORT_TAG + "/disclosure/"
			+ SESSION_TAG + "/responses";

	@Inject
	private HazelcastInstance hzInstance;

	Ringbuffer<ProxyRequest<?, ?>> disclosureBuffer;

	IMap<String, Boolean> clientDisclosureMap;

	IMap<String, Boolean> clientResponseMap;

	@PostConstruct
	public void init() {

		// TODO This should be part of the member registry when a
		// DisclosureBuffer
		// reports itself as part of its construction. Placed here for now to
		// support moving message-lib processing to cluster.
		disclosureBuffer = hzInstance.getRingbuffer(DISCLOSURE_BUFFER);
		clientDisclosureMap = hzInstance.getMap(new ClientDisclosureMap().getDisclosureMapName());
		clientDisclosureMap.put(DISCLOSURE_BUFFER, true);
		clientResponseMap = hzInstance.getMap(new ClientResponseMap().getResponseMapName());
		String responseUrl = buildResponseUrl();
		clientResponseMap.put(responseUrl, true);
		testPipeline();
		log.debug("DisclossureModule constructed");
	}
	
	private String buildResponseUrl() {
		String ret;
		Member member = hzInstance.getCluster().getLocalMember();
		String host = member.getAddress().getHost();
		String port = PayaraMicro.getInstance().getRuntime().getLocalDescriptor().getHttpPorts().get(0).toString();
		ret = RESPONSE_URL_TEMPLATE.replace(HOST_TAG, host);
		ret = ret.replace(PORT_TAG, port);
		return ret;
	}
	
	private void testPipeline() {
		new TestPipeline().submit();
	}

}
