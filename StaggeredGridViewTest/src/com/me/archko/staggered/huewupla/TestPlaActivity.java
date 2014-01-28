package com.me.archko.staggered.huewupla;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AbsListView.LayoutParams;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.mani.staggeredview.demo.model.FlickrGetImagesResponse;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.mani.staggeredview.demo.model.FlickrResponsePhotos;
import com.me.archko.staggered.BaseFlickrPictureActivity;
import com.me.archko.staggered.R;
import com.me.archko.staggered.etsy.SampleAdapter;
import com.me.archko.staggered.utils.Util;

import java.util.ArrayList;

/**
 * @author archko
 */
public class TestPlaActivity extends BaseFlickrPictureActivity {

    private MultiColumnListView mAdapterView=null;
    private SampleAdapter mAdapter=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_act);
        //mAdapterView = (PLA_AdapterView<Adapter>) findViewById(R.id.list);

        mAdapterView=(MultiColumnListView) findViewById(R.id.list);

        {
            for (int i=0; i<3; ++i) {
                //add header view.
                TextView tv=new TextView(this);
                tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                tv.setText("Hello Header!! ........................................................................");
                mAdapterView.addHeaderView(tv);
            }
        }
        {
            for (int i=0; i<3; ++i) {
                //add footer view.
                TextView tv=new TextView(this);
                tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                tv.setText("Hello Footer!! ........................................................................");
                mAdapterView.addFooterView(tv);
            }
        }

        mAdapter=new SampleAdapter(this);
        mAdapterView.setAdapter(mAdapter);
        mAdapterView.setOnItemClickListener(new PLA_AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
                FlickrImage flickrImage=(FlickrImage) mAdapter.getItem(position);
                Log.d("", "item:"+position+" image:"+flickrImage);
                Util.startPictureViewer(flickrImage.getImageUrl(), TestPlaActivity.this);
            }
        });
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1001, 0, "Load More Contents");
        menu.add(Menu.NONE, 1002, 0, "Launch Pull-To-Refresh Activity");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch (item.getItemId()) {
            case 1001: {
                int startCount=mAdapter.getCount();
                for (int i=0; i<100; ++i) {
                    //generate 100 random items.

                    StringBuilder builder=new StringBuilder();
                    builder.append("Hello!![");
                    builder.append(startCount+i);
                    builder.append("] ");

                    char[] chars=new char[mRand.nextInt(100)];
                    Arrays.fill(chars, '1');
                    builder.append(chars);
                    mAdapter.add(builder.toString());
                }
            }
            break;
            case 1002: {
                Intent intent=new Intent(this, PullToRefreshSampleActivity.class);
                startActivity(intent);
            }
            break;
        }*/
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
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
}//end of class
