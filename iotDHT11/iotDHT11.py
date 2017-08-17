import time  
import requests  
import json
import Adafruit_DHT

# Set Firebase URL                                                
firebase_url = 'https://dht11firebase-9a52d.firebaseio.com/'         

while True:  
    #set pin No and read data from dht11
    humidity, temperature = Adafruit_DHT.read_retry(11, 4)
    if humidity is not None and temperature is not None:
        temperature='{0:0.1f}*C'.format(temperature)
        humidity='{0:0.1f}%'.format(humidity)
        #print('Temp={0:0.1f}*C  Humidity={1:0.1f}%'.format(temperature, humidity))   
        print('Temperature='+temperature+' '+'Humidity='+humidity)   

    #update data ， time.strftime("%F %T")=>2017-08-17 22:29:07
    data = {'temperature':temperature,'humidity':humidity,'time':(time.strftime("%F %T"))}
    result = requests.put(firebase_url + '/' + '/temperaturehumidity.json', data=json.dumps(data))  
    print('Status Code = ' + str(result.status_code) + ', Response = ' + result.text)
    #stop 5 second
    time.sleep(5);
