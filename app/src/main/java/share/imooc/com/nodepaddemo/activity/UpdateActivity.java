package share.imooc.com.nodepaddemo.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import cn.zeffect.view.recordbutton.RecordButton;
import me.iwf.photopicker.PhotoPicker;
import share.imooc.com.nodepaddemo.R;
import share.imooc.com.nodepaddemo.listener.SaveImageLister;
import share.imooc.com.nodepaddemo.model.Message;
import share.imooc.com.nodepaddemo.widget.PictureAndTextEditorView;

public class UpdateActivity extends AppCompatActivity{

    private TextView tvCommit;
    private ImageView ivBack;
    private String time;
    private String content;
    private ImageView ivPhoto;
    private PictureAndTextEditorView richEditText;
    private ImageView ivTuya;
    private RecordButton recordButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_update);
        initView();
        initData();
    }

    private void initData() {
        richEditText.setMovementMethod(LinkMovementMethod.getInstance());
        time=getIntent().getStringExtra("time");
        if (getIntent().getStringExtra("content")!=null){
            content=getIntent().getStringExtra("content");
            richEditText.setmContentList(Arrays.asList(content.split("\n")));
        }
       /* if (getIntent().getStringExtra("picPath")!=null){
            picPath=getIntent().getStringExtra("picPath");
            File file=new File(picPath);
            Glide.with(UpdateActivity.this).load(file).into(imageView);
        }*/
        recordButton.setSavePath(Environment.getExternalStorageDirectory().getAbsolutePath());//设置存储路径,6.0注意申请权限
        /*recordButton.setSaveName("mysound");//设置文件名字，如果设置了名字，将会一直使用，后面的录音文件会覆盖前面的文件
        recordButton.setPrefix("my");//设置文件名前缀，不设置名字，只设置前缀，保证文件不重复，存带前缀标志*/
        recordButton.setPrefix("myvoice");
        recordButton.setMaxIntervalTime(60000);//毫秒，设置最长时间
        recordButton.setMinIntervalTime(2000);//毫秒，设置最短录音时间
        recordButton.setTooShortToastMessage("录音时间太短"); //设置时间太短的提示语
        recordButton.setOnFinishedRecordListener(new RecordButton.OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(String s) {
                //Toast.makeText(AddActivity.this, "储存路径为："+s, Toast.LENGTH_SHORT).show();
                //String img=Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"speak.png";
                richEditText.insertVoiceBitmap(s);
            }

            @Override
            public void readCancel() {

            }

            @Override
            public void noCancel() {

            }

            @Override
            public void onActionDown() {

            }

            @Override
            public void onActionUp() {

            }

            @Override
            public void onActionMove() {

            }
        });

    }

    private void initView() {
        tvCommit= (TextView) findViewById(R.id.tv_commit);
        richEditText= (PictureAndTextEditorView) findViewById(R.id.richEditText);
        ivBack= (ImageView) findViewById(R.id.iv_back);
        tvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String strContent=richEditText.getText().toString();
                    SimpleDateFormat formatter=new  SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    Date curDate =  new Date(System.currentTimeMillis());
                    String   curtime   =   formatter.format(curDate);
                    ContentValues values = new ContentValues();
                    values.put("content",strContent);
                    values.put("time",curtime);
                    DataSupport.updateAll(Message.class, values, "time = ?", time);
                    finish();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivPhoto= (ImageView) findViewById(R.id.iv_photo);
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setShowCamera(true)
                        .setShowGif(true)
                        .setPreviewEnabled(true)
                        .start(UpdateActivity.this, PhotoPicker.REQUEST_CODE);
            }
        });

        ivTuya= (ImageView) findViewById(R.id.iv_tuya);
        ivTuya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent=new Intent(UpdateActivity.this,TuyaActivity.class);
                startActivityForResult(intent,1001);
            }
        });
        recordButton= (RecordButton) findViewById(R.id.recordButton);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                for (int i = 0; i <photos.size() ; i++) {
                    richEditText.insertBitmap(photos.get(i));
                }
            }
        }else if(requestCode==1001&&resultCode==1002){
            String path=data.getStringExtra("path");
            richEditText.insertBitmap(path);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
