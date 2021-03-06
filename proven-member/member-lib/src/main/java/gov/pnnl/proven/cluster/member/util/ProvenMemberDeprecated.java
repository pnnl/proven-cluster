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

package gov.pnnl.proven.cluster.member.util;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.swing.text.Utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.Member;
import com.hazelcast.monitor.impl.MemberStateImpl;
import com.hazelcast.ringbuffer.OverflowPolicy;
import com.hazelcast.ringbuffer.Ringbuffer;

import static gov.pnnl.proven.utils.Utils.*;
import static gov.pnnl.proven.utils.Consts.*;


import fish.payara.micro.PayaraMicro;
import fish.payara.micro.PayaraMicroRuntime;
import gov.pnnl.proven.utils.Utils;


@ApplicationScoped
public abstract class ProvenMemberDeprecated implements Serializable {

	private final Logger log = LoggerFactory.getLogger(ProvenMemberDeprecated.class);
	
	@Inject
	HazelcastInstance hazelcast;
	
	@Inject
	PayaraMicroRuntime pmrt;
	
	@Resource(lookup="java:module/ModuleName")
	private String moduleName;
	
	@Resource(lookup="java:app/AppName")
	private String appName;
	
	
	public ProvenMemberDeprecated() {
		log.debug("MemberStartup constructor...");
	}

	@PostConstruct
	void initializeMember() {
		
		log.debug("MemberStartup PostConstruct...");
		log.debug("MEMBER: " + hazelcast.getCluster().getLocalMember().getAddress().toString());
		IQueue<String> testq = hazelcast.getQueue("TEST_QUEUE");
		
		HazelcastInstance hzInstance = ((HazelcastInstance)Utils.lookupJndi("payara/HazelcastData"));
		
		//hazelcast = PayaraMicro.getInstance().getRuntime().get
		
		try {
			testq.put("HELLO");
			testq.put("WORLD");
			log.debug("PARTITION KEY: " + testq.getPartitionKey());
			log.debug("CLUSTER STATE : " + hazelcast.getCluster().getClusterState().name());
			log.debug("MODULE NAME : " + moduleName);
			log.debug("APP NAME : " + appName);
			hazelcast.getRingbuffer("test").addAsync("hello", OverflowPolicy.OVERWRITE);
			
			
			
			//log.debug("MEMBER STATE : " + msi.toJson());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public abstract String getAbstractState();
	
	public String getStateMember() {
		log.debug("Inside getState of proven member...");
		return "Proven member is OFFLINE";
	}

    public void addRequest(ProvenRequest request) {
    	IQueue<ProvenRequest> testRequests = hazelcast.getQueue("TEST_REQUESTS");    	
    	testRequests.add(request);
    	
    	ProvenRequest requestFromQueue = request;
    	try {
			requestFromQueue = testRequests.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	PayaraMicro pmi = PayaraMicro.getInstance();
    	
    	PayaraMicroRuntime pmrt = pmi.getRuntime();
    	log.debug(pmrt.toString());
    	
    	requestFromQueue.getServiceProvider().get().submit();
    	request.getServiceProvider().get().submit();
    	
    	
    	PayaraMicro.getInstance().getRuntime().run(request);
    	
    	//hazelcast.getExecutorService("DEFAULT").submit(request);
    	
    	HazelcastInstance hi1 = Hazelcast.newHazelcastInstance(hazelcast.getConfig());
    	
    	hi1.getExecutorService("DEFAULT").submit(request, request);
    	hi1.getExecutorService("DEFAULT").submit(requestFromQueue);
    	
    	
    	Set<Member> members1 = hazelcast.getCluster().getMembers();
    	Set<Member> members2 = hi1.getCluster().getMembers();
    	
    	log.debug("MEMBERS1");
    	log.debug("-----------");
    	for (Member m : members1) {
    		log.debug(m.toString());
    	}
    	
    	log.debug("MEMBERS2");
    	log.debug("-----------");
    	for (Member m : members2) {
    		log.debug(m.toString());
    	}

    	
    	
    	
    }
	
	
}
