package com.example.mahe.mypaint;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

public class EditDoodle extends Activity {
private int PICK_IMAGE_REQUEST = 1;
private Bitmap bitmap1;
private ImageView imageView;
private PaintView paintView;
    private PaintView paintView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doodle);
        paintView=findViewById(R.id.paintView1);
        paintView.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
// Show only images, no videos or anything else
        //intent.setType("image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            InputStream stream;

            try {
                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);//stores image to be drawn on
                // Log.d(TAG, String.valueOf(bitmap));
                stream = getContentResolver().openInputStream(uri);
                bitmap1 = BitmapFactory.decodeStream(stream);
                paintView = findViewById(R.id.paintView1);
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                paintView.init(metrics,bitmap1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
