package com.example.ebook.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_content")
data class BookContentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookId: Int,
    val chapterIndex: Int,
    val title:String,
    val content: String,
)