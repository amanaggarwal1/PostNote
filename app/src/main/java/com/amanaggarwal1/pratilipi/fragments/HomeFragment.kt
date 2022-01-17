package com.amanaggarwal1.pratilipi.fragments

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.amanaggarwal1.pratilipi.MainActivity
import com.amanaggarwal1.pratilipi.R
import com.amanaggarwal1.pratilipi.Utils.SwipeToDelete
import com.amanaggarwal1.pratilipi.Utils.hideKeyboard
import com.amanaggarwal1.pratilipi.adapters.PostsAdapter
import com.amanaggarwal1.pratilipi.databinding.FragmentHomeBinding
import com.amanaggarwal1.pratilipi.viewmodels.PostActivityViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var postsAdapter: PostsAdapter

    private val postActivityViewModel:PostActivityViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

       // for animation
        exitTransition = MaterialElevationScale(false).apply {
            duration = 350
        }
        enterTransition = MaterialElevationScale(true).apply {
            duration = 350
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)
        val navController = Navigation.findNavController(view)
        requireView().hideKeyboard()

        recyclerViewDisplay()
        swipeToDelete(binding.postsRv)

        // setting on click listener on add FAB layout to create new post
        binding.addPostFab.setOnClickListener{
            binding.appBarLayout.visibility = View.INVISIBLE
            postActivityViewModel.imageUri.value = null
            navController.navigate(HomeFragmentDirections.actionHomeFragmentToPostFragment())
        }

        // setting on click listener on add FAB plus icon to create new post
        binding.innerFab.setOnClickListener{
            binding.appBarLayout.visibility = View.INVISIBLE
            postActivityViewModel.imageUri.value = null
            navController.navigate(HomeFragmentDirections.actionHomeFragmentToPostFragment())
        }

        // setting text watcher on searchbar to update results as user types
        binding.search.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.noDataImage.isVisible = false
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val s = p0.toString()
                if(s.isNotEmpty()){
                    val query = "%$s%"
                    postActivityViewModel.searchPost(query).observe(viewLifecycleOwner){
                        postsAdapter.submitList(it)
                    }
                }else{
                    observerDataChanges()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

        // removing focus when clicked on search icon on keyboard while typing in searchbar
        binding.search.setOnEditorActionListener{v, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                v.clearFocus()
                requireView().hideKeyboard()
            }
            return@setOnEditorActionListener true

        }

        // setting scroll listener on recycler view to change size of FAB for new post
        binding.postsRv.setOnScrollChangeListener { _, scrollX, scrollY, _, oldSrollY ->
            when{
                (scrollY > oldSrollY) -> {binding.chatFabText.isVisible = false}
                (scrollX == scrollY) -> {binding.chatFabText.isVisible = true}
                else -> {binding.chatFabText.isVisible = true}
            }
        }
    }

    // function for deleting post from database when swiped on a post (left or right)
    private fun swipeToDelete(postsRv: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition // getting position of viewholder
                val post = postsAdapter.currentList[position] // getting pos to be deleted
                var actionButtonTapped = false
                postActivityViewModel.deletePost(post) // deleting the post from database
                binding.search.apply {
                    hideKeyboard()
                    clearFocus()
                }
                if(binding.search.text.toString().isEmpty()){
                    observerDataChanges()
                }

                // showing snackbar with delete message and undo option
                val snackBar = Snackbar.make(
                    requireView(), "Post Deleted",Snackbar.LENGTH_LONG
                ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>(){

                    // if snackbar is dismissed
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                    }

                    // if undo option is chosen
                    override fun onShown(transientBottomBar: Snackbar?) {
                        transientBottomBar?.setAction("Undo"){
                            postActivityViewModel.savePost(post)
                            actionButtonTapped = true
                            binding.noDataImage.isVisible = false
                        }
                        super.onShown(transientBottomBar)
                    }

                }).apply {
                    animationMode = Snackbar.ANIMATION_MODE_FADE
                    setAnchorView(R.id.add_post_fab)
                }

                // setting yellow color to undo button
                snackBar.setActionTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.yellowOrange
                    )
                )
                snackBar.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(postsRv)
    }

    // function to decide spanCount of recycler view based on screen orientation
    private fun recyclerViewDisplay() {
        when(resources.configuration.orientation){
            Configuration.ORIENTATION_PORTRAIT -> setUpRecyclerView(2)
            Configuration.ORIENTATION_LANDSCAPE -> setUpRecyclerView(3)
        }
    }

    // function to setup adapter for recycler view
    private fun setUpRecyclerView(spanCount: Int) {
        binding.postsRv.apply {
            layoutManager = StaggeredGridLayoutManager(spanCount,
            StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            postsAdapter = PostsAdapter()
            postsAdapter.stateRestorationPolicy = RecyclerView.Adapter
                .StateRestorationPolicy.PREVENT_WHEN_EMPTY

            adapter = postsAdapter
            postponeEnterTransition(300L, TimeUnit.MILLISECONDS)
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
        observerDataChanges()

    }

    // function to update posts
    private fun observerDataChanges() {
        postActivityViewModel.getAllPost().observe(viewLifecycleOwner){
            binding.noDataImage.isVisible = it.isEmpty()
            postsAdapter.submitList(it)
        }
    }
}