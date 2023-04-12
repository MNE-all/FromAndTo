package com.mne4.fromandto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mne4.fromandto.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {
    lateinit var bindind: ActivityHelpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
    }
}