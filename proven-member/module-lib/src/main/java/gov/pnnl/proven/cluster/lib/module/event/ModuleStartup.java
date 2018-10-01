package gov.pnnl.proven.cluster.lib.module.event;

public class ModuleStartup {

	private String message; 
	
	public ModuleStartup() {
		message = "Module startup message...";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
