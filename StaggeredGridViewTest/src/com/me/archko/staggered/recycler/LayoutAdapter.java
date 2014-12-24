/*
 * Copyright (C) 2014 Lucas Rocha
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.me.archko.staggered.recycler;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrew.apollo.utils.ApolloUtils;
import com.example.staggeredgridviewdemo.views.ScaleImageView;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.me.archko.staggered.R;

import java.util.ArrayList;
import java.util.List;

public class LayoutAdapter extends RecyclerView.Adapter<LayoutAdapter.SimpleViewHolder> {

    private static final int COUNT = 100;

    private final Context mContext;
    private List<FlickrImage> mItems;
    private final int mLayoutId;
    private int mCurrentItemId = 0;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        public final TextView title;
        public ScaleImageView imageView;

        public SimpleViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.txt);
            title.setTextColor(Color.BLACK);
            imageView = (ScaleImageView) view.findViewById(R.id.imageView1);
        }
    }

    public LayoutAdapter(Context context, int layoutId) {
        mContext = context;

        mLayoutId = layoutId;
        mItems = new ArrayList<FlickrImage>();
    }

    public void setDatas(ArrayList<FlickrImage> mDatas) {
        this.mItems = mDatas;
    }

    public List<FlickrImage> getItems() {
        return mItems;
    }
                                 /*public void addItem(int position) {
        final int id = mCurrentItemId++;
        mItems.add(position, id);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }*/

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.test_row_staggered_demo, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        FlickrImage flickrImage = (FlickrImage) mItems.get(position);
        //mLoader.DisplayImage(getItem(position), holder.imageView);
        ApolloUtils.getImageFetcher(mContext).startLoadImage(flickrImage.getImageUrl(), holder.imageView);
        String title = flickrImage.getTitle();
        if (0 != flickrImage.filesize) {
            title += " size:" + flickrImage.filesize;
        }
        holder.title.setText(title);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
