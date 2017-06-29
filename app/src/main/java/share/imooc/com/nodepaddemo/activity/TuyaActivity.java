package share.imooc.com.nodepaddemo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import share.imooc.com.nodepaddemo.R;
import share.imooc.com.nodepaddemo.listener.SaveImageLister;
import share.imooc.com.nodepaddemo.widget.TuyaView;

public class TuyaActivity extends AppCompatActivity implements View.OnClickListener,SaveImageLister {
    private Button btnundo;
    private Button btnredo;
    private Button btnrecover;
    private Button btnsave;
    private Button btnpaintcolor;
    private Button btnpaintsize;
    private Button btnpaintstyle;
    private FrameLayout frameLayout;
    private SeekBar sbsize;
    private TuyaView tuyaView;
    private int select_paint_color_index = 0;
    private int select_paint_style_index = 0;
    private String[] paintColor = new String[]{"红色", "蓝色", "绿色", "黄色", "黑色", "灰色", "蓝绿色"};
    private String[] paintstyle = new String[]{"橡皮擦", "普通画笔"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tuya);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        frameLayout = (FrameLayout) findViewById(R.id.activity_main);
        btnpaintcolor = (Button) findViewById(R.id.btn_paintcolor);
        btnpaintsize = (Button) findViewById(R.id.btn_paintsize);
        btnpaintstyle = (Button) findViewById(R.id.btn_paintstyle);
        btnrecover = (Button) findViewById(R.id.btn_recover);
        btnundo = (Button) findViewById(R.id.btn_undo);
        btnredo = (Button) findViewById(R.id.btn_redo);
        btnsave = (Button) findViewById(R.id.btn_save);
        sbsize = (SeekBar) findViewById(R.id.seekbar);
    }

    private void initData() {
        //获得屏幕宽高
        Display display = getWindowManager().getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();
        tuyaView = new TuyaView(this, screenWidth, screenHeight);
        frameLayout.addView(tuyaView);
        tuyaView.requestFocus();
        tuyaView.selectPaintSize(sbsize.getProgress());
    }

    private void initListener() {
        btnundo.setOnClickListener(this);
        btnredo.setOnClickListener(this);
        btnsave.setOnClickListener(this);
        btnrecover.setOnClickListener(this);
        btnpaintcolor.setOnClickListener(this);
        btnpaintsize.setOnClickListener(this);
        btnpaintstyle.setOnClickListener(this);
        tuyaView.setSaveImageLister(this);
        sbsize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tuyaView.selectPaintSize(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_undo://撤销
                tuyaView.undo();
                break;
            case R.id.btn_redo://重做
                tuyaView.redo();
                break;
            case R.id.btn_recover://恢
                tuyaView.recover();
                break;
            case R.id.btn_save://保存
                tuyaView.saveToSDCard();
                finish();
                break;
            case R.id.btn_paintcolor:
                sbsize.setVisibility(View.GONE);
                showPaintColorDialog(v);
                break;
            case R.id.btn_paintsize:
                sbsize.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_paintstyle:
                sbsize.setVisibility(View.GONE);
                showMoreDialog(v);
                break;
        }
    }

    private void showMoreDialog(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("选择画笔或橡皮擦：");
        alertDialogBuilder.setSingleChoiceItems(paintstyle, select_paint_style_index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select_paint_style_index = which;
                tuyaView.selectPaintStyle(which);
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create().show();
    }

    private void showPaintColorDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择画笔颜色：");
        builder.setSingleChoiceItems(paintColor, select_paint_color_index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select_paint_color_index = which;
                tuyaView.selectPaintColor(which);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onSaveFinshed(String absolutePath) {
        Intent intent=new Intent();
        intent.putExtra("path",absolutePath);
        setResult(1002,intent);
         finish();
    }
}