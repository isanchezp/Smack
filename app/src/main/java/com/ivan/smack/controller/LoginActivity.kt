package com.ivan.smack.controller

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY
import android.widget.Toast
import com.ivan.smack.R
import com.ivan.smack.services.AuthService
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_user.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        pb_login_loader.visibility = View.INVISIBLE
    }

    fun onLoginClicked(view: View) {
        hideKeyboard()
        enableSpinner(true)
        val email = et_email.text.toString()
        val pws = et_password.text.toString()

        if (email.isNotEmpty() && pws.isNotEmpty()) {
            AuthService.login(this, email, pws) { success ->
                if (success) {
                    AuthService.findUserByEmail(this) { success ->
                        if (success) {
                            enableSpinner(false)
                            finish()
                        } else {
                            errorToast()
                        }
                    }
                } else {
                    errorToast()
                }
            }
        } else {
            enableSpinner(false)
            Toast.makeText(this, "Please fill in both email and password", Toast.LENGTH_LONG).show()
        }
    }

    fun onSignupClicked(view: View) {
        startActivity(Intent(this, UserActivity::class.java))
        finish()
    }

    private fun enableSpinner(enable: Boolean) {
        pb_login_loader.visibility = if (enable) View.VISIBLE else View.INVISIBLE
        btn_login.isEnabled = !enable
        btn_signup.isEnabled = !enable
    }

    private fun errorToast() {
        Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }
}