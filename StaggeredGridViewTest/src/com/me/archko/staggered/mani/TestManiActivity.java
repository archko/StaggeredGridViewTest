package com.me.archko.staggered.mani;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import com.mani.staggeredview.demo.app.StaggeredDemoApplication;
import com.mani.staggeredview.demo.griditems.FlickrGridItem;
import com.mani.staggeredview.demo.model.FlickrGetImagesResponse;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.mani.staggeredview.demo.model.FlickrResponsePhotos;
import com.mani.view.StaggeredGridView;
import com.mani.view.StaggeredGridView.OnScrollListener;
import com.mani.view.StaggeredGridViewItem;
import com.me.archko.staggered.BaseFlickrPictureActivity;
import com.me.archko.staggered.R;

/**
 * @author archko
 */
public class TestManiActivity extends BaseFlickrPictureActivity {

    private final String TAG="TestMani";

    private StaggeredGridView mStaggeredView;

    private RelativeLayout mListFooter;

    private OnScrollListener scrollListener=new OnScrollListener() {
        public void onTop() {
        }

        public void onScroll() {

        }

        public void onBottom() {
            loadMoreData();
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);

        actionBarSetup();

        // Initialise Volley Request Queue.
        mVolleyQueue=StaggeredDemoApplication.getRequestQueue();
        mListFooter=(RelativeLayout) findViewById(R.id.footer);

        mStaggeredView=(StaggeredGridView) findViewById(R.id.staggeredview);
        // Be sure before calling initialize that you haven't initialised from XML
        //mStaggeredView.initialize(2, StaggeredGridView.Mode.FIXED);
        mStaggeredView.setOnScrollListener(scrollListener);

        initData();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void actionBarSetup() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            ActionBar ab=getActionBar();
            ab.setTitle("StaggeredGridView Demo");
        }
    }

    private void loadMoreData() {
        if (isLoading) {
            return;
        }

        mListFooter.setVisibility(View.VISIBLE);
        isLoading=true;
        //flickerGetImagesRequest();
    }

    @Override
    public void parseFlickrImageResponse(FlickrResponsePhotos response) {
        FlickrGetImagesResponse photos=response.getPhotos();
        for (int index=0; index<photos.getPhotos().size(); index++) {

            FlickrImage flkrImage=photos.getPhotos().get(index);
            StaggeredGridViewItem item=null;

            item=new FlickrGridItem(this, flkrImage);
            mStaggeredView.addItem(item);
        }
    }
}
