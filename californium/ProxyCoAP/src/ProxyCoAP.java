import java.net.SocketException;

import org.eclipse.californium.core.*;

import org.json.simple.*;
import org.json.simple.parser.ParseException;

public class ProxyCoAP extends CoapServer {

    private final int NUM_NODES = 22;
    private static String[] proxyCache; //cache of the Server where the temperature value will be stored - Maybe it has to become a list in order to store the value of every sensor
    private TemperatureResource[] t;

    //constructor
    public ProxyCoAP() throws SocketException {
    
    	proxyCache = new String[NUM_NODES];
    	t = new TemperatureResource[NUM_NODES];
    	
        for(int i = 0; i < NUM_NODES; i++) {
            t[i] = new TemperatureResource(i);
            this.add(new TemperatureResource(i));
        }

    }

    public static void writeCache(int index, String txt) { proxyCache[index] = txt; }

    public static void printCache() {
    
        System.out.print("[ ");
        for(int j=0; j<proxyCache.length; j++)
            System.out.print(proxyCache[j] + " ");
            
        System.out.println(" ]");
    }

    public static String getCache(int index){ return proxyCache[index]; }
    
    public int getNumNodes() { return NUM_NODES; }

    public static void main (String[] args) {
        //final int NUM_NODES=5; //da sistemare e mettere in un file utils con tutte le costanti
        try {
            // create server
            ProxyCoAP server = new ProxyCoAP();
            server.start();
            
            CoapClient[] resource = new CoapClient[server.getNumNodes()];
            for(int i=0; i<server.getNumNodes(); i++) {
            	
            	resource[i] = new CoapClient("coap://[abcd::c30c:0:0:" + (i+2) + "]:5683/test/value");
            	resource[i].observe(
                        new CoapHandler() {
                            @Override
                            public void onLoad(CoapResponse response) {
                            
                                String tmp = response.getResponseText();

                                try {
                                
                                    JSONObject jobj = (JSONObject) JSONValue.parseWithException(tmp);

                                    String idStr = jobj.get("id").toString();
                                    String temperature = jobj.get("temperature").toString();

                                    int id = Integer.parseInt(idStr);

                                    System.out.println("NOTIFICATION ("+id+"): " + temperature);
                                    ProxyCoAP.writeCache((id-2), temperature);
                                    ProxyCoAP.printCache();
                                    
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
