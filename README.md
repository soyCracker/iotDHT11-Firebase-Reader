# iotDHT11-Firebase-Reader
### 物聯網溫度計：
raspbarry pi3透過dht11讀取溫濕度，並上傳至Firebase，再利用app抓取並顯示。

#### iotDHT11
平台為raspberry pi3，安裝dht11溫溼度感測器，使用python抓取資料。  
需事先安裝程式庫 adafruit dht11：
````
git clone https://github.com/adafruit/Adafruit_Python_DHT.git  '
cd Adafruit_Python_DHT
sudo python setup.py install
````

#### Reader app
##### android
android與firebase連接有官方支援，沒什麼注意事項
##### win10 UWP
需安裝 Nuget：  
Newtonsoft.Json
