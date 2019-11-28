import java.io.*;
import java.net.SocketException;

import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.*;

import org.json.simple.*;
import org.json.simple.parser.ParseException;

public class ProxyCoAP extends CoapServer {

    private final int NUM_NODES = 5;
    private String[] proxyCache; //cache of the Server where the temperature value will be stored - Maybe it has to become a list in order to store the value of every sensor


//constructor
    public ProxyCoAP() throws SocketException {
        proxyCache = new String[NUM_NODES];
        add(new TemperatureResource());
    }

    //Definition of the Temperature Resource 
    class TemperatureResource extends CoapResource {
        
        public TemperatureResource() {
            // set resource identifier
            super("TemperatureResource");
            // set display name
            getAttributes().setTitle("Temperature Resource");
        }
        
    //Definition of the GET handler in order to answer to the Client's requests
        @Override
        public void handleGET(CoapExchange exchange) {
            exchange.respond("Work in progress...");
        }
    }

    public static void main (String[] args) {

        final int NUM_NODES=5;
        try {
            // create server
            ProxyCoAP server = new ProxyCoAP();
            server.start();

            CoapClient[] resource = new CoapClient[NUM_NODES];

            for(int i=0; i<NUM_NODES; i++) {
                System.out.println("Ciclo " + i);
                resource[i] = new CoapClient("coap://[abcd::c30c:0:0:" + (i+2) + "]:5683/test/value");
                resource[i].observe(
                        new CoapHandler() {
                            @Override
                            public void onLoad(CoapResponse response) {
                                String tmp = response.getResponseText();

                                try {
                                    JSONObject jobj = (JSONObject) JSONValue.parseWithException(tmp);

                                    System.out.println("NOTIFICATION (): " + jobj.get("temperature"));
                                } catch(ParseException e) { e.printStackTrace(); }
                            }

                            @Override
                            public void onError() {
                                System.err.println("OBSERVING FAILED (press enter to exit)");
                            }
                        });
            }
        } catch (SocketException e) {    
            System.err.println("Failed to initialize server: " + e.getMessage());
        }
    }
}