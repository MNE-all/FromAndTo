package com.mne4.fromandto

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.asLiveData
import com.mne4.fromandto.API.ViewModel
import com.mne4.fromandto.Models.GetUserRoom
import com.mne4.fromandto.Models.User
import com.mne4.fromandto.Observe.DataModel
import com.mne4.fromandto.databinding.ActivityIntroLoadScreenBinding
import com.mne4.fromandto.db.MainDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class IntroActivity : AppCompatActivity() {
    lateinit var binding: ActivityIntroLoadScreenBinding
    private var viewModel = ViewModel()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroLoadScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var db = MainDB.getDB(this)


/*
        var date = LocalDate.parse("2003-10-27")
        var users= User(
            "Николаев",
            "Вячеслав",
            "Мужской",
            "$date",
            "slavanik@gmail.com",
            "123456",
            "123456",
            "89645843532",
            true,
            5.0,
            null,
            null,
            null
        )
        viewModel.postNewUser(users)
        viewModel.dataModel.ApiReturnUser.observe(this) {
            var user: com.mne4.fromandto.db.User = com.mne4.fromandto.db.User(
                null,
                "${it.id_user}",
                "${it.surname}",
                "${it.name}",
                "${it.birthday}",
                "${it.gender}",
                "${it.phone}",
                false
            )
            CoroutineScope(Dispatchers.IO).launch {
                db.getDao().insertUser(user)
            }
        }
        */
        db.getDao().getAllUser().asLiveData().observe(this) {
            if (it.isEmpty()) {
                Log.d("userEmpty", "null")
            } else {
                Log.d("userEmpty2", "not null")
            }
        }


/*
    //        var password = viewModel.postSMS("79657683902")
    //        Log.d("passwordSms","$password")
            var date = LocalDate.parse("2003-10-27")
            var users= User(
                "Николаев",
                "Вячеслав",
                "Мужской",
                "$date",
                "slavanik@gmail.com",
                "123456",
                "123456",
                "89645843532",
                true,
                5.0,
                null,
                null,
                null
            )
           */

        }
    }