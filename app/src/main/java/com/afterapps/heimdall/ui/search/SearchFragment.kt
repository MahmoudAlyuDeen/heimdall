package com.afterapps.heimdall.ui.search

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.afterapps.heimdall.R
import com.afterapps.heimdall.databinding.FragmentSearchBinding
import com.afterapps.heimdall.ui.MainActivity
import com.afterapps.heimdall.util.OnActionExpandListener
import com.afterapps.heimdall.util.OnQueryTextListener
import com.afterapps.heimdall.util.OnScrollListener
import com.afterapps.heimdall.util.hideKeyboard
import org.koin.android.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val searchViewModel: SearchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentSearchBinding.inflate(inflater)
        initView(binding)
        initEventObservers(binding)
        return binding.root
    }

    private fun initView(binding: FragmentSearchBinding) {
        binding.lifecycleOwner = this
        binding.searchViewModel = searchViewModel
        if (activity is MainActivity) {
            (activity as MainActivity).setSupportActionBar(binding.searchToolbar)
        }
        binding.resultsRecycler.adapter = ResultsAdapter(
                ResultListener(searchViewModel::onResultClick)
        )
    }

    private fun initEventObservers(binding: FragmentSearchBinding) {
        searchViewModel.eventOpenInBrowser.observe(viewLifecycleOwner, Observer {
            it?.let {
                openImageInBrowser(it)
                searchViewModel.onOpenInBrowserDone()
            }
        })
        binding.resultsRecycler.addOnScrollListener(OnScrollListener { hideKeyboard(activity) })
    }

    private fun openImageInBrowser(websiteUrl: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl)))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)
        val searchMenuItem = menu.findItem(R.id.action_search)
        initSearchView(searchMenuItem)
    }

    private fun initSearchView(searchMenuItem: MenuItem) {
        val searchView = SearchView((activity as AppCompatActivity).supportActionBar?.themedContext ?: context)
        searchMenuItem.apply {
            setOnActionExpandListener(OnActionExpandListener { activity?.onBackPressed() })
            actionView = searchView
            expandActionView()
        }
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.setOnQueryTextListener(OnQueryTextListener {
            searchView.clearFocus()
            searchViewModel.onQuerySubmit(it)
        })
        searchViewModel.query.observe(viewLifecycleOwner, Observer {
            it?.let {
                searchView.setQuery(it, false)
                searchView.clearFocus()
            }
        })
    }
}