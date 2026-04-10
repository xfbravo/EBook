package com.example.ebook.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ebook.entity.BookContentEntity

//TODO 查询所有章节标题用到的数据类
data class ChapterInfo(
    val chapterIndex: Int,
    val title: String
)

@Dao
interface BookContentDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bookContent: List<BookContentEntity>)

    @Query("SELECT * FROM book_content WHERE bookId = :bookId ORDER BY chapterIndex ASC")
    suspend fun getAllChapters(bookId: Int): List<BookContentEntity>

    @Query("SELECT * FROM book_content WHERE bookId = :bookId AND chapterIndex = :chapterIndex")
    suspend fun getChapterContent(bookId: Int, chapterIndex: Int): BookContentEntity?

    @Query("SELECT COUNT(*) FROM book_content WHERE bookId = :bookId")
    suspend fun getChapterCount(bookId: Int): Int

    @Query("DELETE FROM book_content WHERE bookId = :bookId")
    suspend fun deleteByBook(bookId: Int)

    @Query("SELECT chapterIndex,title FROM book_content WHERE bookId = :bookId ORDER BY chapterIndex ASC")
    suspend fun getAllChapterTitles(bookId: Int): List<ChapterInfo>
}