package com.lksprovinsi.golibrary.views.fragments

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.lksprovinsi.golibrary.R
import com.lksprovinsi.golibrary.databinding.FragmentCartBinding
import com.lksprovinsi.golibrary.libraries.Dialogs
import com.lksprovinsi.golibrary.libraries.Service
import com.lksprovinsi.golibrary.libraries.StorageBox
import com.lksprovinsi.golibrary.libraries.adapters.CartListAdapter
import com.lksprovinsi.golibrary.network.Services
import com.lksprovinsi.golibrary.network.dto.BookDTO
import com.lksprovinsi.golibrary.network.dto.BorrowingDTO
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var dialog: ProgressDialog
    private val cart: MutableList<BookDTO> = mutableListOf()

    private val cartStrs: List<String>
        get() {
            val box = StorageBox(binding.root.context)
            val cart: String = box.get("cart", String::class.java) ?: ""
            return if (cart.isBlank()) listOf() else cart.split(",")
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        dialog = Dialogs.loading(binding.root.context)

        binding.startDateTv.setOnClickListener{
            pickDate(it as TextView)
        }

        binding.endDateTv.setOnClickListener{
            pickDate(it as TextView)
        }

        binding.bookingBorrowBtn.setOnClickListener{
            borrowBook()
        }

        loadAllBooks()
        prepareDateValue()

        return binding.root
    }

    private fun fetchBook(index: Int){
        val ctx: Context = binding.root.context
        val service: Service<JSONObject> = Services(ctx).findBook(cartStrs[index])

        service.setOnStart{
            if(!dialog.isShowing) dialog.show()
        }

        service.setOnResponse { res, conn ->
            if(conn.responseCode == 200){
                cart.add(BookDTO.fromJson(res!!))
            }else{
                Toast.makeText(ctx, "Cannot find book with id ${cartStrs[index]}", Toast.LENGTH_LONG).show()
            }
            loadAllBooks(index)
        }

        service.execute()
    }

    private fun loadAllBooks(index: Int? = null) {
        if(cartStrs.isNotEmpty()){
            if(index == null){
                fetchBook(0)
            }else{
                if(index != cartStrs.size - 1){
                    val next = index + 1
                    fetchBook(next)
                }else{
                    dialog.dismiss()
                    val adapter = CartListAdapter(cart).apply {
                        setOnItemRemove {
                            removeCartItem(it)
                        }
                    }
                    binding.cartListRv.layoutManager = LinearLayoutManager(binding.root.context)
                    binding.cartListRv.adapter = adapter
                }
            }
        }else{
            val adapter = CartListAdapter(cart).apply {
                setOnItemRemove {
                    removeCartItem(it)
                }
            }
            binding.cartListRv.layoutManager = LinearLayoutManager(binding.root.context)
            binding.cartListRv.adapter = adapter
        }
    }

    private fun removeCartItem(item: BookDTO){
        val items = cartStrs.toMutableList()
        items.remove(item.id)
        StorageBox(binding.root.context)
            .editor
            .putString("cart", items.joinToString(","))
            .commit()
        Toast.makeText(binding.root.context, "Item ${item.name} has been removed", Toast.LENGTH_SHORT).show()
    }

    private fun prepareDateValue(){
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        binding.startDateTv.text = formatter.format(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, 3)
        binding.endDateTv.text = formatter.format(calendar.time)
    }

    private fun pickDate(textView: TextView){
        val calendar: Calendar = parseDate(textView.text.toString())
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        val datePicker = DatePickerDialog(
            binding.root.context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                textView.text = formatter.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        )
        datePicker.show()
    }

    private fun parseDate(date: String): Calendar{
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        calendar.time = formatter.parse(date)!!
        return calendar
    }

    private fun borrowBook(){
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val startDate: Calendar = parseDate(binding.startDateTv.text.toString())
        val endDate: Calendar = parseDate(binding.endDateTv.text.toString())

        val borrowingData = BorrowingDTO(formatter.format(startDate.time), formatter.format(endDate.time), cartStrs)
        val service: Service<JSONObject> = Services(binding.root.context).borrowing(borrowingData)

        service.setOnStart {
            dialog.show()
        }

        service.setOnResponse{ _, conn ->
            dialog.dismiss()
            if(conn.responseCode == 200){
                StorageBox(binding.root.context)
                    .editor
                    .remove("cart")
                    .commit()
                Toast.makeText(binding.root.context, "Booking success", Toast.LENGTH_LONG).show()
                loadAllBooks()
            }else{
                Toast.makeText(binding.root.context, "Failed to borrow books, try again later", Toast.LENGTH_LONG).show()
            }
        }

        service.execute()
    }
}