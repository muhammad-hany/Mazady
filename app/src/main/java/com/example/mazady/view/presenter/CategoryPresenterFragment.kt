package com.example.mazady.view.presenter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mazady.databinding.CategoryPresenterFragmentBinding
import com.example.mazady.view.home.HomeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategoryPresenterFragment : Fragment() {
    private var _binding: CategoryPresenterFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PresenterViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CategoryPresenterFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val submittedList = viewModel.getSubmittedList().map { it.toString() }
        val adapter = PresenterAdapter(submittedList)
        binding.resultList.adapter = adapter
        binding.resultList.layoutManager = LinearLayoutManager(requireContext())

        binding.nextScreenButton.setOnClickListener {
            val intent = Intent(requireContext(), HomeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}