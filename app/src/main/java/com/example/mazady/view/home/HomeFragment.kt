package com.example.mazady.view.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mazady.R
import com.example.mazady.databinding.FragmentSecondBinding
import com.example.mazady.models.Contact
import com.google.android.material.chip.Chip

class HomeFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLiveContactsList()
        setupFilters()

    }

    private fun setupFilters() {
        binding.filterChipGroup.removeAllViews()
        filters.forEach {
            binding.filterChipGroup.addView(createChip(it))
        }
    }


    /**
     * dynamically creating a chip
     */
    private fun createChip(text: String): Chip = Chip(requireContext()).apply {
        this.text = text
        isCheckable = true
        isChecked = false
        chipStrokeWidth = 0f
        textSize = 16f
        chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.filter_chip_color)
        setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.filter_chip_text_color))
        setOnCheckedChangeListener { _, isChecked ->

        }
    }

    private fun setupLiveContactsList() {
        binding.liveContactsList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val liveContactAdapter = LiveContactAdapter()
        liveContactAdapter.submitList(AVATARS)
        binding.liveContactsList.adapter = liveContactAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val AVATARS = listOf(
            Contact("Jane Smith", "987654321", false, R.drawable.avatar),
            Contact("John Doe", "123456789", true, R.drawable.avatar1),
            Contact("Jane Doe", "987654321", true, R.drawable.avatar2),
            Contact("John Smith", "123456789", false, R.drawable.avatar3),
        )
        val filters = listOf(
            "All", "UI/UX", "Illustration", "Web Design"
        )
    }
}