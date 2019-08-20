package com.afterapps.heimdall.ui.collections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import com.afterapps.heimdall.databinding.FragmentCollectionsBinding
import com.afterapps.heimdall.domain.Collection
import org.koin.android.viewmodel.ext.android.viewModel

class CollectionsFragment : Fragment() {

    private val collectionsViewModel: CollectionsViewModel by viewModel()

    /** Holds reference to clicked collection ImageView to use in shared transition. */
    private lateinit var clickedCollectionImageView: ImageView

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
            viewTreeObserver
                .addOnPreDrawListener {
                    startPostponedEnterTransition()
                    true
                }
        }
    }

    private fun initEventObservers() {
        collectionsViewModel.eventNavigateToImages.observe(viewLifecycleOwner, Observer {
            it?.let {
                navigateToImagesFragment(it)
            }
        })
    }

    private fun navigateToImagesFragment(collectionId: String) {
        val extras = FragmentNavigator.Extras
            .Builder()
            .addSharedElement(clickedCollectionImageView, clickedCollectionImageView.transitionName)
            .build()
        val directions = CollectionsFragmentDirections.actionCollectionsFragmentToImagesFragment(collectionId)
        view?.findNavController()?.navigate(directions, extras)
        collectionsViewModel.onNavigationToImagesDone()
    }

}