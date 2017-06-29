package share.imooc.com.nodepaddemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import share.imooc.com.nodepaddemo.adapter.MyAdapter;
import share.imooc.com.nodepaddemo.R;
import share.imooc.com.nodepaddemo.listener.ClickListener;
import share.imooc.com.nodepaddemo.model.Message;

public class SearchActivity extends AppCompatActivity implements TextWatcher,ClickListener{
    private ImageView ivBack;
    private EditText edtSearch;

    @Override
    public void look(Message message) {
        String time=message.getTime();
        String content=message.getContent();
        Intent intent=new Intent(SearchActivity.this,UpdateActivity.class);
        intent.putExtra("time",time);
        if (content!=null){
            intent.putExtra("content",content);
        }

        startActivity(intent);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


    }

    private void update(CharSequence str) {
        
        dataList.clear();
       /* String str=edtSearch.getText().toString()*/;
        str="%"+str+"%";
        List<Message> messages = DataSupport.where("content like ? or time like ?", str.toString(),str.toString()).find(Message.class);
        for (int i = 0; i <messages.size(); i++) {
            Message msg = new Message();
            msg.setContent(messages.get(i).getContent());
            msg.setTime(messages.get(i).getTime());
            dataList.add(msg);
        }
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s.length()>0){
            update(s);
        }else {
            clear();
        }

    }

    private void clear() {
        dataList.clear();
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<Message>dataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search);
        initView();
        initData();

    }

    private void initData() {
        dataList=new ArrayList<Message>();
        myAdapter=new MyAdapter(SearchActivity.this,dataList);
        myAdapter.setClickListener(this);
        recyclerView.setAdapter(myAdapter);
    }

    private void initView() {
        recyclerView= (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager lm=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lm);
        ivBack= (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edtSearch= (EditText) findViewById(R.id.edt_search);
        edtSearch.addTextChangedListener(this);
    }
}
