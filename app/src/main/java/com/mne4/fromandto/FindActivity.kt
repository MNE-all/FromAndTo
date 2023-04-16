package com.mne4.fromandto

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class FindActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find)

        var where = findViewById<TextInputEditText>(R.id.TextInputEditTextWhere)


        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
        var date = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Дата выезда")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()
        date.addOnPositiveButtonClickListener {
            where.setText("${it.first} - ${it.second}")
        }



        where.setOnFocusChangeListener { view, b ->
            if (b) {
                date.show(supportFragmentManager, "DATE_PICKER")
            }
        }

    }
}