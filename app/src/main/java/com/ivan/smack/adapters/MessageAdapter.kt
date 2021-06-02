package com.ivan.smack.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ivan.smack.R
import com.ivan.smack.adapters.MessageAdapter.MessageViewHolder
import com.ivan.smack.model.Message
import com.ivan.smack.services.UserDataService
import kotlinx.android.synthetic.main.item_message.view.*

class MessageAdapter(val messages: MutableList<Message>): RecyclerView.Adapter<MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false))

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) =
        holder.onBind(messages[position])

    override fun getItemCount() = messages.count()

    inner class MessageViewHolder(itemView: View) : ViewHolder(itemView) {

        val image: ImageView = itemView.iv_message
        val user: TextView = itemView.tv_user_name
        val timestamp: TextView = itemView.tv_timestamp
        val message: TextView = itemView.tv_message

        fun onBind(msg: Message){
            val resourceId = itemView.context.resources.getIdentifier(msg.userName, "drawable", itemView.context.packageName)
            image.setImageResource(resourceId)
            image.setBackgroundColor(UserDataService.returnAvatarColor(msg.userAvatarColor))
            user.text = msg.userName
            timestamp.text = msg.timestamp
            message.text = msg.message
        }
    }
}