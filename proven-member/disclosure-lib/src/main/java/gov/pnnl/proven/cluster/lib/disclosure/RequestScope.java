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
package gov.pnnl.proven.cluster.lib.disclosure;

/**
 * Identifies the module servicing scope for a disclosed request. A request is
 * disclosed to a module application running inside a Proven Cluster, and the
 * scope informs the Cluster as to what module or modules will be used to
 * service the request it received. A module will only be included in the scope
 * if it can service the request.
 * 
 * @author d3j766
 *
 */
public enum RequestScope {

	/**
	 * A single module located anywhere in the cluster. The default.
	 */
	ModuleAny(RequestScopeType.REQUEST_SCOPE_MODULE_ANY),

	/**
	 * The module on which the request was disclosed. This will result in error
	 * if the module cannot service the request.
	 */
	Module(RequestScopeType.REQUEST_SCOPE_MODULE),

	/**
	 * Any module located in the member on which the request was disclosed.
	 */
	MemberModule(RequestScopeType.REQUEST_SCOPE_MEMBER_MODULE),

	/**
	 * All modules located in the member on which the request was disclosed.
	 */
	MemberModules(RequestScopeType.REQUEST_SCOPE_MEMBER_MODULES);

	public class RequestScopeType {
		public static final String REQUEST_SCOPE_MODULE_ANY = "request.scope.module_any";
		public static final String REQUEST_SCOPE_MODULE = "request.scope.module";
		public static final String REQUEST_SCOPE_MEMBER_MODULE = "request.scope.member_module";
		public static final String REQUEST_SCOPE_MEMBER_MODULES = "request.scope.member_modules";
	}

	private String scope;

	RequestScope() {
	}

	RequestScope(String scope) {
		this.scope = scope;
	}

	public String getRequestScope() {
		return scope;
	};

}
