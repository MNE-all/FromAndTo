package com.mne4.fromandto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mne4.fromandto.databinding.ActivityIntroLoadScreenBinding

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_load_screen)

        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}