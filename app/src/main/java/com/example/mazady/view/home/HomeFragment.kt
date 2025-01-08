package com.example.mazady.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mazady.R
import com.example.mazady.databinding.FragmentHomeBinding
import com.example.mazady.models.Contact
import com.example.mazady.models.Course
import com.example.mazady.models.CourseTag
import com.google.android.material.chip.Chip

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val coursesAdapter = CoursesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLiveContactsList()
        setupFilters()
        setupCoursesList()
    }

    private fun setupCoursesList() {
        binding.upcomingCoursesList.layoutManager = ProminentLayoutManager(requireContext())
        coursesAdapter.submitList(COURSES)
        binding.upcomingCoursesList.adapter = coursesAdapter
        binding.upcomingCoursesList.addItemDecoration(LinePagerIndicatorDecoration())
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
        chipStrokeWidth = 0f
        textSize = 16f
        chipBackgroundColor =
            ContextCompat.getColorStateList(requireContext(), R.color.filter_chip_color)
        setTextColor(
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.filter_chip_text_color
            )
        )
        setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) return@setOnCheckedChangeListener
            when (text) {
                "All" -> {
                    clearFilters()
                }

                else -> {
                    applyCourseFilter(text)
                }
            }
        }
        isChecked = text == "All"
    }

    private fun applyCourseFilter(text: String) {
        val filteredCourses = COURSES.filter {
            it.tags.any { tag -> tag.title == text }
        }
        coursesAdapter.submitList(filteredCourses) {
            binding.upcomingCoursesList.scrollToPosition(0)
        }
    }

    private fun clearFilters() {
        coursesAdapter.submitList(COURSES) {
            binding.upcomingCoursesList.scrollToPosition(0)
        }
    }

    private fun setupLiveContactsList() {
        binding.liveContactsList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
        private val freeCourseTag = CourseTag("Free", R.color.free)
        private val uixCourseTag = CourseTag("UI/UX", R.color.ui_ux)
        private val illustrationCourseTag = CourseTag("Illustration", R.color.illustration)
        private val webDesignCourseTag = CourseTag("Web Design", R.color.web_design)
        val tags = listOf(freeCourseTag, uixCourseTag, illustrationCourseTag, webDesignCourseTag)
        val filters = listOf("All", *tags.map { it.title }.toTypedArray())
        val COURSES = listOf(
            Course(
                "UI/UX Design",
                "Learn the basics of UI/UX design",
                "https://images.pexels.com/photos/196644/pexels-photo-196644.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                listOf(
                    uixCourseTag,
                    freeCourseTag
                ),
                "Jane Smith",
                "2h 30m",
                "UI/UX Designer",
                3
            ),
            Course(
                "Illustration",
                "Learn the basics of Illustration",
                "https://images.pexels.com/photos/4348403/pexels-photo-4348403.jpeg",
                listOf(
                    illustrationCourseTag
                ),
                "John Doe",
                "1h 45m",
                "Illustrator",
                5
            ),
            Course(
                "Web Design",
                "Learn the basics of Web Design",
                "https://images.pexels.com/photos/577585/pexels-photo-577585.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                listOf(
                    webDesignCourseTag
                ),
                "Jane Doe",
                "3h 15m",
                "Web Designer",
                4
            ),
            Course(
                "Mobile Apps Design",
                "Learn the basics of UI/UX design",
                "https://images.pexels.com/photos/3585088/pexels-photo-3585088.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                listOf(
                    uixCourseTag,
                    freeCourseTag
                ),
                "John Smith",
                "2h 30m",
                "UI/UX Designer",
                3
            ),

            )
    }
}