package com.smlz.yisounews.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.smlz.yisounews.R;
import com.smlz.yisounews.activity.ArticleActivity;
import com.smlz.yisounews.activity.ArticleDetailActivity;
import com.smlz.yisounews.entity.ArticleInfo;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private static final String TAG = "ArticleAdapter";
    private Context context;
    private List<ArticleInfo> articleList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView articleImage;
        TextView articleTitle;
        TextView articleTime;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            articleImage = (ImageView) view.findViewById(R.id.article_image);
            articleTitle = (TextView) view.findViewById(R.id.article_name);
            articleTime = (TextView) view.findViewById(R.id.article_time);
        }
    }

    public ArticleAdapter(List<ArticleInfo> List) {
        articleList = List;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);

        final ViewHolder holder = new ViewHolder(view);
        //给cardView注册了一个监听器,当点击时,构造一个Intent并带到ArticleActivity活动中
        holder.cardView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            ArticleInfo article = articleList.get(position);
            ArticleActivity.curArticle = articleList.get(position);
            Intent intent = new Intent(context, ArticleDetailActivity.class);
            context.startActivity(intent);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ArticleInfo article = articleList.get(position);
        holder.articleTitle.setText(article.getArticleTitle());
        holder.articleTime.setText(article.getArticleTime().toString());
        //使用Glide来加载图片，with方法传入一个Context、Activity或Fragment参数，最后调用load()方法去加载图片在
        Glide.with(context)
                .load(article.getArticleCover())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.articleImage);
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }
}
