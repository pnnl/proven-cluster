import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import gov.pnnl.proven.cluster.lib.disclosure.DisclosureIDSFactory;
import gov.pnnl.proven.cluster.lib.disclosure.exchange.DisclosureItem;
import gov.pnnl.proven.cluster.lib.disclosure.message.DisclosureMessage;
import gov.pnnl.proven.cluster.lib.disclosure.message.KnowledgeMessage;

public class TestHZKm {

	public static void main(String[] args) throws Exception {

		// Get test message
		String testMessage = TestHZKm.readFile("/tmp/km_test/m2");
		JsonReader jsonReader = Json.createReader(new StringReader(testMessage));
		JsonObject message = jsonReader.readObject();

		// Get KM
		DisclosureItem di = new DisclosureItem(message);
		DisclosureMessage dm = new DisclosureMessage(di);
		KnowledgeMessage kmTestWrite = new KnowledgeMessage(dm);

		// Connect client
		ClientConfig config = new ClientConfig();
		config.getSerializationConfig().addDataSerializableFactoryClass(DisclosureIDSFactory.FACTORY_ID, DisclosureIDSFactory.class);
		GroupConfig groupConfig = new GroupConfig();
		groupConfig.setName("proven");
		config.setGroupConfig(groupConfig);
		config.getNetworkConfig().addAddress("127.0.0.1:5701");
		HazelcastInstance hzClient = HazelcastClient.newHazelcastClient(config);
		IMap<String, KnowledgeMessage> messages = hzClient.getMap("gov.pnnl.tmbr.knowledge");
		System.out.println();

		int i = 0;
		int iterations = 1;
		String pmKey = "";
		System.out.println("START :: " + Calendar.getInstance().getTime().toString());
		while (i <= (iterations - 1)) {

			// Create message
			pmKey = kmTestWrite.getMessageKey();
			System.out.println(i + "  " + pmKey);

			// Add message to cluster
			messages.set(pmKey, kmTestWrite);

			i++;

		}
		
		// Read entry
		KnowledgeMessage kmTestRead = messages.get(pmKey);

		// Disconnect client
		hzClient.shutdown();

		System.out.println("END :: " + Calendar.getInstance().getTime().toString());
	}

	private static String readFile(String filePath) {
		StringBuilder contentBuilder = new StringBuilder();
		try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contentBuilder.toString();
	}

}
