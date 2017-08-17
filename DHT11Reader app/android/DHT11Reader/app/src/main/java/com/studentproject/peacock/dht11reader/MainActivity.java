package com.studentproject.peacock.dht11reader;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public String temperature,humidity,time;
    private TextView ResultText;
    private Button readButton;

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
        readButton=(Button)findViewById(R.id.readButton);

        readButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                getFirebaseData();
            }
        });
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
                        //這裡為想讓他執行的工作
                        ResultText.setText("溫度: "+temperature+" \n"+"濕度: "+humidity+" \n"+"測量時間: "+time);
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

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //結束程式釋放HandlerThread
        refreshHT.quit();
    }

}
