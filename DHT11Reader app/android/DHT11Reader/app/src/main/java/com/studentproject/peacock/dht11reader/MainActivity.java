package com.studentproject.peacock.dht11reader;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public String temperature,humidity,time;
    public TextView ResultText,MeasureTimeText;


    //建一個HandlerThread，在後台讀取Firebase的資料
    private HandlerThread refreshHT=new HandlerThread("HandlerThread");
    private Handler refreshH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化控件
        initView();
        //開始執行HandlerThread
        refreshHT.start();
        //生成Looper，重複執行
        refreshH=new Handler(refreshHT.getLooper());
        //指派工作
        refreshH.post(refreshRun);
    }

    public void initView() {
        ResultText=(TextView)findViewById(R.id.ResultText);
        MeasureTimeText=(TextView)findViewById(R.id.MeasureTimeText);
    }

    //從Firebase讀取資料
    public void getFirebaseData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("temperaturehumidity");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                temperature = dataSnapshot.child("temperature").getValue().toString();
                humidity = dataSnapshot.child("humidity").getValue().toString();
                time = dataSnapshot.child("time").getValue().toString();

                runOnUiThread(new Runnable() {
                    public void run(){
                        // UI更新
                        showData(temperature,humidity,time);
                        colorSwitch(temperature);
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private Runnable refreshRun=new Runnable() {
        @Override
        public void run() {
            getFirebaseData();
            try {
                //每次暫停5秒再執行
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    public void showData(String temperature,String humidity,String time)
    {
        ResultText.setText("溫度: "+temperature+" \n"+"濕度: "+humidity);
        MeasureTimeText.setText("測量時間: "+time);
    }

    public  void colorSwitch(String temString)
    {
        //將溫度字串刪去*C
        temString=temString.replace("*C","");
        //轉換為float
        float temperature=Float.parseFloat(temString);
        //改變Shape的顏色(solid color)
        GradientDrawable theDrawable = (GradientDrawable)ResultText.getBackground();
        if(temperature>=28)
        {
            //若溫度>=28，代表嚴熱，將圓塗成紅色
            theDrawable.setColor(Color.argb(255, 231, 76, 60));
        }
        else if(temperature>=17)
        {
            //若溫度>=17、<28，代表正常，將圓塗成綠色
            theDrawable.setColor(Color.argb(255, 162, 231, 103));
        }
        else
        {
            //若溫度<17，代表寒冷，將圓塗成藍色
            theDrawable.setColor(Color.argb(255, 54, 221, 231));
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //結束程式釋放HandlerThread
        refreshHT.quit();
    }
}
