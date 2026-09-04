package com.example.medication_demo.model

data class NewsArticle(
    val title: String,
    val description: String?,
    val sourceName: String?,
    val imageUrl: String?,
    val articleUrl: String,
    val publishedAt: String?
)