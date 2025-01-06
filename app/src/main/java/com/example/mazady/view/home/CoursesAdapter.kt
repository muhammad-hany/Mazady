package com.example.mazady.view.home

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mazady.R
import com.example.mazady.databinding.CourseListItemBinding
import com.example.mazady.models.Course
import com.example.mazady.models.CourseTag

class CoursesAdapter : ListAdapter<Course, CoursesAdapter.CourseViewHolder>(CourseDiffCallback()) {

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = CourseListItemBinding.bind(itemView)
        fun bind(course: Course) {
            // Bind the course data to the view
            binding.courseTitle.text = course.title
            Glide.with(itemView)
                .load(course.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.course1)
                .into(binding.courseImage)
            binding.lessonCount.text = "${course.lessonsCount} lessons"
            binding.readingDuration.text = course.duration
            addTags(course.tags)
            binding.authBio.text = course.authorOccupation
            binding.authorName.text = course.author
        }

        private fun addTags(tags: List<CourseTag>) {
            binding.tagContainers.removeViews(1, binding.tagContainers.childCount - 1)
            tags.forEach {
                val tag = TextView(
                    ContextThemeWrapper(
                        itemView.context,
                        R.style.TagTextAppearance
                    )
                ).apply {
                    text = it.title
                    backgroundTintList =
                        AppCompatResources.getColorStateList(itemView.context, it.color)
                    val layoutParams = ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.setMargins(0, 0, 20, 0)
                    this.layoutParams = layoutParams
                }
                binding.tagContainers.addView(tag)
            }
        }
    }

    private class CourseDiffCallback : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course) =
            oldItem.title == newItem.title

        override fun areContentsTheSame(oldItem: Course, newItem: Course) =
            oldItem.imageUrl == newItem.imageUrl
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.course_list_item, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}