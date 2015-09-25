package com.mobileia.audionews.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.mobileia.audionews.Keys;
import com.mobileia.audionews.R;
import com.mobileia.audionews.model.LNNews;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matiascamiletti on 25/9/15.
 */
public class NewsAdapter extends BaseAdapter {

    private final Context mContext;

    private int mLastPosition = -1;

    private boolean mIsAnimate = true;

    private List<LNNews> mItems = new ArrayList<LNNews>();

    public NewsAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return this.mItems.size();
    }

    @Override
    public LNNews getItem(int i) {
        return this.mItems.get(i);
    }

    public LNNews getLastItem() {
        return this.mItems.get(getCount() - 1);
    }

    @Override
    public long getItemId(int i) {
        return this.mItems.get(i).identifier;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(this.mContext).inflate(R.layout.item_news, parent, false);
        }

        RelativeLayout container = ViewHolder.get(view, R.id.container);
        ImageView imageView = ViewHolder.get(view, R.id.info_image);
        TextView titleView = ViewHolder.get(view, R.id.info_title);
        TextView categoryView = ViewHolder.get(view, R.id.info_category);

        final LNNews post = getItem(position);

        if(post.isSpeeching){
            container.setBackgroundResource(R.color.colorAccent);
            categoryView.setTextColor(Color.WHITE);
        }else{
            container.setBackgroundResource(android.R.color.white);
            categoryView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        }

        titleView.setText(post.title);
        categoryView.setText(post.category);

        if(post.image != null && post.image.length() > 0){
            Ion.with(imageView)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .load(Keys.BASE_URL_IMAGES + post.image);
        }else{
            imageView.setImageResource(R.drawable.placeholder);
        }

        this.animation(view, position);

        return view;
    }

    private void animation(View view, int position) {

        if (!mIsAnimate) {
            return;
        }

        TranslateAnimation animation = null;
        if (position > mLastPosition) {
            animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f);

            animation.setDuration(600);
            view.startAnimation(animation);
            mLastPosition = position;
        }
    }

    public List<LNNews> getList() {
        return this.mItems;
    }

    public void setList(List<LNNews> list) {
        this.mItems = list;
        notifyDataSetChanged();
    }
}