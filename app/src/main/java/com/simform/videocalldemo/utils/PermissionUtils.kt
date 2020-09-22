package com.simform.videocalldemo.utils


import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class PermissionUtils {

  companion object {
    fun checkHasPermission(permission: String, context: Context): Boolean {
      return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permission)
    }

    fun canShowMessage(permission: String, activity: Activity): Boolean {
      return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    fun askPermission(activity: Activity, permissions: Array<String>, requestCode: Int) {
      ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }
  }
}