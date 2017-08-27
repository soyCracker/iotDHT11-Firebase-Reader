using Newtonsoft.Json;
using System;
using System.Threading.Tasks;
using Windows.UI;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Media;
using Windows.Web.Http;

// 空白頁項目範本已記錄在 https://go.microsoft.com/fwlink/?LinkId=402352&clcid=0x404

namespace DHT11Reader
{
    /// <summary>
    /// 可以在本身使用或巡覽至框架內的空白頁面。
    /// </summary>
    public sealed partial class MainPage : Page
    {
        public MainPage()
        {
            this.InitializeComponent();
            getFirebaseData();
        }

        public async void getFirebaseData()
        {
            while(true)
            {
                var client = new HttpClient();
                Uri firebaseUrl = new Uri("https://dht11firebase-9a52d.firebaseio.com/.json");
                HttpResponseMessage response = await client.GetAsync(firebaseUrl);
                response.EnsureSuccessStatusCode();
                string responseBody = await response.Content.ReadAsStringAsync();
                //將JSON字串反序列化成物件
                DHT11Data dht11Data = JsonConvert.DeserializeObject<DHT11Data>(responseBody);
                showData(dht11Data);
                colorSwitch(dht11Data);
                //暫停3秒鐘
                await Task.Delay(5000);
            }           
        }

        public void showData(DHT11Data dht11Data)
        {
            //顯示 dht11 data
            ResultText.Text = "溫度：" + dht11Data.temperaturehumidity.temperature + "\n" + "濕度：" + dht11Data.temperaturehumidity.humidity;
            MeasureTime.Text = "測量時間：" + dht11Data.temperaturehumidity.time;
        }

        public void colorSwitch(DHT11Data dht11Data)
        {
            //正規表達式，刪去*C，並轉換為float
            //Regex regex = new Regex("[\\*C]");
            //float temperature = Convert.ToSingle(regex.Replace(dht11Data.temperaturehumidity.temperature, ""));

            //將溫度字串刪去*C，並轉換為float
            String temString = dht11Data.temperaturehumidity.temperature.Replace("*C", "");
            float temperature = Convert.ToSingle(temString);
            SolidColorBrush scBrush = new SolidColorBrush();
            if (temperature>=28)
            {
                //若溫度>=28，代表嚴熱，theEllipse塗成紅色
                scBrush.Color = Color.FromArgb(255, 231, 76, 60);
            }
            else if(temperature>=17)
            {
                //若溫度>=17、<28，代表正常，theEllipse塗成綠色
                scBrush.Color = Color.FromArgb(255, 162, 231, 103);                
            }
            else
            {
                //若溫度<17，代表寒冷，theEllipse塗成藍色
                scBrush.Color = Color.FromArgb(255, 54, 221, 231);
            }
            theEllipse.Fill = scBrush;
            theEllipse.Stroke = scBrush;
        }

        public class DHT11Data
        {
            public TemperatureHumidity temperaturehumidity { get; set; }
        }

        public class TemperatureHumidity
        {
            public string humidity { get; set; }
            public string temperature{ get; set; }        
            public string time{ get; set; }
        }
    }
}
