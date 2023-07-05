package com.example.climago

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.climago.FragmentsNavBar.User


data class User(val username: String, var isFollowing: Boolean)

class AdapterSeguidores(private val users: List<User>) : RecyclerView.Adapter<AdapterSeguidores.ViewHolder>()  {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.RVtvNome)
        val followButton: Button = view.findViewById(R.id.RVbtSeguir)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lista_seguidores, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.username.text = user.username
        updateFollowButton(holder.followButton, user.isFollowing)

        holder.followButton.setOnClickListener {
            user.isFollowing = !user.isFollowing
            updateFollowButton(holder.followButton, user.isFollowing)
        }
    }

    override fun getItemCount() = users.size

    private fun updateFollowButton(button: Button, isFollowing: Boolean) {
        button.text = if (isFollowing) "Unfollow" else "Follow"
    }
}
