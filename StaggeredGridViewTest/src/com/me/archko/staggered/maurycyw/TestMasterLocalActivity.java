package com.me.archko.staggered.maurycyw;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.andrew.apollo.utils.ApolloUtils;
import com.mani.staggeredview.demo.model.FlickrGetImagesResponse;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.mani.staggeredview.demo.model.FlickrResponsePhotos;
import com.me.archko.staggered.BaseFlickrPictureActivity;
import com.me.archko.staggered.R;
import com.me.archko.staggered.utils.Util;
import com.origamilabs.library.views2.StaggeredGridView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

/**
 * @author archko
 */
public class TestMasterLocalActivity extends BaseFlickrPictureActivity {

    StaggeredGridView mStaggeredGridView;
    StaggeredFlickrImageAdapter mAdapter;
    ArrayList<File> mDataList=new ArrayList<File>();

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

        mAdapter=new StaggeredFlickrImageAdapter(TestMasterLocalActivity.this);

        mStaggeredGridView.setAdapter(mAdapter);
        initData();
    }

    @Override
    public void flickerGetImagesRequest() {
        ApolloUtils.execute(false, new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                File dir=new File(Environment.getExternalStorageDirectory().getPath()+"/.microblog/picture");
                if (dir.exists()) {
                    final int length=80;
                    final int maxSize=1024000;
                    final int minSize=30000;
                    File[] files=dir.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            int i=0;
                            i++;
                            if (i>length) {
                                return false;
                            }

                            if (pathname.length()>minSize&&pathname.length()<maxSize) {
                                return true;
                            }
                            return false;
                        }
                    });
                    if (files.length>0) {
                        for (File f : files) {
                            mDataList.add(f);
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                stopProgress();
                if (mDataList.size()>0) {
                    parseFlickrImageResponse(null);
                } else {
                    showToast("init data failed.");
                }
            }
        });
    }

    @Override
    public void parseFlickrImageResponse(FlickrResponsePhotos response) {
        ArrayList<FlickrImage> list=new ArrayList<FlickrImage>();
        File tmp;
        for (int index=0; index<mDataList.size(); index++) {
            tmp=mDataList.get(index);
            FlickrImage flkrImage=new FlickrImage();
            flkrImage.setTitle(tmp.getAbsolutePath());
            flkrImage.url=tmp.getAbsolutePath();
            list.add(flkrImage);
        }
        mAdapter.setDatas(list);

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, Menu.FIRST, 0, "One column");
        menu.add(0, Menu.FIRST+1, 1, "Two column");
        menu.add(0, Menu.FIRST+2, 2, "Three column");
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
        }
        return super.onOptionsItemSelected(item);
    }
}
