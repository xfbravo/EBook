package com.example.ebook.Reading

import android.content.Context
import androidx.core.content.edit

//TODO sharedPreference 来保存阅读进度
object ReadingProgressManager {
    private const val PREFS_NAME = "reading_progress"

    fun saveProgress(context: Context, bookId:Int, chapterIndex:Int, scrollOffset:Int){
        val prefs= context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit() {
            putInt("${bookId}_chapter", chapterIndex)
            putInt("${bookId}_offset", scrollOffset)
            apply()
        }
    }

    fun getProgress(context: Context, bookId: Int): Pair<Int, Int>? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return if (prefs.contains("${bookId}_chapter")) {
            val chapter = prefs.getInt("${bookId}_chapter", 0)
            val offset = prefs.getInt("${bookId}_offset", 0)
            Pair(chapter, offset)
        } else {
            null
        }
    }

    fun clearProgress(context: Context, bookId:Int){
        val prefs= context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit() {
            remove("${bookId}_chapter")
            remove("${bookId}_offset")
            apply()
        }
    }
}