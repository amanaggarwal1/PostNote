package com.amanaggarwal1.pratilipi.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.amanaggarwal1.pratilipi.model.Post

@Database(entities = [Post::class], version = 1, exportSchema = false)
abstract class PostDatabase: RoomDatabase() {
   abstract fun getPostDao(): DAO

   companion object{
       // creating instance of database
       // using volatile annotation to reading database from a specific location only
       // and creating caches of database
       @Volatile private var instance: PostDatabase? = null
       private val LOCK = Any()

       // synchronised is used to restrict multiple threads to access the instance of database at same time
       // only the thread which have lock can access the instance
       // all other threads will be waiting in line
       operator fun invoke(context: Context) = instance?: synchronized(LOCK){
           instance ?: createDatabase(context).also{
               instance = it
           }
       }

       // function to create database
       private fun createDatabase(context: Context) = Room.databaseBuilder(
           context.applicationContext,
           PostDatabase::class.java,
           "posts_database"
       ).build()

       }
}