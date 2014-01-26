package com.me.archko.staggered.etsy;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;
import com.andrew.apollo.utils.ApolloUtils;
import com.etsy.android.grid.util.DynamicHeightImageView;
import com.etsy.android.grid.util.DynamicHeightTextView;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.me.archko.staggered.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * ADAPTER
 */
public class SampleAdapter extends BaseAdapter {

    private static final String TAG="SampleAdapter";

    static class ViewHolder {

        DynamicHeightTextView txtLineOne;
        Button btnGo;
        DynamicHeightImageView imageView;
    }

    Context mContext;
    ArrayList<FlickrImage> mDatas;
    private final LayoutInflater mLayoutInflater;
    private final Random mRandom;
    private final ArrayList<Integer> mBackgroundColors;

    private static final SparseArray<Double> sPositionHeightRatios=new SparseArray<Double>();

    public SampleAdapter(final Context context) {
        mDatas=new ArrayList<FlickrImage>();
        mLayoutInflater=LayoutInflater.from(context);
        mRandom=new Random();
        mBackgroundColors=new ArrayList<Integer>();
        mBackgroundColors.add(R.color.orange);
        mBackgroundColors.add(R.color.green);
        mBackgroundColors.add(R.color.blue);
        mBackgroundColors.add(R.color.yellow);
        mBackgroundColors.add(R.color.grey);
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
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder holder;
        if (convertView==null) {
            convertView=mLayoutInflater.inflate(R.layout.list_item_sample_etsy, parent, false);
            holder=new ViewHolder();
            holder.txtLineOne=(DynamicHeightTextView) convertView.findViewById(R.id.txt_line1);
            //vh.btnGo=(Button) convertView.findViewById(R.id.btn_go);
            holder.imageView=(DynamicHeightImageView) convertView.findViewById(R.id.imageView1);

            convertView.setTag(holder);
        } else {
            holder=(ViewHolder) convertView.getTag();
        }

        double positionHeight=getPositionRatio(position);
        int backgroundIndex=position>=mBackgroundColors.size() ? position%mBackgroundColors.size() : position;

        convertView.setBackgroundResource(mBackgroundColors.get(backgroundIndex));

        Log.d(TAG, "getView position:"+position+" h:"+positionHeight);

        FlickrImage flickrImage=(FlickrImage) getItem(position);
        //holder.txtLineOne.setHeightRatio(positionHeight);
        holder.txtLineOne.setText(flickrImage.getTitle());
        ApolloUtils.getImageFetcher(mContext).startLoadImage(flickrImage.getImageUrl(), holder.imageView);

        /*vh.btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Toast.makeText(mContext, "Button Clicked Position "+position, Toast.LENGTH_SHORT).show();
            }
        });*/

        return convertView;
    }

    private double getPositionRatio(final int position) {
        double ratio=sPositionHeightRatios.get(position, 0.0);
        // if not yet done generate and stash the columns height
        // in our real world scenario this will be determined by
        // some match based on the known height and width of the image
        // and maybe a helpful way to get the column height!
        if (ratio==0) {
            ratio=getRandomHeightRatio();
            sPositionHeightRatios.append(position, ratio);
            Log.d(TAG, "getPositionRatio:"+position+" ratio:"+ratio);
        }
        return ratio;
    }

    private double getRandomHeightRatio() {
        return (mRandom.nextDouble()/2.0)+1.0; // height will be 1.0 - 1.5 the width
    }
}