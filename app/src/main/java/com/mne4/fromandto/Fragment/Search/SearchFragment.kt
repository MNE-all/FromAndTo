package com.mne4.fromandto.Fragment.Search

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.mne4.fromandto.Data.DataModel
import com.mne4.fromandto.databinding.FragmentSearchBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class SearchFragment : Fragment() {
lateinit var binding: FragmentSearchBinding
    val viewModel: DataModel by activityViewModels()
    lateinit var textTo: String
    lateinit var textFrom: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        From()
        To()
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
                        binding.textInputEditTextFrom.text.toString(),
                        binding.textInputEditTextTo.text.toString()
                    )
                } catch (e: java.lang.IndexOutOfBoundsException) {

                }
            }
        }
    }

    private fun From() {
        viewModel.getCityFrom()
        viewModel.ApiGetTripsCityFrom.observe(requireActivity()) {
            val adapter: ArrayAdapter<String> = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                it
            )
            binding.textInputEditTextFrom.setAdapter(adapter)
        }

    }
    private fun To(){
        binding.textInputEditTextFrom.addTextChangedListener {
            var start_point = binding.textInputEditTextFrom.text.toString()
            if (!TextUtils.isEmpty(start_point)) {
                viewModel.getCityTo(start_point)
                viewModel.ApiGetTripsCityTo.observe(requireActivity()) {
                    val adapter: ArrayAdapter<String> = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        it
                    )
                    binding.textInputEditTextTo.setAdapter(adapter)
                }
            }
        }
    }
    companion object {

        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}