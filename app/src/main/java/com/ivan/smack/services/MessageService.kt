package com.ivan.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.ivan.smack.model.Channel
import com.ivan.smack.utils.URL_GET_CHANNELS
import org.json.JSONException

object MessageService {

    val channels = mutableListOf<Channel>()

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
                headers["Authorization"] = "Bearer ${AuthService.authToken}"
                return headers
            }
        }

        Volley.newRequestQueue(context).add(channelRequest)
    }
}