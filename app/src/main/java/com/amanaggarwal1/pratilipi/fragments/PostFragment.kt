package com.amanaggarwal1.pratilipi.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
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
import java.io.ByteArrayOutputStream
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

        // for animation
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

        // observing image uri to change image in post
        postActivityViewModel.imageUri.observe(viewLifecycleOwner) {
            binding.imageView.setImageURI(it)
        }

        // click listener for saving post
        binding.savePost.setOnClickListener{
            savePost()
        }

        // click listener for sharing post
        binding.sharePost.setOnClickListener {
            sharePost()
        }

        // click listener for back button
        binding.backBtn.setOnClickListener {
            requireView().hideKeyboard()
            postActivityViewModel.imageUri.value = null
            navController.popBackStack()
        }

        // click listener for uploading image
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

        // if it is a new post
        if(post == null){
            binding.lastEdited.text = getString(R.string.new_post)
        }else{
            title.setText(post.title)
            description.setText(post.description)
            lastEdited.text = getString(R.string.edited_on, post.date)
            if(postActivityViewModel.imageUri.value != null &&
                postActivityViewModel.imageUri.value.toString() != ""){
                image.setImageURI(postActivityViewModel.imageUri.value)
            }else {
                image.setImageURI(post.imageUri.toUri())
                postActivityViewModel.imageUri.value = post.imageUri.toUri()
            }
        }



    }

    // function to upload image in post
    private fun uploadImage() {
        requireView().hideKeyboard()
        ImagePicker.with(this)
            .crop()	    			        //Give user option to crop the image
            .compress(1024)			//Final image size will be less than 1 MB
            .start()

        Log.i("TAG", "title = ${post?.title.toString()}  image = ${postActivityViewModel.imageUri.value.toString()} ")

    }

    // function to share post using intents
    private fun sharePost() {

        // message to be sent
        val message = "Hey, see my new post!\n" +
                "${binding.etTitle.text.toString()}\n" +
                binding.etDescription.text.toString()
        requireView().hideKeyboard()


        // sharing only text if there is no image
        if(postActivityViewModel.imageUri.value == null || postActivityViewModel.imageUri.value.toString() == ""){
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }else {

            // converting image to a bitmap
            val bitmap : Bitmap = binding.imageView.drawable.toBitmap()
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(requireContext().contentResolver, bitmap, "tempimage", null)
            val uri = Uri.parse(path)

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/jpg"
            intent.putExtra(Intent.EXTRA_SUBJECT, binding.etTitle.text.toString())
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(intent, "Share via"))
        }

    }

    // function to save / update the post
    private fun savePost() {
        if(binding.etTitle.text.toString().isNotEmpty()){ // save post is title is not empty
            post = args.post
            when(post){
                null -> { // if post id is null it means it's a new post
                    addNewPost()
                }
                else -> { // update existing post
                    updatePost()
                }

            }
            requireView().hideKeyboard()
            navController.popBackStack()
            postActivityViewModel.imageUri.value = null

        }else {
            Toast.makeText(this.context, "Please add a title", Toast.LENGTH_SHORT).show()
        }
    }

    // function to add new post
    private fun addNewPost(){
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

    // function to update an existing post
    private fun updatePost() {

        val imageString = if(postActivityViewModel.imageUri.value == null){
            ""
        }else{
            postActivityViewModel.imageUri.value.toString()
        }

        if(post != null){
            postActivityViewModel.updatePost(
                Post(post!!.id,
                binding.etTitle.text.toString(),
                binding.etDescription.text.toString(),
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