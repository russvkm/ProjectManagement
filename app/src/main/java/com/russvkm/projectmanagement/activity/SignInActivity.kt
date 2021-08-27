package com.russvkm.projectmanagement.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.russvkm.projectmanagement.R
import com.russvkm.projectmanagement.firebase.FireStoreHandler
import com.russvkm.projectmanagement.models.User
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity :BaseActivity(){
    private lateinit var firebaseAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        configuringToolBar()
        firebaseAuth=FirebaseAuth.getInstance()
        signInButton.setOnClickListener {
            signInToFireBase()
        }
    }

    private fun configuringToolBar(){
        setSupportActionBar(signInToolbar)
        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow)
        }
        signInToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    private fun signInToFireBase(){
        val email=signInEmailEditText.text.toString().trim { it <= ' ' }
        val password=signInPasswordEditText.text.toString()
        if(validatingLogInForm(email,password)){
            creatingDialog("Please Wait")
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            task ->
            if(task.isSuccessful){
                FireStoreHandler().loginAndReceivingData(this)
            }else{
                dismissDialog()
                Toast.makeText(this,task.exception!!.message,Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun validatingLogInForm(email:String,password:String):Boolean{

        return when{
            TextUtils.isEmpty(email)->{
                showErrorMessage("Email can not be left empty")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorMessage("Password can not be empty")
                false
            }
            else->{
                true
            }
        }
    }

    fun signInSuccess(user: User){
        dismissDialog()
        startActivity(Intent(this, MainActivity::class.java))
        user.toString()
        finish()
    }
}