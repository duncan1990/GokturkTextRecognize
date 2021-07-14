package com.odev.gokturktextrecognize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.odev.gokturktextrecognize.MainActivity.rotateImage;

public class ActivitySecond extends AppCompatActivity {
    SurfaceView cameraView;
    TextView textView;
    CameraSource cameraSource;
    ImageButton buttonTakePhoto;
    Button buttonOk, buttonCancel;
    ImageView imageV;


    final int RequestCameraPermission = 1001;

    // 3
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RequestCameraPermission: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {

                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        cameraView = findViewById(R.id.surface_view);
        textView = findViewById(R.id.text_view);
        textView.setTypeface(ResourcesCompat.getFont(this, R.font.segoeui));
        buttonTakePhoto = findViewById(R.id.imageButtonTakePhoto);

        // 1
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
       if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "Detected dependence are not found ");
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(3500.0f)
                    .setAutoFocusEnabled(true)
                    .build();

            // 2
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(ActivitySecond.this,new String[]{Manifest.permission.CAMERA},
                                    RequestCameraPermission);
                        }
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                    cameraSource.stop();
                }
            });


            // 4
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                    Log.i("release","asdsa");
                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0 ){
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < items.size();i++){
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                stringBuilder.reverse();
                                textView.setText(stringBuilder.toString());
                                Log.d("Text",stringBuilder.toString());
                            }
                        });
                    }
                }
            });
       }

        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraSource.takePicture(null, new CameraSource.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        setContentView(R.layout.activity_image);
                        buttonOk = findViewById(R.id.buttonOk);
                        buttonCancel = findViewById(R.id.buttonCancel);
                        imageV = findViewById(R.id.imageViewImageActivity);
                        Bitmap btmap = rotateImage(bmp,90);
                        imageV.setImageBitmap(btmap);
                        //Write file
                        String filename = "bitmap.png";
                        FileOutputStream stream = null;
                        try {
                            stream = ActivitySecond.this.openFileOutput(filename, Context.MODE_PRIVATE);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        //Cleanup
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bmp.recycle();

                        buttonCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setContentView(R.layout.activity_second);
                            }
                        });
                        buttonOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {

                                    //Pop intent
                                    Intent in1 = new Intent(ActivitySecond.this, MainActivity.class);
                                    String msg = (String) textView.getText();
                                    in1.putExtra("message", msg);
                                    in1.putExtra("image", filename);
                                    startActivity(in1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });

            }
        });
    }

}