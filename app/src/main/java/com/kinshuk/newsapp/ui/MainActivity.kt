package com.kinshuk.newsapp.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.kinshuk.newsapp.R
import com.kinshuk.newsapp.databinding.ActivityMainBinding
import com.kinshuk.newsapp.db.ArticeDB
import com.kinshuk.newsapp.repository.NewsRepository
import com.kinshuk.newsapp.viewmodel.NewsViewModel
import com.kinshuk.newsapp.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {
    lateinit var newsViewModel: NewsViewModel
    private lateinit var binding: ActivityMainBinding
    lateinit var newsRepository:NewsRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newsRepository = NewsRepository(ArticeDB(this))
        val viewModelProviderFactory = ViewModelFactory(application, newsRepository)
        newsViewModel = ViewModelProvider(this, viewModelProviderFactory)[NewsViewModel::class.java]

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}