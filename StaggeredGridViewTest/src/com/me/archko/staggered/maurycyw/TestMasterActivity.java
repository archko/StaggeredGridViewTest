package com.me.archko.staggered.maurycyw;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.mani.staggeredview.demo.model.FlickrGetImagesResponse;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.mani.staggeredview.demo.model.FlickrResponsePhotos;
import com.me.archko.staggered.BaseFlickrPictureActivity;
import com.me.archko.staggered.PictureViewerActivity;
import com.me.archko.staggered.R;
import com.me.archko.staggered.utils.Util;
import com.origamilabs.library.views2.StaggeredGridView;

import java.util.ArrayList;

/**
 * @author archko
 */
public class TestMasterActivity extends BaseFlickrPictureActivity {

    StaggeredGridView mStaggeredGridView;
    StaggeredFlickrImageAdapter mAdapter;

    /**
     * This will not work so great since the heights of the imageViews
     * are calculated on the iamgeLoader callback ruining the offsets. To fix this try to get
     * the (intrinsic) image width and height and set the views height manually. I will
     * look into a fix once I find extra time.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_activity_main);

        mStaggeredGridView=(StaggeredGridView) this.findViewById(R.id.staggeredGridView1);
        mStaggeredGridView.setSelector(R.drawable.holo_selector);

        int margin=getResources().getDimensionPixelSize(R.dimen.margin);

        mStaggeredGridView.setItemMargin(margin); // set the GridView margin

        mStaggeredGridView.setPadding(margin, 0, margin, 0); // have the margin on the sides as well
        mStaggeredGridView.setOnItemClickListener(new StaggeredGridView.OnItemClickListener() {
            @Override
            public void onItemClick(StaggeredGridView parent, View view, int position, long id) {
                FlickrImage flickrImage=(FlickrImage) mAdapter.getItem(position);
                Log.d("", "item:"+position+" image:"+flickrImage);
                Util.startPictureViewer(flickrImage.getImageUrl(), TestMasterActivity.this);
            }
        });

        mAdapter=new StaggeredFlickrImageAdapter(TestMasterActivity.this);

        mStaggeredGridView.setAdapter(mAdapter);
        initData();
    }

    @Override
    public void parseFlickrImageResponse(FlickrResponsePhotos response) {
        super.parseFlickrImageResponse(response);
        ArrayList<FlickrImage> list=new ArrayList<FlickrImage>();
        FlickrGetImagesResponse photos=response.getPhotos();
        for (int index=0; index<photos.getPhotos().size(); index++) {

            FlickrImage flkrImage=photos.getPhotos().get(index);
            list.add(flkrImage);
        }
        mAdapter.setDatas(list);

        mAdapter.notifyDataSetChanged();
    }
}
