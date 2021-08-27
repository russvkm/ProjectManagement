package com.russvkm.projectmanagement.activity

import android.app.Activity
import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.russvkm.projectmanagement.R
import com.russvkm.projectmanagement.adapters.MemberListAdapter
import com.russvkm.projectmanagement.firebase.FireStoreHandler
import com.russvkm.projectmanagement.models.Board
import com.russvkm.projectmanagement.models.User
import com.russvkm.projectmanagement.utils.Constants
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.add_email_dialog.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MembersActivity : BaseActivity() {

    private lateinit var mBoardDetails: Board
    private lateinit var mMemberList:ArrayList<User>
    private var anyChangesMade:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)
        configuringActionBar()
        if(intent.hasExtra(Constants.BOARD_DETAILS)){
            mBoardDetails=intent.getParcelableExtra<Board>(Constants.BOARD_DETAILS)!!
            FireStoreHandler().getAssignedMemberListDetails(this,mBoardDetails.assignTo)
        }
    }

    private fun configuringActionBar(){
        memberActivityToolbar.title=resources.getString(R.string.member)
        setSupportActionBar(memberActivityToolbar)
        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        memberActivityToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun configuringRecyclerView(arrayList:ArrayList<User>){
        mMemberList=arrayList
        memberRecyclerView.layoutManager=LinearLayoutManager(this)
        memberRecyclerView.setHasFixedSize(true)
        val adapter=MemberListAdapter(this,arrayList)
        memberRecyclerView.adapter=adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_member_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun getMember(user:User){
        mBoardDetails.assignTo.add(user.id)
        FireStoreHandler().assignMemberToFirebase(this,mBoardDetails,user)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.addMemberMenu->{
                dialogSearchMember()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    private fun dialogSearchMember(){
        val dialog=Dialog(this)
        dialog.setContentView(R.layout.add_email_dialog)
        dialog.setCancelable(false)
        dialog.addMember.setOnClickListener {
            val email=dialog.searchEmailEditText.text.toString()
            if(email.isNotEmpty()){
                FireStoreHandler().getMemberList(this,email)
                dialog.dismiss()
            }else{
                searchEmailEditText.error="Enter email address"
                searchEmailEditText.requestFocus()
            }
        }
        dialog.cancelMember.setOnClickListener {
            cancelMember.setTextColor(ContextCompat.getColor(this,R.color.splash_background))
            dialog.dismiss()
        }
        dialog.show()
    }

    fun memberAssignSuccess(user:User){
        mMemberList.add(user)
        configuringRecyclerView(mMemberList)
        anyChangesMade=true

        SendNotificationToUserAsyncTask(mBoardDetails.card_name,token =user.fcmToken)
    }

    private inner class SendNotificationToUserAsyncTask(val boardName:String,val token:String):AsyncTask<Any,Void,String>(){

        override fun onPreExecute() {
            super.onPreExecute()
            creatingDialog(resources.getString(R.string.pls_wait))
        }

        override fun doInBackground(vararg params: Any?): String {
            var result:String
            var connection:HttpURLConnection?=null
            try{
                val url=URL(Constants.FCM_BASE_URL)
                connection=url.openConnection() as HttpURLConnection
                connection.doInput=true
                connection.doOutput=true
                connection.instanceFollowRedirects=false
                connection.requestMethod="POST"
                connection.setRequestProperty("Content-Type","application/json")
                connection.setRequestProperty("charset","utf-8")
                connection.setRequestProperty("Accept","application/json")

                connection.setRequestProperty(
                    Constants.FCM_AUTHORIZATION,"${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}")

                connection.useCaches=false

                val writer=DataOutputStream(connection.outputStream)
                val jsonRequest=JSONObject()
                val dataObject=JSONObject()
                dataObject.put(Constants.FCM_KEY_TITLE,"Assigned to the board $boardName")
                dataObject.put(Constants.FCM_KEY_MESSAGE,"You have been assigned to the board by ${mMemberList[0].name}")
                jsonRequest.put(Constants.FCM_KEY_DATA,dataObject)
                jsonRequest.put(Constants.FCM_KEY_TO,token)
                writer.writeBytes(jsonRequest.toString())
                writer.flush()
                writer.close()

                val httpResult:Int=connection.responseCode
                if(httpResult==HttpURLConnection.HTTP_OK){
                   val inputStream=connection.inputStream
                    val reader=BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder=StringBuilder()
                    var line:String?
                    try {
                        while (reader.readLine().also { line=it }!=null){
                            stringBuilder.append(line+"\n")
                        }
                    }catch (e:IOException){
                        e.printStackTrace()
                    }
                    try {
                        inputStream.close()
                    }catch (e:IOException){
                        e.printStackTrace()
                    }
                    result=stringBuilder.toString()
                }else{
                    result=connection.responseMessage
                }
            }catch (e:SocketTimeoutException){
                result="Connection Time0ut"
            }catch (e:Exception){
                result="Error : "+e.message
            }
            finally {
                connection?.disconnect()
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            dismissDialog()
        }

    }
}