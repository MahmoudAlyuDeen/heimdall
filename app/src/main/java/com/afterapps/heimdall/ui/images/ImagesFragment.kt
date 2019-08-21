package com.afterapps.heimdall.ui.images

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.afterapps.heimdall.R
import com.afterapps.heimdall.databinding.FragmentImagesBinding
import com.afterapps.heimdall.util.DrawableRequestListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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

    /** Loading image without a binding adapter to trigger transition after image is loaded */
    private fun initTransition(binding: FragmentImagesBinding) {
        setTransitionName(binding)
        imagesViewModel.collection.observe(viewLifecycleOwner, Observer {
            it?.let {
                loadCollectionImage(it.coverUrl, binding, isTransitioning = true)
                setTransition(onTransitionEnd = {
                    if (activity != null)
                        loadCollectionImage(it.coverUrl, binding, isTransitioning = false)
                })
            }
        })
    }

    /** Sets header image view transition name to the same transition name set in item_collection.xml */
    private fun setTransitionName(binding: FragmentImagesBinding) {
        val fragmentArgs = arguments?.let { ImagesFragmentArgs.fromBundle(it) }
        val collectionId = fragmentArgs?.collectionId
        collectionId?.let { binding.collectionImageView.transitionName = collectionId }
    }

    /** Inflates and sets a moving transition */
    private fun setTransition(onTransitionEnd: () -> Unit) {
        val move = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = move.addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition) {
                onTransitionEnd()
            }

            // unused callbacks

            override fun onTransitionResume(transition: Transition) = Unit
            override fun onTransitionPause(transition: Transition) = Unit
            override fun onTransitionCancel(transition: Transition) = Unit
            override fun onTransitionStart(transition: Transition) = Unit
        })
    }

    /** If transitioning, postpone transition, only load the image from cache, then start postponed transition */
    private fun loadCollectionImage(coverUrl: String, binding: FragmentImagesBinding, isTransitioning: Boolean) {
        if (isTransitioning) {
            postponeEnterTransition()
        }
        val requestOptions = RequestOptions()
                .centerCrop()
                .onlyRetrieveFromCache(isTransitioning)
                .placeholder(R.drawable.ic_collection_placeholder)
                .error(R.drawable.ic_collection_error)

        Glide.with(this)
                .load(coverUrl)
                .apply {
                    if (isTransitioning) {
                        listener(DrawableRequestListener(onDone = { startPostponedEnterTransition() }))
                    }
                }
                .apply(requestOptions)
                .into(binding.collectionImageView)
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
        if (view?.findNavController()?.currentDestination?.id != R.id.imagesFragment) return
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