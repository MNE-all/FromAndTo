package com.mne4.fromandto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.asLiveData
import com.mne4.fromandto.Data.Room.MainDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
    }

    fun onDriverClick(view: View) {
//        startActivity(Intent(this, ProfileActivity::class.java))

//        var intent = Intent(applicationContext, CreateRequestActivity::class.java)
        intent.putExtra("UserStatus", "Driver")
        startActivity(Intent(applicationContext, FindActivity::class.java))
    }

    fun onPassengerClick(view: View) {
        intent.putExtra("UserStatus", "User")
        startActivity(Intent(applicationContext, ProfileActivity::class.java))
    }

    fun onExit(view: View) {
        var db = MainDB.getDB(this)
        db.getDao().getAllUser().asLiveData().observe(this) {
            if (it.isNotEmpty()) {
                for (user in it){
                    if (user.isInAcc) {
                        CoroutineScope(Dispatchers.IO).launch {
                            db.getDao().updateUserisAcc(
                                "${user.id_user}",
                               false
                            )
                        }
                        var intent = Intent(this, IntroActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}