package com.me.archko.staggered.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.me.archko.staggered.PictureViewerActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

    public static String parseInputStream(InputStream is) throws IOException {
        BufferedReader reader=new BufferedReader(new InputStreamReader(is), 1000);
        StringBuilder responseBody=new StringBuilder();
        String line=reader.readLine();
        while (line!=null) {
            responseBody.append(line);
            line=reader.readLine();
        }
        String string=responseBody.toString();
        return string;
    }
}
