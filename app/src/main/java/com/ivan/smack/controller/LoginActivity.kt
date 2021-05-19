package com.ivan.smack.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ivan.smack.R
import com.ivan.smack.services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun onLoginClicked(view: View) {
        val email = et_email.text.toString()
        val pws = et_password.text.toString()
        AuthService.login(this, email, pws) { success ->
            if (success) {
                AuthService.findUserByEmail(this){success ->
                    if (success) {
                        finish()
                    }
                }
            }
        }
    }

    fun onSignupClicked(view: View) {
        startActivity(Intent(this, UserActivity::class.java))
        finish()
    }
}