package com.mne4.fromandto

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.mne4.fromandto.databinding.ActivityIntroLoginRegisterBinding

class RegisterActivity : AppCompatActivity() {
    lateinit var viewModel:viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro_act10)

        viewModel=viewModel()
    }

   fun butReg(view: View){

        var Surname = findViewById<TextInputEditText>(R.id.surnameField).text
        var Name = findViewById<TextInputEditText>(R.id.nameField).text
        var Phone = "7"+findViewById<TextInputEditText>(R.id.phoneField).text
        var Password = findViewById<TextInputEditText>(R.id.passwordField).text

       if(Surname!!.isEmpty()){
         Toast.makeText(this, "Неправильно введена фамилия!", Toast.LENGTH_SHORT).show()
           return
       }
       if(Name!!.isEmpty()){
           Toast.makeText(this, "Неправильно введено имя!", Toast.LENGTH_SHORT).show()
           return
       }
       if(Phone.isEmpty()){
           Toast.makeText(this, "Неправильно введен телефон!", Toast.LENGTH_SHORT).show()
           return
       }
       if(Password!!.isEmpty()){
           Toast.makeText(this, "Неправильно введен пароль!", Toast.LENGTH_SHORT).show()
           return
       }

       var dialog:AlertDialog.Builder = AlertDialog.Builder(this)
       dialog.setTitle("Подтверждение номера")
       dialog.setMessage("Поддтвердите номер телефона. Введите пароль по смс!")
       viewModel.
       var inflater: LayoutInflater = LayoutInflater.from(this)
       var reg_windom = inflater.inflate(R.layout.intro_act10,null)
       dialog.setView(reg_windom)

       dialog.setPositiveButton("Подтвердить",DialogInterface.OnClickListener{dialog: DialogInterface?, i->



       })






    }
}