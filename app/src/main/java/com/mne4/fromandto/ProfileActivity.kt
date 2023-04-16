package com.mne4.fromandto

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.mne4.fromandto.API.ViewModel
import com.mne4.fromandto.Models.User
import com.mne4.fromandto.databinding.ActivityProfileBinding
import com.mne4.fromandto.db.MainDB
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private var viewModel = ViewModel()
    var gender:String = "Мужской"
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
   //                 viewModel.putEditUser(user.id_user, user.password, USER)//
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
        binding.chip.setOnClickListener {
            if (binding.chip.isChecked) {

              binding.chip.text = "Безопасность подтверждена"
            }else{
                binding.chip.text = "Пройти безопасность"
            }
        }
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
            .setValidator(DateValidatorPointForward.now())
        var date = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Дата выезда")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        date.show(supportFragmentManager, "DATE_PICKER")

    }

    private fun Change(){
        Init(false)
        binding.switchChange.setOnCheckedChangeListener{buttonView, isChecked->
            Init(isChecked)
        }

    }

    private fun Init(truth:Boolean){
        binding.chip.isChecked = false
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
        binding.chip.isEnabled = truth

        binding.butSave.isEnabled = truth
    }
}
