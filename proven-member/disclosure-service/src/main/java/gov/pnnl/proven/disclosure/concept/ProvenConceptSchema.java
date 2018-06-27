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

package gov.pnnl.proven.disclosure.concept;

import static gov.pnnl.proven.disclosure.util.Consts.*;

/**
 * Static ProvEn schema.
 */
public class ProvenConceptSchema {

	// ///////////////////////////////////////////////////////////////////////////////
	// Classes
	// ///////////////////////////////////////////////////////////////////////////////
	public static final String BATCH_SCHEDULE_CLASS = PROVEN_NS + "BatchSchedule";
	public static final String COMPONENT_SERVICE_CLASS = PROVEN_NS + "ComponentService";
	public static final String CONTEXT_CLASS = (PROVEN_NS + "Context");
	public static final String DOMAIN_MODEL_CLASS = PROVEN_NS + "DomainModel";
	public static final String EXCHANGE_CLASS = PROVEN_NS + "Exchange";
	public static final String FOUNDATION_MODEL_CLASS = PROVEN_NS + "FoundationModel";
	public static final String HARVESTER_CLASS = PROVEN_NS + "Harvester";
	public static final String MAINTENANCE_SCHEDULE_CLASS = PROVEN_NS + "MaintenanceSchedule";
	public static final String NATIVE_SOURCE_CLASS = PROVEN_NS + "NativeSource";
	public static final String NATIVE_SOURCE_SCHEDULE_CLASS = PROVEN_NS + "NativeSourceSchedule";
	public static final String ONTOLOGY_CLASS = PROVEN_NS + "Ontology";
	public static final String PRODUCT_CLASS = PROVEN_NS + "Product";
	public static final String PRODUCT_SCHEDULE_CLASS = PROVEN_NS + "ProductSchedule";
	public static final String PROPERTY_CLASS = PROVEN_NS + "Property";
	public static final String PROVENANCE_MESSAGE_CLASS = PROVEN_NS + "ProvenanceMessage";
	public static final String REPRESENTATION_CLASS = PROVEN_NS + "Representation";
	public static final String SCHEDULE_CLASS = PROVEN_NS + "Schedule";

	// ///////////////////////////////////////////////////////////////////////////////
	// DataType Properties
	// ///////////////////////////////////////////////////////////////////////////////
	public static final String RDF_TYPE_PROP = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	
	public static final String HAS_BASE_URI_PROP = PROVEN_NS + "hasBaseUri";
	public static final String HAS_BATCH_JOB_XML_NAME_PROP = PROVEN_NS + "hasBatchJobXmlName";
	public static final String HAS_BLOB_KEY_PROP = PROVEN_NS + "hasBlobKey";
	public static final String HAS_BLOB_STATUS_PROP = PROVEN_NS + "hasBlobStatus";
	public static final String HAS_COMPONENT_PROP = PROVEN_NS + "hasComponent";
	public static final String HAS_COMPONENT_METHOD_PROP = PROVEN_NS + "hasComponentMethod";
	public static final String HAS_COMPONENT_SERVICE_TYPE_PROP = PROVEN_NS
			+ "hasComponentServiceType";
	public static final String HAS_CONCEPT_ID_PROP = PROVEN_NS + "hasConceptId";
	public static final String HAS_CONNECTSTRING_PROP = PROVEN_NS + "hasConnectstring";
	public static final String HAS_CONTEXT_URI_PROP = PROVEN_NS + "hasContextUri";
	public static final String HAS_CREATED_TIME_PROP = PROVEN_NS + "hasCreatedTime";
	public static final String HAS_DESCRIPTION_PROP = PROVEN_NS + "hasDescription";
	public static final String HAS_DISABLED_TIME_PROP = PROVEN_NS + "hasDisabledTime";
	public static final String HAS_ENABLED_TIME_PROP = PROVEN_NS + "hasEnabledTime";
	public static final String HAS_EXCHANGE_SESSION_PROP = PROVEN_NS + "hasExchangeSession";
	public static final String HAS_KEY_PROP = PROVEN_NS + "hasKey";
	public static final String HAS_LOCATION_PROP = PROVEN_NS + "hasLocation";
	public static final String HAS_MAINTENANCE_STATUS_PROP = PROVEN_NS + "hasMaintenanceStatus";
	public static final String HAS_MAINTENANCE_STATUS_TIME_PROP = PROVEN_NS
			+ "hasMaintenanceStatusTime";
	public static final String HAS_MEDIA_TYPE_PROP = PROVEN_NS + "hasMediaType";
	public static final String HAS_MESSAGE_NAME = PROVEN_NS + "hasMessageName";
	public static final String HAS_MESSAGE_ID = PROVEN_NS + "hasMessageID";
	public static final String HAS_NAME_PROP = PROVEN_NS + "hasName";
	public static final String HAS_PROVENANCE_PROP = PROVEN_NS + "hasProvenance";
	public static final String HAS_RDF_FORMAT_PROP = PROVEN_NS + "hasRdfFormat";
	public static final String HAS_RETAIN_LOCAL_COPY_PROP = PROVEN_NS + "hasRetainLocalCopy";
	public static final String HAS_SCHEDULED_ATTEMPTS_PROP = PROVEN_NS + "hasScheduleAttempts";
	public static final String HAS_TIMER_SCHEDULE_PROP = PROVEN_NS + "hasTimerSchedule";
	public static final String HAS_VALUE_PROP = PROVEN_NS + "hasValue";
	public static final String PART_OF_PROVEN_MESSAGE_PROP = PROVEN_NS + "partOfProvenMessage";
	public static final String IS_ENABLED_PROP = PROVEN_NS + "isEnabled";
	public static final String IS_EXCHANGE_MAINTENANCE_PROP = PROVEN_NS + "isExchangeMaintenance";
	public static final String IS_SERVER_MAINTENANCE_PROP = PROVEN_NS + "isExchangeMaintenance";
	

	// ///////////////////////////////////////////////////////////////////////////////
	// Object Properties
	// ///////////////////////////////////////////////////////////////////////////////
	public static final String HAS_COMPONENT_SERVICE_PROP = PROVEN_NS + "hasComponentService";
	public static final String HAS_DOMAIN_MODEL_PROP = PROVEN_NS + "hasDomainModel";
	public static final String HAS_EXPLICIT_CONTENT_PROP = PROVEN_NS + "hasExplicitContent";
	public static final String HAS_EXPLICIT_STRUCTURE_PROP = PROVEN_NS + "hasExplicitStructure";
	public static final String HAS_HARVESTER_PROP = PROVEN_NS + "hasHarvester";
	public static final String HAS_IMPLICIT_CONTENT_PROP = PROVEN_NS + "hasImplicitContent";
	public static final String HAS_IMPLICIT_STRUCTURE_PROP = PROVEN_NS + "hasImplicitStructure";
	public static final String HAS_NATIVE_SOURCE_PROP = PROVEN_NS + "hasNativeSource";
	public static final String HAS_ONTOLOGY_PROP = PROVEN_NS + "hasOntology";
	public static final String HAS_PRODUCT_PROP = PROVEN_NS + "hasProduct";
	public static final String HAS_REPRESENTATION_PROP = PROVEN_NS + "hasRepresentation";

}
