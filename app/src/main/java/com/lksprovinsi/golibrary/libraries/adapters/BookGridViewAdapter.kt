package com.lksprovinsi.golibrary.libraries.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.lksprovinsi.golibrary.databinding.WidgetBookCardBinding
import com.lksprovinsi.golibrary.network.dto.BookDTO

class BookGridViewAdapter(private val ctx: Context, private val books: List<BookDTO>) : BaseAdapter() {
    private var onBookClick: OnBookClick? = null

    fun setOnBookClick(onBookClick: OnBookClick){
        this.onBookClick = onBookClick
    }

    override fun getCount(): Int {
        return books.size
    }

    override fun getItem(position: Int): Any {
        return books[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View{
        val binding: WidgetBookCardBinding
        val item = books[position]

        if(convertView == null){
            binding = WidgetBookCardBinding.inflate(LayoutInflater.from(ctx), parent, false)
            binding.root.tag = binding
        }else{
            binding = convertView.tag as WidgetBookCardBinding
        }

        binding.bookTitleTv.text = item.name
        binding.bookAuthorTv.text = item.authors

        binding.bookCardContainer.setOnClickListener{
            onBookClick?.handle(item)
        }

        return binding.root
    }

    fun interface OnBookClick{
        fun handle(book: BookDTO)
    }

}