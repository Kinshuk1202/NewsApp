package com.kinshuk.newsapp.api

import com.kinshuk.newsapp.models.NewsResponse
import com.kinshuk.newsapp.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("v2/top-headlines")
    suspend fun getHeadlines(
        @Query("country")
        countryCode: String = "us",
        @Query("page")
        pageNumber : Int = 1,
        @Query("apikey")
        apikey :String = API_KEY
    ) : Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber : Int = 1,
        @Query("apikey")
        apikey :String = API_KEY
    ) : Response<NewsResponse>
}