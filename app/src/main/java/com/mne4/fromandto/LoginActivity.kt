package com.mne4.fromandto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class LoginActivity : AppCompatActivity() {
    private var phoneEditText = findViewById<EditText>(R.id.editTextPhone)
    private var passwordEditText = findViewById<EditText>(R.id.editTextPassword)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


    }

    fun butEnter(view: View){
        var phone = phoneEditText.text
        var password = passwordEditText.text


    }

    fun register (view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}