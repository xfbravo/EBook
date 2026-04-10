package com.example.ebook

import nl.siegmann.epublib.epub.EpubReader
import nl.siegmann.epublib.domain.Book as EpubBook
import java.io.InputStream
import com.example.ebook.Reading.Book
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ebook.ui.theme.EBookTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import android.content.Context
import android.content.SharedPreferences
import com.example.ebook.db.BookDatabase
import com.example.ebook.entity.BookContentEntity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.core.content.edit

class MainActivity : ComponentActivity() {
    private lateinit var chapters:List<BookContentEntity>
    private lateinit var db: BookDatabase
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //删除数据库
//        this.deleteDatabase("book_database.db")
        sharedPreferences=getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isFirstLaunch= sharedPreferences.getBoolean("first_launch", true)
        db= BookDatabase.getInstance(this)
        if (isFirstLaunch) {
            // 第一次启动，导入数据
            val bookTitles = listOf("sherlock_holmes", "球状闪电", "乡村教师")
            bookTitles.forEach { bookTitle ->
                lifecycleScope.launch {
                    val inputStream = this@MainActivity.assets.open("${bookTitle}.epub")
                    importEpubIntoDatabase(this@MainActivity, bookTitle.hashCode(), inputStream)
                }
            }

            // 更新标记为非第一次启动
            sharedPreferences.edit() { putBoolean("first_launch", false) }
        }
        enableEdgeToEdge()
        setContent {
            EBookTheme {
                Library()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}


suspend fun importEpubIntoDatabase(context: Context, bookId: Int, epubInputStream: InputStream) {
    val db = BookDatabase.getInstance(context)
    val dao = db.bookContentDao()

    val epubBook: EpubBook = EpubReader().readEpub(epubInputStream)
    val toc = epubBook.tableOfContents.tocReferences

    val entities = toc.mapIndexedNotNull { index, tocRef ->
        try {
            val rawHtml = String(tocRef.resource.data, Charsets.UTF_8)
            val plainText = HtmlCompat.fromHtml(rawHtml, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                .replace(Regex("\\n{2,}"), "\n\n")  // 规范段落间距
                .trim()

            BookContentEntity(
                bookId = bookId,
                chapterIndex = index,
                title = tocRef.title ?: "第${index + 1}章",
                content = plainText
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null // 忽略异常章节
        }
    }

    dao.deleteByBook(bookId)  // 清除之前已有的同一本书的章节
    dao.insertAll(entities)
}

@Composable
fun Library() {
    val context= LocalContext.current
    val books=listOf(
        //TODO 书籍列表
        Book("球状闪电", R.drawable.lightning_ball),
        Book("乡村教师", R.drawable.country_teacher),
        Book("sherlock_holmes", R.drawable.sherlock_homes),
    )

    Column {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Library",
            fontSize = 50.sp,
        )
        BookList(books = books) { book ->
            val intent = Intent(context, BookActivity::class.java)
            val newBook = Book(
                //TODO 传递书籍信息
                title = book.title,
                coverResId = book.coverResId,
            )
            intent.putExtra("book", newBook)
            context.startActivity(intent)
        }
    }
}

@Composable
fun BookList(books:List<Book>,onItemClick:(Book)-> Unit){
    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        items(books.size){ index ->
            val book = books[index]
            BookItem(book = book, onItemClick= onItemClick)
        }
    }
}

@Composable
fun BookItem(book: Book,onItemClick:(Book)->Unit){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(book) },
        shape=RoundedCornerShape(8.dp),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(4.dp)
    ){
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id=book.coverResId),
                contentDescription = book.title,
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text=book.title,
                fontSize = 18.sp,
                color=Color.Black
            )
        }

    }
}