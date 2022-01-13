package com.amanaggarwal1.pratilipi.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.amanaggarwal1.pratilipi.MainActivity
import com.amanaggarwal1.pratilipi.R
import com.amanaggarwal1.pratilipi.Utils.hideKeyboard
import com.amanaggarwal1.pratilipi.databinding.FragmentPostBinding
import com.amanaggarwal1.pratilipi.model.Post
import com.amanaggarwal1.pratilipi.viewmodels.PostActivityViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class PostFragment : Fragment(R.layout.fragment_post) {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentPostBinding
    private lateinit var result: String

    private var post: Post? = null
    private val currentDate = SimpleDateFormat.getInstance().format(Date())
    private val postActivityViewModel: PostActivityViewModel by activityViewModels()

    private val args: PostFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val animation = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragment
            scrimColor = Color.TRANSPARENT
            duration = 300L
        }

        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPostBinding.bind(view)
        navController = Navigation.findNavController(view)

        postActivityViewModel.imageUri.observe(viewLifecycleOwner) {
            binding.imageView.setImageURI(it)
        }

        binding.savePost.setOnClickListener{
            savePost()
        }

        binding.sharePost.setOnClickListener {
            sharePost()
        }

        ViewCompat.setTransitionName(
            binding.postContentFragmentParent,
            "recyclerView_${args.post?.id}"
        )

        binding.backBtn.setOnClickListener {
            requireView().hideKeyboard()
            navController.popBackStack()
        }

        binding.imageUpload.setOnClickListener{
            uploadImage()
        }

        setUpPost()
    }

    private fun setUpPost() {
        val post = args.post
        val title = binding.etTitle
        val description = binding.etDescription
        val lastEdited = binding.lastEdited
        val image = binding.imageView

        if(post == null){
            binding.lastEdited.text = getString(R.string.edited_on,
                SimpleDateFormat.getDateInstance().format(Date()))
        }else{
            title.setText(post.title)
            description.setText(post.description)
            lastEdited.text = getString(R.string.edited_on, post.date)
            image.setImageURI(post.imageUri.toUri())
        }
    }

    fun uploadImage() {
        ImagePicker.with(this)
            .crop()	    			//Crop image(Optional), Check Customization for more option
            .compress(1024)			//Final image size will be less than 1 MB(Optional)
            //.maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
            .start()
    }

    private fun sharePost() {


        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/jpeg"
        intent.putExtra(Intent.EXTRA_SUBJECT, binding.etTitle.text.toString())
        intent.putExtra(Intent.EXTRA_TEXT, binding.etDescription.text.toString())
        intent.putExtra(Intent.EXTRA_STREAM, postActivityViewModel.imageUri.value)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(intent, "Share via"))

    }

    private fun savePost() {
        if(binding.etTitle.text.toString().isNotEmpty() && binding.etDescription.text.toString().isNotEmpty()){
            post = args.post
            when(post){
                null -> {
                    val imageString = if(postActivityViewModel.imageUri.value == null){
                        ""
                    }else{
                        postActivityViewModel.imageUri.value.toString()
                    }
                    postActivityViewModel.savePost(
                        Post(0,
                            binding.etTitle.text.toString(),
                            binding.etDescription.text.toString(),
                            currentDate,
                            imageString
                        )
                    )

                    result = "Post Saved"
                    setFragmentResult("key", bundleOf("bundleKey" to result)
                    )
                }
                else -> {
                    updatePost()
                    navController.popBackStack()
                }

            }
        }

        requireView().hideKeyboard()
        navController.popBackStack()
        postActivityViewModel.imageUri.value = null


    }

    private fun updatePost() {

        val imageString = if(postActivityViewModel.imageUri.value == null){
            ""
        }else{
            postActivityViewModel.imageUri.value.toString()
        }

        if(post != null){
            postActivityViewModel.updatePost(
                Post(post!!.id,
                binding.etTitle.toString(),
                binding.etDescription.toString(),
                currentDate,
                imageString)

            )
        }

        postActivityViewModel.imageUri.value =  null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        postActivityViewModel.imageUri.value = intent?.data


        binding.imageView.setImageURI(postActivityViewModel.imageUri.value)



    }
}