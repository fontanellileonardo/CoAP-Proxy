#include <stdio.h>
#include <stdlib.h>
#include "contiki.h"
#include "net/ip/uip.h"
#include "net/ipv6/uip-ds6.h"
#include "net/ip/uip-debug.h"
#include "contiki-net.h"
#include "rest-engine.h"

static int value = 21; //21 is the starting value
int TIME; //globale perchè contiki del cazzo me lo resett

void get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{

  unsigned int accept = -1;
  REST.get_header_accept(request, &accept);

  if(accept == -1 || accept == REST.type.TEXT_PLAIN){
 
      sprintf((char *) buffer, "Value: %d", value);
      REST.set_header_content_type(response, REST.type.TEXT_PLAIN);
      REST.set_response_payload(response, buffer, strlen((char *) buffer));

  }else if(accept == REST.type.APPLICATION_XML){

      sprintf((char *) buffer, "<Value>\"%d\"</Value>", value);
      REST.set_header_content_type(response, REST.type.APPLICATION_XML);
      REST.set_response_payload(response, buffer, strlen((char *) buffer));

  }else if(accept == REST.type.APPLICATION_JSON){

      sprintf((char *) buffer, "{'Value':%d}", value);
      REST.set_header_content_type(response, REST.type.APPLICATION_JSON);
      REST.set_response_payload(response, buffer, strlen((char *) buffer));

  }else{

      const char *msg = "Bad content type: Supporting plain text; xml; or json";
      memcpy((char *) buffer, msg, strlen(msg)); 
      REST.set_response_status(response, REST.status.NOT_ACCEPTABLE);
      REST.set_response_payload(response, buffer, strlen((char *) buffer));
  
  }
}

void put_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{

  int new_value, len;
  const char *val = NULL;
     
  len = REST.get_post_variable(request, "value", &val);
     
  if(len > 0){

     new_value = atoi(val);
     printf("New value: %d\n", new_value);
     value = new_value;
     REST.set_response_status(response, REST.status.CREATED);
  
  }else{

     REST.set_response_status(response, REST.status.BAD_REQUEST);

  }
}


RESOURCE(getputvalue, "title=\"Get value\"; rt=\"resource for exercise\"", get_handler, NULL, NULL, NULL);


PROCESS(getput_example, "Erbium Example Server for Get and Put");


AUTOSTART_PROCESSES(&getput_example);


PROCESS_THREAD(getput_example, ev, data)
{

  PROCESS_BEGIN();
	
	printf("Starting temperature CoAP mote...\n");

    //initialize the timer
    TIME = ((random_rand() % 10)+1 );
    static struct etimer et;
    etimer_set(&et, TIME*CLOCK_SECOND);
    
  printf("Starting Erbium Example Server\n");

  rest_init_engine();

  rest_activate_resource(&getputvalue, "test/value");
 
  while(1){
    
     PROCESS_WAIT_EVENT();
     
     if(etimer_expired(&et))
        {  
        printf("Timer expired after %d seconds.\n", TIME);          
            //1) randomly choose if there has been a variation (33% probability)
            if((TIME % 2) == 0)
            {
            	int var = (random_rand() % 3);
            	if(var > 0)
            	{
            		
            		//decide wether it is an increase or decrease of temperature
            		
            		if(random_rand() % 2 == 0) var = var*(-1);
            		
            		value += var;
            		printf("Temperature variation registered, new temperature: %d \n", value);
            	}
            	else printf("DEBUG: variazione = 0\n"); 
            }
            else printf("...\n");
	}
            
           //reset random timer
            TIME = ((random_rand() % 10) +1);
            etimer_set(&et, TIME*CLOCK_SECOND);
  }

  PROCESS_END();

}


