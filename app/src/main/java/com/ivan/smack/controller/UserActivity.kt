package com.ivan.smack.controller

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ivan.smack.R
import com.ivan.smack.services.AuthService
import com.ivan.smack.utils.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_user.*
import java.util.*

class UserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        pb_loader.visibility = View.INVISIBLE
    }

    fun onAvatarClicked(view: View) {
        val random = Random()
        val color = random.nextInt(2)
        val avatar = random.nextInt(28)

        if (color == 0) {
            userAvatar = "light$avatar"
        } else {
            userAvatar = "dark$avatar"
        }

        val resourceId = resources.getIdentifier(userAvatar, "drawable", packageName)
        iv_avatar.setImageResource(resourceId)
    }

    fun onGnerateColorClicked(view: View) {
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        iv_avatar.setBackgroundColor(Color.rgb(r, g, b))

        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = b.toDouble() / 255
        avatarColor = "[$savedR, $savedG, $savedB, 1]"
        println(avatarColor)
    }

    fun onCreateUserClicked(view: View) {
        enableSpinner(true)

        val userName = et_user_name.text.toString()
        val email = et_email.text.toString()
        val pws = et_key.text.toString()

        if (userName.isNotEmpty() && email.isNotEmpty() && pws.isNotEmpty()) {
            AuthService.registerUser(email, pws) { registerSuccess ->
                if (registerSuccess) {
                    AuthService.login(email, pws) { loginSuccess ->
                        if (loginSuccess) {
                            AuthService.createUser(
                                userName,
                                email,
                                userAvatar,
                                avatarColor
                            ) { createSuccess ->
                                if (createSuccess) {
                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
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
                    errorToast()
                }
            }
        } else {
            Toast.makeText(this, "Fit all the fields", Toast.LENGTH_LONG).show()
            enableSpinner(false)
        }
    }

    private fun enableSpinner(enable: Boolean) {
        pb_loader.visibility = if (enable) View.VISIBLE else View.INVISIBLE
        btn_create_user.isEnabled = !enable
        iv_avatar.isEnabled = !enable
        btn_color.isEnabled = !enable
    }

    private fun errorToast() {
        Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }
}