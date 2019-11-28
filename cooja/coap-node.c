#include <stdio.h>
#include <stdlib.h>
#include "contiki.h"
#include "net/ip/uip.h"
#include "net/ipv6/uip-ds6.h"
#include "net/ip/uip-debug.h"
#include "contiki-net.h"
#include "rest-engine.h"
#include <stdbool.h>

//make connect-router-cooja PREFIX="abcd::1/64"

bool debug = false; //true per attivare delle printf() di debug

static int value = 21; //21 is the starting value
int TIME; //globale perchè contiki del cazzo me lo resett
const int VAR_RANGE = 3; //range di variazione della temperatura (in gradi)

void event_handler();
void get_handler(void*, void*, uint8_t*, uint16_t, int32_t*);

EVENT_RESOURCE(evt_resource, "title=\"Temperature\";rt=\"temperature\";obs", get_handler, NULL, NULL, NULL, event_handler);

void event_handler()
{
	REST.notify_subscribers(&evt_resource); //chiama in automatico la get_handler()
}

void get_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{

	//sprintf((char *) buffer, "{\"id\":\"%d\",\"temperature\":\"%d\"}", id, value); //volendo si può anche aggiungere il parametro JSON { 'scale':'Celsius' }
	sprintf((char *) buffer, "{\"temperature\":\"%d\"}", value);
	REST.set_header_content_type(response, REST.type.APPLICATION_JSON);
	REST.set_response_payload(response, buffer, strlen((char *) buffer));
	
	/*const char *msg = "Bad content type: Supporting JSON.";
	memcpy((char *) buffer, msg, strlen(msg)); 
	REST.set_response_status(response, REST.status.NOT_ACCEPTABLE);
	REST.set_response_payload(response, buffer, strlen((char *) buffer));*/
}

PROCESS(temperature_node, "Temperature-sensing Process");


AUTOSTART_PROCESSES(&temperature_node);


PROCESS_THREAD(temperature_node, ev, data)
{

	PROCESS_BEGIN();

	printf("Starting temperature CoAP mote...\n");

	//initialize the timer
	TIME = ((random_rand() % 10)+1 );
	static struct etimer et;
	etimer_set(&et, TIME*CLOCK_SECOND);

	printf("Starting Erbium Example Server\n");

	rest_init_engine();

	rest_activate_resource(&evt_resource, "test/value");
 
	while(1)
	{
		PROCESS_WAIT_EVENT();

		if(etimer_expired(&et))
		{
			if(debug) printf("DEBUG: Timer expired after %d seconds.\n", TIME);          
			//1) randomly choose if there has been a variation (33% probability)
			if((TIME % 2) == 0)
			{
				int var = (random_rand() % VAR_RANGE);
				if(var > 0)
				{ 		
					//decide wether it is an increase or decrease of temperature (50% chance)
					if(random_rand() % 2 == 0) var = var*(-1);

					value += var;
					printf("Temperature variation registered, new temperature: %d \n", value);

					//trigger the event to notify observers
					evt_resource.trigger();
				}
				else if(debug) printf("DEBUG: variazione = 0\n"); 
			}
			else if(debug) printf("...\n");
		}
		   //reset random timer
		    TIME = ((random_rand() % 10) +1);
		    etimer_set(&et, TIME*CLOCK_SECOND);
	}
	PROCESS_END();
}


