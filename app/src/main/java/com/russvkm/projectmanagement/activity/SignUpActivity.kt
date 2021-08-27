package com.russvkm.projectmanagement.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.russvkm.projectmanagement.R
import com.russvkm.projectmanagement.firebase.FireStoreHandler
import com.russvkm.projectmanagement.models.User
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {
    private lateinit var firebaseAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        conFiguringActionBar()
        firebaseAuth=FirebaseAuth.getInstance()

        signUpButton.setOnClickListener {
            registerUser()
        }
    }

    private fun conFiguringActionBar(){
        signUpToolbar.title="Sign Up"
        setSupportActionBar(signUpToolbar)
        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow)
        }
        signUpToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun registerUser(){
        val name=nameEditText.text.toString().trim{ it <=' '}
        val email=emailEditText.text.toString().trim{ it <= ' '}
        val password=passEditText.text.toString()
        val cPassword=cPassEditText.text.toString()
        if(validatingSignUpForm(name,email,password,cPassword)){
            creatingDialog(resources.getString(R.string.pls_wait))
            firebaseAuth.createUserWithEmailAndPassword(
                email,password
            ).addOnCompleteListener {
                task->
                if (task.isSuccessful){
                    val firebaseUser: FirebaseUser =task.result!!.user!!
                    val registeredEmail=firebaseUser.email
                    val fireUserData=User(firebaseUser.uid,name,registeredEmail!!)
                    FireStoreHandler().registerUser(this,fireUserData)
                }else{
                    dismissDialog()
                    Toast.makeText(this,"${task.exception}",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validatingSignUpForm(name:String,email:String,password:String,cPassword:String):Boolean{
        return when{
                TextUtils.isEmpty(name)->{
                    showErrorMessage("Enter Name")
                false
                }
            TextUtils.isEmpty(email)->{
                showErrorMessage("Enter your email Id")
            false
            }
            TextUtils.isEmpty(password)->{
                showErrorMessage("Password can not be blank")
            false
            }
            TextUtils.isEmpty(cPassword)->{
                showErrorMessage("Re-enter password")
            false
            }
            !TextUtils.equals(password,cPassword)->{
                showErrorMessage("password did not matched")
            false
            }else->{
                true
            }
        }
    }

    fun userRegisteredSuccess(){
        dismissDialog()
        startActivity(Intent(this@SignUpActivity,MainActivity::class.java))
        finish()
    }
}