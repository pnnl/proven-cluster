package gov.pnnl.proven.cluster.lib.disclosure;

import java.util.ArrayList;
import java.util.List;

import org.leadpony.justify.api.Problem;
import org.leadpony.justify.api.ProblemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A custom handler that saves all problems encountered during validation
 * process.
 *
 * @author d3j766
 * @see ProblemHandler, Problem
 * 
 */
public class ValidatableProblemHandler implements ProblemHandler {

	static Logger log = LoggerFactory.getLogger(ValidatableProblemHandler.class);

	private List<Problem> problems = new ArrayList<>();

	public ValidatableProblemHandler() {
	}

	/**
	 * Callback for JsonParse. Problems, if any, encountered during validation
	 * process are saved for post validation processing.
	 * 
	 * @see #getProblems()
	 */
	@Override
	public void handleProblems(List<Problem> problems) {
		this.problems.addAll(problems);
	}

	/**
	 * Returns all problems encountered during validation process.
	 * 
	 * @return a list of Problem
	 */
	public List<Problem> getProblems() {
		return problems;
	}

	/**
	 * Convenience method. Returns true if no problems encountered during
	 * a validation process.
	 * 
	 * @return
	 */
	public boolean isValid() {
		return problems.isEmpty();
	}

}
