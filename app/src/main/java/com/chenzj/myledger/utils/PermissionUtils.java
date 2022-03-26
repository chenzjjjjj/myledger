package com.chenzj.myledger.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;

/**
 * @description: TODO
 * @author: chenzj
 * @date: 2022/3/25 17:49
 */
public class PermissionUtils {
    private static final int REQUEST_CODE = 1;

    //这是要申请的权限
    private static String[] PERMISSIONS_CAMERA_AND_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
//            , Manifest.permission.CAMERA
    };


    public static boolean isGrantExternalRW(Activity activity) {
        return isGrantExternalRW(activity, REQUEST_CODE);
    }

    /**
     * @param activity
     * @param requestCode
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int storagePermission = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int cameraPermission = activity.checkSelfPermission(Manifest.permission.CAMERA);
            //检测是否有权限，如果没有权限，就需要申请
            if (storagePermission != PackageManager.PERMISSION_GRANTED ||
                    cameraPermission != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                activity.requestPermissions(PERMISSIONS_CAMERA_AND_STORAGE, requestCode);
                //返回false。说明没有授权
                return false;
            }
        }
        //说明已经授权
        return true;
    }

    public static boolean checkPermission(Activity activity) {
        try {
            String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            int permission = ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_CODE);
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
