package com.example.mahe.mypaint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

public class CreateDoodle extends AppCompatActivity {

    private PaintView paintView;
    private Context mContext;
    private int mPickedColor = Color.WHITE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_doodle);
        Intent intent = getIntent();
        mContext = getApplicationContext();
        paintView = findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics,null);
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
                paintView.normal();
                return true;
            case R.id.emboss:
                paintView.emboss();
                return true;
            case R.id.blur:
                paintView.blur();
                return true;
            case R.id.clear:
                paintView.clear();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void plusClick(View view)
    {
        paintView.adJustStrokeWidth(+2);
    }
    public void minusClick(View view)
    {
        paintView.adJustStrokeWidth(-2);
    }
    public void undoClick(View view)
    {
        paintView.undoPathChange();
    }
    public void lineColorClick(View view)
    {
        RelativeLayout r1 = findViewById(R.id.r1);
        GridView gv = (GridView) ColorPicker.getColorPicker(CreateDoodle.this);
        // Initialize a new AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateDoodle.this);
        // Set the alert dialog content to GridView (color picker)
        builder.setView(gv);
        // Initialize a new AlertDialog object
        final AlertDialog dialog = builder.create();
        // Show the color picker window
        dialog.show();
        // Set the color picker dialog size
        dialog.getWindow().setLayout(
                750,750);

        // Set an item click listener for GridView widget
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the pickedColor from AdapterView
                mPickedColor = (int) parent.getItemAtPosition(position);
                paintView.linecolorChange(mPickedColor);
                // close the color picker
                dialog.dismiss();
            }
        });
    }
    public void backColorClick(View view)
    {//set background colour

        RelativeLayout r1 = findViewById(R.id.r1);
        GridView gv = (GridView) ColorPicker.getColorPicker(CreateDoodle.this);
        // Initialize a new AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateDoodle.this);
        // Set the alert dialog content to GridView (color picker)
        builder.setView(gv);
        // Initialize a new AlertDialog object
        final AlertDialog dialog = builder.create();
        // Show the color picker window
        dialog.show();
        // Set the color picker dialog size
        dialog.getWindow().setLayout(
                750,750);

        // Set an item click listener for GridView widget
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the pickedColor from AdapterView
                mPickedColor = (int) parent.getItemAtPosition(position);
                paintView.backcolorChange(mPickedColor);
                // close the color picker
                dialog.dismiss();
            }
        });
    }
    // Custom method to get the screen width in pixels
    private Point getScreenSize(){
        WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        //Display dimensions in pixels
        display.getSize(size);
        return size;
    }
    // Custom method to get status bar height in pixels
    public int getStatusBarHeight() {
        int height = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }
}