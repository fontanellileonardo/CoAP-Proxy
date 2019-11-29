import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;

public class ClientCoAP {

	public static void main(String args[]){

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String rq = "";
		String addr = "";

		URI uri = null;
		int node_id=0;

		System.out.println("CLIENT STARTED");
		System.out.println("Available commands:");
		System.out.println("\tGET for getting the temperature value indicating the node - QUIT for quitting the client");

		while(true){
			System.out.print("\tInsert a command: ");
			try {
				rq = br.readLine();
			} catch (IOException e) { e.printStackTrace(); }

			if(rq.equals("GET") || rq.equals("get")){
				System.out.println("GET request for a node");
				System.out.print("\tInsert now the node at witch you want to send the request: ");
				try {
					rq = br.readLine();
				} catch (IOException e) { e.printStackTrace(); }
				
				if(rq.matches("^[0-9]*$")){
					System.out.println("GET request for node: " + rq);
					try {
						node_id = Integer.parseInt(rq);
						node_id -=2;
						uri = new URI("coap://127.0.0.1:5683/TemperatureResource:"+ node_id);
					} catch (URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} // URI parameter of the request	
					CoapClient client = new CoapClient(uri);

					CoapResponse response = client.get();
					if (response!=null) {
						
						//System.out.println(response.getCode());
						//System.out.println(response.getOptions());
						System.out.print("\033[H\033[2J");  //"clear" the screen
	    					System.out.flush(); 
						System.out.println("Temperature at node "+ (node_id+2) +": "+response.getResponseText()+"°C");
						
						
						// access advanced API with access to more details through .advanced()
						//System.out.println("\nADVANCED\n");
						//System.out.println(Utils.prettyPrint(response));
						
					} else {
						System.out.println("No response received.");
					}	
				}
				else{
					System.out.println("You have to specify an ID of a node, witch is an Integer number ");
				}
			}
			else if(rq.equals("QUIT") || rq.equals("quit")){
				System.out.println("Client quitted");
				break;
			}	 
			else{
				System.out.println("Error! Insert a GET or QUIT command");
			}
		}
	}
}
