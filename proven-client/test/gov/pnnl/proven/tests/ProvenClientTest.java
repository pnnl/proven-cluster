package gov.pnnl.proven.tests;

import gov.pnnl.proven.api.producer.ProvenProducer;


public class ProvenClientTest {

	public static void main(String[] args) throws Exception {
		
		ProvenProducer provenMessageProducer = new ProvenProducer();
		//provenMessageProducer.setExchangeInfo(ExchangeType.HZ, null, null, null );
		provenMessageProducer.hzProducer(null, null, null);
		
		/*//send single message
		String message = "{\"test\": \"test message\"}";
		provenMessageProducer.sendMessage(message, null, null);*/
		
		//send message in a loop
		String message;
		for (int i=0; i<4; i++) {
			message = "{\"test\": \"test message " + i + "\" }";
			provenMessageProducer.sendMessage(message, null, null);
		}
		

	}

	public ProvenClientTest() {
		// TODO Auto-generated constructor stub
	}

}


