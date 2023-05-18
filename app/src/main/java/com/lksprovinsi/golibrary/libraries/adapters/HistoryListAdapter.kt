package com.lksprovinsi.golibrary.libraries.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lksprovinsi.golibrary.databinding.WidgetCartCardBinding
import com.lksprovinsi.golibrary.databinding.WidgetHistoryCardBinding
import com.lksprovinsi.golibrary.network.dto.BookDTO
import com.lksprovinsi.golibrary.network.dto.BorrowingHistoryDTO
import java.text.SimpleDateFormat
import java.util.*

class HistoryListAdapter(
    private val histories: MutableList<BorrowingHistoryDTO>
) : RecyclerView.Adapter<HistoryListAdapter.HistoryListHolder>() {

    private var onItemClick: OnItemClick? = null

    fun setOnItemClick(onItemClick: OnItemClick){
        this.onItemClick = onItemClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryListHolder {
        val binding = WidgetHistoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryListHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryListHolder, position: Int) {
        holder.setItem(histories[position])

        holder.binding.historyContainerRl.setOnClickListener{
            onItemClick?.handle(histories[position])
        }
    }

    override fun getItemCount(): Int {
        return histories.size
    }

    class HistoryListHolder(val binding: WidgetHistoryCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setItem(item: BorrowingHistoryDTO){
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

            val start: String = formatter.format(parser.parse(item.start!!)!!)
            val end: String = formatter.format(parser.parse(item.end!!)!!)

            binding.historyPeriodTv.text = "$start - $end"
            binding.historyStatusTv.text = item.status
            binding.historyBookCountTv.text = "${item.bookCount} Books"
        }
    }

    fun interface OnItemClick{
        fun handle(item: BorrowingHistoryDTO)
    }
}