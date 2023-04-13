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
import com.google.android.material.snackbar.Snackbar
import com.mne4.fromandto.databinding.ActivityProfileBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SpinnerGender()
        DateDialog()
    }

    private fun updateText(calendar: Calendar){
        val dateFormat = "dd-MM-yyyy"
        var simple = SimpleDateFormat(dateFormat,Locale.UK)
        binding.txtCalendar.text = simple.format(calendar.time)
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
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

    }
    private fun DateDialog(){
        //TODO Календарь доработать
        var calendarBox = Calendar.getInstance()

        var dateBox = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->

            calendarBox.set(Calendar.YEAR, year)
            calendarBox.set(Calendar.MONTH, month)
            calendarBox.set(Calendar.DAY_OF_MONTH, day)

            updateText(calendarBox)
        }
        val dialog = DatePickerDialog(this, dateBox,    calendarBox.get(Calendar.YEAR),   calendarBox.get(Calendar.MONTH), calendarBox.get(Calendar.DAY_OF_MONTH))
        dialog.datePicker.maxDate = Date().time

        binding.txtCalendar.setOnClickListener {
            DatePickerDialog(
                this,
                dateBox,
                calendarBox.get(Calendar.YEAR),
                calendarBox.get(Calendar.MONTH),
                calendarBox.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
}
