package com.example.medication_demo.repository

import com.example.medication_demo.BuildConfig
import com.example.medication_demo.model.NewsArticle
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class NewsRepository {

    private val client =
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }

    suspend fun getHealthNews(): List<NewsArticle> {

        val response =
            client.get(
                "https://newsdata.io/api/1/latest"
            ) {
                parameter(
                    "apikey",
                    BuildConfig.NEWSDATA_API_KEY
                )

                parameter(
                    "q",
                    "health OR medicine OR medication"
                )

                parameter(
                    "language",
                    "en"
                )
            }.body<NewsResponse>()

        return response.results
            .mapNotNull { item ->

                val link =
                    item.link
                        ?: return@mapNotNull null

                NewsArticle(
                    title = item.title ?: "Untitled",
                    description = item.description,
                    sourceName = item.sourceName,
                    imageUrl = item.imageUrl,
                    articleUrl = link,
                    publishedAt = item.pubDate
                )
            }
            .distinctBy {
                it.articleUrl
            }
    }
}

@Serializable
private data class NewsResponse(
    val results: List<NewsItem> = emptyList()
)

@Serializable
private data class NewsItem(
    val title: String? = null,
    val description: String? = null,
    val link: String? = null,

    @SerialName("image_url")
    val imageUrl: String? = null,

    @SerialName("source_name")
    val sourceName: String? = null,

    @SerialName("pubDate")
    val pubDate: String? = null
)