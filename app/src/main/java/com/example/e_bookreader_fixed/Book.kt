package com.example.e_bookreader_fixed

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Book (
    val title:String,
    val coverResId:Int,
    val rawResId:Int
)

class BookAdapter(
    private val context: Context,
    private val books:List<Book>,
    private val onItemClick:(Book) -> Unit
):RecyclerView.Adapter<BookAdapter.BookViewHolder>(){
    inner class BookViewHolder(view: View):RecyclerView.ViewHolder(view){
        val imageView: ImageView = view.findViewById(R.id.bookImageView)
        val textview: TextView = view.findViewById(R.id.bookTitleTextView)
        fun bind(book:Book){
            imageView.setImageResource(book.coverResId)
            textview.text = book.title
            itemView.setOnClickListener{
                onItemClick(book)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.item_book,parent,false)
        return BookViewHolder(view)
    }
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }
    override fun getItemCount(): Int {
        return books.size
    }

}
