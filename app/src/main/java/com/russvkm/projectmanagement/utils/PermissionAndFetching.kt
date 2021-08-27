package com.russvkm.projectmanagement.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat

class PermissionAndFetching(private val context:Context,private val activity: Activity) {

    fun meshingWithPermission(){
        when{
            ContextCompat.checkSelfPermission
                (context,Manifest.permission.READ_EXTERNAL_STORAGE)==
                    PackageManager.PERMISSION_GRANTED ->{
                fetchingPhotoFromInternalStorage()
            }

           shouldShowRequestPermissionRationale(activity,Manifest.permission.READ_EXTERNAL_STORAGE)->{
                alertDialog()
            }
            else->{
                requestPermission()
            }
        }
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            Constants.REQUEST_CODE_FOR_STORAGE_PERMISSION)
    }

    private fun alertDialog(){
        AlertDialog.Builder(context)
            .setTitle("Permission Denied")
            .setMessage("Looks like you have denied the permission please enable it")
            .setPositiveButton("Enable"){
                    _, _ ->
                requestPermission()
            }
            .setNegativeButton("Cancel"){
                dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    fun fetchingPhotoFromInternalStorage(){
        val intent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(intent, Constants.IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(uri: Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}