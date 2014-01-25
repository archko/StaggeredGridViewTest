package com.example.staggeredgridviewdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
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
import com.etsy.android.sample.FlickrAdapter;
import com.etsy.android.sample.StaggeredWithPictureActivity;
import com.mani.staggeredview.demo.PictureActivity;
import com.mani.staggeredview.demo.app.StaggeredDemoApplication;
import com.mani.staggeredview.demo.model.FlickrGetImagesResponse;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.mani.staggeredview.demo.model.FlickrResponsePhotos;
import com.mani.staggeredview.demo.volley.GsonRequest;
import com.me.archko.staggered.R;
import com.me.archko.staggered.Utils;
import com.origamilabs.library.views2.StaggeredGridView;

import java.io.File;
import java.util.ArrayList;

/**
 * This will not work so great since the heights of the imageViews
 * are calculated on the iamgeLoader callback ruining the offsets. To fix this try to get
 * the (intrinsic) image width and height and set the views height manually. I will
 * look into a fix once I find extra time.
 *
 * @author Maurycy Wojtowicz
 */
public class FlickrPictureActivity extends Activity {

    FlickrAdapter mAdapter;
    private ProgressDialog mProgress;
    private final String TAG_REQUEST="MY_TAG";
    private boolean isLoading=false;
    GsonRequest<FlickrResponsePhotos> gsonObjRequest;
    private int currPage=1;
    private RequestQueue mVolleyQueue;

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
        mVolleyQueue=StaggeredDemoApplication.getRequestQueue();

        StaggeredGridView gridView=(StaggeredGridView) this.findViewById(R.id.staggeredGridView1);
        gridView.setSelector(R.drawable.holo_selector);

        int margin=getResources().getDimensionPixelSize(R.dimen.margin);

        gridView.setItemMargin(margin); // set the GridView margin

        gridView.setPadding(margin, 0, margin, 0); // have the margin on the sides as well

        mAdapter=new FlickrAdapter(this, null);
        gridView.setAdapter(mAdapter);
        showProgress();
        flickerGetImagesRequest();

        gridView.setOnItemClickListener(new StaggeredGridView.OnItemClickListener() {
            @Override
            public void onItemClick(StaggeredGridView parent, View view, int position, long id) {
                Intent intent=new Intent(FlickrPictureActivity.this, PictureActivity.class);
                FlickrImage flickrImage=(FlickrImage) mAdapter.getItem(position);
                intent.putExtra(PictureActivity.IMAGE_URL, flickrImage.getImageUrl());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void onStop() {
        super.onStop();
        if (mProgress!=null)
            mProgress.dismiss();
    }

    private void flickerGetImagesRequest() {
        final File f=new File(this.getFilesDir().getPath()+File.separator+StaggeredWithPictureActivity.FLICKR_PHOTO);
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
        Toast.makeText(FlickrPictureActivity.this, msg, Toast.LENGTH_LONG).show();
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
