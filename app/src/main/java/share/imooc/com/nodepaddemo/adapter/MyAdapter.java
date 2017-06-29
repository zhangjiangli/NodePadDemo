package share.imooc.com.nodepaddemo.adapter;

import android.content.Context;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import share.imooc.com.nodepaddemo.R;
import share.imooc.com.nodepaddemo.listener.ClickListener;
import share.imooc.com.nodepaddemo.listener.LongClickListener;
import share.imooc.com.nodepaddemo.model.Message;
import share.imooc.com.nodepaddemo.utils.StrParseUtil;

/**
 * Created by asus- on 2017/5/2.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private LongClickListener longClickListener;
    private List<Message> dataList;
    private Context context;
    private ClickListener clickListener;

    public MyAdapter(Context context,List<Message> dataList) {
        this.context=context;
        this.dataList = dataList;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    public void setLongClickListener(LongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, final int position) {
        final Message msg = dataList.get(position);
        holder.tvTime.setText(msg.getTime());
        List<String>contentList= StrParseUtil.parse(msg.getContent(),"content");
        List<String>picPathtList= StrParseUtil.parse(msg.getContent(),"picPath");
        List<String>voicePathtList= StrParseUtil.parse(msg.getContent(),"voicePath");

        if (contentList!=null && contentList.size()>0){
            holder.tvContent.setText(contentList.get(0));
            holder.tvContent.setVisibility(View.VISIBLE);
        }
        if (picPathtList!=null && picPathtList.size()>0){
            File file=new File(picPathtList.get(0));
            Glide.with(context).load(file).into(holder.imageView);
            holder.imageView.setVisibility(View.VISIBLE);
        }
        if (voicePathtList!=null && voicePathtList.size()>0){

        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickListener.del(msg);
                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.look(msg);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent;
        TextView tvTime;
        ImageView imageView;


        public ViewHolder(View itemView) {
            super(itemView);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            imageView= (ImageView) itemView.findViewById(R.id.img);

        }
    }



public void setClickListener(ClickListener clickListener){
    this.clickListener=clickListener;
}

}
