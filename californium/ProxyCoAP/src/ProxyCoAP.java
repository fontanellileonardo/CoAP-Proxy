import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;

public class ProxyCoAP extends CoapServer {
    
    /*
     * Application entry point.
     */
    public static void main (String[] args) {
        
        try {
            
            // create server
            ProxyCoAP server = new ProxyCoAP();
            server.start();
            
        } catch (SocketException e) {
            
            System.err.println("Failed to initialize server: " + e.getMessage());
        }
    }
    
    /*
     * Constructor for a new Hello-World server. Here, the resources
     * of the server are initialized.
     */
    public ProxyCoAP() throws SocketException {
        
        // provide an instance of a Hello-World resource
        add(new TemperatureResource());
    }
    
    /*
     * Definition of the Temperature Resource 
     */
    class TemperatureResource extends CoapResource {
        
        public TemperatureResource() {
            // set resource identifier
            super("TemperatureResource");
            // set display name
            getAttributes().setTitle("Temperature Resource");
        }
        
        @Override
        public void handleGET(CoapExchange exchange) { //questa è la funzione che fa l'handle della GET proveniente dal client. 
                                                        // Quindi qua dobbiamo far partire l'osservazione della risorsa e rispondere al client con la temperatura rilevata e notificarlo ogni volta che cambia
            
        // starting della observation della risorsa dei mote.
        System.out.println("OBSERVE STARTED");

        String content;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); //se serve l'input da tastiera, altrimenti si toglie
        CoapClient client = new CoapClient("coap://"); //indirizzo del border router (credo), da inserire

        CoapObserveRelation relation = client.observe(
                new CoapHandler() {
                    @Override public void onLoad(CoapResponse response) {
                        content = response.getResponseText();
                        System.out.println("NOTIFICATION: " + content);
                        exchange.respond(content);
                    }
                    
                    @Override public void onError() {
                        System.err.println("OBSERVING FAILED (press enter to exit)");
                    }
                });
        
        // wait for user
        try { 
            br.readLine(); 
        } catch (IOException e) { }
        
        System.out.println("CANCELLATION");
        
        relation.proactiveCancel();

        }
    }
}
