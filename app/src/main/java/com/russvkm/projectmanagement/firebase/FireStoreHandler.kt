package com.russvkm.projectmanagement.firebase

import android.app.Activity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.russvkm.projectmanagement.activity.*
import com.russvkm.projectmanagement.models.Board
import com.russvkm.projectmanagement.models.User
import com.russvkm.projectmanagement.utils.Constants

class FireStoreHandler {
    private val fireStore:FirebaseFirestore= FirebaseFirestore.getInstance()
    private val firebaseAuth:FirebaseAuth= FirebaseAuth.getInstance()

    fun registerUser(activity:SignUpActivity,userInfo: User){
        fireStore.collection(Constants.USER)
            .document(firebaseAuth.currentUser!!.uid)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
    }

    fun registeringCardToFirebase(activity:CreateBoardActivity,boardInfo: Board){
        activity.creatingDialog("Please Wait")
        fireStore.collection(Constants.BOARD)
            .document()
            .set(boardInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.boardCreatedSuccessfully()
            }
            .addOnFailureListener {
                exception ->
                activity.dismissDialog()
                Toast.makeText(activity,exception.message,Toast.LENGTH_SHORT).show()
            }
    }

    fun updatingData(activity:Activity,userHashMap:HashMap<String,Any>){
        if (activity is UpdateUserDataActivity)
            activity.creatingDialog("Please Wait")
        if (activity is MainActivity)
            activity.creatingDialog("Please Wait")
        fireStore.collection(Constants.USER)
            .document(userId())
            .update(userHashMap)
            .addOnSuccessListener {
                when(activity){
                    is MainActivity->{
                        activity.tokenUpdateSuccess()
                        activity.dismissDialog()
                    }
                    is UpdateUserDataActivity->{
                        activity.profileUpdateSuccess()
                        activity.dismissDialog()
                    }
                }
            }
            .addOnFailureListener {
                exception ->
                Toast.makeText(activity,exception.message,Toast.LENGTH_LONG).show()
                if (activity is UpdateUserDataActivity)
                    activity.dismissDialog()
                if (activity is MainActivity)
                    activity.dismissDialog()
            }
    }

    fun loginAndReceivingData(activity: Activity,readBoardList:Boolean=false) {
        fireStore.collection(Constants.USER)
            .document(userId())
            .get()
            .addOnSuccessListener {document->
                val loggedIn=document.toObject(User::class.java)
                when(activity){
                    is SignInActivity->{
                        if (loggedIn!=null)
                            activity.signInSuccess(loggedIn)
                    }
                    is MainActivity->{
                        activity.updateNavigationUi(loggedIn!!,readBoardList)
                    }

                    is UpdateUserDataActivity->{
                        activity.updateUserDataToUserUpdateActivity(loggedIn!!)
                    }
                }
            }.addOnFailureListener {
                task->
                Toast.makeText(activity,task.message,Toast.LENGTH_LONG).show()
            }
    }

    fun getBoardDetails(activity:TaskListActivity,documentId:String){
        activity.creatingDialog("Please Wait")
        fireStore.collection(Constants.BOARD)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                document->
                activity.dismissDialog()
                val board=document.toObject(Board::class.java)!!
                board.documentId=document.id
                activity.boardDetails(board)
            }
            .addOnFailureListener {
                exception ->
                activity.dismissDialog()
                Toast.makeText(activity,exception.message,Toast.LENGTH_SHORT).show()
            }
    }

    fun gettingBoard(activity:MainActivity){
        activity.creatingDialog("Please Wait")
        fireStore.collection(Constants.BOARD)
            .whereArrayContains(Constants.ASSIGN_TO,userId())
            .get()
            .addOnSuccessListener {
                document->
                activity.dismissDialog()
                val boardList:ArrayList<Board> = ArrayList()
                for (i in document.documents){
                    val board=i.toObject(Board::class.java)
                    board!!.documentId=i.id
                    boardList.add(board)
                }
                activity.populateRecyclerView(boardList)
            }
            .addOnFailureListener {
                exception->
                activity.dismissDialog()
                Toast.makeText(activity,exception.message,Toast.LENGTH_SHORT).show()

            }
    }

    fun addUpdateTask(activity:Activity,board:Board){
        val taskListHashMap=HashMap<String,Any>()
        taskListHashMap[Constants.TASK_LIST]=board.taskList
        if(activity is TaskListActivity)
        activity.creatingDialog("Please Wait")
        else if(activity is CardDetailActivity)
            activity.creatingDialog("Please Wait")

        fireStore.collection(Constants.BOARD)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                when(activity){
                  is TaskListActivity->{
                      activity.dismissDialog()
                      activity.addUpdateTaskList()
                    }
                   is CardDetailActivity->{
                       activity.dismissDialog()
                       activity.addUpdateTaskList()
                    }
                }
            }
            .addOnFailureListener {
                exception ->
                when(activity){
                    is TaskListActivity->{
                        activity.dismissDialog()
                        Toast.makeText(activity,exception.message,Toast.LENGTH_SHORT).show()
                    }
                    is CardDetailActivity->{
                        activity.dismissDialog()
                        Toast.makeText(activity,exception.message,Toast.LENGTH_SHORT).show()
                    }
                }
            }

    }

    fun getAssignedMemberListDetails(activity:Activity,assignedTo:ArrayList<String>){
        if (activity is MembersActivity)
            activity.creatingDialog("Please Wait")
        if (activity is TaskListActivity)
            activity.creatingDialog("Please Wait")
        fireStore.collection(Constants.USER)
            .whereIn(Constants.ID,assignedTo)
            .get()
            .addOnSuccessListener {
                document->
                val userList:ArrayList<User> =ArrayList()
                for(i in document.documents){
                    val user=i.toObject(User::class.java)!!
                    userList.add(user)
                }
                if(activity is MembersActivity){
                    activity.configuringRecyclerView(userList)
                    activity.dismissDialog()}
                else if(activity is TaskListActivity){
                    activity.mAssignedUserDetail(userList)
                    activity.dismissDialog()}
            }
            .addOnFailureListener {
                exception->
                if (activity is MembersActivity){
                    activity.dismissDialog()
                    Toast.makeText(activity,exception.message,Toast.LENGTH_SHORT).show()
                }
                if (activity is TaskListActivity){
                    activity.dismissDialog()
                    Toast.makeText(activity,exception.message,Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun getMemberList(activity:MembersActivity,email:String){
        activity.creatingDialog("Please Wait")
        fireStore.collection(Constants.USER)
            .whereEqualTo(Constants.EMAIL,email)
            .get()
            .addOnSuccessListener {
                document->
                activity.dismissDialog()
                if(document.documents.size>0){
                    val user=document.documents[0].toObject(User::class.java)!!
                    activity.getMember(user)
                }else{
                    activity.showErrorMessage("No Such User Found")
                }
            }
            .addOnFailureListener {
                exception ->
                activity.dismissDialog()
                Toast.makeText(activity,exception.message,Toast.LENGTH_SHORT).show()
            }
    }

    fun assignMemberToFirebase(activity: MembersActivity,board:Board,user:User){
        activity.creatingDialog("Please Wait")
        val hashMap:HashMap<String,Any> = HashMap()
        hashMap[Constants.ASSIGN_TO]=board.assignTo
        fireStore.collection(Constants.BOARD)
            .document(board.documentId)
            .update(hashMap)
            .addOnSuccessListener {
                activity.dismissDialog()
                activity.memberAssignSuccess(user)
            }
            .addOnFailureListener {
                exception ->
                activity.dismissDialog()
                Toast.makeText(activity,exception.message,Toast.LENGTH_SHORT).show()
            }
    }

    fun userId():String{
        val currentUser=FirebaseAuth.getInstance().currentUser
        var currentUserId=""
        if (currentUser!=null){
            currentUserId=currentUser.uid
        }
        return currentUserId
    }
}