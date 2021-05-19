package com.ivan.smack.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ivan.smack.R
import com.ivan.smack.services.AuthService
import com.ivan.smack.services.UserDataService
import com.ivan.smack.utils.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.btn_login

class MainActivity : AppCompatActivity() {

    private val userDataChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (AuthService.isLoggedIn) {
                tv_name.text = UserDataService.name
                tv_mail.text = UserDataService.email
                val resourceId =
                    resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                iv_avatar.setImageResource(resourceId)
                iv_avatar.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                btn_login.text = "Logout"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            userDataChangeReceiver, IntentFilter(
                BROADCAST_USER_DATA_CHANGE
            )
        )
    }

    fun onLoginClicked(view: View) {
        if (AuthService.isLoggedIn) {
            // logout
            UserDataService.logout()
            tv_name.text = ""
            tv_mail.text = ""
            iv_avatar.setImageResource(R.drawable.profiledefault)
            iv_avatar.setBackgroundColor(Color.TRANSPARENT)
            btn_login.text = "Login"
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    fun onAddChannelClicked(view: View) {}

    fun onSendClicked(view: View) {}
}