package com.me.archko.staggered.etsy;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;
import com.etsy.android.grid.StaggeredGridView;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.mani.staggeredview.demo.model.FlickrResponsePhotos;
import com.me.archko.staggered.BaseLocalActivity;
import com.me.archko.staggered.R;
import com.me.archko.staggered.utils.Util;

import java.util.ArrayList;

/**
 * @author archko
 */
public class TestEtsyLocalActivity extends BaseLocalActivity implements AbsListView.OnScrollListener, AbsListView.OnItemClickListener {

    private static final String TAG="TestEtsyActivity";
    public static final String SAVED_DATA_KEY="SAVED_DATA";

    private StaggeredGridView mGridView;
    private boolean mHasRequestedMore;
    private SampleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sgv);

        setTitle("SGV");
        mGridView=(StaggeredGridView) findViewById(R.id.grid_view);

        LayoutInflater layoutInflater=getLayoutInflater();

        View header=layoutInflater.inflate(R.layout.list_item_header_footer, null);
        View footer=layoutInflater.inflate(R.layout.list_item_header_footer, null);
        TextView txtHeaderTitle=(TextView) header.findViewById(R.id.txt_title);
        TextView txtFooterTitle=(TextView) footer.findViewById(R.id.txt_title);
        txtHeaderTitle.setText("THE HEADER!");
        txtFooterTitle.setText("THE FOOTER!");

        mGridView.addHeaderView(header);
        mGridView.addFooterView(footer);
        mAdapter=new SampleAdapter(this);

        mGridView.setAdapter(mAdapter);
        mGridView.setOnScrollListener(this);
        mGridView.setOnItemClickListener(this);

        initData();
    }

    @Override
    public ArrayList<FlickrImage> parseFlickrImageResponse(FlickrResponsePhotos response) {
        ArrayList<FlickrImage> list=super.parseFlickrImageResponse(response);
        if (null!=list) {
            mAdapter.setDatas(list);

        } else {
            mAdapter.setDatas(new ArrayList<FlickrImage>());
        }
        mAdapter.notifyDataSetChanged();

        return null;
    }

    @Override
    public void onScrollStateChanged(final AbsListView view, final int scrollState) {
        Log.d(TAG, "onScrollStateChanged:"+scrollState);
    }

    @Override
    public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
        Log.d(TAG, "onScroll firstVisibleItem:"+firstVisibleItem+
            " visibleItemCount:"+visibleItemCount+
            " totalItemCount:"+totalItemCount);
        // our handling
        if (!mHasRequestedMore) {
            int lastInScreen=firstVisibleItem+visibleItemCount;
            if (lastInScreen>=totalItemCount) {
                Log.d(TAG, "onScroll lastInScreen - so load more");
                mHasRequestedMore=true;
                onLoadMoreItems();
            }
        }
    }

    private void onLoadMoreItems() {
        /*final ArrayList<String> sampleData=SampleData.generateSampleData();
        for (String data : sampleData) {
            mAdapter.add(data);
        }
        // stash all the data in our backing store
        mData.addAll(sampleData);
        // notify the adapter that we can update now
        mAdapter.notifyDataSetChanged();
        mHasRequestedMore=false;*/
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        //Toast.makeText(this, "Item Clicked: "+position, Toast.LENGTH_SHORT).show();
        FlickrImage flickrImage=(FlickrImage) mAdapter.getItem(position-mGridView.getHeaderViewsCount());
        Log.d(TAG, "item:"+position+" image:"+flickrImage);
        Util.startPictureViewer(flickrImage.getImageUrl(), TestEtsyLocalActivity.this);
    }
}
