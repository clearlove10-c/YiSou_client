package com.smlz.yisounews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.smlz.yisounews.R;
import com.smlz.yisounews.entity.NewsCommentResponse;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder>{

    private List<NewsCommentResponse> newsComments;
    private Context context;
//    private List<String> commentList;
//    private List<String> userList;
//    private List<String> commentTimeList;
    private View inflater;

    public CommentAdapter(List<NewsCommentResponse> newsComments, Context context) {
        this.newsComments = newsComments;
        this.context = context;
    }

    //    public CommentAdapter(Context context, List<String> commentList,List<String> userList,List<String> commentTimeList){
//        this.context = context;
//        this.userList=userList;
//        this.commentTimeList=commentTimeList;
//        this.commentList=commentList;
//    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //创建ViewHolder，返回每一项的布局
        inflater = LayoutInflater.from(context).inflate(R.layout.item_comment,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(inflater);
        return myViewHolder;
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //将数据和控件绑定
        holder.userID.setText("用户名： "+newsComments.get(position).getUserName());
        holder.commentTime.setText("评论时间： "+newsComments.get(position).getCommentTime());
        holder.commentContent.setText("评论内容： "+newsComments.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return newsComments.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView userID;
        TextView commentTime;
        TextView commentContent;
        public MyViewHolder(View itemView) {
            super(itemView);
            userID = (TextView) itemView.findViewById(R.id.comment_username);
            commentTime=(TextView) itemView.findViewById(R.id.comment_time);
            commentContent=(TextView) itemView.findViewById(R.id.comment_content);
        }
    }
}
