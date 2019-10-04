package com.afterapps.heimdall.ui.collections

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import com.afterapps.heimdall.R
import com.afterapps.heimdall.databinding.FragmentCollectionsBinding
import com.afterapps.heimdall.domain.Collection
import com.afterapps.heimdall.ui.MainActivity
import org.koin.android.viewmodel.ext.android.viewModel

class CollectionsFragment : Fragment() {

    private val collectionsViewModel: CollectionsViewModel by viewModel()

    /** Holds reference to clicked collection ImageView to use in shared transition. */
    private lateinit var clickedCollectionImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentCollectionsBinding.inflate(inflater)
        initView(binding)
        initReturnTransition(binding)
        initEventObservers()
        return binding.root
    }

    private fun initView(binding: FragmentCollectionsBinding) {
        binding.lifecycleOwner = this
        binding.collectionsViewModel = collectionsViewModel
        if (activity is MainActivity) {
            (activity as MainActivity).setSupportActionBar(binding.collectionsToolbar)
        }
        binding.collectionsRecycler.adapter = CollectionsAdapter(
            CollectionListener { collection: Collection, imageView: ImageView ->
                clickedCollectionImageView = imageView
                collectionsViewModel.onCollectionClick(collection)
            }
        )
    }

    /** Delays transition until collections recycler is about to be drawn to start a smooth return transition */
    private fun initReturnTransition(binding: FragmentCollectionsBinding) {
        binding.collectionsRecycler.apply {
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener { startPostponedEnterTransition(); true }
        }
    }

    private fun initEventObservers() {
        collectionsViewModel.eventNavigateToImages.observe(viewLifecycleOwner, Observer {
            it?.let {
                navigateToImagesFragment(it)
                collectionsViewModel.onNavigationToImagesDone()
            }
        })
        collectionsViewModel.eventNavigateToSearch.observe(viewLifecycleOwner, Observer {
            it?.let {
                navigateToSearchFragment()
                collectionsViewModel.onNavigationToSearchDone()
            }
        })
    }

    private fun navigateToImagesFragment(collectionId: String) {
        if (view?.findNavController()?.currentDestination?.id != R.id.collectionsFragment) return
        val extras = FragmentNavigator.Extras
            .Builder()
            .addSharedElement(clickedCollectionImageView, clickedCollectionImageView.transitionName)
            .build()
        view?.findNavController()?.navigate(CollectionsFragmentDirections.navigateToImages(collectionId), extras)
    }

    private fun navigateToSearchFragment() {
        if (view?.findNavController()?.currentDestination?.id != R.id.collectionsFragment) return
        view?.findNavController()?.navigate(CollectionsFragmentDirections.navigateToSearch())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_collections, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_start_search -> {
                collectionsViewModel.onSearchClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}