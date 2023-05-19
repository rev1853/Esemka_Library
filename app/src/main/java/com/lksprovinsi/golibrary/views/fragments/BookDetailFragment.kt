package com.lksprovinsi.golibrary.views.fragments

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.lksprovinsi.golibrary.R
import com.lksprovinsi.golibrary.databinding.ActivityMainBinding
import com.lksprovinsi.golibrary.databinding.FragmentBookDetailBinding
import com.lksprovinsi.golibrary.libraries.Dialogs
import com.lksprovinsi.golibrary.libraries.Service
import com.lksprovinsi.golibrary.libraries.StorageBox
import com.lksprovinsi.golibrary.network.Services
import com.lksprovinsi.golibrary.network.dto.BookDTO
import com.lksprovinsi.golibrary.views.activities.MainActivity
import org.json.JSONObject

class BookDetailFragment : Fragment() {
    private lateinit var parentBinding: ActivityMainBinding
    private lateinit var binding: FragmentBookDetailBinding
    private lateinit var id: String
    private var book: BookDTO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString("id")!!
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity){
            parentBinding = context.binding
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookDetailBinding.inflate(inflater, container, false)

        binding.addBookToCartBtn.setOnClickListener{
            addBookToCart()
        }

        fetchBook()

        return binding.root
    }

    private fun fetchBook(){
        val ctx: Context = binding.root.context
        val dialog: ProgressDialog = Dialogs.loading(ctx)
        val service: Service<JSONObject> = Services.findBook(id)

        service.setOnStart {
            dialog.show()
        }

        service.setOnResponse { res, conn ->
            dialog.dismiss()
            if(conn.responseCode == 200){
                book = BookDTO.fromJson(res!!)
                binding.bookNameTv.text = book!!.name
                binding.bookAuthorTv.text = book!!.authors
                binding.bookIsbnTv.text = book!!.isbn
                binding.bookPublisherTv.text = book!!.publisher
                binding.bookAvailableTv.text = book!!.available.toString()
                binding.bookDescTv.text = book!!.description
            }else{
                Toast.makeText(ctx, "Book not found", Toast.LENGTH_LONG).show()
            }
        }

        service.execute()
    }

    private fun addBookToCart(){
        var selectedBooks: String = StorageBox.user!!.get("cart", String::class.java) ?: ""
        var cart = mutableListOf<String>()

        if(selectedBooks.isNotBlank()){
            cart = selectedBooks.split(',').toMutableList()
        }

        if(cart.contains(id)){
            Toast.makeText(binding.root.context, "Book already selected", Toast.LENGTH_SHORT).show()
            return
        }

        cart.add(id)
        selectedBooks = cart.joinToString(",")
        StorageBox.user!!.edit {
            putString("cart", selectedBooks)
        }

        Toast.makeText(binding.root.context, "Book has been added to cart", Toast.LENGTH_SHORT).show()

        val fragment = CartFragment()
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(parentBinding.mainFl.id, fragment)
            .commit()
    }

    companion object {
        fun newInstance(id: String): BookDetailFragment {
            return BookDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("id", id)
                }
            }
        }
    }
}