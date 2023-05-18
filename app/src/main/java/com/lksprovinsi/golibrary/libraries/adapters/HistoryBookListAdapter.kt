package com.lksprovinsi.golibrary.libraries.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lksprovinsi.golibrary.databinding.WidgetCartCardBinding
import com.lksprovinsi.golibrary.databinding.WidgetHistoryBookCardBinding
import com.lksprovinsi.golibrary.network.dto.BookBorrowingDTO
import com.lksprovinsi.golibrary.network.dto.BookDTO

class HistoryBookListAdapter(
    private val items: MutableList<BookBorrowingDTO>
) : RecyclerView.Adapter<HistoryBookListAdapter.HistoryBookListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryBookListViewHolder {
        val binding = WidgetHistoryBookCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryBookListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryBookListViewHolder, position: Int) {
        holder.setItem(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class HistoryBookListViewHolder(val binding: WidgetHistoryBookCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setItem(item: BookBorrowingDTO){
            binding.bookNameTv.text = item.book!!.name
            binding.bookAuthorTv.text = item.book!!.authors
            binding.bookIsbnTv.text = item.book!!.isbn
        }
    }
}