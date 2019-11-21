import java.net.SocketException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.CoapExchange;


public class ProxyCoAP extends CoapServer {
    
    /*
     * Application entry point.
     */
    public static void main(String[] args) {
        
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
        add(new HelloWorldResource());
    }
    
    /*
     * Definition of the Hello-World Resource 
     */
    class HelloWorldResource extends CoapResource {
        
        public HelloWorldResource() {
            
            // set resource identifier
            super("helloWorld");
            
            // set display name
            getAttributes().setTitle("Hello-World Resource");
        }
        
        @Override
        public void handleGET(CoapExchange exchange) {
            
            // respond to the request
            exchange.respond("Hello World!");
        }
    }
}
