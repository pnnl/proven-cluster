package gov.pnnl.proven.cluster.lib.module;

import java.io.Serializable;
import java.util.Optional;

import javax.validation.constraints.AssertFalse;

//import org.junit.Test;

public class TestOptional<T,S> implements Serializable {

	private static final long serialVersionUID = 1L;

	public Optional<T> input = Optional.empty();
	
	public Optional<Class<T>> inputClass = Optional.empty();
	
	public Optional<Class<S>> outputClass = Optional.empty();
	
	
	public TestOptional(Class<T> ic) {
		inputClass = Optional.of(ic);
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		TestOptional<Void, String> testO = new TestOptional<>(Void.class); 
		
		System.out.println(testO.inputClass.isPresent());
		
		System.out.println(testO.inputClass.isPresent());
		
		System.out.println(testO.inputClass.get().equals(String.class) );
		
	}

}
