package com.bulletnoid.android.demo;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.bulletnoid.android.widget.StaggeredGridView.StaggeredGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshStaggeredGridView;
import com.me.archko.staggered.R;

public class STGVWithPTRActivity extends Activity {
    PullToRefreshStaggeredGridView ptrstgv;
    StaggeredGridView mStaggeredGridView;
    STGVAdapter mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_stgv_with_ptr);

        ptrstgv = (PullToRefreshStaggeredGridView) findViewById(R.id.ptrstgv);
        mStaggeredGridView=ptrstgv.getRefreshableView();

        mAdapter = new STGVAdapter(this, getApplication());

        ptrstgv.setMode(PullToRefreshBase.Mode.BOTH);
        //mStaggeredGridView.setHeaderView(new TextView(this));
        View footerView;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerView = inflater.inflate(R.layout.layout_loading_footer, null);
        //ptrstgv.getRefreshableView().setFooterView(footerView);
        ptrstgv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mStaggeredGridView.setOnItemLongClickListener(new StaggeredGridView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(StaggeredGridView parent, View view, int position, long id) {
                Log.d("long", "item:"+position);
                Toast.makeText(STGVWithPTRActivity.this, "long:"+position, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        mStaggeredGridView.setOnItemClickListener(new StaggeredGridView.OnItemClickListener() {
            @Override
            public void onItemClick(StaggeredGridView parent, View view, int position, long id) {
                Log.d("click", "item:"+position);
                Toast.makeText(STGVWithPTRActivity.this, "click:"+position, Toast.LENGTH_SHORT).show();
            }
        });

        ptrstgv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<StaggeredGridView>() {
            @Override
            public void onRefresh(PullToRefreshBase<StaggeredGridView> refreshView) {
                //mAdapter.getNewItem();
                mAdapter.notifyDataSetChanged();

                ptrstgv.onRefreshComplete();
            }
        });

        /*ptrstgv.setOnLoadmoreListener(new StaggeredGridView.OnLoadmoreListener() {
            @Override
            public void onLoadmore() {
                new LoadMoreTask().execute();
            }
        });*/

    }

    public class LoadMoreTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mAdapter.getMoreItem();
            mAdapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }

    }
}