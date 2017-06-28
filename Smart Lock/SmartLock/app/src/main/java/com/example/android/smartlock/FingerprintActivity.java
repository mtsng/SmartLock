package com.example.android.smartlock;

/**
 * Created by derek on 3/10/2017.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FingerprintActivity extends AppCompatActivity {
    Button enrollFinger;
    Button scanFinger;
    String msg;
    final BtMsger QRmsg = new BtMsger();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_demo);

        enrollFinger = (Button) findViewById(R.id.enrollFinger);
        scanFinger = (Button) findViewById(R.id.scanFinger);

        enrollFinger.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                msg = QRmsg.BtReceiveMsg("enrollfinger");
                if(msg == null){
                    msg = "Something happened. Please make sure device is on.";
                }
                QRtoast();
            }
        });
        scanFinger.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                msg = QRmsg.BtReceiveMsg("scanfinger");
                if(msg == null){
                    msg = "Something happened. Please make sure device is on.";
                }
                QRtoast();
            }
        });

    }

    public void QRtoast(){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}