package com.lksprovinsi.golibrary.libraries

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager {
    companion object{
        fun check(ctx: Context, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(ctx, permission) == PackageManager.PERMISSION_GRANTED
        }

        fun request(activity: Activity, permissions: Array<String>, requestCode: Int){
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
        }
    }
}