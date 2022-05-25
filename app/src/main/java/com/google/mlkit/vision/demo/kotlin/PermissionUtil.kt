package com.google.mlkit.vision.demo.kotlin

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

public object  PermissionUtil {

    public fun checkPermission(context: Context, permissionList: List<String>): Boolean {
        for (i: Int in permissionList.indices) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permissionList[i]
                ) == PackageManager.PERMISSION_DENIED
            ) {
                return false
            }
        }

        return true

    }

    public fun requestPermission(activity: Activity, permissionList: List<String>){
        ActivityCompat.requestPermissions(activity, permissionList.toTypedArray(), 10)
    }
}
