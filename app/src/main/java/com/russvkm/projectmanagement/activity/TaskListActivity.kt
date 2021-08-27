package com.russvkm.projectmanagement.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.russvkm.projectmanagement.R
import com.russvkm.projectmanagement.adapters.TaskListItemAdapter
import com.russvkm.projectmanagement.firebase.FireStoreHandler
import com.russvkm.projectmanagement.models.Board
import com.russvkm.projectmanagement.models.Card
import com.russvkm.projectmanagement.models.Task
import com.russvkm.projectmanagement.models.User
import com.russvkm.projectmanagement.utils.Constants
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : BaseActivity() {

    private lateinit var mBoardDetails:Board
    private lateinit var documentId: String
    lateinit var mAssignedUserList:ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        receivingExtraFromIntent()
    }

    private fun receivingExtraFromIntent(){
        if (intent.hasExtra(Constants.DOCUMENT_ID)){
            documentId=intent.getStringExtra(Constants.DOCUMENT_ID)!!
            FireStoreHandler().getBoardDetails(this,documentId)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK&&requestCode==Constants.TASK_LIST_ACTIVITY_REQUEST_CODE ||requestCode==Constants.CARD_DETAIL_ACTIVITY_REQUEST_CODE){
            FireStoreHandler().getBoardDetails(this,documentId)
        }
    }

    fun boardDetails(board:Board){
        mBoardDetails=board
        configuringActionBar(board.card_name)
        FireStoreHandler().getAssignedMemberListDetails(this,mBoardDetails.assignTo)
    }

    private fun configuringActionBar(title:String){
        taskListActivityToolBar.title=title
        setSupportActionBar(taskListActivityToolBar)
        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow)
        }
        taskListActivityToolBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun addUpdateTaskList(){
        FireStoreHandler().getBoardDetails(this,mBoardDetails.documentId)
    }

    fun createTaskList(taskLisName:String){
        val task=Task(taskLisName,FireStoreHandler().userId())
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        FireStoreHandler().addUpdateTask(this,mBoardDetails)
    }

    fun updateTaskList(position:Int,listName:String,model:Task){
        val task=Task(listName,model.createdBy)
        mBoardDetails.taskList[position]=task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        FireStoreHandler().addUpdateTask(this,mBoardDetails)
    }

    fun deleteTaskList(position:Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        FireStoreHandler().addUpdateTask(this,mBoardDetails)
    }

    fun createCard(position: Int,cardName:String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        val cardAssignedUserList:ArrayList<String> = ArrayList()
        cardAssignedUserList.add(FireStoreHandler().userId())
        val card= Card(cardName,FireStoreHandler().userId(),cardAssignedUserList)
        val cardList=mBoardDetails.taskList[position].cards
        cardList.add(card)
        val task=Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardList
        )
        mBoardDetails.taskList[position]=task
        FireStoreHandler().addUpdateTask(this,mBoardDetails)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater=menuInflater
        inflater.inflate(R.menu.member_action_bar,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionMember->{
                val intent=Intent(this,MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAILS,mBoardDetails)
                startActivityForResult(intent,Constants.TASK_LIST_ACTIVITY_REQUEST_CODE)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun intendingToCardDetailActivity(taskListPosition:Int,cardPosition:Int){
        val intent=Intent(this,CardDetailActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAILS,mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION,taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION,cardPosition)
        intent.putExtra(Constants.BOARD_MEMBER_LIST,mAssignedUserList)
        startActivityForResult(intent,Constants.CARD_DETAIL_ACTIVITY_REQUEST_CODE)
    }

    fun mAssignedUserDetail(list:ArrayList<User>){
        mAssignedUserList=list
        val taskList=Task(resources.getString(R.string.add_list))
        mBoardDetails.taskList.add(taskList)
        taskListRecyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        val adapter=TaskListItemAdapter(this,mBoardDetails.taskList)
        taskListRecyclerView.adapter=adapter
    }

}