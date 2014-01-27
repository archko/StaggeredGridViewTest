package com.me.archko.staggered;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import com.andrew.apollo.utils.ApolloUtils;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.mani.staggeredview.demo.model.FlickrResponsePhotos;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

/**
 * @author archko
 */
public class BaseLocalActivity extends BaseFlickrPictureActivity {

    protected int COUNT=80;
    final int maxSize=3072000;
    final int minSize=30000;
    protected ArrayList<File> mDataList=new ArrayList<File>();

    /**
     * This will not work so great since the heights of the imageViews
     * are calculated on the iamgeLoader callback ruining the offsets. To fix this try to get
     * the (intrinsic) image width and height and set the views height manually. I will
     * look into a fix once I find extra time.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void flickerGetImagesRequest() {
        ApolloUtils.execute(false, new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                File dir=new File(Environment.getExternalStorageDirectory().getPath()+"/.microblog/picture");
                if (dir.exists()) {
                    File[] files=dir.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            int i=0;
                            i++;
                            if (i>COUNT) {
                                return false;
                            }

                            if (pathname.length()>minSize&&pathname.length()<maxSize) {
                                return true;
                            }
                            return false;
                        }
                    });
                    if (files.length>0) {
                        for (File f : files) {
                            mDataList.add(f);
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                stopProgress();
                if (mDataList.size()>0) {
                    parseFlickrImageResponse(null);
                } else {
                    showToast("init data failed.");
                }
            }
        });
    }

    @Override
    public ArrayList<FlickrImage> parseFlickrImageResponse(FlickrResponsePhotos response) {
        ArrayList<FlickrImage> list=new ArrayList<FlickrImage>();
        File tmp;
        for (int index=0; index<mDataList.size(); index++) {
            tmp=mDataList.get(index);
            FlickrImage flkrImage=new FlickrImage();
            flkrImage.setTitle(tmp.getAbsolutePath());
            flkrImage.url=tmp.getAbsolutePath();
            list.add(flkrImage);
        }

        return list;
    }
}
