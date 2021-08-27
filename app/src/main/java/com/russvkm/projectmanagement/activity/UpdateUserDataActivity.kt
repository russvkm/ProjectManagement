package com.russvkm.projectmanagement.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.russvkm.projectmanagement.R
import com.russvkm.projectmanagement.firebase.FireStoreHandler
import com.russvkm.projectmanagement.models.User
import com.russvkm.projectmanagement.utils.Constants
import com.russvkm.projectmanagement.utils.PermissionAndFetching
import kotlinx.android.synthetic.main.activity_update_user_data.*

class UpdateUserDataActivity : BaseActivity() {
    private var activityResultPhotoUri:Uri?=null
    private  var profileImageUrl:String=""
    private lateinit var userDetail:User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user_data)
        configuringActionBar()
        FireStoreHandler().loginAndReceivingData(this)
        updateUserImage.setOnClickListener {
            PermissionAndFetching(
                this,
                this
            ).meshingWithPermission()
        }

        updateButton.setOnClickListener {
            if (activityResultPhotoUri!=null){
                savingPhotoToFireBase()
            }else{
                updatingUser()
            }
        }
    }

    private fun configuringActionBar(){
        setSupportActionBar(updateActivityToolBar)
        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow)
        }
        updateActivityToolBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun updateUserDataToUserUpdateActivity(user: User){
        userDetail=user
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_update_user_image)
            .into(updateUserImage)
        updateNameEditText.setText(user.name)
        updateEmailEditText.setText(user.email)
        if(user.mobile!=0L)
        updatePhoneEditText.setText(user.mobile.toString())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==Constants.REQUEST_CODE_FOR_STORAGE_PERMISSION){
            PermissionAndFetching(
                this,
                this
            ).fetchingPhotoFromInternalStorage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== Activity.RESULT_OK){
            if (requestCode==Constants.IMAGE_REQUEST_CODE){
                activityResultPhotoUri =data!!.data
                updateUserImage.setImageURI(activityResultPhotoUri)
            }
        }
    }

    private fun savingPhotoToFireBase(){
        creatingDialog(resources.getString(R.string.pls_wait))
        val firebaseRef:StorageReference= FirebaseStorage.getInstance().reference.child(FireStoreHandler().userId()+"."+
                PermissionAndFetching(
                    this,
                    this
                ).getFileExtension(activityResultPhotoUri))
        firebaseRef.putFile(activityResultPhotoUri!!).addOnSuccessListener {
            taskSnapshot ->
            Log.i("TAG",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                uri->
                Log.i("TAG",uri.toString())
                profileImageUrl=uri.toString()
                updatingUser()
            }
        }.addOnFailureListener {
            exception ->
            Toast.makeText(this,exception.message,Toast.LENGTH_LONG).show()
            dismissDialog()
        }
    }



    fun profileUpdateSuccess(){
        dismissDialog()
        setResult(Activity.RESULT_OK)
        Toast.makeText(this,"Profile Updated SuccessFully",Toast.LENGTH_LONG).show()
        finish()
    }

    private fun updatingUser(){
        val userHashMap=HashMap<String,Any>()
        var anyChangesMade=false
        if(profileImageUrl.isNotEmpty()&&profileImageUrl!=userDetail.image){
            userHashMap["image"] = profileImageUrl
            anyChangesMade=true
        }
        if (updateNameEditText.text.toString()!=userDetail.name){
            userHashMap["name"]=updateNameEditText.text.toString()
            anyChangesMade=true
        }
        if (updatePhoneEditText.text.toString()!=userDetail.mobile.toString()){
            userHashMap["mobile"]=updatePhoneEditText.text.toString().toLong()
            anyChangesMade=true
        }
        if(anyChangesMade)
            FireStoreHandler().updatingData(this,userHashMap)
    }
}