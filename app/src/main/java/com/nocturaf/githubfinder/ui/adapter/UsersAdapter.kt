package com.nocturaf.githubfinder.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nocturaf.githubfinder.R
import com.nocturaf.githubfinder.ui.model.UserUiModel
import kotlinx.android.synthetic.main.item_user.view.*

class UsersAdapter : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    companion object {
        @LayoutRes
        const val USER_VIEWHOLDER_LAYOUT = R.layout.item_user
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            with(parent) {
                LayoutInflater.from(context).inflate(USER_VIEWHOLDER_LAYOUT, this, false)
            }
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<UserUiModel>() {
        override fun areItemsTheSame(oldItem: UserUiModel, newItem: UserUiModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserUiModel, newItem: UserUiModel): Boolean {
            return oldItem == newItem
        }
    })

    /**
     * User item viewholder
     */
    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: UserUiModel) {
            itemView.apply {
                // render user content
                tvUserName.text = user.username
                tvUsernameLink.text = context.resources.getString(R.string.url_view, user.username)
                Glide.with(this)
                    .load(user.avatar)
                    .placeholder(R.drawable.ic_image_loading)
                    .error(R.drawable.ic_image_loading)
                    .into(ivUserImage)
            }
        }
    }
}