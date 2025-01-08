package com.example.mazady.view.category

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mazady.R
import com.example.mazady.databinding.CategoryListItemBinding
import com.example.mazady.models.Category
import com.example.mazady.models.CategoryOtherPropertyClick
import com.example.mazady.models.CategoryPropertyClick
import com.example.mazady.models.CategoryPropertyInput
import com.example.mazady.models.CategoryPropertyListItem
import com.example.mazady.models.ItemClickAction
import com.example.mazady.models.ListItem
import com.example.mazady.models.MainCategoryClick
import com.example.mazady.models.MainCategoryListItem
import com.example.mazady.models.SubCategoryClick
import com.example.mazady.models.SubCategoryListItem
import com.example.mazady.utils.showAndEnable
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText

class CategoryAdapter(private val onItemClickAction: (ItemClickAction) -> Unit) :
    ListAdapter<ListItem, RecyclerView.ViewHolder>(CategoryDiffCallback()) {

    inner class CategoryViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val categoryBinding = CategoryListItemBinding.bind(item)
        fun onBind(item: ListItem) {
            when (item) {
                is MainCategoryListItem -> bindMainCategory(item)
                is SubCategoryListItem -> bindSubCategory(item)
                is CategoryPropertyListItem -> bindCategoryProperty(item)
            }
            // checking if there is an error
            categoryBinding.mainCategory.error = if(item.showError) {
                itemView.context.getString(R.string.required_field)
            } else {
                null
            }
        }

        private fun bindMainCategory(item: MainCategoryListItem) {
            handleCategories(item.data, true, item.selectionIndex)
            categoryBinding.mainCategory.hint = itemView.context.getString(R.string.main_category)
        }

        private fun bindSubCategory(item: SubCategoryListItem) {
            handleCategories(item.data, false, item.selectionIndex)
            categoryBinding.mainCategory.hint =
                itemView.context.getString(R.string.secondary_category)
        }

        private fun bindCategoryProperty(item: CategoryPropertyListItem) {
            categoryBinding.mainCategory.showAndEnable()
            categoryBinding.mainCategory.hint = item.data.name
            val listItems =
                item.data.options?.mapNotNull { it.name }?.toTypedArray() ?: emptyArray()
            applySimpleList(listItems, item.selectionIndex) { position ->
                val selectedItem = item.data.options?.getOrNull(position)
                if (selectedItem != null && selectedItem.slug == "other") {
                    onItemClickAction(CategoryOtherPropertyClick(item.data, position))
                } else {
                    onItemClickAction(CategoryPropertyClick(item.data, position))
                }
            }
        }

        private fun handleCategories(
            categories: List<Category>,
            isMainCategory: Boolean,
            selection: Int
        ) {
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

        private fun applySimpleList(
            listItems: Array<String>,
            selection: Int,
            onItemClick: (Int) -> Unit
        ) {
            (categoryBinding.mainCategory.editText as? MaterialAutoCompleteTextView)?.apply {
                setSimpleItems(listItems)
                if (selection != -1) setText(listItems[selection], false) else text = null
                setOnItemClickListener { _, _, position, _ ->
                    onItemClick(position)
                }
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    inner class EditCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryBinding = CategoryListItemBinding.bind(itemView)
        fun onBind(item: CategoryPropertyListItem) {
            categoryBinding.mainCategory.showAndEnable()
            categoryBinding.mainCategory.hint = item.data.name
            (categoryBinding.mainCategory.editText as? TextInputEditText)?.apply {
                if (item.inputText != null) {
                    if (item.inputText != text.toString()) {
                        setText(item.inputText)
                        setSelection(item.inputText.length)
                    }
                } else {
                    text = null
                }

                // checking if there is an error
                error = if(item.showError) {
                    itemView.context.getString(R.string.required_field)
                } else {
                    null
                }

                doAfterTextChanged { text ->
                    // making sure change from user
                    if (!hasFocus()) return@doAfterTextChanged

                    // adding some delay until the user finish typing to avoid multiple requests
                    runnable?.let { this@CategoryAdapter.handler.removeCallbacks(it) }
                    runnable = Runnable {
                        onItemClickAction(CategoryPropertyInput(item.data, text.toString()))
                    }
                    runnable?.let { this@CategoryAdapter.handler.postDelayed(it, 800) }
                }
            }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return if (oldItem.javaClass.name != newItem.javaClass.name) false
            else {
                when (oldItem) {
                    is MainCategoryListItem -> oldItem.hashCode() == (newItem as MainCategoryListItem).hashCode()
                    is SubCategoryListItem -> oldItem.hashCode() == (newItem as SubCategoryListItem).hashCode()
                    is CategoryPropertyListItem -> oldItem.hashCode() == (newItem as CategoryPropertyListItem).hashCode()
                }
            }
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return if (oldItem.javaClass.name != newItem.javaClass.name) false
            else {
                oldItem.selection == newItem.selection && oldItem.userInput == newItem.userInput && oldItem.showError == newItem.showError
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item is CategoryPropertyListItem && item.data.options.isNullOrEmpty()) {
            TYPE_EDIT
        } else {
            TYPE_DOP_DOWN
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_EDIT) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.category_edit_list_item, parent, false)
            EditCategoryViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_list_item, parent, false)
            CategoryViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EditCategoryViewHolder) {
            holder.onBind(getItem(position) as CategoryPropertyListItem)
        } else {
            (holder as? CategoryViewHolder)?.onBind(getItem(position))
        }
    }

    companion object {
        private const val TYPE_DOP_DOWN = 1
        private const val TYPE_EDIT = 2
    }
}