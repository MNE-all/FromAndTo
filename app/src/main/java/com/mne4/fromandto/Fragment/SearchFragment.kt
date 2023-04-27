package com.mne4.fromandto.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.mne4.fromandto.R
import com.mne4.fromandto.databinding.FragmentSearchBinding
import java.text.SimpleDateFormat
import java.util.*

class SearchFragment : Fragment() {
lateinit var binding: FragmentSearchBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
        val date = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Дата выезда")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        date.addOnPositiveButtonClickListener {
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
            val result = "${simpleDateFormat.format(it.first)} - ${simpleDateFormat.format(it.second)}"
            binding.textInputEditTextWhere.setText(result)
        }



        // Слушатели для нажатия на "Куда"
        binding.textInputEditTextWhere.setOnClickListener {
            date.show(super.getParentFragmentManager(), "DATE_PICKER")
        }
        binding.textInputEditTextWhere.setOnFocusChangeListener { _, b ->
            if (b) {
                date.show(super.getParentFragmentManager(), "DATE_PICKER")
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}