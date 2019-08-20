package com.afterapps.heimdall.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.afterapps.heimdall.databinding.FragmentGalleryBinding
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class GalleryFragment : Fragment() {

    /** GalleryViewModel is initialized lazily. So when this is called, getArguments() != null */
    private val galleryViewModel: GalleryViewModel by viewModel {
        val fragmentArgs = arguments?.let { GalleryFragmentArgs.fromBundle(it) }
        parametersOf(fragmentArgs?.collectionId, fragmentArgs?.initialImageId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentGalleryBinding.inflate(inflater)
        initView(binding)
        initEventObservers(binding)
        return binding.root
    }

    private fun initView(binding: FragmentGalleryBinding) {
        binding.lifecycleOwner = this
        binding.galleryViewModel = galleryViewModel
        val galleryPagerAdapter = GalleryPagerAdapter()
        binding.galleryViewPager.adapter = galleryPagerAdapter
    }

    private fun initEventObservers(binding: FragmentGalleryBinding) {
        binding.galleryViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) = Unit
            override fun onPageScrolled(p: Int, o: Float, x: Int) = Unit

            override fun onPageSelected(position: Int) {
                galleryViewModel.onPageChanged(position)
            }

        })
    }
}