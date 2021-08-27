package com.russvkm.projectmanagement.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.russvkm.projectmanagement.R
import com.russvkm.projectmanagement.firebase.FireStoreHandler
import com.russvkm.projectmanagement.models.Board
import com.russvkm.projectmanagement.utils.Constants
import com.russvkm.projectmanagement.utils.PermissionAndFetching
import kotlinx.android.synthetic.main.activity_create_board.*

class CreateBoardActivity : BaseActivity() {

    private var activityResultPhotoUri: Uri?=null
    private var receivedUserName:String?=""
    private var boardImageUrl:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)
        configuringToolbar()
        fetchingImageFromInternalStorage()

        if(intent!=null){
            receivedUserName=intent.getStringExtra("userName")
        }
        submitCardToFirebase()
    }

    private fun configuringToolbar(){
        setSupportActionBar(createBoardToolbar)
        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow)
        }
        createBoardToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun fetchingImageFromInternalStorage(){
        boardImage.setOnClickListener {
            PermissionAndFetching(
                this,
                this
            ).meshingWithPermission()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==Constants.IMAGE_REQUEST_CODE)
                if(data!=null){
                    activityResultPhotoUri = data.data
                    boardImage.setImageURI(activityResultPhotoUri)
                }
        }
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

    private fun collectingAndPushingDataToFirebase(){
        val assignedUserArrayList:ArrayList<String> = ArrayList()
        assignedUserArrayList.add(FireStoreHandler().userId())
        val boardData=Board(
            boardName.text.toString(),
            boardImageUrl,
            receivedUserName!!,
            assignedUserArrayList
        )
        FireStoreHandler().registeringCardToFirebase(this,boardData)
    }

    private fun uploadImageToFirebase(){
        creatingDialog(resources.getString(R.string.pls_wait))
        val storageRef:StorageReference=FirebaseStorage.getInstance().reference.child(
            FireStoreHandler().userId()+System.currentTimeMillis()+"."+
                    PermissionAndFetching(this,this).getFileExtension(activityResultPhotoUri))
        storageRef.putFile(activityResultPhotoUri!!)
            .addOnSuccessListener {
                taskSnapshot ->
                dismissDialog()
                creatingDialog(resources.getString(R.string.pls_wait))
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener {
                        uri ->
                        dismissDialog()
                        boardImageUrl=uri.toString()
                        collectingAndPushingDataToFirebase()
                    }
                    .addOnFailureListener{
                        exception->
                        dismissDialog()
                        Toast.makeText(this,exception.message,Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener{
                    exception->
                dismissDialog()
                Toast.makeText(this,exception.message,Toast.LENGTH_SHORT).show()
            }
    }

    fun boardCreatedSuccessfully(){
        Toast.makeText(this,"Board Created Successfully",Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK)
        dismissDialog()
        finish()
    }

    private fun submitCardToFirebase(){
        createBoardButton.setOnClickListener {
            if (boardName.text.toString().isNotEmpty()){
               if(activityResultPhotoUri!=null){
                   uploadImageToFirebase()
               } else{
                   collectingAndPushingDataToFirebase()
               }
            }else{
                showErrorMessage("Board name can not be left blank :(")
            }
        }
    }
}