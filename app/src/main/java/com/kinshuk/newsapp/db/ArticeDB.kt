package com.kinshuk.newsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.kinshuk.newsapp.models.Article

@Database(
    entities = [Article::class],
    version = 1

)
@TypeConverters(Converters::class)

abstract class ArticeDB:RoomDatabase() {

    abstract fun getArticleDao():ArticleDao
    companion object{
        @Volatile
        private var instance:ArticeDB?=null
        private var LOCK = Any()
        operator fun invoke(context: Context) = instance?: synchronized(LOCK){
           instance ?: createDataBase(context).also {
               instance = it
           }
        }

        private fun createDataBase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticeDB::class.java,
                name = "article_db.db"
            ).build()
    }
    }
