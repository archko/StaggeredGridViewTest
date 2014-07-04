package com.me.archko.staggered;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

    protected int COUNT=120;
    final int maxSize=4096000;
    final int minSize=4000;
    protected ArrayList<File> mDataList=new ArrayList<File>();
    public static final String ak_path=Environment.getExternalStorageDirectory().getPath()+"/.microblog/picture";
    public static final String sina_path=Environment.getExternalStorageDirectory().getPath()+"/gifshow/.cache";
    protected File dir=new File(ak_path);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, Menu.FIRST+104, 0, "换目录");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==Menu.FIRST+104) {
            if (dir.getAbsolutePath().endsWith(ak_path)) {
                dir=new File(sina_path);
            } else {
                dir=new File(ak_path);
            }
            initData();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void flickerGetImagesRequest() {
        ApolloUtils.execute(false, new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                Log.d("", "file path:"+dir);
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
                    Log.d("", "file length:"+files.length);
                    if (files.length>0) {
                        mDataList.clear();
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
        Log.d("local", "parseFlickrImageResponse:"+mDataList);
        ArrayList<FlickrImage> list=new ArrayList<FlickrImage>();
        File tmp;
        for (int index=0; index<mDataList.size(); index++) {
            tmp=mDataList.get(index);
            FlickrImage flkrImage=new FlickrImage();
            flkrImage.setTitle(tmp.getAbsolutePath());
            flkrImage.url=tmp.getAbsolutePath();
            flkrImage.filesize=tmp.length();
            list.add(flkrImage);
        }

        return list;
    }

    public void deleteDialog(String title, int msg, final int pos) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(msg)
            .setNegativeButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.cancel();
                    }
                }
            ).setPositiveButton(getResources().getString(R.string.confirm),
            new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    delete(pos);
                    arg0.cancel();
                }
            }
        ).create().show();
    }

    private void delete(final int pos) {
        ApolloUtils.execute(false, new AsyncTask<Object, Object, File>() {
            @Override
            protected File doInBackground(Object... params) {
                return doDelete(pos);
            }

            @Override
            protected void onPostExecute(File o) {
                //Log.d("onPostExecute", "delete"+(o));
                afterDelete(o);
            }
        });
    }

    public File doDelete(int pos) {

        return null;
    }

    public void afterDelete(File o) {

    }
}
