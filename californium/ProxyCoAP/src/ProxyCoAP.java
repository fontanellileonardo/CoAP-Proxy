import java.net.SocketException;

import org.eclipse.californium.core.*;

import org.json.simple.*;
import org.json.simple.parser.ParseException;

public class ProxyCoAP extends CoapServer {

    private final int NUM_NODES = 20;
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

    public static int getNum(String ch){
        int num = 0;
        switch(ch){
            case "a": num = 10; break;
            case "b": num = 11; break;
            case "c": num = 12; break;
            case "d": num = 13; break;
            case "e": num = 14; break;
            case "f": num = 15; break;
            default: num = Integer.parseInt(ch);
        }
        return num;
    }

    public int getNumNodes() { return NUM_NODES; }

    public static void main (String[] args) {
        try {
            // create server
            ProxyCoAP server = new ProxyCoAP();
            server.start();
            
            CoapClient[] resource = new CoapClient[server.getNumNodes()];
            int j = 10;
            for(int i=0; i<server.getNumNodes(); i++) {
            	
            	String hex=Integer.toHexString(i+2);
            	System.out.println("coap://[abcd::c30c:0:0:" + hex + "]:5683/test/value");
            	resource[i] = new CoapClient("coap://[abcd::c30c:0:0:" + hex + "]:5683/test/value");
            	/*if(i>=8 && i<=13) {
            		String hex=Integer.toHexString(i+2);
            		//System.out.println(hex);
            		resource[i] = new CoapClient("coap://[abcd::c30c:0:0:" + hex + "]:5683/test/value");
            	} else {
            		if(i>13) { resource[i] = new CoapClient("coap://[abcd::c30c:0:0:" + j + "]:5683/test/value"); j++; }
            		else resource[i] = new CoapClient("coap://[abcd::c30c:0:0:" + (i+2) + "]:5683/test/value");
            		//System.out.println(i+2); 
            		}*/
            	resource[i].observe(
                        new CoapHandler() {
                            @Override
                            public void onLoad(CoapResponse response) {
                            
                                String tmp = response.getResponseText();

                                try {
                                
                                    JSONObject jobj = (JSONObject) JSONValue.parseWithException(tmp);
                                    //String idStr = jobj.get("id").toString();
                                    //int num = Integer.parseInt(idStr, 16); //getNum(idStr);
                                    //System.out.println("DEBUG: Node ID: " + idStr);
                                    
                                    String temperature = jobj.get("temperature").toString();

                                    int id = Integer.parseInt(jobj.get("id").toString());
                                    System.out.println("NOTIFICATION from node "+id+" (0x"+Integer.toHexString(id)+"): " + temperature);
                                    
                                    ProxyCoAP.writeCache((id-2), temperature);
                                    System.out.println("Data saved in position "+(id-2));
                                    
                                    ProxyCoAP.printCache();
                                    System.out.println("\n");
                                    
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
