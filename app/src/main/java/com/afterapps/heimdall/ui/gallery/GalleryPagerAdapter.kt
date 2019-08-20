package com.afterapps.heimdall.ui.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.afterapps.heimdall.databinding.PageGalleryBinding
import com.afterapps.heimdall.domain.Image

class GalleryPagerAdapter : PagerAdapter() {

    var images: List<Image> = emptyList()

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        val binding = PageGalleryBinding.inflate(LayoutInflater.from(container.context))
        binding.image = images[position]
        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }
}