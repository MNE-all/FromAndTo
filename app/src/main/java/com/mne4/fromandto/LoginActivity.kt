package com.mne4.fromandto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.asLiveData
import com.mne4.fromandto.API.ViewModel
import com.mne4.fromandto.db.MainDB
import com.mne4.fromandto.db.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var phoneEditText: EditText
    private lateinit var passwordEditText: EditText
    var viewModel = ViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        phoneEditText = findViewById(R.id.phoneField)
        passwordEditText = findViewById(R.id.passField)

    }

    fun butEnter(view: View){

        // TODO Нормально оформить корутины, а то вылетает иногда
        var phone = phoneEditText.text
        var password = passwordEditText.text

        viewModel.postAuthentication("$phone","$password")
        viewModel.dataModelUsers.ApiPostAuthentication.observe(this) { userFull ->
            if (userFull != null) {
                var db = MainDB.getDB(this)
                db.getDao().getAllUser().asLiveData().observe(this) {
                    if (it.isNotEmpty()) {
                        for (user in it){
                            if (user.id_user == userFull.id_user) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    db.getDao().updateUserisAcc(
                                        "${user.id}",
                                        true
                                    )
                                    Log.d("col","$userFull")
                                    return@launch
                                }
                                runOnUiThread {
                                    Toast.makeText(
                                        this,
                                        "Пользователь авторизирован!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    var intent = Intent(this, WelcomeActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                    else {
                        Log.d("colNull","$userFull")
                        CoroutineScope(Dispatchers.IO).launch {
                            val user = User(
                                null, "${userFull.id_user}", "${userFull.password}", "${userFull.surname}", "${userFull.name}",
                                "${userFull.birthday}", "${userFull.gender}", "${userFull.phone}", false
                            )
                            db.getDao().insertUser(user)

                        }
                        runOnUiThread {
                            var intent = Intent(this, WelcomeActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }

            }else{
                Toast.makeText(this, "Пользователь не найден!",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun register (view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}