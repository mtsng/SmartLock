package com.example.android.smartlock;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * Created by derek on 3/10/2017.
 */
public class QRActivity extends AppCompatActivity{

    /*
    created by Alexander Farber, ZXing library
     */

    Button sendQR;
    Button makeQR;
    String msg;
    ImageView img;
    final BtMsger QRmsg = new BtMsger();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        img = (ImageView) findViewById(R.id.QRcode);
        Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.emptyqr);
        img.setImageBitmap(icon);

        makeQR = (Button) findViewById(R.id.generateQR);
        sendQR = (Button) findViewById(R.id.scanQR);

        makeQR.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                msg = QRmsg.BtReceiveMsg("makeqr");
                if(msg == null){
                    msg = "Something happened. Please make sure device is on.";
                }
                else{
                    QRdisplay(msg);
                }
                QRtoast();
            }
        });
        sendQR.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                msg = QRmsg.BtReceiveMsg("scanqr");
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

    public void QRdisplay(String msg){
        Bitmap bit;
        try{
            bit = generateQR(msg);
            img.setImageBitmap(bit);
        }
        catch(WriterException e){
            e.printStackTrace();
        }
    }

    public Bitmap generateQR(String s) throws WriterException {
        BitMatrix result;
        Bitmap b = null;
        int w = 400;
        int h = 400;
        int WHITE = 0xFFFFFFFF;
        int BLACK = 0xFF000000;
        if(s != null){
            try{
                result = new MultiFormatWriter().encode(s, BarcodeFormat.QR_CODE, w, h, null);
            }
            catch(IllegalArgumentException e){
                return null;
            }
            int ww = result.getWidth();
            int hh = result.getHeight();
            int[] pixels = new int[ww*hh];
            for(int y=0;y<hh;y++){
                int offset = y * w;
                for(int x=0;x<ww;x++){
                    pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
                }
            }

            b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            b.setPixels(pixels, 0, ww, 0, 0, ww, hh);
        }
        return b;
    }

}
