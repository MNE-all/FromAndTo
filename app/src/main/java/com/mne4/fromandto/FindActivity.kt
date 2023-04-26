package com.mne4.fromandto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Locale


class FindActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find)

        val where = findViewById<TextInputEditText>(R.id.TextInputEditTextWhere)


        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
        val date = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Дата выезда")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        date.addOnPositiveButtonClickListener {
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
            val result = "${simpleDateFormat.format(it.first)} - ${simpleDateFormat.format(it.second)}"
            where.setText(result)
        }



        // Слушатели для нажатия на "Куда"
        where.setOnClickListener {
            date.show(supportFragmentManager, "DATE_PICKER")
        }
        where.setOnFocusChangeListener { _, b ->
            if (b) {
                date.show(supportFragmentManager, "DATE_PICKER")
            }
        }

    }
}