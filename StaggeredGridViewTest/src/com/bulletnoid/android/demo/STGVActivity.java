package com.bulletnoid.android.demo;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.bulletnoid.android.widget.StaggeredGridView.StaggeredGridView;
import com.me.archko.staggered.R;

public class STGVActivity extends Activity {
    StaggeredGridView stgv;
    STGVAdapter mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_stgv);

        stgv = (StaggeredGridView) findViewById(R.id.stgv);
        stgv.setSelector(R.drawable.holo_selector);

        int margin = getResources().getDimensionPixelSize(R.dimen.stgv_margin);

        stgv.setItemMargin(margin);
        stgv.setPadding(margin, 0, margin, 0);

        stgv.setHeaderView(new Button(this));
        View footerView;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerView = inflater.inflate(R.layout.layout_loading_footer, null);
        stgv.setFooterView(footerView);

        mAdapter = new STGVAdapter(this, getApplication());
        stgv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        stgv.setOnLoadmoreListener(new StaggeredGridView.OnLoadmoreListener() {
            @Override
            public void onLoadmore() {
                new LoadMoreTask().execute();
            }
        });

        stgv.setOnItemClickListener(new StaggeredGridView.OnItemClickListener() {
            @Override
            public void onItemClick(StaggeredGridView parent, View view, int position, long id) {
                Log.d("click", "item:"+position);
                Toast.makeText(STGVActivity.this, "click:"+position, Toast.LENGTH_SHORT).show();
            }
        });
        stgv.setOnItemLongClickListener(new StaggeredGridView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(StaggeredGridView parent, View view, int position, long id) {
                Log.d("long", "item:"+position);
                Toast.makeText(STGVActivity.this, "long:"+position, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
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