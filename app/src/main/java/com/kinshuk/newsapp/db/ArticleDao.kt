package com.kinshuk.newsapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kinshuk.newsapp.models.Article

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article):Long   //pk of article

    @Query("SELECT * FROM articles")
    fun getAllArtices():LiveData<List<Article>>

    @Delete
    suspend fun deleteArtice(article: Article)
}