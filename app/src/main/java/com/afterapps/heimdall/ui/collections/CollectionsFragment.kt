package com.afterapps.heimdall.ui.collections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.afterapps.heimdall.databinding.FragmentCollectionsBinding
import org.koin.android.viewmodel.ext.android.viewModel

class CollectionsFragment : Fragment() {

    private val collectionsViewModel: CollectionsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentCollectionsBinding.inflate(inflater)
        initView(binding)
        initEventListeners()
        return binding.root
    }

    private fun initView(binding: FragmentCollectionsBinding) {
        binding.lifecycleOwner = this
        binding.collectionsViewModel = collectionsViewModel
        binding.collectionsRecycler.adapter = CollectionsAdapter(
            CollectionListener(collectionsViewModel::onCollectionClick)
        )
    }

    private fun initEventListeners() {
        collectionsViewModel.eventNavigateToImages.observe(viewLifecycleOwner, Observer {
            it?.let {
                navigateToImagesFragment(it)
            }
        })
    }

    private fun navigateToImagesFragment(collectionId: String?) {
        collectionId?.let {
            view?.findNavController()
                ?.navigate(CollectionsFragmentDirections.actionCollectionsFragmentToImagesFragment(collectionId))
            collectionsViewModel.onNavigationToImagesDone()
        }
    }

}