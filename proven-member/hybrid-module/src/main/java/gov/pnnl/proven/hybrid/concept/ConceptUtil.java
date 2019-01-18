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

package gov.pnnl.proven.hybrid.concept;

import static gov.pnnl.proven.hybrid.util.Consts.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.cluster.lib.disclosure.message.ProvenStatement;
import gov.pnnl.proven.cluster.lib.disclosure.message.exception.InvalidProvenStatementsException;

/**
 * Utility methods and Types for the Concept package.
 */
public class ConceptUtil {

	private static final Logger log = LoggerFactory.getLogger(ConceptUtil.class);

	/**
	 * Representation's Blob status
	 */
	public enum BlobStatus {
		/**
		 * Default status
		 */
		NONE,

		/**
		 * Representation is stored remotely
		 */
		REMOTE,

		/**
		 * Blob representation was added successfully to the blob store
		 */
		ADD_COMPLETE,

		/**
		 * Blob representation failed to be added to the blob store
		 */
		ADD_FAIL
	}

	/**
	 * Identifies type of information for a ProvEn context
	 */
	public enum ContextType {
		/**
		 * Explicit Content
		 */
		EC,

		/**
		 * Explicit Structure
		 *
		 */
		ES,

		/**
		 * Implicit Content
		 */
		IC,

		/**
		 * Implicit Structure
		 */
		IS;
	}

	/**
	 * Identifies purpose of a component service.
	 */
	public enum ComponentServiceType {
		/**
		 * Native source provenance production
		 */
		NATIVE_SOURCE_PROVENANCE,

		/**
		 * Native source representation generation
		 */
		NATIVE_SOURCE_REPRESENTATION,

		/**
		 * Maintenance tasks performed on server and/or exchange
		 */
		MAINTENANCE,

		/**
		 * Domain product generation
		 */
		PRODUCT;
	}

	/**
	 * Identifies the types of ProvEn schedules
	 * 
	 */
	public enum ScheduleType {
		/**
		 * Native source harvesting schedule
		 */
		NATIVE_SOURCE,

		/**
		 * Maintenance task schedule
		 */
		MAINTENANCE,

		/**
		 * ProvEn server batch schedule
		 */
		BATCH,

		/**
		 * Domain product generation schedule
		 */
		PRODUCT;
	}

	/**
	 * Generates a new resource identifier for a Proven concept
	 */
	public static String genConceptId(Concept concept) {
		return PROVEN_NS + UUID.randomUUID().toString() + "_" + concept.getClass().getSimpleName();
	}

	/**
	 * Generates a new resource identifier for a Proven context (i.e. named
	 * graph)
	 */
	public static String genContextId(String name, Context context) {
		return PROVEN_NS + name + UUID.randomUUID().toString() + "_" + context.getClass().getSimpleName();
	}

	/**
	 * Generates a new blob key identifier
	 */
	public static String genBlobId() {
		return PROVEN_BLOB_NS + UUID.randomUUID().toString();
	}

	public static Resource toResource(String resource) {
		ValueFactoryImpl vf = ValueFactoryImpl.getInstance();
		return vf.createURI(resource);
	}

	public static Resource toContext(String resource) {
		ValueFactoryImpl vf = ValueFactoryImpl.getInstance();
		return vf.createURI(PROVEN_NS, resource);
	}

	public static URI toUri(String uri) {
		ValueFactoryImpl vf = ValueFactoryImpl.getInstance();
		return vf.createURI(uri);
	}

	public static String toIri(String uri) {
		ValueFactoryImpl vf = ValueFactoryImpl.getInstance();
		return "<" + vf.createURI(uri) + ">";
	}

	public static String toIri(URI uri) {
		return toIri(uri.toString());
	}

	/**
	 * Returns conversion of object to Literal. Supported types include:
	 * <p>
	 * <ul>
	 * <li>String</li>
	 * <li>int</li>
	 * <li>float</li>
	 * <li>long</li>
	 * <li>short</li>
	 * <li>double</li>
	 * <li>byte</li>
	 * <li>Date</li>
	 * </ul>
	 * 
	 * @param literal
	 *            object to be converted to Literal
	 * @return result of Literal conversion
	 * 
	 * @throws IllegalArgumentException
	 *             if object type not supported
	 */
	public static Literal toLiteral(Object literal) {

		Literal ret = null;

		ValueFactoryImpl vf = ValueFactoryImpl.getInstance();

		if (literal instanceof String) {
			ret = vf.createLiteral((String) literal);
		} else if (literal instanceof Value) {
			ret = vf.createLiteral(((Value) literal).stringValue());
		} else if (literal instanceof Integer) {
			ret = vf.createLiteral((Integer) literal);
		} else if (literal instanceof Float) {
			ret = vf.createLiteral((Float) literal);
		} else if (literal instanceof Long) {
			ret = vf.createLiteral((Long) literal);
		} else if (literal instanceof Short) {
			ret = vf.createLiteral((Short) literal);
		} else if (literal instanceof Double) {
			ret = vf.createLiteral((Double) literal);
		} else if (literal instanceof Byte) {
			ret = vf.createLiteral((Byte) literal);
		} else if (literal instanceof Date) {
			ret = vf.createLiteral((Date) literal);
		} else {
			throw new IllegalArgumentException("Data type not supported :: " + literal.getClass().getSimpleName());
		}

		return ret;
	}

	/**
	 * Converts collection of {@link ProvenStatement} statements to a collection
	 * of Sesame statements. This conversion assumes no blank nodes exist
	 * in the source Proven statements.
	 * 
	 * @param source
	 *            Jena model
	 * @return a collection of Sesame statements
	 * @throws InvalidProvenStatementsException
	 */
	public static Collection<Statement> getSesameStatements(Collection<ProvenStatement> pStatements)
			throws InvalidProvenStatementsException {

		Collection<Statement> sStatements = new ArrayList<Statement>();

		try {

			for (ProvenStatement ps : pStatements) {

				Resource s = toResource(ps.getSubject().toString());
				URI p = (URI) toResource(ps.getPredicate().toString());
				Value o = (ps.getObjectValueType() == ProvenStatement.ObjectValueType.Literal)
						? toLiteral(ps.getObject()) : toResource(ps.getObject());
				Statement sStatement = new StatementImpl(s, p, o);
				sStatements.add(sStatement);
				
			}

		} catch (Exception e) {
			throw new InvalidProvenStatementsException("Failed to convert ProvenStatment to Sesame Statement.", e);
		}

		return sStatements;
	}

}
