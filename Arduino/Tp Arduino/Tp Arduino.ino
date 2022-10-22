#include <SoftwareSerial.h>

SoftwareSerial BTserial(8,12); // RX | TX
char c = ' ';
const int timeThreshold = 300;
const int intPin = 2;
volatile int ISRCounter = 0;
int counter = 0;
long startTime = 0;
// Tipos de eventos disponibles .
//----------------------------------------------
#define TIPO_EVENTO_CONTINUAR						2000
#define TIPO_EVENTO_SENSOR_TEMP_BAJA 				3000
#define TIPO_EVENTO_SENSOR_TEMP_IDEAL				4000
#define TIPO_EVENTO_SENSOR_TEMP_ALTA				5000
#define TIPO_EVENTO_MSJ_ENVIADO						6000
#define TIPO_EVENTO_SENSOR_HAY_AGUA					7000
#define TIPO_EVENTO_SIN_AGUA						8000
#define TIPO_EVENTO_APAGADO							9000
#define TIPO_EVENTO_SENSOR_TEMP_ALTA			    10000
#define TIPO_EVENTO_ENCENDER						11000
//----------------------------------------------

//----------------------------------------------
// Estado del embeded ...
#define ESTADO_APAGADO								100
#define ESTADO_CON_AGUA								200
#define ESTADO_CLIMATIZADOR_ESPERANDO_RESPUESTA_MSJ	300
#define ESTADO_SIN_AGUA								400
#define ESTADO_TEMPERATURA_BAJA						500
#define APAGADO										600
#define ESTADO_FINALIZADO							700
#define ESTADO_TEMPERATURA_ALTA						800
#define ESTADO_TEMPERATURA_IDEAL					900
//----------------------------------------------

//----------------------------------------------
// Estado de un sensor ...
#define ESTADO_SENSOR_OK 							108
#define ESTADO_SENSOR_ERROR   						666
//----------------------------------------------

//----------------------------------------------
// Estado de un mensaje ...
#define MENSAJE_ENVIADO_OK 							10
#define MENSAJE_ENVIADO_ERROR  						666
//----------------------------------------------

// Otras constantes ....
//----------------------------------------------
#define UMBRAL_DIFERENCIA_TIMEOUT					50
#define UMBRAL_TEMPERATURA_MINIMA					100
#define UMBRAL_TEMPERATURA_FRIO						300
#define UMBRAL_TEMPERATURA_MEDIO					516
#define UMBRAL_TEMPERATURA_ALTA						900
#define UMBRAL_HUMEDAD_CON_AGUA						100
#define UMBRAL_HUMEDAD_SIN_AGUA						0
#define MAX_CANT_SENSORES  							2
#define SENSOR_TEMPERATURA							0
#define SENSOR_HUMEDAD								1
#define PIN_SENSOR_TEMPERATURA						A5
#define PIN_SENSOR_HUMEDAD							A0
#define PIN_LED_AZUL 								10
#define PIN_LED_VERDE 	 							9
#define PIN_LED_ROJO 	 							11
#define btn											2
#define PIN_RESISTENCIA								4
//----------------------------------------------

//----------------------------------------------
struct stSensor
{
  int  pin;
  int  estado;
  int  estadoH;
  long valor_actual;
  long valor_previo;
};
stSensor sensores[MAX_CANT_SENSORES];
//----------------------------------------------

//----------------------------------------------
struct stEvento
{
  int tipo;
  int param1;
  int param2;
};
stEvento evento;
//----------------------------------------------


// Variables globales ...
//----------------------------------------------
int estado;
bool estado_boton;
bool estado_boton_anterior;
bool timeout;
long lct;
//----------------------------------------------


//----------------------------------------------
void do_init()
{
  Serial.begin(9600);

  pinMode (btn, INPUT);
  pinMode(PIN_LED_VERDE, OUTPUT);
  pinMode(PIN_LED_AZUL , OUTPUT);
  pinMode(PIN_LED_ROJO , OUTPUT);
  pinMode(PIN_RESISTENCIA , OUTPUT);
BTserial.begin(9600);
 
  sensores[SENSOR_TEMPERATURA].pin 	  = PIN_SENSOR_TEMPERATURA;
  sensores[SENSOR_TEMPERATURA].estado = ESTADO_SENSOR_OK;
  sensores[SENSOR_HUMEDAD].pin 	  = PIN_SENSOR_HUMEDAD;
  sensores[SENSOR_HUMEDAD].estado = ESTADO_SENSOR_OK;
  
  // Inicializo el evento inicial
  estado = ESTADO_APAGADO;
  estado_boton = false;
  estado_boton_anterior = false;
  attachInterrupt(digitalPinToInterrupt(btn),handle_btn,RISING);
  
  timeout = false;
  lct 	  = millis();
  BTserial.print("inicializando...");
}
//----------------------------------------------
void debounceCount()
{
	if (millis() - startTime > timeThreshold)
	{
		ISRCounter++;
		startTime = millis();
    estado_boton = !estado_boton;
	}
}
//-----------------------------------------------

void handle_btn(){
  debounceCount();
 
}

//----------------------------------------------
long leerSensorTemperatura( )
{
  return analogRead(PIN_SENSOR_TEMPERATURA);
}
//----------------------------------------------
//----------------------------------------------
long leerSensorHumedad( )
{
  return analogRead(PIN_SENSOR_HUMEDAD);
}
//----------------------------------------------
//----------------------------------------------
long leerBoton1( )
{
  return digitalRead(btn);
}
//----------------------------------------------

//----------------------------------------------
void apagar_leds( )
{
  analogWrite(PIN_LED_VERDE, 0);
  analogWrite(PIN_LED_AZUL , 0);
  analogWrite(PIN_LED_ROJO , 0);
  
}
//----------------------------------------------

//----------------------------------------------
void apagar_calentador( )
{
  digitalWrite(PIN_RESISTENCIA, LOW);
}
//----------------------------------------------

//----------------------------------------------
void encender_calentador( )
{
  digitalWrite(PIN_RESISTENCIA, HIGH);
}
//----------------------------------------------


//----------------------------------------------
void actualizar_indicador_led_azul( )
{
  analogWrite(PIN_LED_VERDE, 0);
  analogWrite(PIN_LED_AZUL , 255 );
  analogWrite(PIN_LED_ROJO , 0);
  
}
//----------------------------------------------

//----------------------------------------------
void actualizar_indicador_led_verde( )
{
  analogWrite(PIN_LED_VERDE, 255);
  analogWrite(PIN_LED_AZUL , 0);
  analogWrite(PIN_LED_ROJO , 0);
  
}
//----------------------------------------------

//----------------------------------------------
void actualizar_indicador_led_rojo( )
{
  analogWrite(PIN_LED_VERDE, 0);
  analogWrite(PIN_LED_AZUL , 0);
  analogWrite(PIN_LED_ROJO , 255 );
 
}
//----------------------------------------------

//----------------------------------------------

// ---------------------------------------------
bool verificarEstadoSensorHumedad()
{
	

  sensores[SENSOR_HUMEDAD].valor_actual = leerSensorHumedad( );
  Serial.print("Humedad ");
  Serial.println(sensores[SENSOR_HUMEDAD].valor_actual);

  int valor_actual = sensores[SENSOR_HUMEDAD].valor_actual;
  Serial.println("valor de humedad ahora");
  Serial.println(valor_actual);
  Serial.println(valor_actual);
delay(100);

  if(valor_actual > UMBRAL_HUMEDAD_SIN_AGUA)
  {
    Serial.println("entre del primero");
	  if( valor_actual < UMBRAL_HUMEDAD_CON_AGUA )
	  {
      Serial.println("entre del sin agua");
		evento.tipo   = TIPO_EVENTO_SIN_AGUA;
		estado = ESTADO_SIN_AGUA;
	  }
	  else if( valor_actual >= UMBRAL_HUMEDAD_CON_AGUA)
	  {
    Serial.println("entre con agua");
    Serial.println(evento.tipo);
		evento.tipo   = TIPO_EVENTO_SENSOR_HAY_AGUA;
     Serial.println(evento.tipo);
		evento.param1 = SENSOR_HUMEDAD;
		evento.param2 = valor_actual;
    estado=ESTADO_CON_AGUA;
		return true;
	  }
  	}
  
  return false;
}

// ---------------------------------------------
bool verificarEstadoSensorTemperatura( )
{
 
  sensores[SENSOR_TEMPERATURA].valor_actual = leerSensorTemperatura( );
 // Serial.print("Temp ");
 // Serial.println(sensores[SENSOR_TEMPERATURA].valor_actual);

  int valor_actual = sensores[SENSOR_TEMPERATURA].valor_actual;
  int valor_previo = sensores[SENSOR_TEMPERATURA].valor_previo;
    
  if(valor_actual > UMBRAL_TEMPERATURA_MINIMA && valor_actual != valor_previo )
  {
	  sensores[SENSOR_TEMPERATURA].valor_previo = valor_actual;
	  
	  if(  valor_actual < UMBRAL_TEMPERATURA_FRIO  )
	  {
		evento.tipo   = TIPO_EVENTO_SENSOR_TEMP_BAJA;
		evento.param1 = SENSOR_TEMPERATURA;
		evento.param2 = valor_actual;
	  }
	  else if( (valor_actual >= UMBRAL_TEMPERATURA_FRIO)&& (valor_actual<UMBRAL_TEMPERATURA_MEDIO))
	  {
		evento.tipo   = TIPO_EVENTO_SENSOR_TEMP_IDEAL;
		evento.param1 = SENSOR_TEMPERATURA;
		evento.param2 = valor_actual;
	  }
	  else if( valor_actual >= UMBRAL_TEMPERATURA_MEDIO )
	  {
		evento.tipo   = TIPO_EVENTO_SENSOR_TEMP_ALTA;
		evento.param1 = SENSOR_TEMPERATURA;
		evento.param2 = valor_actual;
	  }
      if( valor_actual >= UMBRAL_TEMPERATURA_ALTA)
      {
        evento.tipo = TIPO_EVENTO_APAGADO;
      }
	  return true;
  }
  
  return false;
}
//----------------------------------------------

//----------------------------------------------
bool verificarBoton1()
{
  
  if(BTserial.available())
	  {
      c = BTserial.read();
		if(c=='a')
			{
				estado = ESTADO_APAGADO;
				evento.tipo = TIPO_EVENTO_APAGADO;
          			estado_boton_anterior = estado_boton;
				return true;
			}

		if(c=='c')	
			{
				evento.tipo   = TIPO_EVENTO_ENCENDER;
	    	    estado_boton_anterior = estado_boton;
				return true;
			}
		
	  }
	return false;
}
//----------------------------------------------
//----------------------------------------------
//----------------------------------------------

//----------------------------------------------

//----------------------------------------------


//----------------------------------------------

//----------------------------------------------
void genera_evento()
{
	long ct = millis();
	int  diferencia = (ct - lct);
	timeout = (diferencia > UMBRAL_DIFERENCIA_TIMEOUT)? (true):(false);

	if( timeout )
	{
		// Doy acuse de la recepcion del timeout
		timeout = false;
		lct 	= ct;

		if( (verificarBoton1() == true)	)
		{
				
		if( (verificarEstadoSensorHumedad() == true) )
		{
       Serial.println("llegue al if humedad");
			if( (verificarEstadoSensorTemperatura() == true))
			{
        Serial.println("llegue al if temperatura");
				return;
			}
  
         }
         }
      
	}
	//Serial.println(evento.tipo);	
	// Genero evento dummy ....
  Serial.println("dummy");
	evento.tipo = TIPO_EVENTO_CONTINUAR;
//	Serial.println(evento.tipo);
}
//----------------------------------------------

//----------------------------------------------
void maquina_estados_Pava_Inteligente( )
{
	//Serial.println(estado);
	genera_evento();
//	Serial.println(estado);
  
	switch( estado )
	{
		case ESTADO_APAGADO:
		{
			switch(evento.tipo)
			{
			case TIPO_EVENTO_ENCENDER:
				{
			//		Serial.println("-----------------------------------------------------");
				//	Serial.println("Estado ESTADO_APAGADO...");
				//	Serial.println("Evento TIPO_EVENTO_ENCENDER...");
				//	Serial.println("-----------------------------------------------------");
					

					estado = ESTADO_SIN_AGUA;
				
				}
				break;
				case TIPO_EVENTO_CONTINUAR:
				{
					//Serial.println("-----------------------------------------------------");
					//Serial.println("Estado ESTADO_APAGADO...");
					//Serial.println("Evento TIPO_EVENTO_CONTINUAR...");
					//Serial.println("-----------------------------------------------------");
					
					apagar_calentador();
					apagar_leds();
                    estado = ESTADO_APAGADO;
				}
				break;
				
				default:

              	break;
			}
		break;
        }

		
		case ESTADO_SIN_AGUA:
		{
			switch(evento.tipo)
			{
			case TIPO_EVENTO_SENSOR_HAY_AGUA:
					{
						Serial.println("-----------------------------------------------------");
						Serial.println("Estado ESTADO_SIN_AGUA...");
						Serial.println("Evento TIPO_EVENTO_SENSOR_HAY_AGUA...");
						Serial.println(evento.param2);
						Serial.println("-----------------------------------------------------");
						
						encender_calentador();
						
						estado = ESTADO_CON_AGUA;
					}
					break;
				
					case TIPO_EVENTO_APAGADO:
					{
						Serial.println("-----------------------------------------------------");
						Serial.println("Estado APAGADO...");
						Serial.println("Evento TIPO_EVENTO_APAGADO...");
						Serial.println("-----------------------------------------------------");
						
						apagar_calentador();
						apagar_leds();
						estado = APAGADO;
					}
					break;
					
					case TIPO_EVENTO_CONTINUAR:
					{
						Serial.println("-----------------------------------------------------");
						Serial.println("Estado ESTADO_SIN_AGUA...");
						Serial.println("Evento TIPO_EVENTO_CONTINUAR...");
						Serial.println("-----------------------------------------------------");
							
						estado = ESTADO_SIN_AGUA;
					}
					break;
					
					default:

              		break;
			}
			break;
		}
		
		case ESTADO_CON_AGUA: 
		{
			switch(evento.tipo)
			{

				case TIPO_EVENTO_SIN_AGUA:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_CON_AGUA...");
					Serial.println("Evento TIPO_EVENTO_SIN_AGUA...");
					Serial.println("-----------------------------------------------------");
					
					
					estado = ESTADO_SIN_AGUA;
				}
				break;



				case TIPO_EVENTO_SENSOR_TEMP_BAJA:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_CON_AGUA...");
					Serial.println("Evento TIPO_EVENTO_SENSOR_TEMP_BAJA...");
					Serial.println(evento.param2);
					Serial.println("-----------------------------------------------------");

					actualizar_indicador_led_azul( );
					estado = ESTADO_TEMPERATURA_BAJA;
				}
				break;
			
              	case TIPO_EVENTO_SENSOR_TEMP_IDEAL:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_CON_AGUA...");
					Serial.println("Evento TIPO_EVENTO_SENSOR_TEMP_IDEAL...");
					Serial.println(evento.param2);
					Serial.println("-----------------------------------------------------");

					actualizar_indicador_led_verde( );
					estado = ESTADO_TEMPERATURA_IDEAL;
				}
				break;
              
              			
              	case TIPO_EVENTO_SENSOR_TEMP_ALTA:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_CON_AGUA...");
					Serial.println("Evento TIPO_EVENTO_SENSOR_TEMP_ALTA...");
					Serial.println(evento.param2);
					Serial.println("-----------------------------------------------------");

					actualizar_indicador_led_rojo( );
					estado = ESTADO_TEMPERATURA_ALTA;
				}
				break;
              
				case TIPO_EVENTO_APAGADO:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_CON_AGUA...");
					Serial.println("Evento TIPO_EVENTO_APAGADO...");
					Serial.println("-----------------------------------------------------");
					
					apagar_calentador();
					apagar_leds();
					estado = APAGADO;
				}
				break;
				
				case TIPO_EVENTO_CONTINUAR:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_CON_AGUA...");
					Serial.println("Evento TIPO_EVENTO_CONTINUAR...");
					Serial.println("-----------------------------------------------------");
						

					estado = ESTADO_CON_AGUA;
				}
				break;
				

				
				default:

              	break;
			}
		break;
		}	

        
		case ESTADO_TEMPERATURA_BAJA:
		{
			switch(evento.tipo)
			{
				case TIPO_EVENTO_SENSOR_TEMP_IDEAL:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_TEMPERATURA_BAJA...");
					Serial.println("Evento TIPO_EVENTO_SENSOR_TEMP_IDEAL...");
					Serial.println(evento.param2);
					Serial.println("-----------------------------------------------------");
					
					actualizar_indicador_led_verde( );
					
					estado = ESTADO_TEMPERATURA_IDEAL;
				}
				break;
			
              	case TIPO_EVENTO_SENSOR_TEMP_ALTA:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_TEMPERATURA_BAJA...");
					Serial.println("Evento TIPO_EVENTO_SENSOR_TEMP_ALTA...");
					Serial.println(evento.param2);
					Serial.println("-----------------------------------------------------");
					
					actualizar_indicador_led_rojo( );
					
					estado = ESTADO_TEMPERATURA_ALTA;
				}
				break;
              
				case TIPO_EVENTO_APAGADO:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_TEMPERATURA_BAJA...");
					Serial.println("Evento TIPO_EVENTO_APAGADO...");
					Serial.println("-----------------------------------------------------");
					
					apagar_calentador();
					apagar_leds();
					estado = APAGADO;
				}
				break;
				
				case TIPO_EVENTO_SIN_AGUA:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_SIN_AGUA...");
					Serial.println("Evento TIPO_EVENTO_SIN_AGUA...");
					Serial.println("-----------------------------------------------------");
						
					estado = ESTADO_SIN_AGUA;
				}
				break;
				
				case TIPO_EVENTO_CONTINUAR:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_TEMPERATURA_BAJA...");
					Serial.println("Evento TIPO_EVENTO_CONTINUAR...");
					Serial.println("-----------------------------------------------------");
						
					estado = ESTADO_TEMPERATURA_BAJA;
				}
				break;
				default:

              	break;
			}
		break;
        }


		case ESTADO_TEMPERATURA_IDEAL:
		{
			switch(evento.tipo)
			{
				case TIPO_EVENTO_SENSOR_TEMP_ALTA:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_TEMPERATURA_IDEAL...");
					Serial.println("Evento TIPO_EVENTO_SENSOR_TEMP_ALTA...");
					Serial.println(evento.param2);
					Serial.println("-----------------------------------------------------");
					
					actualizar_indicador_led_rojo( );
					
					estado = ESTADO_TEMPERATURA_ALTA;
				}
				break;
			
              	case TIPO_EVENTO_SENSOR_TEMP_BAJA:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_TEMPERATURA_IDEAL...");
					Serial.println("Evento TIPO_EVENTO_SENSOR_TEMP_BAJA...");
					Serial.println(evento.param2);
					Serial.println("-----------------------------------------------------");
					
					actualizar_indicador_led_azul( );
					
					estado = ESTADO_TEMPERATURA_BAJA;
				}
				break;
              
				case TIPO_EVENTO_CONTINUAR:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_TEMPERATURA_IDEAL...");
					Serial.println("Evento TIPO_EVENTO_CONTINUAR...");
				  Serial.println("-----------------------------------------------------");
						
					estado = ESTADO_TEMPERATURA_IDEAL;
				}
				break;
			
				case TIPO_EVENTO_APAGADO:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_TEMPERATURA_IDEAL...");
					Serial.println("Evento TIPO_EVENTO_APAGADO...");
					Serial.println("-----------------------------------------------------");

					apagar_calentador();
					apagar_leds();
					estado = APAGADO;
				}
				break;
				
				case TIPO_EVENTO_SIN_AGUA:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_SIN_AGUA...");
					Serial.println("Evento TIPO_EVENTO_SIN_AGUA...");
					Serial.println("-----------------------------------------------------");
						
					estado = ESTADO_SIN_AGUA;
				}
				break;
		
				default:

              	break;
			}
          break;
		}
        break;

		case ESTADO_TEMPERATURA_ALTA:
		{
			switch(evento.tipo)
			{
				case TIPO_EVENTO_SENSOR_TEMP_ALTA:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_TEMPERATURA_ALTA...");
					Serial.println("Evento TIPO_EVENTO_SENSOR_TEMP_ALTA...");
					Serial.println(evento.param2);
					Serial.println("-----------------------------------------------------");
					
					actualizar_indicador_led_rojo( );
					
					estado = ESTADO_FINALIZADO;
				}
				break;
			
              	case TIPO_EVENTO_SENSOR_TEMP_IDEAL:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_TEMPERATURA_ALTA...");
					Serial.println("Evento TIPO_EVENTO_SENSOR_TEMP_IDEAL...");
					Serial.println(evento.param2);
					Serial.println("-----------------------------------------------------");
					
					actualizar_indicador_led_verde( );
					
					estado = ESTADO_TEMPERATURA_IDEAL;
				}
				break;
				
				case TIPO_EVENTO_SENSOR_TEMP_BAJA:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_TEMPERATURA_ALTA...");
					Serial.println("Evento TIPO_EVENTO_SENSOR_TEMP_BAJA...");
					Serial.println(evento.param2);
					Serial.println("-----------------------------------------------------");
					
					actualizar_indicador_led_azul( );
					
					estado = ESTADO_TEMPERATURA_BAJA;
				}
				break;
				
				case TIPO_EVENTO_CONTINUAR:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_TEMPERATURA_ALTA...");
					Serial.println("Evento TIPO_EVENTO_CONTINUAR...");
					Serial.println("-----------------------------------------------------");
						
					estado = ESTADO_TEMPERATURA_ALTA;
				}
				break;			
			
				case TIPO_EVENTO_SIN_AGUA:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_SIN_AGUA...");
				  Serial.println("Evento TIPO_EVENTO_SIN_AGUA...");
					Serial.println("-----------------------------------------------------");
						
					estado = ESTADO_SIN_AGUA;
				}
				break;
			
				case TIPO_EVENTO_APAGADO:
				{
					Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_TEMPERATURA_ALTA...");
					Serial.println("Evento TIPO_EVENTO_APAGADO...");
					Serial.println("-----------------------------------------------------");

					apagar_calentador();
					apagar_leds();
					estado = APAGADO;
				}
				break;
				
				default:

              	break;
			}
          break;
		}
        break;

		case ESTADO_FINALIZADO:
		{
			switch(evento.tipo)
			{
			
				case TIPO_EVENTO_APAGADO:
				{
			   	Serial.println("-----------------------------------------------------");
					Serial.println("Estado ESTADO_FINALIZADO...");
					Serial.println("Evento TIPO_EVENTO_APAGADO...");
					Serial.println("-----------------------------------------------------");

					apagar_calentador();
					apagar_leds();
					estado = ESTADO_APAGADO;
				}
				break;
				
				case TIPO_EVENTO_CONTINUAR:
				{
			  		Serial.println("-----------------------------------------------------");
		  			Serial.println("Estado ESTADO_FINALIZADO...");
	  				Serial.println("Evento TIPO_EVENTO_CONTINUAR...");
            Serial.println("-----------------------------------------------------");
						
					apagar_calentador();
					apagar_leds();
					estado = ESTADO_APAGADO;
				}
				break;		
				
				default:

              	break;
			}
          break;
		}

	break;
	}
  
  // Consumo el evento...
  evento.tipo   = TIPO_EVENTO_CONTINUAR;
  evento.param1 = -1;
  evento.param2 = -1;
}


// Funciones de arduino !. 
//----------------------------------------------
void setup()
{
    do_init();
}
//----------------------------------------------

//----------------------------------------------
void loop()
{
	maquina_estados_Pava_Inteligente();
  //delay(100);
}
//----------------------------------------------