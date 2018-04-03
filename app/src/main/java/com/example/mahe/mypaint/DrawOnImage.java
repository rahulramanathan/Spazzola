package com.example.mahe.mypaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

public class DrawOnImage extends AppCompatActivity {
    ImageView imageView;
    Bitmap originalBitmap,image;
    PaintView paintView2;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_on_image);
        mContext = getApplicationContext();
        paintView2 = findViewById(R.id.paintView2);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height1 = displaymetrics.heightPixels-100;
        int width1 = displaymetrics.widthPixels;
        originalBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.icons_design);
        originalBitmap =Bitmap.createScaledBitmap(originalBitmap, width1, height1, false);
        image = originalBitmap.copy(Bitmap.Config.RGB_565, true);
        paintView2.init(displaymetrics);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.normal:
                paintView2.normal();
                return true;
            case R.id.emboss:
                paintView2.emboss();
                return true;
            case R.id.blur:
                paintView2.blur();
                return true;
            case R.id.clear:
                paintView2.clear();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
