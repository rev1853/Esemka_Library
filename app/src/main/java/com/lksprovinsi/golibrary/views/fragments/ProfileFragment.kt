package com.lksprovinsi.golibrary.views.fragments

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.lksprovinsi.golibrary.R
import com.lksprovinsi.golibrary.databinding.ActivityMainBinding
import com.lksprovinsi.golibrary.databinding.FragmentProfileBinding
import com.lksprovinsi.golibrary.libraries.Dialogs
import com.lksprovinsi.golibrary.libraries.ImageHelper
import com.lksprovinsi.golibrary.libraries.Service
import com.lksprovinsi.golibrary.libraries.StorageBox
import com.lksprovinsi.golibrary.libraries.adapters.HistoryListAdapter
import com.lksprovinsi.golibrary.network.Services
import com.lksprovinsi.golibrary.network.dto.BorrowingHistoryDTO
import com.lksprovinsi.golibrary.network.dto.UserDTO
import com.lksprovinsi.golibrary.views.activities.LoginActivity
import com.lksprovinsi.golibrary.views.activities.MainActivity
import org.json.JSONArray
import org.json.JSONObject

class ProfileFragment : Fragment() {

    private lateinit var parentBinding: ActivityMainBinding
    private lateinit var binding: FragmentProfileBinding
    private var histories: MutableList<BorrowingHistoryDTO> = mutableListOf()
    private var user: UserDTO? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.logoutBtn.setOnClickListener{
            logout()
        }

        fetchHistories()
        fetchUser()
        loadImage()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity){
            parentBinding = context.binding
        }
    }

    private fun fetchHistories(){
        val ctx: Context = binding.root.context
        val dialog: ProgressDialog = Dialogs.loading(ctx)
        val service: Service<JSONArray> = Services(ctx).borrowingHistories()

        service.setOnStart{
            dialog.show()
        }

        service.setOnResponse{ res, conn ->
            dialog.dismiss()
            if(conn.responseCode == 200){
                for(i in 0 until res!!.length()){
                    histories.add(BorrowingHistoryDTO.fromJson(res.getJSONObject(i)))
                }
                showHistories()
            }else{
                Toast.makeText(ctx, "Fetch histories failed", Toast.LENGTH_LONG).show()
            }
        }

        service.execute()
    }

    private fun showHistories(){
        val adapter = HistoryListAdapter(histories)
        binding.historyListRv.layoutManager = LinearLayoutManager(binding.root.context)
        binding.historyListRv.adapter = adapter

        adapter.setOnItemClick{
            val fragment = HistoryDetailFragment.newInstance(it.id!!)
            requireActivity()
                .supportFragmentManager
                .beginTransaction()
                .replace(parentBinding.mainFl.id, fragment)
                .commit()
        }
    }

    private fun fetchUser(){
        val ctx: Context = binding.root.context
        val dialog: ProgressDialog = Dialogs.loading(ctx)
        val service: Service<JSONObject> = Services(ctx).profile()

        service.setOnStart {
            dialog.show()
        }

        service.setOnResponse{ res, conn ->
            dialog.dismiss()
            if(conn.responseCode == 200){
                user = UserDTO.fromJson(res!!)
                binding.userNameTv.text = user!!.name
                binding.userEmailTv.text = user!!.name
            }else{
                Toast.makeText(ctx, "Fetch user data failed", Toast.LENGTH_LONG).show()
            }
        }

        service.execute()
    }

    private fun logout(){
        val box = StorageBox(binding.root.context)
        box.editor
            .clear()
            .commit()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        requireActivity().startActivity(intent)
        requireActivity().finish()
    }

    private fun loadImage(){
        ImageHelper.loadImage("${Services.BASE_URL}/user/me/photo", binding.userImageIv)
    }
}