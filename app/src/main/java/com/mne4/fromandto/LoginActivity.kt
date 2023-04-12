package com.mne4.fromandto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mne4.fromandto.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    fun butEnter(view: View){
        var phone = binding.editTextPhone.text
        var password = binding.editTextPassword.text


    }

    fun register (view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}