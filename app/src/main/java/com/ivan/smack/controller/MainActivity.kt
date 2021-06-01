package com.ivan.smack.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ivan.smack.R
import com.ivan.smack.model.Channel
import com.ivan.smack.services.AuthService
import com.ivan.smack.services.MessageService
import com.ivan.smack.services.UserDataService
import com.ivan.smack.utils.BROADCAST_USER_DATA_CHANGE
import com.ivan.smack.utils.SOCKET_URL
import com.ivan.smack.utils.hideKeyboard
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.btn_login
import java.util.zip.ZipEntry

class MainActivity : AppCompatActivity() {

    private val socket = IO.socket(SOCKET_URL)
    private lateinit var channelAdapter: ArrayAdapter<Channel>

    private val onNewChannel = Emitter.Listener {
        runOnUiThread {
            val channelName = it[0] as String
            val channelDescription = it[1] as String
            val channelId = it[2] as String
            val newChannel = Channel(channelName, channelDescription, channelId)
            MessageService.channels.add(newChannel)
            channelAdapter.notifyDataSetChanged()
        }
    }

    private val userDataChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (App.sharedPreferences.isLoggedIn) {
                tv_name.text = UserDataService.name
                tv_mail.text = UserDataService.email
                val resourceId =
                    resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                iv_avatar.setImageResource(resourceId)
                iv_avatar.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                btn_login.text = "Logout"

                MessageService.getChannels(context) { success ->
                    if (success) {
                        channelAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        setupAdapters()
        socket.connect()
        socket.on("channelCreated", onNewChannel)

        if (App.sharedPreferences.isLoggedIn) {
            AuthService.findUserByEmail(this){ success ->
                if (success) {

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            userDataChangeReceiver, IntentFilter(
                BROADCAST_USER_DATA_CHANGE
            )
        )
    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        super.onDestroy()
    }

    fun onLoginClicked(view: View) {
        if (App.sharedPreferences.isLoggedIn) {
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

    fun onAddChannelClicked(view: View) {
        if (App.sharedPreferences.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.add_channel_dialog, null)
            builder.setView(view)
                .setPositiveButton("Add") { dialogInterface, i ->
                    val nameEt = view.findViewById<EditText>(R.id.et_add_channel_name)
                    val descriptionEt = view.findViewById<EditText>(R.id.et_channel_description)
                    val name = nameEt.text.toString()
                    val description = descriptionEt.text.toString()

                    socket.emit("newChannel", name, description)
                }
                .setNegativeButton("Cancel") { dialogInterface, i ->
                }
                .show()
        }
    }

    fun onSendClicked(view: View) {
        hideKeyboard()
    }

    private fun setupAdapters() {
        channelAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter
    }
}