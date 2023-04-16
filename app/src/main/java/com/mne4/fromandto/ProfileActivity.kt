package com.mne4.fromandto

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.asLiveData
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.mne4.fromandto.API.ViewModel
import com.mne4.fromandto.Models.User
import com.mne4.fromandto.databinding.ActivityProfileBinding
import com.mne4.fromandto.db.MainDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private var viewModel = ViewModel()
    var gender:String = ""
    var phone:String = ""
    var password:String = ""
    private lateinit var USER: User

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        InitUser()

        Change()
        ChipActive()
        SpinnerGender()
    }

    fun InitUser(){

        var db = MainDB.getDB(this)
        db.getDao().getAllUser().asLiveData().observe(this) {
            for (user in it) {
                if (user.isInAcc) {
                    viewModel.getCurrentUser(user.id_user)
                    return@observe
                }
            }
        }

        viewModel.dataModelUsers.ApiGetCurrentUser.observe(this){
            USER = it
            binding.surnameField.setText(it.surname)
            binding.nameField.setText(it.name)
            binding.txtCalendar.setText(it.birthday)
            binding.emailField.setText(it.email)
            binding.passportField.setText(it.passport)
            binding.licenseField.setText(it.license)

        }
    }
    fun butSave(view:View){
        var db = MainDB.getDB(this)
        db.getDao().getAllUser().asLiveData().observe(this){
            for(user in it){
                if(user.isInAcc){
                    var surname = binding.surnameField.text.toString()
                    var name = binding.nameField.text.toString()
                    var email = binding.emailField.text.toString()
                    var birthday:String? = binding.txtCalendar.text.toString()
                    if(binding.txtCalendar.text.toString() == "") {
                        birthday = null
                    }
                    var passport = binding.passportField.text.toString()
                    var license = binding.licenseField.text.toString()
                    if(TextUtils.isEmpty(surname) || TextUtils.isEmpty(name)){
                        Toast.makeText(applicationContext,"Фамилия и имя не должны быть пустыми!",Toast.LENGTH_SHORT).show()
                        return@observe
                    }
                     USER.surname = surname
                     USER.name = name
                     USER.email = email
                     USER.birthday = gender
                     USER.birthday = birthday
                     USER.passport = passport
                     USER.license = license

                     if(phone!="" && password != ""){
                         if(binding.passwordField.text.toString() == binding.passwordFieldStill.text.toString()) {
                             viewModel.postIsPhoneUnique(binding.phoneField.text.toString())
                             viewModel.dataModelUsers.ApiPostIsPhoneUnique.observe(this){
                                 if(it){
                                    USER.phone = binding.phoneField.text.toString()
                                    USER.password = binding.passwordField.text.toString()
                                     viewModel.putEditUserSecure(user.id_user, user.password, USER)
                                     viewModel.dataModelUsers.ApiPutEditUser.observe(this) {
                                         CoroutineScope(Dispatchers.IO).launch {
                                             db.getDao().updateUser(it.id_user, it.password, true)
                                             return@launch
                                         }
                                     }
                                 }else{
                                     Toast.makeText(this,"Номер уже существует!",Toast.LENGTH_SHORT).show()
                                 }
                             }
                         }else{
                             Toast.makeText(this,"Пароли не совпадают!",Toast.LENGTH_SHORT).show()
                         }
                     }else {
                         viewModel.putEditUser(user.id_user, user.password, USER)
                     }
                }
            }

        }


    }

    private fun updateText(calendar: Calendar){
        val dateFormat = "dd-MM-yyyy"
        var simple = SimpleDateFormat(dateFormat,Locale.UK)
        binding.txtCalendar.text = simple.format(calendar.time)
    }

    private fun ChipActive(){
        binding.chipSecurity.setOnClickListener {
            if (binding.chipSecurity.isChecked) {
                var dialog:AlertDialog.Builder = AlertDialog.Builder(this)

                dialog.setTitle("Подтвердите данные")
                var inflater: LayoutInflater = LayoutInflater.from(this)
                var entry_data = inflater.inflate(R.layout.entry_data_user,null)
                var phones: TextInputEditText = entry_data.findViewById(R.id.phoneFieldData)
                var passwords: TextInputEditText = entry_data.findViewById(R.id.passwordFieldData)
                dialog.setView(entry_data)
                dialog.setPositiveButton("Подтвердить", DialogInterface.OnClickListener { dialog: DialogInterface?, i ->
                    phone = phones.text.toString()
                    password = passwords.text.toString()
                    viewModel.postAuthentication(phone,password)
                    viewModel.dataModelUsers.ApiPostAuthentication.observe(this) {
                        if (it != null) {
                            if (USER.password == it.password && USER.phone == it.phone) {
                                binding.chipSecurity.text = "Безопасность подтверждена"
                                isVisibleSecurity(true)
                                binding.phoneField.setText(phone)
                                binding.passwordField.setText(password)
                                binding.passwordFieldStill.setText(password)
                                return@observe
                            }
                        }
                        Toast.makeText(applicationContext, "Неправильно введенные данные!",Toast.LENGTH_SHORT).show()
                        binding.chipSecurity.text = "Пройти безопасность"
                        isVisibleSecurity(false)
                    }
                })
                dialog.show()
            }else{
                binding.chipSecurity.text = "Пройти безопасность"
                isVisibleSecurity(false)
            }
        }
    }

    private fun isVisibleSecurity(truth: Boolean){
        binding.phoneFieldLayout.isVisible = truth
        binding.passwordFieldLayout.isVisible = truth
        binding.passwordFieldLayoutStill.isVisible = truth

        binding.chipSecurity.isChecked = truth
    }
    private fun SpinnerGender(){

        var genderList = arrayOf("Мужской","Женский")
        var arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, genderList)
        binding.spinnerGender.adapter = arrayAdapter
        binding.spinnerGender.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Snackbar.make(view!!,"Вы выбрали пол: ${genderList[position]}",Snackbar.LENGTH_SHORT).show()
                gender = genderList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

    }
    fun DateDialog(view: View){


        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())
        var date = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Дата рождения")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()


        date.show(supportFragmentManager, "DATE_PICKER")
        date.addOnPositiveButtonClickListener {
            binding.txtCalendar.text = SimpleDateFormat("yyyy-MM-dd").format(it)
        }

    }

    private fun Change(){
        Init(false)
        binding.switchChange.setOnCheckedChangeListener{buttonView, isChecked->
            Init(isChecked)
        }

    }

    private fun Init(truth:Boolean){

        isVisibleSecurity(true)
        isVisibleSecurity(false)

        binding.surnameField.isEnabled = truth
        binding.nameField.isEnabled = truth
        binding.emailField.isEnabled = truth

        binding.phoneField.isEnabled = truth
        binding.passwordField.isEnabled = truth
        binding.passwordFieldStill.isEnabled = truth

        binding.passportField.isEnabled = truth
        binding.licenseField.isEnabled = truth

        binding.spinnerGender.isEnabled = truth
        binding.txtCalendar.isEnabled = truth
        binding.chipSecurity.isEnabled = truth

        binding.butSave.isEnabled = truth
    }
}
