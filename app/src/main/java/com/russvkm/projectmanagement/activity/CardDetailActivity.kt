package com.russvkm.projectmanagement.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.russvkm.projectmanagement.R
import com.russvkm.projectmanagement.adapters.CardMemberListAdapter
import com.russvkm.projectmanagement.dialog.LabelColorListDialog
import com.russvkm.projectmanagement.dialog.MemberDialog
import com.russvkm.projectmanagement.firebase.FireStoreHandler
import com.russvkm.projectmanagement.models.*
import com.russvkm.projectmanagement.utils.Constants
import kotlinx.android.synthetic.main.activity_card_detail.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailActivity : BaseActivity() {

    private lateinit var mBoardDetails:Board
    private var cardPosition:Int = -1
    private var taskPosition:Int= -1
    private var selectedColor:String=""
    private lateinit var mAssignedUserList:ArrayList<User>
    private var mSelectDueDateMilliSecond:Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_detail)
        receivingDataFromTaskListActivity()
        configuringActionBar()
        selectDate()
        cardNameEditText.setText(mBoardDetails.taskList[taskPosition].cards[cardPosition].card_name)
        cardNameEditText.setSelection(cardNameEditText.text.toString().length)

        selectedColor=mBoardDetails.taskList[taskPosition].cards[cardPosition].labelColor
        if(selectedColor.isNotEmpty()){
            setColor()
        }

        mSelectDueDateMilliSecond=mBoardDetails.taskList[taskPosition].cards[cardPosition].dueDate
        if (mSelectDueDateMilliSecond>0){
            val sdf=SimpleDateFormat("dd/mm/yyyy",Locale.getDefault())
            selectDueDateEditText.text=sdf.format(Date(mSelectDueDateMilliSecond))
        }

        updateCardDetailButton.setOnClickListener {
            if(cardNameEditText.text.toString().isNotEmpty()){
                updateCardDetail()
            }else{
                cardNameEditText.error="Card name can't be left blank"
                cardNameEditText.requestFocus()
            }
        }

        selectColorEditText.setOnClickListener{
            labelColorListDialog()
        }
        triggerMember()
        setUpSelectedMemberList()
    }

    private fun configuringActionBar(){
        cardActivityToolBar.title=mBoardDetails.taskList[taskPosition].cards[cardPosition].card_name
        setSupportActionBar(cardActivityToolBar)
        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow)
        }
        cardActivityToolBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    private fun receivingDataFromTaskListActivity(){
        if(intent.hasExtra(Constants.BOARD_DETAILS)){
            mBoardDetails=intent.getParcelableExtra(Constants.BOARD_DETAILS)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            taskPosition=intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,-1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            cardPosition=intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION,-1)
        }
        if(intent.hasExtra(Constants.BOARD_MEMBER_LIST)){
            mAssignedUserList=intent.getParcelableArrayListExtra(Constants.BOARD_MEMBER_LIST)!!
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.card_detail_activity_delete_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.cardDetailActivityDeleteMenu->{
                createDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun addUpdateTaskList(){
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun updateCardDetail(){
        val card=Card(
            cardNameEditText.text.toString(),
            mBoardDetails.taskList[taskPosition].cards[cardPosition].createdBy,
            mBoardDetails.taskList[taskPosition].cards[cardPosition].assignedTo,
            selectedColor,
            mSelectDueDateMilliSecond
        )

        val taskList:ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)
        mBoardDetails.taskList[taskPosition].cards[cardPosition]=card
        FireStoreHandler().addUpdateTask(this,mBoardDetails)
    }

    private fun deleteCards(){
        val cardList:ArrayList<Card> = mBoardDetails.taskList[taskPosition].cards
        cardList.removeAt(cardPosition)
        val taskList:ArrayList<Task> =mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)
        taskList[taskPosition].cards=cardList
        FireStoreHandler().addUpdateTask(this,mBoardDetails)
    }


    private fun colorList():ArrayList<String>{
        val colorArrayList:ArrayList<String> = ArrayList()
        colorArrayList.add("#43C86F")
        colorArrayList.add("#0C90F1")
        colorArrayList.add("#F72400")
        colorArrayList.add("#7A8089")
        colorArrayList.add("#D57C1D")
        colorArrayList.add("#770000")
        colorArrayList.add("#FFE277")
        colorArrayList.add("#0022F8")
        return colorArrayList
    }

    private fun setColor(){
        selectColorEditText.text=""
        selectColorEditText.setBackgroundColor(Color.parseColor(selectedColor))
    }

    private fun createDialog(){
        AlertDialog.Builder(this)
            .setTitle("Alert")
            .setIcon(R.drawable.ic_warning)
            .setMessage("Do you want to delete ${mBoardDetails.taskList[taskPosition].cards[cardPosition].card_name}")
            .setPositiveButton("YES"){
                    _, _ ->
                deleteCards()
            }
            .setNegativeButton("NO"){
                dialog,_->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun labelColorListDialog(){
        val listColor:ArrayList<String> = colorList()

        val listDialog=object:LabelColorListDialog(
           this,
            listColor,
            selectedColor,
            resources.getString(R.string.select_color)
        ){
            override fun onItemSelected(color: String) {
                selectedColor = color
                setColor()
            }
        }
        listDialog.create()
        listDialog.show()
    }

    private fun triggerMember(){
        selectMemberEditText.setOnClickListener {
            addMemberToCard()
        }
     }

    private fun setUpSelectedMemberList(){
        val cardAssignMemberList=mBoardDetails.taskList[taskPosition].cards[cardPosition].assignedTo
        val selectedMember=ArrayList<SelectedMember>()
        for (i in mAssignedUserList.indices) {
            for (j in cardAssignMemberList) {
                if (mAssignedUserList[i].id == j) {
                    val selected=SelectedMember(
                        mAssignedUserList[i].id,
                        mAssignedUserList[i].image
                    )
                    selectedMember.add(selected)
                }
            }
        }
        if(selectedMember.size>0){
            selectedMember.add(SelectedMember("",""))
            selectMemberTextField.visibility= View.GONE
            memberListRecyclerView.visibility=View.VISIBLE
            memberListRecyclerView.layoutManager=GridLayoutManager(this,5)
            val adapter=CardMemberListAdapter(this,selectedMember,true)
            memberListRecyclerView.adapter=adapter
            adapter.setOnClickListener(
                object: CardMemberListAdapter.OnClickListener{
                    override fun onClick() {
                        addMemberToCard()
                    }

                }
            )
        }else{
            selectMemberTextField.visibility= View.VISIBLE
            memberListRecyclerView.visibility=View.GONE
        }
    }

    private fun addMemberToCard(){
        val cardAssignMemberList =
            mBoardDetails.taskList[taskPosition].cards[cardPosition].assignedTo
        if (cardAssignMemberList.size > 0) {
            for (i in mAssignedUserList.indices) {
                for (j in cardAssignMemberList) {
                    if (mAssignedUserList[i].id == j) {
                        mAssignedUserList[i].selected = true
                    }
                }
            }
        } else {
            for (i in mAssignedUserList.indices) {
                mAssignedUserList[i].selected = false
            }
        }
        val listDialog = object : MemberDialog(
            this,
            mAssignedUserList,
            resources.getString(R.string.select_member)
        ){
            override fun onItemSelected(user: User, action: String) {
                if(action==Constants.SELECT){
                    if(!mBoardDetails.taskList[taskPosition].
                        cards[cardPosition].assignedTo.contains(user.id)){
                        mBoardDetails.taskList[taskPosition].
                        cards[cardPosition].assignedTo.add(user.id)
                    }
                }else{
                    mBoardDetails.taskList[taskPosition].
                    cards[cardPosition].assignedTo.remove(user.id)

                    for(i in mAssignedUserList.indices){
                        if (mAssignedUserList[i].id==user.id){
                            mAssignedUserList[i].selected=false
                        }
                    }
                }
                setUpSelectedMemberList()
            }

        }
        listDialog.show()

    }

    private fun selectDate(){
        selectDueDateEditText.setOnClickListener {
            datePicker()
        }
    }

    private fun datePicker(){
        val calendar= Calendar.getInstance()
        val year=calendar.get(Calendar.YEAR)
        val month=calendar.get(Calendar.MONTH)
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        val dpd=DatePickerDialog(this, DatePickerDialog.OnDateSetListener {
                _, sYear, sMonth, dayOfMonth ->
            val sDay= if(dayOfMonth<10) "0$dayOfMonth" else "$dayOfMonth"
            val selectedMonth=if(sMonth+1<10) "0${sMonth+1}" else "${sMonth+1}"
            val selectedDate="$sDay/$selectedMonth/$sYear"
            selectDueDateEditText.text=selectedDate
            val sdf=SimpleDateFormat("dd/mm/yyyy",Locale.getDefault())
            mSelectDueDateMilliSecond=sdf.parse(selectedDate)!!.time
        },year,
        month,
        day)
        dpd.show()
    }

}