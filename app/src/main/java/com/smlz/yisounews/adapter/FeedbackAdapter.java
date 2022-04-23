package com.smlz.yisounews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.smlz.yisounews.R;
import com.smlz.yisounews.entity.FeedbackInfo;

import java.util.List;

public class FeedbackAdapter extends ArrayAdapter<FeedbackInfo> {

    private int resourceId;

    public FeedbackAdapter(@NonNull Context context, int resource, @NonNull List<FeedbackInfo> objects) {
        super(context, resource, objects);
        resourceId=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FeedbackInfo data = getItem(position);
        View view =null;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
        } else {
            view = convertView;
        }
        TextView userId = (TextView) view.findViewById(R.id.admin_item_userId);
        userId.setText("用户ID  "+data.getUserId().toString());
        TextView repoContent = (TextView) view.findViewById(R.id.admin_item_repo_content);
        repoContent.setText("反馈内容  "+data.getFeedbackContent());
        TextView repoFeedback = (TextView) view.findViewById(R.id.admin_item_repo_feedback);
        repoFeedback.setText("反馈回复  "+data.getFeedbackRepo());
        TextView repoTime = (TextView) view.findViewById(R.id.admin_item_repo_time);
        repoTime.setText("反馈时间  "+data.getFeedbackTime());
        TextView repoStatus = (TextView) view.findViewById(R.id.admin_item_repo_status);
        repoStatus.setText("反馈状态  "+data.getFeedbackStatus());

        return view;
    }
}
