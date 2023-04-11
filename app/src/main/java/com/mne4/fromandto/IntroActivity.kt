package com.mne4.fromandto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mne4.fromandto.databinding.ActivityIntroLoadScreenBinding

class IntroActivity : AppCompatActivity() {
    lateinit var binding: ActivityIntroLoadScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroLoadScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}