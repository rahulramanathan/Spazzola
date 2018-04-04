package com.example.mahe.mypaint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Random;

import static android.content.ContentValues.TAG;

public class EditDoodle extends AppCompatActivity {
    Bitmap originalBitmap,image;
    private int mPickedColor = Color.BLACK;
    ImageView iv_ttx;
    FrameLayout frame;
    private PaintView paintView;
    EditText et_sample;
    int PICK_IMAGE_REQUEST=1;
    Paint paint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doodle);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
// Show only images, no videos or anything else
        //intent.setType("image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    String path = getPathFromURI(selectedImageUri);
                    Log.i(TAG, "Image Path : " + path);
                    // Set the image in ImageView
//                    ImageView imageView= ((ImageView) findViewById(R.id.iv_ttx));
//                    imageView.setImageURI(selectedImageUri);
//                    imageView.buildDrawingCache();
//                    Bitmap bitmap = imageView.getDrawingCache();
//                    Canvas canvas = new Canvas(bitmap);

                    Bitmap bitmap = null;
                    frame = findViewById(R.id.painting_frame);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
                    bitmap = Bitmap.createScaledBitmap(bitmap,frame.getWidth(),frame.getHeight(),true);
                    //Log.e("Edit Doodle", String.valueOf(bitmap.isMutable()));
                    paintView = new PaintView(this);
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    paintView.init(metrics, bitmap);
                    frame.addView(paintView);
//                    View v = new MyCanvas(getApplicationContext(),null);
//                    imageView.setImageBitmap(bitmap);
//                    v.draw(canvas);
                }
            }
        }
    }

    /* Get the real path from the URI */
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
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
            case R.id.save:
                paintView.save();
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
        paintView.invalidate();
    }
    public void lineColorClick(View view)
    {
        RelativeLayout r1 = findViewById(R.id.r1);
        GridView gv = (GridView) ColorPicker.getColorPicker(EditDoodle.this);
        gv.setColumnWidth(5);
        gv.setNumColumns(8);
        // Initialize a new AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(EditDoodle.this);
        // Set the alert dialog content to GridView (color picker)
        builder.setView(gv);
        // Initialize a new AlertDialog object
        final AlertDialog dialog = builder.create();
        // Show the color picker window
        dialog.show();
        // Set the color picker dialog size
        dialog.getWindow().setLayout(
                800,800);

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
}
//class MyCanvas extends View {
//    public static int BRUSH_SIZE = 20;
//    public static final int DEFAULT_COLOR = Color.BLACK;
//    public static final int DEFAULT_BG_COLOR = Color.WHITE;
//    private static final float TOUCH_TOLERANCE = 4;
//    private float mX, mY;
//    private Path mPath;
//    private Paint mPaint;
//    private ArrayList<FingerPath> paths = new ArrayList<>();
//    private int currentColor = DEFAULT_COLOR;
//    private int backgroundColor = DEFAULT_BG_COLOR;
//    private int strokeWidth;
//    private boolean emboss;
//    private boolean blur;
//    private MaskFilter mEmboss;
//    private MaskFilter mBlur;
//    private Bitmap mBitmap;
//    private Canvas mCanvas;
//    public MyCanvas(Context context) {
//        super(context);
//
//        // TODO Auto-generated constructor stub
//    }
//    public MyCanvas(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        mPaint = new Paint();
//        mPaint.setAntiAlias(true);
//        mPaint.setDither(true);
//        mPaint.setColor(currentColor);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeJoin(Paint.Join.ROUND);
//        mPaint.setStrokeCap(Paint.Cap.ROUND);
//        mPaint.setXfermode(null);
//        mPaint.setAlpha(0xff);
//
//        mEmboss = new EmbossMaskFilter(new float[] {1, 1, 1}, 0.4f, 6, 3.5f);
//        mBlur = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);
//    }
//    @Override
//    protected void onDraw(Canvas canvas) {
//        // TODO Auto-generated method stub
////        super.onDraw(canvas);
//        //canvas.drawRect(0, 0, 512, 512, pBackground);
//        Paint pText = new Paint();
//        pText.setColor(Color.BLACK);
//        pText.setTextSize(100);
//        canvas.drawText("Sample Text", 100, 100, pText);
//
//    }
//    private void touchStart(float x, float y) {
//        mPath = new Path();
//        FingerPath fp = new FingerPath(currentColor, emboss, blur, strokeWidth, mPath);
//        paths.add(fp);
//
//        mPath.reset();
//        mPath.moveTo(x, y);
//        mX = x;
//        mY = y;
//    }
//
//    private void touchMove(float x, float y) {
//        float dx = Math.abs(x - mX);
//        float dy = Math.abs(y - mY);
//
//        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
//            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
//            mX = x;
//            mY = y;
//        }
//    }
//
//    private void touchUp() {
//        mPath.lineTo(mX, mY);
//    }
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        float x = event.getX();
//        float y = event.getY();
//
//        switch(event.getAction()) {
//            case MotionEvent.ACTION_DOWN :
//                touchStart(x, y);
//                invalidate();
//                break;
//            case MotionEvent.ACTION_MOVE :
//                touchMove(x, y);
//                invalidate();
//                break;
//            case MotionEvent.ACTION_UP :
//                touchUp();
//                invalidate();
//                break;
//        }
//
//        return true;
//    }
//
//}