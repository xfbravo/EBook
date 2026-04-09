package com.example.e_bookreader_fixed

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager=GridLayoutManager(this,3)
        val bookList= listOf(
            Book("What if",R.drawable.book_cover_image,R.raw.book1),
            Book("Loving Hot Family",R.drawable.lovinghotfamily_bookcover,R.raw.lovinghotfamily)
        )
        val adapter=BookAdapter(this,bookList){book ->
            val intent= Intent(this,ReaderActivity::class.java)
            intent.putExtra("bookName",book.title)
            intent.putExtra("bookRawResId",book.rawResId)
            startActivity(intent)
        }
        recyclerView.adapter=adapter
    }
}