package com.bck.simpleword

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import java.io.File
import java.util.*

fun sortFileListByModifiedDate(fileList: Array<File>): Array<File>{
    Arrays.sort(fileList) { f2, f1 -> java.lang.Long.valueOf(f1.lastModified()).compareTo(f2.lastModified()) }
    return fileList
} // 파일리스트 수정된 날짜로 정렬하기
fun sortFileListByIsDirectory(fileList: Array<File>): Array<File>{
    Arrays.sort(fileList) { f2, f1 -> f1.isDirectory.compareTo(f2.isDirectory) }
    return fileList
} // 파일리스트 폴더를 위에 두기



const val MY_PERMISSIONS_REQUEST_READ_CONTACTS : Int = 5982
fun getPermission(activity: Activity, permissionNumber : Int){
// 사용자로부터 허가 권한을 얻기 위해서는 app-manifests-AndroidManifest.xml 파일에
// 다음과 같이 넣어줘야 한다.
//
// <?xml version="1.0" encoding="utf-8"?>
// <manifest xmlns:android="http://schemas.android.com/apk/res/android"
// package="com.bck.simpleword">
// <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
// <application
// android:allowBackup="true"
//
    var permissionString = ""
    when (permissionNumber) {
        10 -> permissionString = Manifest.permission.READ_EXTERNAL_STORAGE
        11 -> permissionString = Manifest.permission.WRITE_EXTERNAL_STORAGE
        else -> Toast.makeText(activity, "Warning! : Permission Error", Toast.LENGTH_SHORT).show()
    }

    if (ContextCompat.checkSelfPermission(activity, permissionString) != PackageManager.PERMISSION_GRANTED) {
        // Permission is not granted
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionString)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(permissionString), MY_PERMISSIONS_REQUEST_READ_CONTACTS)
            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    } else {
        // Permission has already been granted
    }
} // 기기 사용 권한 받아오기
