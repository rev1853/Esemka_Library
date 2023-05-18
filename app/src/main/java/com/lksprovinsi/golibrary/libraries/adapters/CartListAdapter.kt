package com.lksprovinsi.golibrary.libraries.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lksprovinsi.golibrary.databinding.WidgetCartCardBinding
import com.lksprovinsi.golibrary.network.dto.BookDTO

class CartListAdapter(
    private val items: MutableList<BookDTO>
) : RecyclerView.Adapter<CartListAdapter.CartListViewHolder>() {

    private var onItemRemove: OnItemRemove? = null

    fun setOnItemRemove(onItemRemove: OnItemRemove){
        this.onItemRemove = onItemRemove
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartListViewHolder {
        val binding = WidgetCartCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartListViewHolder, position: Int) {
        holder.setItem(items[position])

        holder.binding.removeCartBtn.setOnClickListener{
            val item: BookDTO = items.removeAt(position)
            notifyItemRemoved(position)
            onItemRemove?.handle(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class CartListViewHolder(val binding: WidgetCartCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setItem(item: BookDTO){
            binding.bookNameTv.text = item.name
            binding.bookAuthorTv.text = item.authors
            binding.bookAvailableTv.text = item.available.toString()
            binding.bookIsbnTv.text = item.isbn
        }
    }

    fun interface OnItemRemove{
        fun handle(item: BookDTO)
    }
}