package com.me.archko.staggered.recycler;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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

import java.io.File;
import java.util.ArrayList;

/**
 * @author archko
 */
public class TestStaggeredRecyclerLocalActivity extends BaseLocalActivity {

    private RecyclerView mRecyclerView;
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
        setContentView(R.layout.layout_staggered_recycler);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLongClickable(true);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        final ItemClickSupport itemClick = ItemClickSupport.addTo(mRecyclerView);

        itemClick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View child, int position, long id) {
                FlickrImage flickrImage = (FlickrImage) mAdapter.getItems().get(position);
                Log.d("", "item:" + position + " image:" + flickrImage);
                Util.startPictureViewer(flickrImage.getImageUrl(), TestStaggeredRecyclerLocalActivity.this);
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

        final Drawable divider = getResources().getDrawable(R.drawable.divider);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(divider));

        mAdapter = new LayoutAdapter(this, 0);
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
        menu.add(0, Menu.FIRST + 2, 2, "Three column");
        menu.add(0, Menu.FIRST + 3, 3, "gif");
        menu.add(0, Menu.FIRST + 4, 4, "picture");
        menu.add(0, Menu.FIRST + 5, 5, "vertical");
        menu.add(0, Menu.FIRST + 6, 6, "horizontal");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == Menu.FIRST) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
            staggeredGridLayoutManager.setSpanCount(1);
        } else if (id == Menu.FIRST + 1) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
            staggeredGridLayoutManager.setSpanCount(2);
        } else if (id == Menu.FIRST + 2) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
            staggeredGridLayoutManager.setSpanCount(3);
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
        } else if (id == Menu.FIRST + 5) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
            staggeredGridLayoutManager.setOrientation(StaggeredGridLayoutManager.VERTICAL);
        } else if (id == Menu.FIRST + 6) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
            staggeredGridLayoutManager.setOrientation(StaggeredGridLayoutManager.HORIZONTAL);
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
