package com.example.mazady.view.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mazady.R
import com.example.mazady.databinding.CategoryListItemBinding
import com.example.mazady.models.Category
import com.example.mazady.models.CategoryPropertyClick
import com.example.mazady.models.CategoryPropertyListItem
import com.example.mazady.models.ItemClickAction
import com.example.mazady.models.ListItem
import com.example.mazady.models.MainCategoryClick
import com.example.mazady.models.MainCategoryListItem
import com.example.mazady.models.SubCategoryClick
import com.example.mazady.models.SubCategoryListItem
import com.example.mazady.utils.showAndEnable
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class CategoryAdapter(private val onItemClickAction: (ItemClickAction) -> Unit) :
    ListAdapter<ListItem, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    inner class CategoryViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val categoryBinding = CategoryListItemBinding.bind(item)
        fun onBind(item: ListItem) {
            when (item) {
                is MainCategoryListItem -> bindMainCategory(item)
                is SubCategoryListItem -> bindSubCategory(item)
                is CategoryPropertyListItem -> bindCategoryProperty(item)
//                is OptionsListItem -> bindOptions(item)

            }
        }

        private fun bindMainCategory(item: MainCategoryListItem) {
            handleCategories(item.data,true, item.selectionIndex)
            categoryBinding.mainCategory.hint = itemView.context.getString(R.string.main_category)
        }

        private fun bindSubCategory(item: SubCategoryListItem) {
            handleCategories(item.data, false, item.selectionIndex)
            categoryBinding.mainCategory.hint = itemView.context.getString(R.string.secondary_category)
        }

        private fun bindCategoryProperty(item: CategoryPropertyListItem) {
            categoryBinding.mainCategory.showAndEnable()
            categoryBinding.mainCategory.hint = item.data.name
            val listItems = item.data.options?.mapNotNull { it.name}?.toTypedArray() ?: emptyArray()
            applySimpleList(listItems, item.selectionIndex) { position ->
                onItemClickAction(CategoryPropertyClick(item.data, position))
            }
        }

//        private fun bindOptions(item: OptionsListItem) {
//            categoryBinding.mainCategory.hint = itemView.context.getString(R.string.property_options)
//            categoryBinding.mainCategory.showAndEnable()
//            val listItems = item.data.mapNotNull { it.name }.toTypedArray()
//            applySimpleList(listItems, item.selectionIndex) { position ->
//                onItemClickAction(OptionsClick(item.data[position], position))
//            }
//        }

        private fun handleCategories(categories: List<Category>, isMainCategory: Boolean, selection: Int) {
            categoryBinding.mainCategory.showAndEnable()
            val listItems = categories.mapNotNull { it.name }.toTypedArray()
            applySimpleList(listItems, selection) { position ->
                if (isMainCategory) {
                    onItemClickAction(MainCategoryClick(categories[position], position))
                } else {
                    onItemClickAction(SubCategoryClick(categories[position], position))
                }
            }
        }

        private fun applySimpleList(listItems: Array<String>, selection: Int, onItemClick: (Int) -> Unit) {
            (categoryBinding.mainCategory.editText as? MaterialAutoCompleteTextView)?.apply {
                setSimpleItems(listItems)
                if (selection != -1) setText(listItems[selection], false) else text = null
                setOnItemClickListener { _, _, position, _ ->
                    onItemClick(position)
                }
            }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_list_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}