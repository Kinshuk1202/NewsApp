package com.kinshuk.newsapp.repository

import com.kinshuk.newsapp.api.RetrofitInstance
import com.kinshuk.newsapp.db.ArticeDB
import com.kinshuk.newsapp.models.Article

class NewsRepository(val db:ArticeDB) {
    suspend fun getHeadlines(countryCode:String,pageNumber: Int) =
            RetrofitInstance.api.getHeadlines(countryCode,pageNumber)
    suspend fun searchNews(searchQuery:String,pageNumber: Int) =
            RetrofitInstance.api.searchForNews(searchQuery,pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)
    fun getAllArtices() = db.getArticleDao().getAllArtices()

    suspend fun deleteArtice(article: Article) = db.getArticleDao().deleteArtice(article)
}