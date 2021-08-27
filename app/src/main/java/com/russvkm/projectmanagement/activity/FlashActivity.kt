package com.russvkm.projectmanagement.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.russvkm.projectmanagement.R
import com.russvkm.projectmanagement.firebase.FireStoreHandler

class FlashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        Handler().postDelayed({
            if(FireStoreHandler().userId().isNotEmpty()){
                startActivity(Intent(this@FlashActivity,
                    MainActivity::class.java))
            }else{
                startActivity(Intent(this@FlashActivity,
                    CredentialManagement::class.java))
            }

            finish()
        },2000)
    }
}