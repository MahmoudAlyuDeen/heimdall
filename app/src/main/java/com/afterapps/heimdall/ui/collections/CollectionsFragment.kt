package com.afterapps.heimdall.ui.collections

import android.os.Bundle
import android.util.Log
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
        binding.lifecycleOwner = this
        binding.collectionsViewModel = collectionsViewModel
        collectionsViewModel.collections.observe(viewLifecycleOwner, Observer {
            Log.d("onCollectionsChanged", it.toString())
        })
        return binding.root
    }

    fun navigateToImagesFragment(collectionId: String) {
        view?.findNavController()
            ?.navigate(CollectionsFragmentDirections.actionCollectionsFragmentToImagesFragment(collectionId))
    }

}