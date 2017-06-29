package share.imooc.com.nodepaddemo.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.SimpleFormatter;

import share.imooc.com.nodepaddemo.listener.SaveImageLister;

/**
 * Created by asus- on 2017/4/21.
 */

public class TuyaView extends View{
    private SaveImageLister saveImageLister;
    private Context context;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;//画布的画笔
    private Paint mBitmapPaint;//真实的画笔
    private Path mPath;
    private float mX,mY;//临时点坐标
    private static final float TOUCH_TOLERANCE = 4;
    // 保存Path路径的集合
    private static List<DrawPath>savepath;
    // 保存已删除Path路径的集合
    private static List<DrawPath> deletePath;
    // 记录Path路径的对象
    private DrawPath dp;
    private int screenWidth,screenHeight;
    private int currentColor= Color.RED;
    private int currentSize=5;
    private int currentStyle=1;
    //颜色集合
    private int[]paintColor;

    private class DrawPath {
        public Path path;// 路径
        public Paint paint;// 画笔
    }
    public TuyaView(Context context, int w,int h) {
        super(context);
        this.context=context;
        screenWidth=w;
        screenHeight=h;
        paintColor=new int[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.BLACK, Color.GRAY, Color.CYAN };
        //设置默认样式，去除dis-in的黑色方框以及clear模式的黑线效果
        setLayerType(LAYER_TYPE_SOFTWARE,null);
        initCanvas();
        savepath = new ArrayList<DrawPath>();
        deletePath = new ArrayList<DrawPath>();
    }

    private void initCanvas() {
        setPaintStyle();
        mBitmapPaint=new Paint(Paint.DITHER_FLAG);
        mBitmap=Bitmap.createBitmap(screenWidth,screenHeight,Bitmap.Config.ARGB_8888);
        //把图片设置成透明
        mBitmap.eraseColor(Color.argb(0,0,0,0));
        mCanvas = new Canvas(mBitmap);  //所有mCanvas画的东西都被保存在了mBitmap中
        mCanvas.drawColor(Color.TRANSPARENT);
    }
    //初始化画笔样式
    private void setPaintStyle() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
        //设置抗锯齿
        mPaint.setAntiAlias(true);
        //该方法是设置防抖动。
        mPaint.setDither(true);
        //普通画笔
        if (currentStyle == 1) {
            mPaint.setStrokeWidth(currentSize);
            mPaint.setColor(currentColor);
        }
        //橡皮擦
        else {
            mPaint.setAlpha(0);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            mPaint.setColor(Color.TRANSPARENT);
            mPaint.setStrokeWidth(50);
        }

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap,0,0,mBitmapPaint);
        if (mPath!=null){
            // 实时的显示
            canvas.drawPath(mPath,mPaint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
       float x=event.getX();
        float y=event.getY();
switch (event.getAction()){
    case MotionEvent.ACTION_DOWN:
        // 每次down下去重新new一个Path
        mPath=new Path();
        //每一次记录的路径对象是不一样的
        dp = new DrawPath();
        dp.path=mPath;
        dp.paint=mPaint;
        touch_start(x,y);
        invalidate();
        break;
    case MotionEvent.ACTION_MOVE:
        touch_move(x, y);
        invalidate();
        break;
    case MotionEvent.ACTION_UP:
        touch_up();
        invalidate();
        break;
}
        return true;
    }

    private void touch_up() {
        mPath.lineTo(mX,mY);
        mCanvas.drawPath(mPath,mPaint);
        //将一条完整的路径保存下来
        savepath.add(dp);
        mPath = null;// 重新置空
    }

    private void touch_move(float x, float y) {
        float dx=Math.abs(mX-x);
        float dy= Math.abs(y-mY);
        if (dx>=TOUCH_TOLERANCE||dy>=TOUCH_TOLERANCE){
            mPath.quadTo(mX,mY,(mX+x)/2,(mY+y)/2);
            mX = x;
            mY = y;
        }
    }

    private void touch_start(float x, float y) {
        mPath.moveTo(x,y);
        mX = x;
        mY = y;
    }

/* 撤销
   撤销的核心思想就是将画布清空，
   将保存下来的Path路径最后一个移除掉，
   重新将路径画在画布上面。
* */
    public void undo(){
        if (savepath!=null&&savepath.size()>0){
            DrawPath lastDrawPath=savepath.get(savepath.size()-1);
            deletePath.add(lastDrawPath);
            savepath.remove(savepath.size()-1);
            redrawOnBitmap();


        }
    }

    private void redrawOnBitmap() {
        initCanvas();//清空画布
        //重新将路径画在画布上面
        Iterator<DrawPath> iterator=savepath.iterator();
        while (iterator.hasNext()){
            DrawPath drawPath=iterator.next();
            mCanvas.drawPath(drawPath.path,drawPath.paint);
        }
        invalidate();
    }

    //重做
    public void redo(){
        if (savepath!=null&&savepath.size()>0){
            savepath.clear();
            redrawOnBitmap();
        }
    }
    //恢复,恢复的核心就是将删除的那条路径重新添加到savapath中重新绘画即可
    public void recover(){
        if (deletePath!=null&&deletePath.size()>0){
            //将删除的路径列表中的最后一个，也就是最顶端路径取出（栈）,并加入路径保存列表中
            DrawPath drawPath=deletePath.get(deletePath.size()-1);
            savepath.add(drawPath);
            //将取出的路径重绘在画布上
            mCanvas.drawPath(drawPath.path, drawPath.paint);
            //将该路径从删除的路径列表中去除
            deletePath.remove(deletePath.size() - 1);
            invalidate();
        }
    }

    //以下为样式修改内容
    //设置画笔样式
    public void selectPaintStyle(int which){
        //选择画笔
         if (which==0){
             currentStyle=1;
             setPaintStyle();
         } //当选择的是橡皮擦时，设置颜色为白色
         else if(which==1) {
             currentStyle = 2;
             setPaintStyle();
         }
    }
    //选择画笔大小
    public void selectPaintSize(int which){
        currentSize=which;
        setPaintStyle();
    }
    //设置画笔颜色
    public void selectPaintColor(int which){
        currentColor=paintColor[which];
        setPaintStyle();
    }

    //保存到sd卡
    public void saveToSDCard(){
        //获得系统当前时间，并以该时间作为文件名
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate=new Date(System.currentTimeMillis());
        String str=simpleDateFormat.format(curDate)+"paint.png";
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/" + str);
        FileOutputStream fos=null;
        try {
            fos=new FileOutputStream(file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

     mBitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
        //发送Sd卡的就绪广播,要不然在手机图库中不存在
       /* Intent intent=new Intent(Intent.ACTION_MEDIA_MOUNTED);
        intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
        context.sendBroadcast(intent);*/
        try {
            fos.close();
            saveImageLister.onSaveFinshed(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("TAG", "图片已保存");
    }



    public void setSaveImageLister(SaveImageLister saveImageLister){
        this.saveImageLister=saveImageLister;
    }
}
