package com.rony.drawingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    LinearLayout linearLayout;

    AppCompatButton textButton, pictureButton, drawingButton, saveImageButton;
    ImageView imageView, drawImageView;
    TextView textView;

    String text;
    Uri uri;

    float floatStartX = -1, floatStartY = -1,floatEndX = -1, floatEndY = -1;

    Bitmap bitmap;
    Canvas canvas;
    Paint paint = new Paint();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        textButton = findViewById(R.id.textButton);
        pictureButton = findViewById(R.id.pictureButton);
        drawingButton = findViewById(R.id.drawingButton);
        saveImageButton = findViewById(R.id.saveImageButton);
        imageView = findViewById(R.id.imageView);
        drawImageView = findViewById(R.id.drawImageView);
        textView = findViewById(R.id.textView);
        linearLayout = findViewById(R.id.linearLayout);

        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setVisibility(View.INVISIBLE);
                linearLayout.setVisibility(View.INVISIBLE);
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.pop_up_edittext);
                dialog.show();

                AppCompatButton addButton;
                EditText nameEditText;

                addButton = dialog.findViewById(R.id.addButton);
                nameEditText = dialog.findViewById(R.id.nameEditText);

                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        text = nameEditText.getText().toString();
                        textView.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.INVISIBLE);
                        linearLayout.setVisibility(View.INVISIBLE);
                        textView.setText(text);
                        dialog.dismiss();
                    }
                });
            }
        });

        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(MainActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        drawingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.setVisibility(View.VISIBLE);
                textView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
            }
        });

        saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File fileSaveImage = new File(MainActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        Calendar.getInstance().getTime().toString()+".jpg");

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(fileSaveImage);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    Toast.makeText(MainActivity.this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        uri = data.getData();
        textView.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageURI(uri);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN){
            floatStartX = event.getX();
            floatStartY = event.getY();
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE){

            floatEndX = event.getX();
            floatEndY = event.getY();
            drawPaintSketchImage();

            floatStartX = event.getX();
            floatStartY = event.getY();
        }

        if (event.getAction() == MotionEvent.ACTION_UP){
            floatEndX = event.getX();
            floatEndY = event.getY();
            drawPaintSketchImage();
        }

        return super.onTouchEvent(event);
    }

    private void drawPaintSketchImage(){
        if (bitmap == null){
            bitmap = Bitmap.createBitmap(drawImageView.getWidth(),
                    drawImageView.getHeight(),
                    Bitmap.Config.ARGB_8888);

            canvas = new Canvas(bitmap);

            paint.setColor(Color.RED);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(8);
        }

        canvas.drawLine(floatStartX,
                floatStartY-220,
                floatEndX,
                floatEndY-220,
                paint);

        drawImageView.setImageBitmap(bitmap);
    }
}