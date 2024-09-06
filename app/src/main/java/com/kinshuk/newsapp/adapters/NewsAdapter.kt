package com.kinshuk.newsapp.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kinshuk.newsapp.R
import com.kinshuk.newsapp.models.Article
import kotlinx.coroutines.GlobalScope
import org.jetbrains.annotations.Async
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_DATE_TIME

class NewsAdapter:RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {
    inner class ArticleViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    lateinit var articleImage:ImageView
    lateinit var articleTitle:TextView
    lateinit var articleSource:TextView
    lateinit var articleDescription:TextView
    lateinit var articleDateTime:TextView
    private val differCallback = object :DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }
    val  differ = AsyncListDiffer(this,differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_news,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    private var onItemClickListener: ((Article)->Unit) ?= null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        articleImage = holder.itemView.findViewById(R.id.articleImage)
        articleSource = holder.itemView.findViewById(R.id.articleSource)
        articleTitle = holder.itemView.findViewById(R.id.articleTitle)
        articleDescription = holder.itemView.findViewById(R.id.articleDescription)
        articleDateTime = holder.itemView.findViewById(R.id.articleDateTime)
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(articleImage)
            articleSource.text = article.source!!.name
            articleTitle.text = article!!.title
            articleDescription.text = article!!.description
            articleDateTime.text = article!!.publishedAt?.let { convertUtcToLocalTime(it) }

            setOnClickListener{
                onItemClickListener ?.let{
                    it(article)
                }
            }
        }
    }
    fun setOnItemClickListner(listner:(Article)->Unit){
        onItemClickListener = listner
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertUtcToLocalTime(utcTime: String): String {
        // Parse the UTC time string
        val utcDateTime = OffsetDateTime.parse(utcTime, ISO_DATE_TIME)

        // Convert to local time zone
        val localDateTime = utcDateTime.atZoneSameInstant(ZoneId.systemDefault())

        // Define your desired format
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        // Format the local time
        return localDateTime.format(formatter)
    }
}