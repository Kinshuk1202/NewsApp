package com.kinshuk.newsapp.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kinshuk.newsapp.models.Article
import com.kinshuk.newsapp.models.NewsResponse
import com.kinshuk.newsapp.repository.NewsRepository
import com.kinshuk.newsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.util.Locale.IsoCountryCode

class NewsViewModel(app:Application, val newsRepository: NewsRepository):   AndroidViewModel(app) {

    val headlines : MutableLiveData<Resource<NewsResponse>>  = MutableLiveData()
    var headlinePage = 1
    var headlineResponse : NewsResponse ?= null
    val search : MutableLiveData<Resource<NewsResponse>>  = MutableLiveData()
    var searchPage = 1
    var searchResponse : NewsResponse ?= null

    var newSearchQuery: String ?= null
    var oldSearchQuery: String ?= null

    init {
        getHeadlines(countryCode = "us")
    }

    fun getHeadlines(countryCode:String) = viewModelScope.launch {
        headlinesInternet(countryCode)
    }
    fun searchNews(query:String) = viewModelScope.launch {
        searchInternet(query)
    }
    private fun handleHeadlineResponse(response: Response<NewsResponse>):Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body().let {
                headlinePage++
                if(headlineResponse == null)
                    headlineResponse = it
                else{
                    val oldArticles = headlineResponse?.articles
                    val newArticles = it?.articles
                    oldArticles?.addAll(newArticles!!)
                }
                return Resource.Success(headlineResponse ?: it!!)
            }
        }
        return Resource.Error(response.message())
    }
    private fun handleSearchResponse(response: Response<NewsResponse>):Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body().let {
                if(searchResponse == null || newSearchQuery!=oldSearchQuery){
                    searchPage = 1;
                    oldSearchQuery = newSearchQuery
                    searchResponse = it
                }
                else{
                    searchPage++
                    val oldArticles = searchResponse?.articles
                    val newArticles = it?.articles
                    oldArticles?.addAll(newArticles!!)
                }
                return Resource.Success(searchResponse ?: it!!)
            }
        }
        return Resource.Error(response.message())
    }
    fun addToFavourites(article:Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }
    fun getFavNews() = newsRepository.getAllArtices()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArtice(article)
    }
    fun internetConnection(context:Context):Boolean{
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when{
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI)->true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)->true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->true
                    else -> false
                }
            }?:false
        }
    }
    private suspend fun headlinesInternet(countryCode: String){
        headlines.postValue(Resource.loading())
        try{
            if(internetConnection(this.getApplication())){
                val response = newsRepository.getHeadlines(countryCode,headlinePage)
                headlines.postValue(handleHeadlineResponse(response))
            }
            else{
                headlines.postValue(Resource.Error("No Internet!"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException->headlines.postValue(Resource.Error("Unable to connect to internet"))
                else -> headlines.postValue(Resource.Error("No Signal"))
            }
        }
    }
    private suspend fun searchInternet(searchQuery: String){
        newSearchQuery = searchQuery
        search.postValue(Resource.loading())
        try{
            if(internetConnection(this.getApplication())){
                val response = newsRepository.searchNews(searchQuery,searchPage)
                search.postValue(handleSearchResponse(response))
            }
            else{
                search.postValue(Resource.Error("No Internet!"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException->search.postValue(Resource.Error("Unable to connect to internet"))
                else -> search.postValue(Resource.Error("No Signal"))
            }
        }
    }


}