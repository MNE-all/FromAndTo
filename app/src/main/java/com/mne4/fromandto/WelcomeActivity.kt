package com.mne4.fromandto

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.mne4.fromandto.Data.DataModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class WelcomeActivity : AppCompatActivity() {
    val viewModel: DataModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        findViewById<TextView>(R.id.textViewExitAccount).setOnClickListener {
            onExit()
        }
        viewModel.ApiGetCurrentUser.observe(this) { userAPI ->
            val img = findViewById<ImageView>(R.id.imageViewAvatar)
            Picasso.get().load(userAPI.image_url)
                .placeholder(R.drawable.baseline_account_circle_24)
                .error(R.drawable.search_result)
                .into(img)
            val welcomeText = "Добро пожаловать, ${userAPI.name}!"
            findViewById<TextView>(R.id.textViewWelcome).text = welcomeText
        }
        viewModel.getLocalDB(this).getDao().getAllUser().asLiveData().observe(this) {
            for (user in it) {
                if (user.isInAcc) {
                    viewModel.getCurrentUser(user.id_user)
                }
            }
        }


    }

    fun onEnterClick(view: View) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        if (view.id == R.id.buttonDriver) {
            intent.putExtra(ARG_USER_STATUS, "Driver")
            startActivity(intent)
        }
        else {
            intent.putExtra(ARG_USER_STATUS, "User")
            startActivity(intent)
        }
    }

    private fun onExit() {
        val localDB = viewModel.getLocalDB(this)
        localDB.getDao().getAllUser().asLiveData().observe(this) {
            if (it.isNotEmpty()) {
                for (user in it){
                    if (user.isInAcc) {
                        CoroutineScope(Dispatchers.IO).launch {
                            localDB.getDao().updateUserisAcc(
                                user.id_user,
                               false
                            )
                        }
                        val intent = Intent(this, IntroActivity::class.java)
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