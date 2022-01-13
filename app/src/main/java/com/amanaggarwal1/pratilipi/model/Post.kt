package com.amanaggarwal1.pratilipi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Post(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val title: String,
    val description: String,
    val date: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var image:ByteArray
): Serializable