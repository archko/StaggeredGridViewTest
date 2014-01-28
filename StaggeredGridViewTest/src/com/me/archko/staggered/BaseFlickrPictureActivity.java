package com.me.archko.staggered;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.andrew.apollo.utils.ApolloUtils;
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
import com.google.gson.Gson;
import com.mani.staggeredview.demo.app.StaggeredDemoApplication;
import com.mani.staggeredview.demo.model.FlickrGetImagesResponse;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.mani.staggeredview.demo.model.FlickrResponsePhotos;
import com.mani.staggeredview.demo.volley.GsonRequest;
import com.me.archko.staggered.utils.Util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * This base activity ,prepare list dataset for staggeredgridview,using flickr api
 *
 * @author archko
 */
public class BaseFlickrPictureActivity extends Activity {

    protected ProgressDialog mProgress;
    private final String TAG_REQUEST="BaseFlickrPictureActivity";
    public boolean isLoading=false;
    protected GsonRequest<FlickrResponsePhotos> gsonObjRequest;
    protected int currPage=1;
    protected RequestQueue mVolleyQueue;

    /**
     * This will not work so great since the heights of the imageViews
     * are calculated on the iamgeLoader callback ruining the offsets. To fix this try to get
     * the (intrinsic) image width and height and set the views height manually. I will
     * look into a fix once I find extra time.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVolleyQueue=StaggeredDemoApplication.getRequestQueue();
        ApolloUtils.getImageFetcher(this);
        //initData();
    }

    public void initData() {
        showProgress();
        flickerGetImagesRequest();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mProgress!=null) {
            mProgress.dismiss();
        }
    }

    public void flickerGetImagesRequest() {
        final File f=new File(this.getFilesDir().getPath()+File.separator+Util.FLICKR_PHOTO);
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
        builder.appendQueryParameter("per_page", "60");
        builder.appendQueryParameter("page", Integer.toString(currPage));

        gsonObjRequest=new GsonRequest<FlickrResponsePhotos>(Request.Method.GET, builder.toString(),
            FlickrResponsePhotos.class, null, new Response.Listener<FlickrResponsePhotos>() {
            @Override
            public void onResponse(FlickrResponsePhotos response) {
                try {
                    if (response!=null&&response.getPhotos()!=null&&response.getPhotos().getPhotos()!=null
                        &&response.getPhotos().getPhotos().size()>0) {
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
                loadLocalJson();
            }
        }
        );
        gsonObjRequest.setTag(TAG_REQUEST);
        mVolleyQueue.add(gsonObjRequest);
    }

    private void loadLocalJson() {
        ApolloUtils.execute(false, new AsyncTask<Object, Object, FlickrResponsePhotos>() {
            @Override
            protected FlickrResponsePhotos doInBackground(Object... params) {
                AssetManager am=BaseFlickrPictureActivity.this.getAssets();
                try {
                    InputStream is=am.open("flickr.json");
                    String json=Util.parseInputStream(is);
                    return new Gson().fromJson(json, FlickrResponsePhotos.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(FlickrResponsePhotos o) {
                Log.d(TAG_REQUEST, "onPostExecute"+(null==o));
                if (null!=o) {
                    parseFlickrImageResponse(o);
                }
            }
        });
    }

    public void showProgress() {
        mProgress=ProgressDialog.show(this, "", "Loading...");
    }

    public void stopProgress() {
        isLoading=false;
        if (null!=mProgress) {
            mProgress.cancel();
        }
    }

    public void showToast(String msg) {
        Toast.makeText(BaseFlickrPictureActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    public ArrayList<FlickrImage> parseFlickrImageResponse(FlickrResponsePhotos response) {
        Log.d(TAG_REQUEST, "parseFlickrImageResponse:");
        ArrayList<FlickrImage> list=new ArrayList<FlickrImage>();
        FlickrGetImagesResponse photos=response.getPhotos();
        for (int index=0; index<photos.getPhotos().size(); index++) {

            FlickrImage flkrImage=photos.getPhotos().get(index);
            list.add(flkrImage);
        }

        return null;
    }
}
