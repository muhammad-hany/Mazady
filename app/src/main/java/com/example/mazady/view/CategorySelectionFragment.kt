package com.example.mazady.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mazady.databinding.FragmentCategoriesBinding
import com.example.mazady.models.CategoryState
import com.example.mazady.utils.gone
import com.example.mazady.utils.show
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategorySelectionFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategorySelectionViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.categoryState.collectLatest(::onCategoryState)
            }
        }

        setupListeners()
    }

    private fun setupListeners() {
        (binding.mainCategory.editText as? MaterialAutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->
            viewModel.onMainCategorySelected(position)
            onMainCategorySelected()
        }
        (binding.categoryProperty.editText as? MaterialAutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->
            viewModel.onPropertySelected(position)
            onPropertySelected()
        }

        (binding.subCategory.editText as? MaterialAutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->
            viewModel.onSubCategorySelected(position)
            onSubCategorySelected()
        }
    }

    private fun onCategoryState(state: CategoryState) {
        val categories = state.categories
        if (categories.isNotEmpty()) {
            binding.mainCategory.isEnabled = true
            val mainCategoriesNames = categories.map { it.name }.toTypedArray()
            (binding.mainCategory.editText as? MaterialAutoCompleteTextView)?.apply {
                setSimpleItems(mainCategoriesNames)
            }
        } else {
            binding.mainCategory.isEnabled = false
        }

        if (state.isLoading) binding.progressBar.show() else binding.progressBar.gone()
        // TODO show error state

        state.selectedMainCategory?.let {
//            onMainCategorySelected()
        } ?: run {
            binding.subCategory.gone()
            binding.categoryProperty.gone()
        }

        state.selectedSubCategory?.let {
//            onSubCategorySelected()
        } ?: run {
            binding.categoryProperty.gone()
        }

        if (state.subCategoryProperties.isNotEmpty()) {
            (binding.categoryProperty.editText as? MaterialAutoCompleteTextView)?.apply {
                val propertiesNames = state.subCategoryProperties.map { it.name }.toTypedArray()
                setSimpleItems(propertiesNames)
            }
        }
    }

    private fun onMainCategorySelected() {
        val mainCategory = viewModel.categoryState.value.selectedMainCategory
        binding.subCategory.isEnabled = true
        binding.subCategory.show()
        (binding.subCategory.editText as? MaterialAutoCompleteTextView)?.apply {
            val subCategoriesNames =
                mainCategory?.children?.map { it.name }?.toTypedArray() ?: emptyArray()
            // TODO handle subCategoriesNames.isEmpty()
            setSimpleItems(subCategoriesNames)

        }
    }

    private fun onSubCategorySelected() {
        val subCategory = viewModel.categoryState.value.selectedSubCategory
        binding.subCategory.isEnabled = true
        binding.categoryProperty.show()
        subCategory?.id?.let { viewModel.getCategoryProperties(it) }
        // TODO handle subCategory.id == null
    }

    private fun onPropertySelected() {
        val property = viewModel.categoryState.value.selectedProperty

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}