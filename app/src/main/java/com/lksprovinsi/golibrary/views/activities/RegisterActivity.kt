package com.lksprovinsi.golibrary.views.activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.lksprovinsi.golibrary.R
import com.lksprovinsi.golibrary.databinding.ActivityRegisterBinding
import com.lksprovinsi.golibrary.libraries.Dialogs
import com.lksprovinsi.golibrary.libraries.Service
import com.lksprovinsi.golibrary.network.Services
import com.lksprovinsi.golibrary.network.dto.RegisterDTO
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)

        binding.signupBtn.setOnClickListener {
            submit()
        }
    }

    private val registerData: RegisterDTO?
        get() {
            var errMsg: String? = null
            val name: String = binding.nameEt.text.toString()
            val password: String = binding.passwordEt.text.toString()
            val email: String = binding.emailEt.text.toString()

            if(name.isBlank()){
                errMsg = "Name is required"
            }

            if(password.isBlank()){
                errMsg = "Password is required"
            }

            if(password != binding.confirmPasswordEt.text.toString()){
                errMsg = "Confirm Password is not valid"
            }

            if(email.isBlank()){
                errMsg = "Email not valid"
            }

            if(errMsg != null){
                Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show()
                return null
            }

            return RegisterDTO(name, password, email)
        }

    private fun submit(){
        val dialog = Dialogs.loading(this)
        val data: RegisterDTO = registerData ?: return
        val service: Service<JSONObject> = Services(this).register(data)

        service.setOnStart{
            dialog.show()
        }

        service.setOnResponse{_, conn ->
            dialog.dismiss()
            if(conn.responseCode == 200){
                finish()
                Toast.makeText(this, "Sign up success, please login", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "Sign up failed", Toast.LENGTH_LONG).show()
            }
        }

        service.execute()
    }
}