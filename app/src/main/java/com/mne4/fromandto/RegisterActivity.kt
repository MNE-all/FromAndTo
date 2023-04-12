package com.mne4.fromandto

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.mne4.fromandto.API.ViewModel
import com.mne4.fromandto.db.MainDB
import com.mne4.fromandto.db.User


class RegisterActivity : AppCompatActivity() {
    lateinit var viewModel:ViewModel
    private val surname = findViewById<TextInputEditText>(R.id.surnameField)
    private val name = findViewById<TextInputEditText>(R.id.nameField)
    private val phone = findViewById<TextInputEditText>(R.id.phoneField)
    private val password = findViewById<TextInputEditText>(R.id.passwordField)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        viewModel= ViewModel()
    }

    private fun addUser() {
        val user = com.mne4.fromandto.Models.User(surname.text.toString(), name.text.toString(),null, null, null, null, password.text.toString(), phone.text.toString(), false, null, null, null, null )
        viewModel.postNewUser(user)
        var db = MainDB.getDB(this)
        viewModel.dataModelUsers.ApiPostNewUser.observe(this) {
            val user = User(
                null, it.id_user, it.password, it.surname, it.name,
                null, null, it.phone, true
            )
            db.getDao().insertUser(user)
            var intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }
    }

   @SuppressLint("MissingInflatedId")
   fun butReg(view: View){
        var phone_num = "7${phone.text}"

       if(surname.text.isNullOrEmpty()){
         Toast.makeText(this, "Неправильно введена фамилия!", Toast.LENGTH_SHORT).show()
           return
       }
       if(name.text.isNullOrEmpty()){
           Toast.makeText(this, "Неправильно введено имя!", Toast.LENGTH_SHORT).show()
           return
       }
       if(phone.text.isNullOrEmpty()){
           Toast.makeText(this, "Неправильно введен телефон!", Toast.LENGTH_SHORT).show()
           return
       }
       if(password.text.isNullOrEmpty()){
           Toast.makeText(this, "Неправильно введен пароль!", Toast.LENGTH_SHORT).show()
           return
       }

       var dialog:AlertDialog.Builder = AlertDialog.Builder(this)
       dialog.setTitle("Подтверждение номера")
       dialog.setMessage("Подтвердите номер телефона. Введите пароль по смс!")
       var passwordGenereted = viewModel.postSMS(phone_num)
       var inflater: LayoutInflater = LayoutInflater.from(this)
       var reg_window = inflater.inflate(R.layout.activity_sms_send_dialog,null)
       var number:EditText = reg_window.findViewById(R.id.editTextNumber)
       dialog.setView(reg_window)



       dialog.setPositiveButton("Подтвердить",DialogInterface.OnClickListener{dialog: DialogInterface?, i->

           var text = number.text.toString()

           if(text == passwordGenereted.toString()) {
               Toast.makeText(this, "Номер подтвержден!", Toast.LENGTH_SHORT).show()
               addUser()
           }
           else
           {
               Toast.makeText(this, "Неправильно введенный пароль!", Toast.LENGTH_SHORT).show()
           }
       })

       dialog.show()

    }
}