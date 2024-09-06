package com.kinshuk.newsapp.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kinshuk.newsapp.R
import com.kinshuk.newsapp.adapters.NewsAdapter
import com.kinshuk.newsapp.databinding.FragmentFavouritesBinding
import com.kinshuk.newsapp.ui.MainActivity
import com.kinshuk.newsapp.viewmodel.NewsViewModel
import com.kinshuk.newsapp.viewmodel.ViewModelFactory

class FavouritesFragment : Fragment(R.layout.fragment_favourites) {

    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var binding: FragmentFavouritesBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavouritesBinding.bind(view)

        val application = requireActivity().application
        val repository = (requireActivity() as MainActivity).newsRepository
        newsViewModel = ViewModelProvider(this, ViewModelFactory(application, repository)).get(NewsViewModel::class.java)
        setupFavRecyclerView()
        newsAdapter.setOnItemClickListner {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(R.id.action_favouritesFragment2_to_articleFragment,bundle)
        }
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                                                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val  pos = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[pos]
                newsViewModel.deleteArticle(article)
                Snackbar.make(view,"Removed from favourites",Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        newsViewModel.addToFavourites(article)
                    }
                    show()
                }
            }
        }
        ItemTouchHelper(itemTouchHelper).apply {
            attachToRecyclerView(binding.recyclerFavourites)
        }
        newsViewModel.getFavNews().observe(viewLifecycleOwner, Observer {
            newsAdapter.differ.submitList((it))
        })
    }
    private fun setupFavRecyclerView(){
        newsAdapter = NewsAdapter()
        binding.recyclerFavourites.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}