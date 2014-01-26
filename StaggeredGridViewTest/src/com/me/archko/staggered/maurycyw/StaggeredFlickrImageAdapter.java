package com.me.archko.staggered.maurycyw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.andrew.apollo.utils.ApolloUtils;
import com.example.staggeredgridviewdemo.views.ScaleImageView;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.me.archko.staggered.R;

import java.util.ArrayList;

/**
 * @author archko
 */
public class StaggeredFlickrImageAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<FlickrImage> mDatas;

    public StaggeredFlickrImageAdapter(Context context) {
        mContext=context;
        mDatas=new ArrayList<FlickrImage>();
    }

    public void setDatas(ArrayList<FlickrImage> mDatas) {
        this.mDatas=mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView==null) {
            LayoutInflater layoutInflator=LayoutInflater.from(mContext);
            convertView=layoutInflator.inflate(R.layout.test_row_staggered_demo, null);
            holder=new ViewHolder();
            holder.imageView=(ScaleImageView) convertView.findViewById(R.id.imageView1);
            if (ApolloUtils.hasHoneycomb()) {
                holder.imageView.setLayerType(View.LAYER_TYPE_NONE, null);
            }
            holder.txt=(TextView) convertView.findViewById(R.id.txt);
            convertView.setTag(holder);
        }

        holder=(ViewHolder) convertView.getTag();
        FlickrImage flickrImage=(FlickrImage) getItem(position);
        //mLoader.DisplayImage(getItem(position), holder.imageView);
        ApolloUtils.getImageFetcher(mContext).startLoadImage(flickrImage.getImageUrl(), holder.imageView);
        holder.txt.setText(flickrImage.getTitle());

        return convertView;
    }

    static class ViewHolder {

        ScaleImageView imageView;
        TextView txt;
    }
}
