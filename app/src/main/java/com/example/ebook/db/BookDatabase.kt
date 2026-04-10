package com.example.ebook.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ebook.dao.BookContentDao
import com.example.ebook.dao.BookmarkDao
import com.example.ebook.entity.BookContentEntity
import com.example.ebook.entity.Bookmark

@Database(
    entities = [BookContentEntity::class,Bookmark::class],
    version = 2,
    exportSchema = false
)
abstract class BookDatabase :RoomDatabase() {
    abstract fun bookContentDao():BookContentDao
    abstract fun bookmarkDao():BookmarkDao
    companion object{
        @Volatile private var INSTANCE: BookDatabase? = null
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 创建 bookmarks 表
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS `bookmarks` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `bookId` INTEGER NOT NULL,
                `chapterIndex` INTEGER NOT NULL,
                `scrollOffset` INTEGER NOT NULL,
                `name` TEXT NOT NULL
            )
            """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): BookDatabase {
            return INSTANCE?:synchronized(this) {
                INSTANCE?:Room.databaseBuilder(
                    context.applicationContext,
                    BookDatabase::class.java,
                    "book_database.db"
                ).addMigrations(MIGRATION_1_2).build().also{
                    INSTANCE = it
                }
            }
        }
    }
}