package com.afterapps.heimdall.ui.images

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afterapps.heimdall.databinding.FragmentImagesBinding
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ImagesFragment : Fragment() {

    /** ImagesViewModel is initialized lazily. So when this is called, getArguments() != null */
    private val imagesViewModel: ImagesViewModel by viewModel {
        val fragmentArgs = arguments?.let { ImagesFragmentArgs.fromBundle(it) }
        parametersOf(fragmentArgs?.collectionId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentImagesBinding.inflate(inflater)
        initView(binding)
        return binding.root
    }

    private fun initView(binding: FragmentImagesBinding) {
        binding.lifecycleOwner = this
        binding.imagesViewModel = imagesViewModel
        binding.imagesRecyclerParent.imagesRecycler.adapter = ImagesAdapter(
            ImageListener(imagesViewModel::onImageClick)
        )
    }

}