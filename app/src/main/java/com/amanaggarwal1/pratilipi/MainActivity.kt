package com.amanaggarwal1.pratilipi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.amanaggarwal1.pratilipi.databinding.ActivityMainBinding
import com.amanaggarwal1.pratilipi.db.PostDatabase
import com.amanaggarwal1.pratilipi.repository.PostRepository
import com.amanaggarwal1.pratilipi.viewmodels.PostActivityViewModel
import com.amanaggarwal1.pratilipi.viewmodels.PostActivityViewModelFactory
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var postActivityViewModel: PostActivityViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)


        try{
            setContentView(binding.root)
            val postRepository = PostRepository(PostDatabase(this))
            val homeFragmentViewModelFactory = PostActivityViewModelFactory(postRepository)
            postActivityViewModel = ViewModelProvider(this,
                homeFragmentViewModelFactory)[PostActivityViewModel::class.java]
        }catch (e: Exception){
            Log.d("TAG", "Error")
        }
    }
}