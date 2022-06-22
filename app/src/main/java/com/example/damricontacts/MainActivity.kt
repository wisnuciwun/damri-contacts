package com.example.damricontacts

import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.damricontacts.helper.Constants
import com.example.damricontacts.helper.PreferenceHelper
import kotlinx.android.synthetic.main.activity_contacts.*
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {
    lateinit var sharedPreferences: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = PreferenceHelper(this)

        btn_login.setOnClickListener {
            if(username.text.isNotEmpty() && password.text.isNotEmpty()){

                val login = Retros("https://testapi.damri.co.id/index.php/Loginact/").getRetroClientInstance().create(UserClient::class.java)
                val responseBody = login.requestLogin(username.text.toString(), password.text.toString(), "463d2c83201694a5404d1e2d58b0350c")

                responseBody.enqueue(object : Callback<UserResponse>{
                    override fun onResponse(
                        call: Call<UserResponse>,
                        response: Response<UserResponse>
                    ) {
                        println(response)
                        val code = response.body()?.kode
                        println(code)
                        if(code == 200){
                            val cookie: String = response.headers().get("Set-Cookie")?.split(";")?.get(0).toString()
                            saveSession(username.text.toString(), password.text.toString(), cookie)
                            moveIntent()
                            Toast.makeText(applicationContext,"Login success", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(applicationContext,"Username/password yang anda input salah", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        TODO("Not yet implemented")
                    }
                })
            }else{
                Toast.makeText(applicationContext,"Silahkan isi username dan password anda", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun moveIntent(){
        if(sharedPreferences.getBoolean(Constants.APP_IS_LOGIN)){
            startActivity(
                Intent(this, ContactsActivity::class.java)
            )
            finish()
        }
    }

    private fun saveSession( username: String, password: String, cookie: String ){
        sharedPreferences.put( Constants.APP_USERNAME, username)
        sharedPreferences.put( Constants.APP_PASSWORD, password)
        sharedPreferences.put( Constants.APP_IS_LOGIN, true)
        sharedPreferences.put( Constants.COOKIES, cookie)
    }
}