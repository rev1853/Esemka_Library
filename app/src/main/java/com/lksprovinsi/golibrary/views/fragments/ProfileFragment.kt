package com.lksprovinsi.golibrary.views.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.lksprovinsi.golibrary.databinding.ActivityMainBinding
import com.lksprovinsi.golibrary.databinding.FragmentProfileBinding
import com.lksprovinsi.golibrary.libraries.*
import com.lksprovinsi.golibrary.libraries.adapters.HistoryListAdapter
import com.lksprovinsi.golibrary.network.Services
import com.lksprovinsi.golibrary.network.dto.BorrowingHistoryDTO
import com.lksprovinsi.golibrary.network.dto.UserDTO
import com.lksprovinsi.golibrary.views.activities.LoginActivity
import com.lksprovinsi.golibrary.views.activities.MainActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

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

        binding.uploadImageBtn.setOnClickListener{
            val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
            if(!PermissionManager.check(requireContext(), permission)) {
                PermissionManager.request(requireActivity(), arrayOf(permission), PICK_IMAGE_REQUEST_CODE)
            }else{
                pickImage()
            }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PICK_IMAGE_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            val imageUri: Uri = data.data!!

            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = requireContext().contentResolver!!.query(imageUri, projection, null, null, null)

            cursor.use {
                if(it?.moveToFirst() == true){
                    val colIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
                    val filePath: String? = it.getString(colIndex)
                    if(filePath != null){
                        val file = File(filePath)
                        val type: String = requireContext().contentResolver!!.getType(imageUri)!!
                        uploadImage(file.name, type, file)
                    }
                }
            }
        }
    }

    private fun fetchHistories(){
        val ctx: Context = binding.root.context
        val dialog: ProgressDialog = Dialogs.loading(ctx)
        val service: Service<JSONArray> = Services.borrowingHistories()

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
        val service: Service<JSONObject> = Services.profile()

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
        StorageBox.global!!.edit { clear() }

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        requireActivity().startActivity(intent)
        requireActivity().finish()
    }

    private fun pickImage(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    private fun loadImage(){
        ImageHelper.loadImage("${Services.BASE_URL}/user/me/photo", binding.userImageIv)
    }

    private fun uploadImage(name: String, type: String, file: File){
        val dialog: ProgressDialog = Dialogs.loading(requireContext())
        val service: Service<JSONObject> = Services.uploadPhoto(name, type, file)

        service.setOnStart{
            dialog.show()
        }

        service.setOnResponse{ _, conn ->
            dialog.dismiss()
            if(conn.responseCode in 200 until 300){
                loadImage()
                Toast.makeText(requireContext(), "Image has been uploaded", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(), "Cannot upload image, try again later", Toast.LENGTH_LONG).show()
            }
        }

        service.execute()
    }

    companion object{
         const val PICK_IMAGE_REQUEST_CODE: Int = 101
    }
}