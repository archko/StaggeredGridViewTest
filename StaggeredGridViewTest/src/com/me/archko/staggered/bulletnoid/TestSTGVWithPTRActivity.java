package com.me.archko.staggered.bulletnoid;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bulletnoid.android.widget.StaggeredGridView.StaggeredGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshStaggeredGridView;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.mani.staggeredview.demo.model.FlickrResponsePhotos;
import com.me.archko.staggered.BaseLocalActivity;
import com.me.archko.staggered.R;
import com.me.archko.staggered.maurycyw.StaggeredFlickrImageAdapter;
import com.me.archko.staggered.utils.Util;

import java.io.File;
import java.util.ArrayList;

/**
 * @author archko
 */
public class TestSTGVWithPTRActivity extends BaseLocalActivity {

    PullToRefreshStaggeredGridView ptrstgv;
    StaggeredGridView mStaggeredGridView;
    StaggeredFlickrImageAdapter mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_stgv_with_ptr);

        ptrstgv=(PullToRefreshStaggeredGridView) findViewById(R.id.ptrstgv);
        mStaggeredGridView=ptrstgv.getRefreshableView();

        ptrstgv.setMode(PullToRefreshBase.Mode.BOTH);
        Button button=new Button(this);
        button.setText("header button");
        //mStaggeredGridView.setHeaderView(button);
        View footerView;
        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerView=inflater.inflate(R.layout.layout_loading_footer, null);
        //ptrstgv.getRefreshableView().setFooterView(footerView);

        mAdapter=new StaggeredFlickrImageAdapter(TestSTGVWithPTRActivity.this);
        ptrstgv.setAdapter(mAdapter);

        initData();

        mStaggeredGridView.setOnItemLongClickListener(new StaggeredGridView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(StaggeredGridView parent, View view, int position, long id) {
                Toast.makeText(TestSTGVWithPTRActivity.this, "long:"+position, Toast.LENGTH_SHORT).show();
                FlickrImage flickrImage=(FlickrImage) mAdapter.getItem(position);
                deleteDialog(getString(R.string.dialog_title)+" size:"+(flickrImage.filesize/1000)+"k", R.string.dialog_msg, position);
                return true;
            }
        });
        mStaggeredGridView.setOnItemClickListener(new StaggeredGridView.OnItemClickListener() {
            @Override
            public void onItemClick(StaggeredGridView parent, View view, int position, long id) {
                Log.d("click", "item:"+position);
                Toast.makeText(TestSTGVWithPTRActivity.this, "click:"+position, Toast.LENGTH_SHORT).show();
                FlickrImage flickrImage=(FlickrImage) mAdapter.getItem(position);
                Log.d("", "item:"+position+" image:"+flickrImage);
                Util.startPictureViewer(flickrImage.getImageUrl(), TestSTGVWithPTRActivity.this);
            }
        });

        ptrstgv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<StaggeredGridView>() {
            @Override
            public void onRefresh(PullToRefreshBase<StaggeredGridView> refreshView) {
                mAdapter.notifyDataSetChanged();
                ptrstgv.onRefreshComplete();
            }
        });
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        mStaggeredGridView.performAccessibilityAction(0x00001000);
        Log.d("onPrepareOptionsMenu", "performAccessibilityAction");
        return super.onPrepareOptionsMenu(menu);
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