package com.example.mahe.mypaint;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PaintView extends View {

    public static int BRUSH_SIZE = 20;
    int counter = 0;
    public static final int DEFAULT_COLOR = Color.BLACK;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<FingerPath> paths = new ArrayList<>();
    private int currentColor = DEFAULT_COLOR;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private boolean emboss;
    private boolean blur;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private boolean existingBitmap = false;

    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(currentColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);

        mEmboss = new EmbossMaskFilter(new float[] {1, 1, 1}, 0.4f, 6, 3.5f);
        mBlur = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);
    }


    public void init(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        /*else
        {//edit doodle with input as bitmap1 immutable bitmap
            Bitmap mutableBitmap = bitmap1.copy(Bitmap.Config.ARGB_8888,true);
            mCanvas.drawBitmap(mutableBitmap, 30, 30, mBitmapPaint);
            Bitmap tempBitmap = Bitmap.createBitmap(mutableBitmap.getWidth(),mutableBitmap.getHeight(), Bitmap.Config.RGB_565);
            mCanvas = new Canvas(tempBitmap);
            mCanvas.drawBitmap(mutableBitmap,0,0,null);
            mBitmap = mutableBitmap;
        }
        */
        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;
        backgroundColor = DEFAULT_BG_COLOR;
    }
    public void init(DisplayMetrics metrics, Bitmap bitmap) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        mBitmap = bitmap;
        existingBitmap = true;
//        bitmap.setHeight(height);
//        bitmap.setWidth(width);
        mCanvas = new Canvas(bitmap);
        /*else
        {//edit doodle with input as bitmap1 immutable bitmap
            Bitmap mutableBitmap = bitmap1.copy(Bitmap.Config.ARGB_8888,true);
            mCanvas.drawBitmap(mutableBitmap, 30, 30, mBitmapPaint);
            Bitmap tempBitmap = Bitmap.createBitmap(mutableBitmap.getWidth(),mutableBitmap.getHeight(), Bitmap.Config.RGB_565);
            mCanvas = new Canvas(tempBitmap);
            mCanvas.drawBitmap(mutableBitmap,0,0,null);
            mBitmap = mutableBitmap;
        }
        */
        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;
        backgroundColor = DEFAULT_BG_COLOR;
    }
    public void normal() {
        emboss = false;
        blur = false;
    }

    public void emboss() {
        emboss = true;
        blur = false;
    }

    public void blur() {
        emboss = false;
        blur = true;
    }

    public void clear() {
        if(!existingBitmap)
            backgroundColor = DEFAULT_BG_COLOR;
        paths.clear();
        invalidate();
    }
    public void adJustStrokeWidth(int n)
    {
        strokeWidth+=n;
    }
    public void linecolorChange(int a)
    {
        currentColor = a;
    }
    public void backcolorChange(int a)
    {
        backgroundColor = a;
        invalidate();
    }
    public void undoPathChange()
    {//remove the most recent path drawn
        if(paths.size()!=0)
            paths.remove(paths.size()-1);
        invalidate();
    }
    public void save() {//enter code to save bitmap image to gallery
        File storageLoc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); //context.getExternalFilesDir(null);
        String filename = "SpazzolaPic"+counter;
        counter++;
        File file = new File(storageLoc, filename + ".jpg");

        try{
            FileOutputStream fos = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            scanFile(getContext(), Uri.fromFile(file));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getContext(),"Check your Directory_pictures",Toast.LENGTH_SHORT).show();
    }
    private static void scanFile(Context context, Uri imageUri){
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        context.sendBroadcast(scanIntent);

    }
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        if(!existingBitmap)
            mCanvas.drawColor(backgroundColor);

        for (FingerPath fp : paths) {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mPaint.setMaskFilter(null);

            if (fp.emboss)
                mPaint.setMaskFilter(mEmboss);
            else if (fp.blur)
                mPaint.setMaskFilter(mBlur);

            mCanvas.drawPath(fp.path, mPaint);

        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    private void touchStart(float x, float y) {
        mPath = new Path();
        FingerPath fp = new FingerPath(currentColor, emboss, blur, strokeWidth, mPath);
        paths.add(fp);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE :
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP :
                touchUp();
                invalidate();
                break;
        }

        return true;
    }
}