package com.mani.staggeredview.demo.griditems;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.andrew.apollo.utils.ApolloUtils;
import com.mani.staggeredview.demo.PictureActivity;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.mani.view.StaggeredGridViewItem;
import com.me.archko.staggered.R;

/**
 * Item with user uploaded
 *
 * @author maniselvaraj
 */
public class FlickrGridItem extends StaggeredGridViewItem {

    private Context mContext;
    private FlickrImage mImage;
    private View mView;
    private int mHeight;

    public FlickrGridItem(Context context, FlickrImage image) {
        mImage=image;
        mContext=context;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent) {
        mView=inflater.inflate(R.layout.grid_item, null);

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, PictureActivity.class);
                intent.putExtra(PictureActivity.IMAGE_URL, mImage.getImageUrl());
                mContext.startActivity(intent);
            }
        });
        return mView;
    }

    @Override
    public int getViewHeight(LayoutInflater inflater, ViewGroup parent) {
        FrameLayout item_containerFrameLayout=(FrameLayout) mView.findViewById(R.id.container);
        item_containerFrameLayout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        mHeight=item_containerFrameLayout.getMeasuredHeight();
        return mHeight;
    }

    @Override
    public void load() {
        ImageView image=(ImageView) mView.findViewById(R.id.image);

        ApolloUtils.getImageFetcher(mContext).startLoadImage(mImage.getImageUrl(), image);
    }

    @Override
    public void unload() {
        ImageView image=(ImageView) mView.findViewById(R.id.image);
        image.setImageDrawable(null);
    }
}
