package com.afterapps.heimdall.ui.images

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.afterapps.heimdall.databinding.FragmentImagesBinding
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ImagesFragment : Fragment() {

    private val collectionId = "203007992"
    private val imagesViewModel: ImagesViewModel by viewModel { parametersOf(collectionId) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentImagesBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.imagesViewModel = imagesViewModel
        imagesViewModel.images.observe(viewLifecycleOwner, Observer {
            Log.d("onImagesChanged", it.toString())
        })
        return binding.root
    }

}