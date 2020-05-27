import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;

import org.json.simple.*;
import org.json.simple.parser.ParseException;

public class ClientCoAP {
	
	public static void printMsg() {
		System.out.println("Available commands:");
		System.out.println("GET ---> get the temperature from a specific node\nQUIT ---> close the client\nHELP ---> view available commands");
	}
	public static void main(String args[]){

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String rq = "";
		String addr = "";

		URI uri = null;
		int node_id=0;
		
		System.out.print("\033[H\033[2J");  //"clear" the screen
		System.out.flush(); 
		System.out.println("======================CLIENT STARTED======================");
		printMsg();

		while(true){
			System.out.println("\n");
			System.out.print("[Insert a command or HELP]: ");
			try {
				rq = br.readLine();
			} catch (IOException e) { e.printStackTrace(); }

			if(rq.equals("GET") || rq.equals("get")){
				//System.out.println("GET request for a node");
				System.out.print("[Insert node ID]: ");
				try {
					rq = br.readLine();
				} catch (IOException e) { e.printStackTrace(); }
				
				if(rq.matches("^[0-9]*$")){
					System.out.println("GET request for node: " + rq);
					
					try {
						node_id = Integer.parseInt(rq);
						node_id -=2;
						uri = new URI("coap://127.0.0.1:5683/TemperatureResource:"+ node_id);
						
					} catch (URISyntaxException e1) { e1.printStackTrace(); }
					
					// URI parameter of the request	
					CoapClient client = new CoapClient(uri);
					CoapResponse response = client.get();
					
					if (response!=null) {
						
						String tmp = response.getResponseText();
						
						try {
							JSONObject jobj = (JSONObject) JSONValue.parseWithException(tmp);
							String temperature = jobj.get("temperature").toString();
						
						//System.out.println(response.getCode());
						//System.out.println(response.getOptions());
							System.out.print("\033[H\033[2J");  //"clear" the screen
		    					System.out.flush(); 
							System.out.println("Temperature at node "+ (node_id+2) +": "+temperature+"°C");
							
						} catch(ParseException pe) { pe.printStackTrace(); }
						
						// access advanced API with access to more details through .advanced()
						//System.out.println("\nADVANCED\n");
						//System.out.println(Utils.prettyPrint(response));
						
					} else System.out.println("ERROR: No response received.");	
				}
				else System.out.println("ERROR: You have to specify an ID of a node, witch is an Integer number ");
			}
			else if(rq.equals("QUIT") || rq.equals("quit")) {
				System.out.println("Closing the Client...");
				break;
			}
			else if(rq.equals("HELP") || rq.equals("help")) printMsg();
			else System.out.println("ERROR: Invalid command. Enter HELP  to view available commands.");
		}
	}
}
