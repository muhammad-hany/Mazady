package com.example.mazady.view.presenter

import androidx.lifecycle.ViewModel
import com.example.mazady.data.repository.Repository

class PresenterViewModel(private val repository: Repository) : ViewModel() {

    fun getSubmittedList() = repository.getSubmittedItems()
}