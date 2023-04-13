package com.mne4.fromandto

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.MapKitFactory

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_load_screen)
        // Initialize the library to load required native libraries.
        // Warning! It's heavy operation
        MapKitFactory.setApiKey("429ae64e-46c4-4b6a-aebe-e8ef49cbc0c5")
        MapKitFactory.initialize(this)

        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}