package com.arun.speedtester

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun checkHasCoarseLocationPermission(context: Context) = ActivityCompat.checkSelfPermission(
    context,
    Manifest.permission.ACCESS_COARSE_LOCATION
) == PackageManager.PERMISSION_GRANTED

fun checkHasFineLocationPermission(context: Context) = ActivityCompat.checkSelfPermission(
    context,
    Manifest.permission.ACCESS_FINE_LOCATION
) == PackageManager.PERMISSION_GRANTED

fun checkHasReadPhoneStatePermission(context: Context) = ActivityCompat.checkSelfPermission(
    context,
    Manifest.permission.READ_PHONE_STATE
) == PackageManager.PERMISSION_GRANTED

fun checkHasAccessNetworkStatePermission(context: Context) = ActivityCompat.checkSelfPermission(
    context,
    Manifest.permission.ACCESS_NETWORK_STATE
) == PackageManager.PERMISSION_GRANTED

fun requestPermission(context: Context) {
    val permissionToRequest = mutableListOf<String>()
    if (!checkHasCoarseLocationPermission(context)) {
        permissionToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
    }
    if (!checkHasFineLocationPermission(context)) {
        permissionToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    if (!checkHasReadPhoneStatePermission(context)) {
        permissionToRequest.add(Manifest.permission.READ_PHONE_STATE)
    }
    if (!checkHasAccessNetworkStatePermission(context)) {
        permissionToRequest.add(Manifest.permission.ACCESS_NETWORK_STATE)
    }

    if (permissionToRequest.isNotEmpty()) {
        ActivityCompat.requestPermissions(context as Activity, permissionToRequest.toTypedArray(), 0)
    }
}

fun checkHasAllPermission(context: Context): Boolean{
    return checkHasReadPhoneStatePermission(context) && checkHasAccessNetworkStatePermission(context) && checkHasCoarseLocationPermission(context) && checkHasFineLocationPermission(context)
}