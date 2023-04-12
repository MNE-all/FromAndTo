package com.mne4.fromandto

import android.app.Notification.PRIORITY_HIGH
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.asLiveData
import com.mne4.fromandto.API.ViewModel
import com.mne4.fromandto.db.MainDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import java.time.LocalDate

class IntroActivity : AppCompatActivity() {
    private var viewModel = ViewModel()



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_load_screen)

        checkLocalDb()
    }

    private fun firstIntroPage() {
        setContentView(R.layout.activity_intro_first_page)
        var button = findViewById<Button>(R.id.buttonIntroFirstNext)
        button.setOnClickListener {
            secondIntroPage()
        }
    }

    private fun secondIntroPage() {
        setContentView(R.layout.activity_intro_second_page)
        var button = findViewById<Button>(R.id.buttonNextIntroSecondPage)
        button.setOnClickListener {
            notificationPage()
        }
    }


    private fun notificationPage() {
        setContentView(R.layout.activity_intro_notification_page)
        // TODO Запросить разрешение на уведомление
        var buttonYes = findViewById<Button>(R.id.buttonNotificationOn)
        var buttonNo = findViewById<Button>(R.id.buttonNotificationOff)
        buttonYes.setOnClickListener {
            // TODO Пользователь разрешает уведомления

            loginOrRegister()
        }
        buttonNo.setOnClickListener {
            // TODO Пользователь запрещает уведомления
            loginOrRegister()
        }
    }

    private fun loginOrRegister() {
        setContentView(R.layout.activity_intro_login_register)
        var buttonLogin = findViewById<Button>(R.id.buttonLoginLoginRegister)
        var buttonRegister = findViewById<Button>(R.id.buttonRegisterLoginRegister)

        buttonLogin.setOnClickListener {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        buttonRegister.setOnClickListener {
            var intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkLocalDb() {
        var db = MainDB.getDB(this)
        db.getDao().getAllUser().asLiveData().observe(this) {
            var isInAccount = false
            if (it.isEmpty()) {
                Log.d("isUserEmpty", "true")
                firstIntroPage()
            }
            else
            {
                for (user in it) {
                    if (user.isInAcc) {
                        isInAccount = true
                    }
                }
                Log.d("isUserEmpty", "false")
                if (isInAccount) {
                    var intent = Intent(this, WelcomeActivity::class.java)
                    startActivity(intent)
                }
                else {
                    // TODO в alertDialog предлагать использовать один из аккаунтов из локальной базы данных + авторизовать пользователя
                }
            }
        }
    }
}