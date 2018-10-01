package gov.pnnl.proven.cluster.lib.module.request;

import java.sql.Date;

public class testing {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Date date = new Date(new java.util.Date().getTime());
		
		NoopRequest<Date> req = new NoopRequest<>(date);
		
		ModuleRequest<Date> req2 = new NoopRequest<Date>(date);
		
		//req = req2;
		
		ModuleRequest<Date> req3 = req2; 

	}

}
