package com.me.archko.staggered;

import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 现有工具类太多，且很多工具类中只有很少的方法，故诞生此类，所有不容易归类的方法都可以定义在该类中。
 *
 * @author huangshan1 2012-11-8
 */
public class Utils {

    public static void serializeObject(Object obj, String filename) {
        // DLog.d("serialization", "serialize: " + obj.getClass().getSimpleName());
        try {
            File file=new File(filename);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(file, false));
            out.writeObject(obj);
            out.flush();
            out.close();
        } catch (Exception e) {
            //if (DLog.isLog) {
            e.printStackTrace();
            //}
        }
    }

    public static Object deserializeObject(String filename) {
        // DLog.d("serialization", "deserialize: " + filename);
        try {
            ObjectInputStream in=new ObjectInputStream(new FileInputStream(filename));
            Object object=in.readObject();
            in.close();
            return object;
        } catch (Exception e) {
            //if (DLog.isLog) {
            e.printStackTrace();
            //}
        }
        return null;
    }

    public static Object deserializeObject(byte[] data) {
        // DLog.d("serialization", "deserialize: " + data);
        if (data!=null&&data.length>0) {
            try {
                ObjectInputStream in=new ObjectInputStream(new ByteArrayInputStream(data));
                Object obj=in.readObject();
                in.close();
                return obj;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static byte[] getSerializedBytes(Object obj) {
        // DLog.d("serialization", "deserialize: " + obj);
        try {
            ByteArrayOutputStream bao=new ByteArrayOutputStream();
            ObjectOutputStream out=new ObjectOutputStream(bao);
            out.writeObject(obj);
            out.flush();
            byte[] data=bao.toByteArray();
            out.close();
            bao.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    public static Object deepClone(Serializable obj) {
        try {
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            ObjectOutputStream oos=new ObjectOutputStream(baos);
            oos.writeObject(obj);

            ByteArrayInputStream bais=new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois=new ObjectInputStream(bais);
            return ois.readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 以下由于序列化的问题，为临时静态方法
     */
    /**
     * 传一个key，获得对应的JSONObject
     */
    public static JSONObject getJsonObject(HashMap<String, String> nameValues, String name) {
        if (nameValues==null) {
            return null;
        }

        JSONObject jsonObject=null;

        String value=nameValues.get(name);
        if (!TextUtils.isEmpty(value)) {
            value=value.equals("null") ? "" : value;
            if (value.length()>0&&value.charAt(0)=='{') {
                try {
                    jsonObject=new JSONObject(value);
                } catch (Exception e) {
                }
            }
        }

        return jsonObject;
    }

    /**
     * 判断是否为中介，0为直招，1为中介
     */
    public static int getZhiJie(HashMap<String, String> nameValues) {
        int value=0;

        JSONObject jsonObject=getJsonObject(nameValues, "icons");
        if (jsonObject!=null) {
            value=jsonObject.optInt("iszhijie");
        }
        return value;
    }

    /**
     * 返回是否为置顶贴，1为置顶贴
     */
    public static int getDing(HashMap<String, String> nameValues) {
        int value=0;

        JSONObject jsonObject=getJsonObject(nameValues, "icons");
        if (jsonObject!=null) {
            value=jsonObject.optInt("ding");
        }
        return value;
    }

    /**
     * 返回是否为验证贴，1为验证贴
     */
    public static int getYan(HashMap<String, String> nameValues) {
        int value=0;

        JSONObject jsonObject=getJsonObject(nameValues, "icons");
        if (jsonObject!=null) {
            value=jsonObject.optInt("yan");
        }
        return value;
    }

    /**
     * 获得屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        if (context!=null) {
            return context.getResources().getDisplayMetrics().widthPixels;
        } else {
            return 0;
        }
    }

    /**
     * 获得屏幕高度
     */
    public static int getScreenHeight(Context context) {
        if (context!=null) {
            return context.getResources().getDisplayMetrics().heightPixels;
        } else {
            return 0;
        }
    }

}
