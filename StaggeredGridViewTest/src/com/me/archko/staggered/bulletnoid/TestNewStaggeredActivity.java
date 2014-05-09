package com.me.archko.staggered.bulletnoid;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.bulletnoid.android.widget.StaggeredGridView.StaggeredGridView2;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshStaggeredGridView2;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.mani.staggeredview.demo.model.FlickrResponsePhotos;
import com.me.archko.staggered.BaseLocalActivity;
import com.me.archko.staggered.R;
import com.me.archko.staggered.maurycyw.StaggeredFlickrImageAdapter;

import java.util.ArrayList;

/**
 * @author archko
 */
public class TestNewStaggeredActivity extends BaseLocalActivity {

    PullToRefreshStaggeredGridView2 ptrstgv;
    StaggeredGridView2 mStaggeredGridView;
    StaggeredFlickrImageAdapter mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_stgv_with_ptr_new);

        ptrstgv=(PullToRefreshStaggeredGridView2) findViewById(R.id.ptrstgv);
        mStaggeredGridView=ptrstgv.getRefreshableView();

        ptrstgv.setMode(PullToRefreshBase.Mode.BOTH);
        Button button=new Button(this);
        button.setText("header button");
        mStaggeredGridView.setHeaderView(button);
        View footerView;
        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerView=inflater.inflate(R.layout.layout_loading_footer, null);
        //ptrstgv.getRefreshableView().setFooterView(footerView);

        mAdapter=new StaggeredFlickrImageAdapter(TestNewStaggeredActivity.this);
        ptrstgv.setAdapter(mAdapter);

        initData();

        mStaggeredGridView.setOnItemLongClickListener(new StaggeredGridView2.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(StaggeredGridView2 parent, View view, int position, long id) {
                Log.d("long", "item:"+position);
                Toast.makeText(TestNewStaggeredActivity.this, "long:"+position, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mStaggeredGridView.setOnItemClickListener(new StaggeredGridView2.OnItemClickListener() {
            @Override
            public void onItemClick(StaggeredGridView2 parent, View view, int position, long id) {
                Log.d("click", "item:"+position);
                Toast.makeText(TestNewStaggeredActivity.this, "click:"+position, Toast.LENGTH_SHORT).show();
            }
        });

        ptrstgv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<StaggeredGridView2>() {
            @Override
            public void onRefresh(PullToRefreshBase<StaggeredGridView2> refreshView) {
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

        } else {
            mAdapter.setDatas(new ArrayList<FlickrImage>());
        }
        mAdapter.notifyDataSetChanged();

        return null;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mStaggeredGridView.performAccessibilityAction(0x00001000);
        Log.d("onPrepareOptionsMenu", "performAccessibilityAction");
        return super.onPrepareOptionsMenu(menu);
    }
}