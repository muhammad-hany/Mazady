package com.example.mazady.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mazady.R
import com.example.mazady.databinding.ContactsItemBinding
import com.example.mazady.models.Contact

class LiveContactAdapter : ListAdapter<Contact, LiveContactAdapter.LiveContactViewHolder>(ContactDiffer()) {

    inner class LiveContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ContactsItemBinding.bind(itemView)
        fun bind(contact: Contact) {
            binding.avatarImage.setImageResource(contact.imageId)
        }
    }

    private class ContactDiffer : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.phone == newItem.phone
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.isLive == newItem.isLive
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contacts_item, parent, false)
        return LiveContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: LiveContactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}