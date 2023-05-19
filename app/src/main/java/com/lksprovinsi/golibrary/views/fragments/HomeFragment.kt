package com.lksprovinsi.golibrary.views.fragments

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.lksprovinsi.golibrary.R
import com.lksprovinsi.golibrary.databinding.ActivityMainBinding
import com.lksprovinsi.golibrary.databinding.FragmentHomeBinding
import com.lksprovinsi.golibrary.libraries.Dialogs
import com.lksprovinsi.golibrary.libraries.Service
import com.lksprovinsi.golibrary.libraries.adapters.BookGridViewAdapter
import com.lksprovinsi.golibrary.network.Services
import com.lksprovinsi.golibrary.network.dto.BookDTO
import com.lksprovinsi.golibrary.views.activities.MainActivity
import org.json.JSONArray


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var parentBinding: ActivityMainBinding
    private lateinit var bookData: MutableList<BookDTO>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.searchTv.addTextChangedListener (
            {_, _, _, _ -> },
            {_, _, _, _ ->  },
            {
                loadBooks()
            }
        )

        loadBooks()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity){
            parentBinding = context.binding
        }
    }

    private fun loadBooks(){
        val ctx: Context = binding.root.context
        val dialog: ProgressDialog = Dialogs.loading(ctx)
        val service: Service<JSONArray> = Services.getBooks(binding.searchTv.text.toString())

        service.setOnStart {
            dialog.show()
        }

        service.setOnResponse { res, conn ->
            dialog.dismiss()
            if(conn.responseCode == 200){

                bookData = mutableListOf()
                for(i in 0 until res!!.length()){
                    bookData.add(BookDTO.fromJson(res.getJSONObject(i)))
                }
                setBookGridView()
            }else{
                Toast.makeText(binding.root.context, "Cannot load books", Toast.LENGTH_SHORT).show()
            }
        }
        service.execute()
    }

    private fun setBookGridView(){
        val adapter = BookGridViewAdapter(binding.root.context, bookData)
        adapter.setOnBookClick{
            openDetail(it.id)
        }
        binding.booksGv.adapter = adapter
    }

    private fun openDetail(id: String){
        val fragment = BookDetailFragment.newInstance(id)
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(parentBinding.mainFl.id, fragment)
            .commit()
    }
}