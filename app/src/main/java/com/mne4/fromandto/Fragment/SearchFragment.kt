package com.mne4.fromandto.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.mne4.fromandto.Data.DataModel
import com.mne4.fromandto.R
import com.mne4.fromandto.databinding.FragmentSearchBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class SearchFragment : Fragment() {
lateinit var binding: FragmentSearchBinding
    val viewModel: DataModel by activityViewModels()
    lateinit var textTo: String
    lateinit var textFrom: String
    var cityList = arrayOf("Санкт-Петербург","Москва")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        SpinnerFrom()
        SpinnerTo()
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
        binding.butFind.setOnClickListener{

            var txtDate = binding.textInputEditTextWhere.text.toString()
            if(txtDate != "") {
                var date = txtDate.split(" ")
                val inputFormat: DateFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
                val outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
                val date1: Date?
                date1 = outputFormat.parse(date[0]) as Date
                val outputDate1: String = inputFormat.format(date1)
                val date2: Date?
                date2 = outputFormat.parse(date[2]) as Date
                val outputDate2: String = inputFormat.format(date2)
                try {
                    viewModel.getReadDateStartToDateEndToFrom(
                        outputDate1,
                        outputDate2,
                        textFrom,
                        textTo
                    )
                } catch (e: java.lang.IndexOutOfBoundsException) {

                }
            }
            viewModel.ApiGetTripsReadDateStartToDateEndToFrom.observe(requireActivity()) {
                for (trip in it) {
                    Log.d("array<trips>", "${trip}")
                }
            }
        }
    }
    private fun SpinnerTo(){
        var arrayAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, cityList)
        binding.textSpinnerTo.adapter = arrayAdapter
        binding.textSpinnerTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                textTo = cityList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

    }
    private fun SpinnerFrom(){
        var arrayAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, cityList)
        binding.textSpinnerFrom.adapter = arrayAdapter
        binding.textSpinnerFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                textFrom = cityList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

    }
    companion object {

        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}