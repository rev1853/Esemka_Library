package com.lksprovinsi.golibrary.views.fragments

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.lksprovinsi.golibrary.R
import com.lksprovinsi.golibrary.databinding.FragmentHistoryDetailBinding
import com.lksprovinsi.golibrary.libraries.Dialogs
import com.lksprovinsi.golibrary.libraries.adapters.HistoryBookListAdapter
import com.lksprovinsi.golibrary.network.Services
import com.lksprovinsi.golibrary.network.dto.BorrowingHistoryDTO
import java.text.SimpleDateFormat
import java.util.*


class HistoryDetailFragment : Fragment() {

    private lateinit var binding: FragmentHistoryDetailBinding
    private lateinit var id: String
    private var history: BorrowingHistoryDTO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString("id")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryDetailBinding.inflate(inflater, container, false)

        fetchHistory()

        return binding.root
    }

    private fun fetchHistory(){
        val dialog: ProgressDialog = Dialogs.loading(requireContext())
        val service = Services(requireContext()).historyDetail(id)

        service.setOnStart{
            dialog.show()
        }

        service.setOnResponse{res, conn ->
            dialog.dismiss()
            if(conn.responseCode == 200){
                history = BorrowingHistoryDTO.fromJson(res!!)

                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

                val start: String = formatter.format(parser.parse(history!!.start!!)!!)
                val end: String = formatter.format(parser.parse(history!!.end!!)!!)

                binding.historyPeriodTv.text = "$start - $end"
                showHistories()
            }else{
                Toast.makeText(requireContext(), "Fetch history failed", Toast.LENGTH_LONG).show()
            }
        }

        service.execute()
    }

    private fun showHistories(){
        val adapter = HistoryBookListAdapter(history!!.bookBorrowings!!.toMutableList())
        binding.historyBookRv.layoutManager = LinearLayoutManager(requireContext())
        binding.historyBookRv.adapter = adapter
    }

    companion object {
        fun newInstance(id: String) =
            HistoryDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("id", id)
                }
            }
    }
}