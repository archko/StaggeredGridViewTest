package com.me.archko.staggered.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.me.archko.staggered.PictureViewerActivity;

/**
 * @author: archko 14-1-26 :下午1:14
 */
public class Util {

    public static final String FLICKR_PHOTO="flickr_photo";

    public static void startPictureViewer(String url, Activity activity) {
        Intent intent=new Intent(activity, PictureViewerActivity.class);
        intent.putExtra(PictureViewerActivity.IMAGE_URL, url);
        activity.startActivity(intent);
    }
}
