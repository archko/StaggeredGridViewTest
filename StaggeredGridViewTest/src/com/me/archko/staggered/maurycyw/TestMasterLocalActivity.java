package com.me.archko.staggered.maurycyw;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.mani.staggeredview.demo.model.FlickrResponsePhotos;
import com.me.archko.staggered.BaseLocalActivity;
import com.me.archko.staggered.R;
import com.me.archko.staggered.utils.Util;
import com.origamilabs.library.views2.StaggeredGridView;

import java.io.File;
import java.util.ArrayList;

/**
 * @author archko
 */
public class TestMasterLocalActivity extends BaseLocalActivity {

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
        mStaggeredGridView.setColumnCount(1);

        int margin=getResources().getDimensionPixelSize(R.dimen.margin);

        mStaggeredGridView.setItemMargin(margin); // set the GridView margin

        mStaggeredGridView.setPadding(margin, 0, margin, 0); // have the margin on the sides as well
        mStaggeredGridView.setOnItemClickListener(new StaggeredGridView.OnItemClickListener() {
            @Override
            public void onItemClick(StaggeredGridView parent, View view, int position, long id) {
                FlickrImage flickrImage=(FlickrImage) mAdapter.getItem(position);
                Log.d("", "item:"+position+" image:"+flickrImage);
                Util.startPictureViewer(flickrImage.getImageUrl(), TestMasterLocalActivity.this);
            }
        });
        mStaggeredGridView.setOnItemLongClickListener(new StaggeredGridView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(StaggeredGridView parent, View view, int position, long id) {
                FlickrImage flickrImage=(FlickrImage) mAdapter.getItem(position);
                deleteDialog(getString(R.string.dialog_title)+" size:"+(flickrImage.filesize/1000)+"k", R.string.dialog_msg, position);
                return true;
            }
        });

        mAdapter=new StaggeredFlickrImageAdapter(TestMasterLocalActivity.this);

        mStaggeredGridView.setAdapter(mAdapter);
        initData();
    }

    @Override
    public ArrayList<FlickrImage> parseFlickrImageResponse(FlickrResponsePhotos response) {
        ArrayList<FlickrImage> list=super.parseFlickrImageResponse(response);
        if (null!=list) {
            mAdapter.setDatas(list);

            mAdapter.notifyDataSetChanged();
        }

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, Menu.FIRST, 0, "One column");
        menu.add(0, Menu.FIRST+1, 1, "Two column");
        menu.add(0, Menu.FIRST+2, 2, "Three column");
        menu.add(0, Menu.FIRST+3, 3, "gif");
        menu.add(0, Menu.FIRST+4, 4, "picture");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==Menu.FIRST) {
            mStaggeredGridView.setColumnCount(1);
        } else if (id==Menu.FIRST+1) {
            mStaggeredGridView.setColumnCount(2);
        } else if (id==Menu.FIRST+2) {
            mStaggeredGridView.setColumnCount(3);
        } else if (id==Menu.FIRST+3) {
            dir=new File(Environment.getExternalStorageDirectory().getPath()+"/.microblog/gif");
            mDataList.clear();
            mAdapter.setDatas(new ArrayList<FlickrImage>());
            mAdapter.notifyDataSetChanged();
            initData();
        } else if (id==Menu.FIRST+4) {
            dir=new File(Environment.getExternalStorageDirectory().getPath()+"/.microblog/picture");
            mDataList.clear();
            mAdapter.setDatas(new ArrayList<FlickrImage>());
            mAdapter.notifyDataSetChanged();
            initData();
        }
        return super.onOptionsItemSelected(item);
    }

    public File doDelete(int pos) {
        if (mAdapter!=null||mAdapter.getCount()>0) {
            FlickrImage flickrImage=(FlickrImage) mAdapter.getItem(pos);
            File file=new File(flickrImage.getTitle());
            boolean flag=file.delete();
            Log.d("doDelete", "pos:"+pos+" flag:"+flag+" delete file:"+file);
            return file;
        }
        return null;
    }

    public void afterDelete(File o) {
        if (null!=o) {
            ArrayList<FlickrImage> list=mAdapter.getDatas();
            int index=0;
            for (FlickrImage flickrImage : list) {
                if (o.getAbsolutePath().equals(flickrImage.getTitle())) {
                    break;
                }
                index++;
            }

            try {
                mDataList.remove(o);
                if (index<list.size()) {
                    list.remove(index);
                    mAdapter.setDatas(list);
                    mAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
