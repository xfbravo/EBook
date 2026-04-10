package com.example.ebook.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    val id :Int =0,
    val bookId: Int,
    val chapterIndex: Int,
    val scrollOffset: Int,
    val name :String
)