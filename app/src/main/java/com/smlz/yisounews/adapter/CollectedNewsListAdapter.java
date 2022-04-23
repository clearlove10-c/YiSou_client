package com.smlz.yisounews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smlz.yisounews.R;
import com.smlz.yisounews.entity.NewsInfo;
import com.smlz.yisounews.entity.NewsIntro;

import java.util.List;

public class CollectedNewsListAdapter extends ArrayAdapter<NewsInfo> {

    private int resourceId;

    public CollectedNewsListAdapter(Context context, int textViewResourceId, List<NewsInfo> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewsInfo data = getItem(position);
        View view =null;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
        } else {
            view = convertView;
        }
        TextView newsName = (TextView) view.findViewById(R.id.title_news);
        newsName.setText(data.getNewsTitle());
        return view;
    }
    public class ViewHoder{
        private TextView newsName;
    }
}