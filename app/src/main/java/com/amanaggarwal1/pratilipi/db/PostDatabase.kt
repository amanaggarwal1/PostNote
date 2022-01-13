package com.amanaggarwal1.pratilipi.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.amanaggarwal1.pratilipi.model.Post

@Database(entities = [Post::class], version = 1, exportSchema = false)
abstract class PostDatabase: RoomDatabase() {
   abstract fun getPostDao(): DAO

   companion object{
       @Volatile private var instance: PostDatabase? = null
       private val LOCK = Any()

       


   }
}