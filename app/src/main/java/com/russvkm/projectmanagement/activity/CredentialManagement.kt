package com.russvkm.projectmanagement.activity

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.russvkm.projectmanagement.R
import kotlinx.android.synthetic.main.activity_credential_management.*

class CredentialManagement : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credential_management)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        signUp.setOnClickListener {
            startActivity(Intent(this,
                SignUpActivity::class.java))
        }

        signIn.setOnClickListener {
            startActivity(Intent(this,
                SignInActivity::class.java))
        }
    }
}