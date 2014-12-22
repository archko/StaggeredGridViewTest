package com.me.archko.staggered.recycler;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.mani.staggeredview.demo.model.FlickrResponsePhotos;
import com.me.archko.staggered.BaseLocalActivity;
import com.me.archko.staggered.R;
import com.me.archko.staggered.utils.Util;
import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.widget.DividerItemDecoration;
import org.lucasr.twowayview.widget.StaggeredGridLayoutManager;
import org.lucasr.twowayview.widget.TwoWayView;

import java.io.File;
import java.util.ArrayList;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * @author archko
 */
public class TestRecyclerLocalActivity extends BaseLocalActivity {

    private TwoWayView mRecyclerView;
    private Toast mToast;
    LayoutAdapter mAdapter;

    /**
     * This will not work so great since the heights of the imageViews
     * are calculated on the iamgeLoader callback ruining the offsets. To fix this try to get
     * the (intrinsic) image width and height and set the views height manually. I will
     * look into a fix once I find extra time.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_staggered_grid);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);

        mRecyclerView = (TwoWayView) findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLongClickable(true);

        /*mPositionText = (TextView) view.getRootView().findViewById(R.id.position);
        mCountText = (TextView) view.getRootView().findViewById(R.id.count);

        mStateText = (TextView) view.getRootView().findViewById(R.id.state);*/
        //updateState(SCROLL_STATE_IDLE);

        final ItemClickSupport itemClick = ItemClickSupport.addTo(mRecyclerView);

        itemClick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View child, int position, long id) {
                /*mToast.setText("Item clicked: " + position);
                mToast.show();*/
                FlickrImage flickrImage = (FlickrImage) mAdapter.getItems().get(position);
                Log.d("", "item:" + position + " image:" + flickrImage);
                Util.startPictureViewer(flickrImage.getImageUrl(), TestRecyclerLocalActivity.this);
            }
        });

        itemClick.setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView parent, View child, int position, long id) {
                mToast.setText("Item long pressed: " + position);
                mToast.show();
                return true;
            }
        });

        /*mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                updateState(scrollState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                mPositionText.setText("First: " + mRecyclerView.getFirstVisiblePosition());
                mCountText.setText("Count: " + mRecyclerView.getChildCount());
            }
        });*/

        final Drawable divider = getResources().getDrawable(R.drawable.divider);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(divider));

        mAdapter = new LayoutAdapter(this, mRecyclerView, 0);
        mRecyclerView.setAdapter(mAdapter);
        initData();
    }

    @Override
    public ArrayList<FlickrImage> parseFlickrImageResponse(FlickrResponsePhotos response) {
        ArrayList<FlickrImage> list = super.parseFlickrImageResponse(response);
        if (null != list) {
            mAdapter.setDatas(list);

        } else {
            mAdapter.setDatas(new ArrayList<FlickrImage>());
        }
        mAdapter.notifyDataSetChanged();

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(0, Menu.FIRST, 0, "One column");
        menu.add(0, Menu.FIRST + 1, 1, "Two column");
        menu.add(0, Menu.FIRST + 3, 3, "gif");
        menu.add(0, Menu.FIRST + 4, 4, "picture");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == Menu.FIRST) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
            staggeredGridLayoutManager.setNumColumns(1);
        } else if (id == Menu.FIRST + 1) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
            staggeredGridLayoutManager.setNumColumns(2);
        } else if (id == Menu.FIRST + 3) {
            dir = new File(Environment.getExternalStorageDirectory().getPath() + "/.microblog/gif");
            mDataList.clear();
            mAdapter.setDatas(new ArrayList<FlickrImage>());
            mAdapter.notifyDataSetChanged();
            initData();
        } else if (id == Menu.FIRST + 4) {
            dir = new File(Environment.getExternalStorageDirectory().getPath() + "/.microblog/picture");
            mDataList.clear();
            mAdapter.setDatas(new ArrayList<FlickrImage>());
            mAdapter.notifyDataSetChanged();
            initData();
        }
        return super.onOptionsItemSelected(item);
    }

    public File doDelete(int pos) {
        /*if (mAdapter != null || mAdapter.getCount() > 0) {
            FlickrImage flickrImage = (FlickrImage) mAdapter.getItem(pos);
            File file = new File(flickrImage.getTitle());
            boolean flag = file.delete();
            Log.d("doDelete", "pos:" + pos + " flag:" + flag + " delete file:" + file);
            return file;
        }*/
        return null;
    }

    public void afterDelete(File o) {
        if (null != o) {
            /*ArrayList<FlickrImage> list = mAdapter.getDatas();
            int index = 0;
            for (FlickrImage flickrImage : list) {
                if (o.getAbsolutePath().equals(flickrImage.getTitle())) {
                    break;
                }
                index++;
            }

            try {
                mDataList.remove(o);
                if (index < list.size()) {
                    list.remove(index);
                    mAdapter.setDatas(list);
                    mAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
    }
}
