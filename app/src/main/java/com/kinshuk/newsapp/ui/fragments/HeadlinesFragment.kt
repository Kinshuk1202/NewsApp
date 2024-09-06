package com.kinshuk.newsapp.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kinshuk.newsapp.R
import com.kinshuk.newsapp.adapters.NewsAdapter
import com.kinshuk.newsapp.databinding.FragmentHeadlinesBinding
import com.kinshuk.newsapp.ui.MainActivity
import com.kinshuk.newsapp.util.Constants
import com.kinshuk.newsapp.util.Resource
import com.kinshuk.newsapp.viewmodel.NewsViewModel

class HeadlinesFragment : Fragment(R.layout.fragment_headlines) {
    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var retryButton:Button
    lateinit var errorTxt:TextView
    lateinit var itemHeadlineError:CardView
    private lateinit var binding: FragmentHeadlinesBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeadlinesBinding.bind(view)
        itemHeadlineError = view.findViewById(R.id.itemHeadlinesError)

        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view:View = inflater.inflate(R.layout.item_error,null)

        retryButton = view.findViewById(R.id.retryButton)
        newsViewModel = (activity as MainActivity).newsViewModel
        setupHeadlinesRecycler()
        newsAdapter.setOnItemClickListner {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(R.id.action_headlinesFragment2_to_articleFragment,bundle)
        }

        newsViewModel.headlines.observe(viewLifecycleOwner, Observer { response->
            when(response){
                is Resource.Success<*>->{
                    hideProgressBar()
                    hideErrorMsg()
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles.toList())
                        val totalPages = it.totalResults/Constants.QUERY_PAGE_SIZE
                        isLastPage = newsViewModel.headlinePage == totalPages
                        if(isLastPage)
                            binding.recyclerHeadlines.setPadding(0,0,0,0)
                    }
                }
                is Resource.Error<*>->{
                    hideProgressBar()
                    response.message?.let{
                        Toast.makeText(activity,"Error $it",Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.loading<*>->{
                    showprogressBar()
                }
            }
        })
        retryButton.setOnClickListener{
            newsViewModel.getHeadlines("us")
        }
    }
    var isError = false
    var isLoading  = false
    var isLastPage = false
    var isScrolling = false

    val scrollListner = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val isNoErrors = !isError
            val isNotLoadingAndNotLastPage =  !isLastPage && !isLoading
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >=totalItemCount
            val isNotAtBeg = firstVisibleItemPosition>=0
            val isTotalmoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeg && isTotalmoreThanVisible && isScrolling
            if(shouldPaginate){
                newsViewModel.getHeadlines("us")
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }
    }
    private fun setupHeadlinesRecycler(){
        newsAdapter = NewsAdapter()
        binding.recyclerHeadlines.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@HeadlinesFragment.scrollListner)
        }
    }
    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }
    private fun showprogressBar(){
        isLoading = true
        binding.paginationProgressBar.visibility = View.VISIBLE
    }
    private fun hideErrorMsg(){
        itemHeadlineError.visibility = View.INVISIBLE
        isError = false
    }
    private fun showErrorMsg(message:String){
        itemHeadlineError.visibility = View.VISIBLE
        errorTxt.text = message
        isError = true
    }
}