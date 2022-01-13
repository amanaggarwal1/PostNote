package com.amanaggarwal1.pratilipi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amanaggarwal1.pratilipi.databinding.ActivityMainBinding
import com.amanaggarwal1.pratilipi.db.PostDatabase
import com.amanaggarwal1.pratilipi.repository.PostRepository

class MainActivity : AppCompatActivity() {

    lateinit var postDatabase: PostDatabase
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)

    }
}