package com.mne4.fromandto

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.mne4.fromandto.API.ViewModel
import com.mne4.fromandto.db.MainDB
import com.mne4.fromandto.db.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


class RegisterActivity : AppCompatActivity() {
    lateinit var viewModel:ViewModel
    lateinit var surname: TextInputEditText
    lateinit var name :TextInputEditText
    lateinit var phone: TextInputEditText
    lateinit var password: TextInputEditText

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        surname = findViewById(R.id.surnameField)
        name = findViewById(R.id.nameField)
        phone = findViewById(R.id.phoneField)
        password = findViewById(R.id.passwordField)

        viewModel= ViewModel()
    }

    private fun addUser() {
        var intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)

        val user = com.mne4.fromandto.Models.User("${surname.text}", "${name.text}","Мужской", null, null, null, "${password.text}", "${phone.text}", false, null, null, null, null )
        var db = MainDB.getDB(this)
        viewModel.postNewUser(user)
        viewModel.dataModelUsers.ApiPostNewUser.observe(this) {
            CoroutineScope(Dispatchers.IO).launch {
                val user = User(
                    null, "${it.id_user}", "${it.password}", "${it.surname}", "${it.name}",
                    "${it.birthday}", "${it.gender}", "${it.phone}", false
                )
                db.getDao().insertUser(user)
            }



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


       addUser()

//       var dialog:AlertDialog.Builder = AlertDialog.Builder(this)
//       dialog.setTitle("Подтверждение номера")
//       dialog.setMessage("Подтвердите номер телефона. Введите пароль по смс!")
//       var passwordGenereted = viewModel.postSMS(phone_num)
//       var inflater: LayoutInflater = LayoutInflater.from(this)
//       var reg_window = inflater.inflate(R.layout.activity_sms_send_dialog,null)
//       var number:EditText = reg_window.findViewById(R.id.editTextNumber)
//       dialog.setView(reg_window)
//
//
//
//       dialog.setPositiveButton("Подтвердить",DialogInterface.OnClickListener{dialog: DialogInterface?, i->
//
//           var text = number.text.toString()
//
//           if(text == passwordGenereted.toString()) {
//               Toast.makeText(this, "Номер подтвержден!", Toast.LENGTH_SHORT).show()
//               addUser()
//           }
//           else
//           {
//               Toast.makeText(this, "Неправильно введенный пароль!", Toast.LENGTH_SHORT).show()
//           }
//       })
//
//       dialog.show()

    }
}