package com.mne4.fromandto
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.asLiveData
import androidx.viewpager2.widget.ViewPager2
import com.mne4.fromandto.Adapter.OnboardAdapter
import com.mne4.fromandto.Data.DataModel
import com.mne4.fromandto.Data.Room.Entities.User
import com.yandex.mapkit.MapKitFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IntroActivity : AppCompatActivity() {
    val viewModel: DataModel by viewModels()
    private var userLocalDB:User? = null
    private var localSize:Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loadscreen)
        try {
            if (MapKitFactory.getInstance() != null) {

            }
        }catch (ex: UnsatisfiedLinkError){
            MapKitFactory.setApiKey("429ae64e-46c4-4b6a-aebe-e8ef49cbc0c5")
            MapKitFactory.initialize(applicationContext)
        }

        viewModel.ApiPostAuthenticationAuto.observe(this) {
            if (it) {
                var intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                Toast.makeText(
                    applicationContext,
                    "Быстрый вход!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                loginOrRegister()
                Toast.makeText(
                    applicationContext,
                    "Аккаунт не найден!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.IO).launch {
            runOnUiThread{
                viewModel.getLocalDB(this@IntroActivity).getDao().getAllUser().asLiveData().observe(this@IntroActivity) {
                    localSize = it.size
                    for(user in it){
                        if(user.isInAcc){
                            userLocalDB = user
                        }
                    }
                    checkLocalDb()
                }
            }
        }
    }

    private fun onboard() {
        setContentView(R.layout.activity_intro)

        val adapter = OnboardAdapter(this)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = adapter

        val buttons = arrayOf<ImageView>(findViewById(R.id.imageViewBtn1),
            findViewById(R.id.imageViewBtn2), findViewById(R.id.imageViewBtn3))

        val skipBtn = findViewById<TextView>(R.id.textViewSkip)
        skipBtn.setOnClickListener {
            notificationPage()
        }
        viewModel.onboardPosition.observe(this) { it ->
            buttons.forEach { it ->
                it.setImageDrawable(getDrawable(R.drawable.baseline_radio_button_unchecked_24))
            }
            buttons[it].setImageDrawable(getDrawable(R.drawable.baseline_radio_button_checked_24))
            if (it == adapter.itemCount - 1) {
                skipBtn.text = "Завершить"
            }
            else {
                skipBtn.text = "Пропустить"
            }
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
        if (localSize == 0) {
            onboard()
        } else {
            if (userLocalDB != null) {
                viewModel.postAuthenticationAuto(userLocalDB!!.id_user, userLocalDB!!.password)
            } else {
                // TODO в alertDialog предлагать использовать один из аккаунтов из локальной базы данных + авторизовать пользователя
                loginOrRegister()
                Toast.makeText(
                    applicationContext,
                    "Зарегистрируйтесь или войдите!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}