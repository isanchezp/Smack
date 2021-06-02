package com.ivan.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.ivan.smack.controller.App
import com.ivan.smack.model.Channel
import com.ivan.smack.model.Message
import com.ivan.smack.utils.URL_GET_CHANNELS
import com.ivan.smack.utils.URL_GET_MESSAGES
import org.json.JSONException

object MessageService {

    val channels = mutableListOf<Channel>()
    val messages = mutableListOf<Message>()

    fun getChannels(context: Context, complete: (Boolean) -> Unit) {

        val channelRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null,
            Response.Listener {
                try {
                    for (channel in 0 until it.length()) {
                        val channelJsonObject = it.getJSONObject(channel)
                        val name = channelJsonObject.getString("name")
                        val description = channelJsonObject.getString("description")
                        val id = channelJsonObject.getString("_id")
                        val newChannel = Channel(name, description, id)
                        this.channels.add(newChannel)
                    }
                    complete(true)
                } catch (e: JSONException) {
                    Log.d("JSON", "EXC: ${e.localizedMessage}")
                    complete(false)
                }
            },
            Response.ErrorListener {
                Log.d("ERROR", "Could not retrieve channels")
                complete(false)
            }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${App.sharedPreferences.authToken}"
                return headers
            }
        }

        App.sharedPreferences.requestQueue.add(channelRequest)
    }

    fun getMessages(channelId: String, complete: (Boolean) -> Unit) {
        val url = "$URL_GET_MESSAGES$channelId"

        val messagesRequest = object : JsonArrayRequest(Method.GET, url, null,
            Response.Listener {
                clearMessages()
                try {
                    for (msg in 0 until it.length()) {
                        val message = it.getJSONObject(msg)
                        val messageBody = message.getString("messageBody")
                        val channelId = message.getString("channelId")
                        val id = message.getString("_id")
                        val userName = message.getString("userName")
                        val userAvatar = message.getString("userAvatar")
                        val userAvatarColor = message.getString("userAvatarColor")
                        val timeStamp = message.getString("timeStamp")
                        val newMessage = Message(messageBody, userName, channelId, userAvatar, userAvatarColor, id, timeStamp)
                        this.messages.add(newMessage)
                    }
                    complete(true)
                } catch (e: JSONException) {
                    Log.d("JSON", "EXC: ${e.localizedMessage}")
                    complete(false)
                }
            },
            Response.ErrorListener {
                Log.d("ERROR", "Could not receive channels")
                complete(false)
            }) {
            override fun getPostBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = mutableMapOf<String, String>()
                headers["Authorization"] = "Bearer ${App.sharedPreferences.authToken}"
                return headers
            }
        }

        App.sharedPreferences.requestQueue.add(messagesRequest)
    }

    fun clearMessages() {
        messages.clear()
    }

    fun clearChannels() {
        channels.clear()
    }
}