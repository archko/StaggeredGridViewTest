package com.me.archko.staggered.huewupla;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.huewu.pla.lib.MultiColumnListView2;
import com.huewu.pla.lib.internal.PLA_AbsListView2.LayoutParams;
import com.huewu.pla.lib.internal.PLA_AdapterView2;
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
public class TestPla2LocalActivity extends BaseLocalActivity {

    private MultiColumnListView2 mAdapterView=null;
    private StaggeredFlickrImageAdapter mAdapter=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_act_local2);

        mAdapterView=(MultiColumnListView2) findViewById(R.id.list);
        mAdapterView.setSelector(R.drawable.holo_selector);
        /*mAdapterView.setRecyclerListener(new RecycleHolder());
        mAdapterView.setOnScrollListener(new PLA_AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {
                if (scrollState==SCROLL_STATE_IDLE) {
                    ImageCache.getInstance(TestPlaLocalActivity.this).setPauseDiskCache(false);
                    mAdapter.notifyDataSetChanged();
                } else {
                    ImageCache.getInstance(TestPlaLocalActivity.this).setPauseDiskCache(true);
                }
            }

            @Override
            public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });*/

        final int headerCount=2;
        {
            for (int i=0; i<headerCount; ++i) {
                //add header view.
                TextView tv=new TextView(this);
                tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                tv.setText("Hello Header!! ........................................................................");
                mAdapterView.addHeaderView(tv);
            }
        }
        {
            for (int i=0; i<2; ++i) {
                //add footer view.
                TextView tv=new TextView(this);
                tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                tv.setText("Hello Footer!! ........................................................................");
                mAdapterView.addFooterView(tv);
            }
        }

        mAdapter=new StaggeredFlickrImageAdapter(this);
        mAdapterView.setAdapter(mAdapter);
        mAdapterView.setOnItemClickListener(new PLA_AdapterView2.OnItemClickListener() {
            @Override
            public void onItemClick(PLA_AdapterView2<?> parent, View view, int position, long id) {
                Log.d("", "item:"+position+" image:");
                FlickrImage flickrImage=(FlickrImage) mAdapter.getItem(position-headerCount);
                Util.startPictureViewer(flickrImage.getImageUrl(), TestPla2LocalActivity.this);
            }
        });
        mAdapterView.setOnItemLongClickListener(new PLA_AdapterView2.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(PLA_AdapterView2<?> parent, View view, int position, long id) {
                FlickrImage flickrImage=(FlickrImage) mAdapter.getItem(position-headerCount);
                deleteDialog(getString(R.string.dialog_title)+" size:"+(flickrImage.filesize/1000)+"k", R.string.dialog_msg, position);
                return true;
            }
        });
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*menu.add(Menu.NONE, 1001, 0, "Load More Contents");
        menu.add(Menu.NONE, 1002, 0, "Launch Pull-To-Refresh Activity");*/
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
        return super.onOptionsItemSelected(item);
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

        } else {
            mAdapter.setDatas(new ArrayList<FlickrImage>());
        }
        mAdapter.notifyDataSetChanged();

        return null;
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
}//end of class
