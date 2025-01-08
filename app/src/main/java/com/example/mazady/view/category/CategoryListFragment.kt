package com.example.mazady.view.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mazady.R
import com.example.mazady.databinding.FragmentCategoryListBinding
import com.example.mazady.models.ScreenState
import com.example.mazady.utils.gone
import com.example.mazady.utils.show
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategoryListFragment : Fragment() {

    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryListViewModel by viewModel()
    private val adapter = CategoryAdapter { viewModel.onItemClicked(it) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSelectionList()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.userSelectionFlow.collectLatest { items ->
                    if (items.isEmpty()) return@collectLatest
                    adapter.submitList(items)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.screenStateFlow.collectLatest(::onCollectScreenState)
            }
        }


        binding.submitButton.setOnClickListener {
            val isValidated = viewModel.validateData()
            if (isValidated) {
                // move to the next screen
                findNavController().navigate(R.id.selection_to_presenter_action)
            }
        }
    }

    private fun onCollectScreenState(screenState: ScreenState) {
        if (screenState.isLoading) binding.progressBar.show() else binding.progressBar.gone()
        if (!screenState.error.isNullOrEmpty()) createErrorSnackbar(screenState.error)
        if (screenState.submitButtonVisible) binding.submitButton.show() else binding.submitButton.gone()
    }

    private fun createErrorSnackbar(errorMessage: String) {
        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
    }

    private fun setupSelectionList() {
        binding.categoryList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.categoryList.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}