/**
 * 
 */
package gov.pnnl.proven.cluster.lib.module.component;

/**
 * Common interface for all Proven components. A component, in this context,
 * represents an element of the Proven distributed processing framework. 
 * 
 * @author d3j766
 *
 */
public interface ProvenComponent {

	/**
	 * Each component must have a unique identifier.
	 * 
	 * @return a string identifier
	 */
	public String getIdentifier();
	
	/**
	 * The type of component.
	 * 
	 * @return the {@link ComponentType}
	 */
	public ComponentType getType();

	/**
	 * The current state of the component.
	 * 
	 * @return the {@link ComponentState}
	 */
	public ComponentState getState();

	
	/**
	 * Sets component state.
	 * 
	 * @param state the {@link ComponentState}
	 */
	public void setState(ComponentState state);

}
