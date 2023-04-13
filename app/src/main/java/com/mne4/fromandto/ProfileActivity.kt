package com.mne4.fromandto

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mne4.fromandto.databinding.ActivityProfileBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.time.Duration.Companion.days


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var maxDate:LocalDate
    private lateinit var minDate:LocalDate
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        maxDate = LocalDate.now()
        minDate = maxDate.minusYears(100)


        var calendarBox = Calendar.getInstance()

        var dateBox = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            calendarBox.set(Calendar.YEAR, year)
            calendarBox.set(Calendar.MONTH, month)
            calendarBox.set(Calendar.DAY_OF_MONTH, day)

            updateText(calendarBox)
        }
        var datePicker: DatePickerDialog = DatePickerDialog(this,dateBox,2023,4,12)

         datePicker.datePicker.maxDate = (maxDate.year).toLong()
        datePicker.datePicker.minDate = (minDate.year).toLong()


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

    private fun updateText(calendar: Calendar){


        val dateFormat = "dd-MM-yyyy"
        var simple = SimpleDateFormat(dateFormat,Locale.UK)
        binding.txtCalendar.text = simple.format(calendar.time)




    }
}
