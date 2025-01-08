package com.example.mazady.view.presenter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mazady.R
import com.example.mazady.databinding.PresenterListItemBinding

class PresenterAdapter(private val items: List<String>) : RecyclerView.Adapter<PresenterAdapter.PresenterViewHolder>() {

    class PresenterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val binding: PresenterListItemBinding = PresenterListItemBinding.bind(itemView)

        fun onBind(item: String) {
            binding.textView.text = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresenterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.presenter_list_item, parent, false)
        return PresenterViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PresenterViewHolder, position: Int) {
        holder.onBind(items[position])
    }
}