package com.mne4.fromandto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.asLiveData
import com.mne4.fromandto.Data.DataModel
import com.mne4.fromandto.Data.Room.MainDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class WelcomeActivity : AppCompatActivity() {
    val viewModel: DataModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
    }

    fun onDriverClick(view: View) {
//        startActivity(Intent(this, ProfileActivity::class.java))

        var intent = Intent(applicationContext, CreateRequestActivity::class.java)
        intent.putExtra(Companion.ARG_USER_STATUS, "Driver")

        startActivity(intent)
    }

    fun onPassengerClick(view: View) {
        var intent = Intent(applicationContext, CreateRequestActivity::class.java)
        intent.putExtra(Companion.ARG_USER_STATUS, "User")
        startActivity(intent)
    }

    fun onExit(view: View) {
        var localDB = viewModel.getLocalDB(this)
        localDB.getDao().getAllUser().asLiveData().observe(this) {
            if (it.isNotEmpty()) {
                for (user in it){
                    if (user.isInAcc) {
                        CoroutineScope(Dispatchers.IO).launch {
                            localDB.getDao().updateUserisAcc(
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

    companion object {
        const val ARG_USER_STATUS = "UserStatus"
    }
}