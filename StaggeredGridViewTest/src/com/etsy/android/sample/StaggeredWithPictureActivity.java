package com.etsy.android.sample;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.etsy.android.grid.StaggeredGridView;
import com.mani.staggeredview.demo.PictureActivity;
import com.mani.staggeredview.demo.app.StaggeredDemoApplication;
import com.mani.staggeredview.demo.model.FlickrGetImagesResponse;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.mani.staggeredview.demo.model.FlickrResponsePhotos;
import com.mani.staggeredview.demo.volley.GsonRequest;
import com.me.archko.staggered.R;
import com.me.archko.staggered.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author archko
 */
public class StaggeredWithPictureActivity extends Activity {

    private StaggeredGridView mStaggeredView;
    FlickrAdapter mAdapter;
    private RequestQueue mVolleyQueue;
    private ProgressDialog mProgress;
    private int currPage=1;
    GsonRequest<FlickrResponsePhotos> gsonObjRequest;

    public static final String FLICKR_PHOTO="flickr_photo";

    //private RelativeLayout mListFooter;
    private boolean isLoading=false;

    private final String TAG_REQUEST="MY_TAG";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sgv_empy_view);

        actionBarSetup();

        // Initialise Volley Request Queue.
        mVolleyQueue=StaggeredDemoApplication.getRequestQueue();
        //mListFooter=(RelativeLayout) findViewById(R.id.footer);

        mStaggeredView=(StaggeredGridView) findViewById(R.id.grid_view);
        // Be sure before calling initialize that you haven't initialised from XML
        //mStaggeredView.initialize(2, StaggeredGridView.Mode.FIXED);

        LayoutInflater layoutInflater=getLayoutInflater();

        View header=layoutInflater.inflate(R.layout.list_item_header_footer, null);
        View footer=layoutInflater.inflate(R.layout.list_item_header_footer, null);
        TextView txtHeaderTitle=(TextView) header.findViewById(R.id.txt_title);
        TextView txtFooterTitle=(TextView) footer.findViewById(R.id.txt_title);
        txtHeaderTitle.setText("THE HEADER!");
        txtFooterTitle.setText("THE FOOTER!");

        mStaggeredView.addHeaderView(header);
        mStaggeredView.addFooterView(footer);
        mStaggeredView.setEmptyView(findViewById(android.R.id.empty));
        showProgress();

        mAdapter=new FlickrAdapter(this, null);
        mStaggeredView.setAdapter(mAdapter);

        flickerGetImagesRequest();

        mStaggeredView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(StaggeredWithPictureActivity.this, PictureActivity.class);
                FlickrImage flickrImage=(FlickrImage) mAdapter.getItem(position);
                intent.putExtra(PictureActivity.IMAGE_URL, flickrImage.getImageUrl());
                startActivity(intent);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void actionBarSetup() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            ActionBar ab=getActionBar();
            ab.setTitle("StaggeredGridView Demo");
        }
    }

    public void onStop() {
        super.onStop();
        if (mProgress!=null)
            mProgress.dismiss();
    }

    private void loadMoreData() {

        if (isLoading)
            return;

        //mListFooter.setVisibility(View.VISIBLE);
        isLoading=true;
        flickerGetImagesRequest();
    }

    private void flickerGetImagesRequest() {
        final File f=new File(this.getFilesDir().getPath()+File.separator+FLICKR_PHOTO);
        if (f.exists()) {
            FlickrResponsePhotos responsePhotos=(FlickrResponsePhotos) Utils.deserializeObject(f.getAbsolutePath());
            if (responsePhotos!=null) {
                Log.d(TAG_REQUEST, "exits file:"+f);
                parseFlickrImageResponse(responsePhotos);
                stopProgress();
                return;
            }
        }

        String url="http://api.flickr.com/services/rest";
        Uri.Builder builder=Uri.parse(url).buildUpon();
        builder.appendQueryParameter("api_key", "5e045abd4baba4bbcd866e1864ca9d7b");
        builder.appendQueryParameter("method", "flickr.interestingness.getList");
        builder.appendQueryParameter("format", "json");
        builder.appendQueryParameter("nojsoncallback", "1");
        builder.appendQueryParameter("per_page", "50");
        builder.appendQueryParameter("page", Integer.toString(currPage));

        gsonObjRequest=new GsonRequest<FlickrResponsePhotos>(Request.Method.GET, builder.toString(),
            FlickrResponsePhotos.class, null, new Response.Listener<FlickrResponsePhotos>() {
            @Override
            public void onResponse(FlickrResponsePhotos response) {
                try {
                    if (response!=null) {
                        //mStaggeredView.onRefreshComplete();
                        parseFlickrImageResponse(response);
                        Utils.serializeObject(response, f.getAbsolutePath());
                        currPage++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast("JSON parse error");
                }
                stopProgress();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle your error types accordingly.For Timeout & No connection error, you can show 'retry' button.
                // For AuthFailure, you can re login with user credentials.
                // For ClientError, 400 & 401, Errors happening on client side when sending api request.
                // In this case you can check how client is forming the api and debug accordingly.
                // For ServerError 5xx, you can do retry or handle accordingly.
                if (error instanceof NetworkError) {
                } else if (error instanceof ClientError) {
                } else if (error instanceof ServerError) {
                } else if (error instanceof AuthFailureError) {
                } else if (error instanceof ParseError) {
                } else if (error instanceof NoConnectionError) {
                } else if (error instanceof TimeoutError) {
                }
                //mStaggeredView.onRefreshComplete();
                stopProgress();
                showToast(error.getMessage());
            }
        }
        );
        gsonObjRequest.setTag(TAG_REQUEST);
        mVolleyQueue.add(gsonObjRequest);
    }

    private void showProgress() {
        mProgress=ProgressDialog.show(this, "", "Loading...");
    }

    private void stopProgress() {
        isLoading=false;
        //mListFooter.setVisibility(View.GONE);
        mProgress.cancel();
    }

    private void showToast(String msg) {
        Toast.makeText(StaggeredWithPictureActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    private void parseFlickrImageResponse(FlickrResponsePhotos response) {
        Log.d(TAG_REQUEST, "parseFlickrImageResponse:");
        ArrayList<FlickrImage> list=new ArrayList<FlickrImage>();
        FlickrGetImagesResponse photos=response.getPhotos();
        for (int index=0; index<photos.getPhotos().size(); index++) {

            FlickrImage flkrImage=photos.getPhotos().get(index);
            list.add(flkrImage);
        }
        mAdapter.setItems(list);
        mAdapter.notifyDataSetChanged();
    }

}
