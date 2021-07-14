package com.odev.gokturktextrecognize;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.FileInputStream;


public class MainActivity extends AppCompatActivity {
    ImageView mImageView;
    ImageButton cameraBtn;
    ImageButton detectBtn;
    Bitmap bmpfromSecond;
    String message;
    TextView textView;
    String log = "error";

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundley = getIntent().getExtras();
        if (bundley != null) {
            message = bundley.getString("message");

            message = message.replaceAll("A",getString(R.string.letterThirtySecond));
            message = message.replaceAll("R",getString(R.string.letterThirtySecond));

            message = message.replaceAll("N",getString(R.string.letterThird));

            message = message.replaceAll("T",getString(R.string.letterSeventeenth));

            message = message.replaceAll("h",getString(R.string.letterTwentyFirst));

            message = message.replaceAll("x",getString(R.string.letterSeventh));
            message = message.replaceAll("X",getString(R.string.letterSeventh));

            message = message.replaceAll("g",getString(R.string.letterTwentyThird));
            message = message.replaceAll("9",getString(R.string.letterTwentyThird));

            message = message.replaceAll("4",getString(R.string.letterSixteenth));

            message = message.replaceAll("6",getString(R.string.letterFourth));

            message = message.replaceAll("D",getString(R.string.letterTwentySecond));

            message = message.replaceAll("H",getString(R.string.letterTenth));

            message = message.replaceAll("33",getString(R.string.letterSixth));

            message = message.replaceAll("e",getString(R.string.letterNineth));

            message = message.replaceAll("Y",getString(R.string.letterThirteenth));

            message = message.replaceAll("M",getString(R.string.letterThirtyFifth));

            message = message.replaceAll("1",getString(R.string.letterTwentyEighth));

            message = message.replaceAll("3",getString(R.string.letterTwentySixth));

          //  message = message.replaceAll("o",getString(R.string.letterThirtySeventh));

            String filename = getIntent().getStringExtra("image");
            try {
                FileInputStream is = this.openFileInput(filename);
                bmpfromSecond = BitmapFactory.decodeStream(is);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            bmpfromSecond = rotateImage(bmpfromSecond,90); // bitmap'i 90 derece döndürmek için
            mImageView.setImageBitmap(bmpfromSecond);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = findViewById(R.id.mImageView);
        cameraBtn = findViewById(R.id.cameraButton);
        detectBtn = findViewById(R.id.detectButton);
        textView = findViewById(R.id.textView);
        textView.setTypeface(ResourcesCompat.getFont(this, R.font.segoeui));

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ActivitySecond.class);;
                startActivity(intent);
                textView.setText("");
            }
        });
        detectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    textView.setText(message);
            }
        });
    }

    public static Bitmap rotateImage(Bitmap src, float degree)
    {
        // create new matrix
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(degree);
        Bitmap bitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return bitmap;
    }
}