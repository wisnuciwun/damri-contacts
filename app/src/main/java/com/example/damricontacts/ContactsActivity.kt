package com.example.damricontacts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.damricontacts.helper.Constants
import com.example.damricontacts.helper.PreferenceHelper
import kotlinx.android.synthetic.main.activity_contact_card.*
import kotlinx.android.synthetic.main.activity_contact_card.view.*
import kotlinx.android.synthetic.main.activity_contacts.*
import kotlinx.android.synthetic.main.activity_pop_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ContactsActivity : AppCompatActivity() {
    lateinit var sharedPreferences: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
        sharedPreferences = PreferenceHelper(this)
        username_value.text = sharedPreferences.getString( Constants.APP_USERNAME )
        loadContacts()

        btn_logout.setOnClickListener{
            val contacts = Retros("https://testapi.damri.co.id/index.php/Loginact/logout/").getRetroClientInstance().create(UserClient::class.java)
            val responseContact = contacts.requestLogout()

            responseContact.enqueue(object : Callback<UserResponse>{
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    var code = response.body()?.kode
                    if(code == 200){
                        sharedPreferences.put( Constants.APP_IS_LOGIN, false)
                        moveIntent()
                    }
                }
                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }

        btn_insert.setOnClickListener{
            popUpModal("Tambah", 0)
        }
    }

    private fun moveIntent(){
            startActivity(
                Intent(this, MainActivity::class.java)
            )
            finish()
    }

    private fun popUpModal(action: String, id: Int){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.activity_pop_up, null)
        val editNama = dialogLayout.findViewById<EditText>(R.id.insert_name)
        val editNomor = dialogLayout.findViewById<EditText>(R.id.insert_number)

        with(builder){
            setTitle(action+" kontak")
            setPositiveButton("Simpan"){
                dialog, which ->

                if(action == "Tambah" && editNama.text.isNotEmpty() && editNomor.text.isNotEmpty()){
                    insertNewContact(editNama.text.toString(), editNomor.text.toString())
                }
                else if(action == "Ubah" && editNama.text.isNotEmpty() && editNomor.text.isNotEmpty()){
                    editContact(editNama.text.toString(), editNomor.text.toString(), id)
                }
                else{
                    Toast.makeText(applicationContext,"Silahkan lengkapi data kontak", Toast.LENGTH_LONG).show()
                }

            }
            setNegativeButton("Tutup"){
                    dialog, which -> Log.d("cancel", "")
            }
            setView(dialogLayout)
            show()
        }
    }

    private fun editContact(newNama: String, newNomor: String, id: Int){
        val cookies = sharedPreferences.getString( Constants.COOKIES ).toString()
        val editUser = Retros("https://testapi.damri.co.id/index.php/kontak/").getRetroClientInstance().create(UserClient::class.java)
        val responseBody = editUser.editContact(cookies, id, newNama, newNomor)

        responseBody.enqueue(object : Callback<UserResponse>{
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val code = response.body()?.kode
                println(response)
                if(code == 200){
                    Toast.makeText(applicationContext,"Yeay, ubah kontak berhasil !", Toast.LENGTH_SHORT).show()
                    loadContacts()
                }else{
                    Toast.makeText(applicationContext,"Terjadi kesalahan, silahkan coba lagi", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private  fun insertNewContact(newNama: String, newNomor: String) {
        val cookies = sharedPreferences.getString( Constants.COOKIES ).toString()
        val insertUser = Retros("https://testapi.damri.co.id/index.php/kontak/").getRetroClientInstance().create(UserClient::class.java)
        val responseBody = insertUser.insertContact(cookies, newNama, newNomor)

        responseBody.enqueue(object : Callback<UserResponse>{
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val code = response.body()?.kode
                println(response)
                if(code == 200){
                    Toast.makeText(applicationContext,"Yeay, tambah kontak berhasil !", Toast.LENGTH_SHORT).show()
                    loadContacts()
                }else{
                    Toast.makeText(applicationContext,"Terjadi kesalahan, silahkan coba lagi", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun loadContacts(){
        val cookies = sharedPreferences.getString( Constants.COOKIES ).toString()
        val contacts = Retros("https://testapi.damri.co.id/index.php/Loginact/logout/").getRetroClientInstance().create(UserClient::class.java)
        val responseContact = contacts.requestContact(cookies)

        responseContact.enqueue(object : Callback<ContactList>{
            override fun onResponse(
                call: Call<ContactList>,
                response: Response<ContactList>
            ) {
                var code = response.body()?.kode
                if(code == 200){
                    val data = response.body()?.data
                    val length: Int = data?.size?.toInt() ?: 1
                    contacts_container.removeAllViewsInLayout()

                    for (i in 0 until length){
                        val view = layoutInflater.inflate(R.layout.activity_contact_card, null)
                        val nameView: TextView = view.findViewById(R.id.name)
                        val phoneView: TextView = view.findViewById(R.id.phone)
                        val index: Int = data?.get(i)?.id?.toInt() ?: 1

                        nameView.text = data?.get(i)?.nama
                        phoneView.text = data?.get(i)?.nomor
                        contacts_container.addView(view)

                        view.btn_edit.setOnClickListener {
                            popUpModal("Ubah", index)
                        }

                        view.btn_delete.setOnClickListener {
                            val logout = Retros("https://testapi.damri.co.id/index.php/Loginact/logout/").getRetroClientInstance().create(UserClient::class.java)
                            val responseBody = logout.deleteContact(cookies, index)

                            responseBody.enqueue(object : Callback<UserResponse>{
                                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                                    val code = response.body()?.kode
                                    println(response)
                                    if(code == 200){
                                        Toast.makeText(applicationContext,"Hapus kontak berhasil", Toast.LENGTH_SHORT).show()
                                        loadContacts()
                                    }else{
                                        Toast.makeText(applicationContext,"Terjadi kesalahan, silahkan coba lagi", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                                    TODO("Not yet implemented")
                                }
                            })
                        }
                    }


                }

            }
            override fun onFailure(call: Call<ContactList>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

//    private fun saveContacts( contactsData : List<Object> ){
//        sharedPreferences.put( Constants.CONTACT_LIST, contactsData)
//    }

}