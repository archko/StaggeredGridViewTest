package com.etsy.android.sample;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.bulletnoid.android.demo.DataSet;
import com.bulletnoid.android.demo.Item;
import com.bulletnoid.android.demo.STGVImageView;
import com.mani.staggeredview.demo.app.StaggeredDemoApplication;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.me.archko.staggered.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FlickrAdapter extends BaseAdapter {

    private Context mContext;
    private Application mAppContext;
    private DataSet mData=new DataSet();
    private ArrayList<FlickrImage> mItems=new ArrayList<FlickrImage>();
    private ImageLoader mImageLoader;

    public FlickrAdapter(Context context, Application app) {
        mContext=context;
        mAppContext=app;
        mImageLoader=StaggeredDemoApplication.getImageLoader();
    }

    public void setItems(ArrayList<FlickrImage> mItems) {
        this.mItems=mItems;
    }

    @Override
    public int getCount() {
        return mItems==null ? 0 : mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=null;
        final FlickrImage item=mItems.get(position);

        String url=item.getImageUrl();

        if (convertView==null) {
            Holder holder=new Holder();
            view=View.inflate(mContext, R.layout.flickr_grid_item, null);
            holder.img_content=(ImageView) view.findViewById(R.id.image);
            //holder.tv_info=(TextView) view.findViewById(R.id.tv_info);

            view.setTag(holder);
        } else {
            view=convertView;
        }

        Holder holder=(Holder) view.getTag();

        /**
         * StaggeredGridView has bugs dealing with child TouchEvent
         * You must deal TouchEvent in the child view itself
         **/
        holder.img_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        /*holder.tv_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        holder.tv_info.setText(position+" : "+item.getTitle()+"/"+item.getId());*/

        /*holder.img_content.mHeight=item.height;
        holder.img_content.mWidth=item.width;*/

        Picasso.with(mContext.getApplicationContext()).load(url).into(holder.img_content);
        /*mImageLoader.get(url,
            ImageLoader.getImageListener(holder.img_content, R.drawable.bg_no_image, android.R.drawable.ic_dialog_alert), parent.getWidth(), 0);*/
        return view;
    }

    class Holder {

        ImageView img_content;
        TextView tv_info;
    }

}
