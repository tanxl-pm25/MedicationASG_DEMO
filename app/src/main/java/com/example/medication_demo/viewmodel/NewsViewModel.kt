package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medication_demo.model.NewsArticle
import com.example.medication_demo.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    private val repository =
        NewsRepository()

    private val _news =
        MutableStateFlow<List<NewsArticle>>(
            emptyList()
        )

    val news: StateFlow<List<NewsArticle>> =
        _news.asStateFlow()

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    fun loadNews() {

        viewModelScope.launch {

            _isLoading.value = true
            _errorMessage.value = null

            try {

                _news.value =
                    repository.getHealthNews()

            } catch (e: Exception) {

                _errorMessage.value =
                    e.message
                        ?: "Failed to load news."

            } finally {

                _isLoading.value = false
            }
        }
    }
}