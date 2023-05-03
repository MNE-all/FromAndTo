package com.mne4.fromandto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.asLiveData
import androidx.room.Dao
import com.mne4.fromandto.Data.DataModel
import com.mne4.fromandto.Data.Retrofit2.Models.UserFull
import com.mne4.fromandto.Data.Room.DAO.DaoUser
import com.mne4.fromandto.Data.Room.MainDB
import com.mne4.fromandto.Data.Room.Entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var phoneEditText: EditText
    private lateinit var passwordEditText: EditText
    private var userGlobal: String? = null
    val viewModel: DataModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        phoneEditText = findViewById(R.id.phoneField)
        passwordEditText = findViewById(R.id.passField)
        viewModel.getLocalDB(this).getDao().getAllUser().asLiveData().observe(this) {
            for (user in it) {
                if(user.isInAcc){
                    userGlobal = user.id_user
                }
            }
        }
    }

    fun butEnter(view: View) {

        // TODO Нормально оформить корутины, а то вылетает иногда
        var phone = phoneEditText.text
        var password = passwordEditText.text


        viewModel.postAuthentication("$phone", "$password")
        viewModel.ApiPostAuthentication.observe(this) { userFull ->
            if (userFull == null) {
                Toast.makeText(this, "Пользователь не найден!", Toast.LENGTH_SHORT).show()
                return@observe
            }
            if (userGlobal != null) {
                if(userGlobal == userFull.id_user) {
                    AuthUser(userGlobal)
                }
           }
            if(userGlobal == null){
                AddLocalDB(userFull)
            }

            //            else {
//                Log.d("colNull", "$userFull")
//                CoroutineScope(Dispatchers.IO).launch {
//                    val user = User(
//                        null, "${userFull.id_user}", "${userFull.password}", false
//                    )
//                    db.getDao().insertUser(user)
//
//                }
//                runOnUiThread {
//                    var intent = Intent(this, WelcomeActivity::class.java)
//                    startActivity(intent)
//                }
//            }
        }

    }

    fun AuthUser(guid:String?){

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getLocalDB(this@LoginActivity).getDao().updateUserisAcc(
                "${guid}",
                true
            )

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

    fun AddLocalDB(userFull:UserFull){
        CoroutineScope(Dispatchers.IO).launch {
            val user = User(
                null, "${userFull.id_user}", "${userFull.password}", true
            )
            viewModel.getLocalDB(this@LoginActivity).getDao().insertUser(user)
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

    fun example(){

    }
    fun register (view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}