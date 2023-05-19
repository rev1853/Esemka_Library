package com.lksprovinsi.golibrary.views.activities

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences.Editor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.lksprovinsi.golibrary.R
import com.lksprovinsi.golibrary.databinding.ActivityLoginBinding
import com.lksprovinsi.golibrary.libraries.Dialogs
import com.lksprovinsi.golibrary.libraries.Service
import com.lksprovinsi.golibrary.libraries.StorageBox
import com.lksprovinsi.golibrary.network.Services
import com.lksprovinsi.golibrary.network.dto.LoginDTO
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.toSignUpBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.loginBtn.setOnClickListener{
            submit()
        }
    }

    private val loginData: LoginDTO
        get() {
            val email: String = binding.emailEt.text.toString()
            val password: String = binding.passwordEt.text.toString()
            return LoginDTO(email, password)
        }

    private fun submit(){
        val dialog = Dialogs.loading(this)
        val data: LoginDTO = loginData
        val service: Service<JSONObject> = Services.login(data)

        service.setOnStart{
            dialog.show()
        }

        service.setOnResponse { res, conn ->
            dialog.dismiss()
            if(conn.responseCode == 200){
                StorageBox.global!!.edit {
                    putString("token", res!!.getString("token"))
                    putString("email", data.email)
                    putString("password", data.password)
                }
                StorageBox.initUser(data.email, this)

                Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }

        service.execute()
    }
}