import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;


public class ClientCoAP {

	public static void main(String args[]) {
		
		URI uri = null;
		try {
			uri = new URI("coap://127.0.0.1:5683/helloWorld");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // URI parameter of the request		
		CoapClient client = new CoapClient(uri);
		CoapResponse response = client.get();
		if (response!=null) {
			
			System.out.println(response.getCode());
			System.out.println(response.getOptions());
			System.out.println(response.getResponseText());
			
			System.out.println("\nADVANCED\n");
			// access advanced API with access to more details through .advanced()
			System.out.println(Utils.prettyPrint(response));
			
		} else {
			System.out.println("No response received.");
		}		 
	}
}
