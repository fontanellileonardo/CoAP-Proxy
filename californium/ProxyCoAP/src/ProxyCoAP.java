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
    
    private String proxyCache; //cache of the Server where the temperature value will be stored - Maybe it has to become a list in order to store the value of every sensor
    private CoapClient resource;
    
    /*
     * Constructor for a new Proxy server.
     */
    public ProxyCoAP() throws SocketException {
        proxyCache = "";
        resource = new CoapClient("coap://[abcd::c30c:0:0:2]:5683/test/value"); //address of the observable resource 
        add(new TemperatureResource());
    }

    public void observe(){
        System.out.println("OBSERVE STARTED");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 
        
        CoapObserveRelation relation = resource.observe(
            new CoapHandler() {
                @Override public void onLoad(CoapResponse response) {
                    proxyCache = response.getResponseText();
                    System.out.println("NOTIFICATION: " + proxyCache);
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
        
    /*
    *   Definition of the GET handler in order to answer to the Client's requests
    */
        @Override
        public void handleGET(CoapExchange exchange) {
            exchange.respond(proxyCache);
        }
    }

    public static void main (String[] args) {
        
        try {
            // create server
            ProxyCoAP server = new ProxyCoAP();
            server.start();
            server.observe();
            
        } catch (SocketException e) {    
            System.err.println("Failed to initialize server: " + e.getMessage());
        }
    }

}
