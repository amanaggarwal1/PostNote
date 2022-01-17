package com.amanaggarwal1.pratilipi.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.amanaggarwal1.pratilipi.model.Post

@Dao
interface DAO {

    // inserting an item to database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPost(post: Post)

    // updating an existing from database
    @Update
    suspend fun updatePost(post: Post)

    // query to retrieve all posts (sorted by date in descending order)
    @Query("SELECT * FROM Post ORDER BY date DESC")
    fun getAllPosts(): LiveData<List<Post>>

    // query to search all posts with matching contents (sorted by date in descending order)
    @Query("SELECT * FROM post WHERE title LIKE :query OR description LIKE :query OR date LIKE :query ORDER BY date DESC")
    fun searchPost(query: String): LiveData<List<Post>>

    // deleting a post from database
    @Delete
    suspend fun deletePost(post: Post)
}