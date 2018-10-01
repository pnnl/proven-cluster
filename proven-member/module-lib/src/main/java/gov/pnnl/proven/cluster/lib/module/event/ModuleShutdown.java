package gov.pnnl.proven.cluster.lib.module.event;

public class ModuleShutdown {

	private String message; 
	
	public ModuleShutdown() {
		message = "Module startup message...";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
