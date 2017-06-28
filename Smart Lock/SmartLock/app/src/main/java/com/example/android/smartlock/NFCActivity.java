package com.example.android.smartlock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by derek on 3/10/2017.
 */
public class NFCActivity extends AppCompatActivity {
    Button enrollNFC;
    Button unlockNFC;
    String msg;
    final BtMsger QRmsg = new BtMsger();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        enrollNFC = (Button) findViewById(R.id.enrollNFC);
        unlockNFC = (Button) findViewById(R.id.unlockNFC);

        enrollNFC.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                msg = QRmsg.BtReceiveMsg("enrollnfc");
                if(msg == null){
                    msg = "Something happened. Please make sure device is on.";
                }
                QRtoast();
            }
        });
        unlockNFC.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                msg = QRmsg.BtReceiveMsg("scannfc");
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
