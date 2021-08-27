package com.russvkm.projectmanagement.activity

import android.app.Dialog
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.russvkm.projectmanagement.R
import kotlinx.android.synthetic.main.wait_dialog.*

open class BaseActivity : AppCompatActivity() {

    private lateinit var dialog:Dialog
    private var pressTwiceExitOneTime:Boolean=false

    fun creatingDialog(message:String){
        dialog= Dialog(this)
        dialog.setContentView(R.layout.wait_dialog)
        dialog.dialogTextView.text=message
        dialog.setCancelable(false)
        dialog.show()
    }

    fun dismissDialog(){
        dialog.dismiss()
    }

    fun showErrorMessage(error:String){
        val snackBar=Snackbar.make(findViewById(android.R.id.content),error,Snackbar.LENGTH_LONG)
        val snackBarView=snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this,R.color.snack_bar_color))
        snackBar.show()
    }

    fun pressTwiceExit(){
        if(pressTwiceExitOneTime){
            super.onBackPressed()
            return
        }
        this.pressTwiceExitOneTime=true
        Toast.makeText(this,"Press again to exit",Toast.LENGTH_SHORT).show()
        Handler().postDelayed({pressTwiceExitOneTime=false},2000)
    }
}