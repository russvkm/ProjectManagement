package com.russvkm.projectmanagement.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.russvkm.projectmanagement.R
import com.russvkm.projectmanagement.adapters.BoardAdapter
import com.russvkm.projectmanagement.firebase.FireStoreHandler
import com.russvkm.projectmanagement.models.Board
import com.russvkm.projectmanagement.models.User
import com.russvkm.projectmanagement.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.main_content.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener{
    private lateinit var userNameToPass:String
    private lateinit var mSharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configuringActionBar()
        navView.setNavigationItemSelectedListener(this)
        mSharedPreferences=this.getSharedPreferences(Constants.PROJECT_MANAGEMENT_PREF, Context.MODE_PRIVATE)
        FireStoreHandler().loginAndReceivingData(this,true)
        floatingActionButton.setOnClickListener {
            val intent=Intent(this,CreateBoardActivity::class.java)
            intent.putExtra("userName",userNameToPass)
            startActivityForResult(intent, BOARD_INFO_REQUEST_CODE)
        }

        val tokenUpdated=mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED,false)
        if(tokenUpdated){
            FireStoreHandler().loginAndReceivingData(this,true)
        }else{
            FirebaseInstanceId.getInstance().instanceId
                .addOnSuccessListener(this@MainActivity) {
                    instanceIdResult ->
                    updateFcmToken(instanceIdResult.token)
                }
        }
    }

    private fun configuringActionBar(){
        setSupportActionBar(toolBarMainActivity)
        toolBarMainActivity.setNavigationIcon(R.drawable.ic_navigation_icon)
        toolBarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer(){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            pressTwiceExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.profileName->{
                val intent=Intent(this,UpdateUserDataActivity::class.java)
                startActivityForResult(intent,PROFILE_UPDATE_REQUEST_CODE)}
            R.id.signOut->{
                FirebaseAuth.getInstance().signOut()
                mSharedPreferences.edit().clear().apply()
                val intent= Intent(this@MainActivity,CredentialManagement::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun updateNavigationUi(user: User,readBoardList:Boolean){
        userNameToPass=user.name
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.user_image)
            .into(navUserImage)

        navUserName.text=user.name

        if (readBoardList){
            FireStoreHandler().gettingBoard(this)
        }
        dismissDialog()
    }

    fun populateRecyclerView(arrayList:ArrayList<Board>){
        dismissDialog()
        boardHolderRecyclerView.layoutManager=LinearLayoutManager(this)
        boardHolderRecyclerView.setHasFixedSize(true)
        val adapter=BoardAdapter(this,arrayList)
        boardHolderRecyclerView.adapter=adapter
        adapter.setOnClickListener(object :BoardAdapter.OnClickListener{
            override fun onClick(position: Int, model: Board) {
                val intent=Intent(this@MainActivity,TaskListActivity::class.java)
                intent.putExtra(Constants.DOCUMENT_ID,model.documentId)
                startActivity(intent)
            }
        })
    }

    companion object{
        private const val PROFILE_UPDATE_REQUEST_CODE=30
        private const val BOARD_INFO_REQUEST_CODE=79
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== Activity.RESULT_OK){
            if(requestCode== PROFILE_UPDATE_REQUEST_CODE){
                FireStoreHandler().loginAndReceivingData(this,true)
            }
            if (requestCode== BOARD_INFO_REQUEST_CODE){
                FireStoreHandler().gettingBoard(this)
            }
        }
    }

    fun tokenUpdateSuccess(){
        val editor:SharedPreferences.Editor=mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED,true)
        editor.apply()
        FireStoreHandler().loginAndReceivingData(this,true)
    }

   private fun updateFcmToken(token:String){
       val userHashMap=HashMap<String,Any>()
       userHashMap[Constants.FCM_TOKEN]=token
       FireStoreHandler().updatingData(this,userHashMap)
   }
}