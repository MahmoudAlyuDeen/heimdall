package com.afterapps.heimdall.ui.images

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.transition.TransitionInflater
import com.afterapps.heimdall.R
import com.afterapps.heimdall.databinding.FragmentImagesBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class ImagesFragment : Fragment() {

    /** ImagesViewModel is initialized lazily. So when this is called, getArguments() != null */
    private val imagesViewModel: ImagesViewModel by viewModel {
        val fragmentArgs = arguments?.let { ImagesFragmentArgs.fromBundle(it) }
        parametersOf(fragmentArgs?.collectionId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentImagesBinding.inflate(inflater)
        initView(binding)
        initTransition(binding)
        initEventObservers()
        return binding.root
    }

    private fun initView(binding: FragmentImagesBinding) {
        binding.lifecycleOwner = this
        binding.imagesViewModel = imagesViewModel
        binding.imagesRecyclerParent.imagesRecycler.adapter = ImagesAdapter(
            ImageListener(imagesViewModel::onImageClick)
        )
        (activity as AppCompatActivity).setSupportActionBar(binding.imagesToolbar)
        imagesViewModel.collection.observe(viewLifecycleOwner, Observer {
            it?.let { activity?.title = it.name }
        })
    }

    /**
     *  Sets header image view transition name to the same transition name set by in item_collection.xml
     *  Loads header image without a binding adapter, adding add Glide.RequestListener to trigger a smooth transition
     * */
    private fun initTransition(binding: FragmentImagesBinding) {
        postponeEnterTransition()
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        val fragmentArgs = arguments?.let { ImagesFragmentArgs.fromBundle(it) }
        val collectionId = fragmentArgs?.collectionId
        collectionId?.let { binding.collectionImageView.transitionName = collectionId }
        imagesViewModel.collection.observe(viewLifecycleOwner, Observer {
            val requestOptions = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_collection_placeholder)
                .error(R.drawable.ic_collection_error)

            Glide.with(this)
                .load(it?.coverUrl)
                .listener(getGlideRequestListener())
                .apply(requestOptions)
                .into(binding.collectionImageView)
        })
    }

    /** Calling startPostponedEnterTransition() after Glide is done loading the image */
    private fun getGlideRequestListener(): RequestListener<Drawable> = object : RequestListener<Drawable> {
        override fun onResourceReady(r: Drawable?, m: Any?, t: Target<Drawable>?, d: DataSource?, i: Boolean): Boolean {
            startPostponedEnterTransition()
            return false
        }

        override fun onLoadFailed(e: GlideException?, m: Any?, t: Target<Drawable>?, i: Boolean): Boolean {
            startPostponedEnterTransition()
            return false
        }
    }

    private fun initEventObservers() {
        imagesViewModel.eventNavigateToGallery.observe(viewLifecycleOwner, Observer {
            it?.let {
                navigateToGalleryFragment(it)
                imagesViewModel.onNavigationToGalleryDone()
            }
        })

        imagesViewModel.eventOpenInBrowser.observe(viewLifecycleOwner, Observer {
            it?.let {
                openCollectionInBrowser(it)
                imagesViewModel.onOpenInBrowserDone()
            }
        })
    }

    private fun navigateToGalleryFragment(ids: Pair<String, String>) {
        view?.findNavController()
            ?.navigate(ImagesFragmentDirections.actionImagesFragmentToGalleryFragment(ids.first, ids.second))
    }

    private fun openCollectionInBrowser(collectionUrl: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(collectionUrl)))
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_images, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_browser -> {
                onOpenInBrowserClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onOpenInBrowserClick() {
        imagesViewModel.onOpenInBrowserClick()
    }
}