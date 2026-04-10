package com.example.ebook.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.ebook.entity.Bookmark

@Dao
interface BookmarkDao{
    @Insert
    suspend fun insertBookmark(bookmark: Bookmark)

    @Delete
    suspend fun delete(bookmark: Bookmark)

    @Query("SELECT * FROM bookmarks WHERE bookId = :bookId")
    suspend fun getBookmarks(bookId: Int): List<Bookmark>
}