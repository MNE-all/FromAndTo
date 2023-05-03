package com.mne4.fromandto.Fragment.Search

import android.content.Intent
import android.os.Bundle
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
import com.mne4.fromandto.FindActivity
import com.mne4.fromandto.R
import com.mne4.fromandto.WelcomeActivity
import com.mne4.fromandto.databinding.FragmentSearchBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class SearchFragment : Fragment() {
lateinit var binding: FragmentSearchBinding
    val viewModel: DataModel by activityViewModels()
    lateinit var textTo: String
    lateinit var textFrom: String
    var userStatus = ""
    lateinit var fragmentView: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)

        binding.textInputEditTextTo.isEnabled = false

        fragmentView = binding.root
        return fragmentView
    }

    override fun onResume() {
        super.onResume()
        viewModel.UserStatus.observe(requireActivity()) {
            userStatus = it
            From()
            To()
        }
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
        binding.butFind.setOnClickListener {

            var txtDate = binding.textInputEditTextWhere.text.toString()
            var txtFrom = binding.textInputEditTextFrom.text.toString()
            var txtTo = binding.textInputEditTextTo.text.toString()

            val intent = Intent(requireContext(), FindActivity::class.java)
            intent.putExtra(WelcomeActivity.ARG_USER_STATUS, userStatus)
            var outputDate1:String = "null"
            var outputDate2:String = "null"
            if (txtDate != "") {
                var date = txtDate.split(" ")
                val inputFormat: DateFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
                val outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
                val date1: Date?
                date1 = outputFormat.parse(date[0]) as Date
                outputDate1 = inputFormat.format(date1)
                val date2: Date?
                date2 = outputFormat.parse(date[2]) as Date
                outputDate2 = inputFormat.format(date2)
            }
            intent.putExtra("outputDate1", outputDate1)
            intent.putExtra("outputDate2", outputDate2)
            intent.putExtra("txtFrom", txtFrom)
            intent.putExtra("txtTo", txtTo)
            startActivity(intent)
        }
    }
    private fun From() {
        if(userStatus == "User") viewModel.getCityFrom(false)
        else if(userStatus == "Driver") viewModel.getCityFrom(true)

        activity?.let {
            viewModel.ApiGetTripsCityFrom.observe(requireActivity()) {
                val adapter: ArrayAdapter<String> = ArrayAdapter(
                    fragmentView.context.applicationContext,
                    R.layout.style_dropdown_item,
                    it
                )
                binding.textInputEditTextFrom.setAdapter(adapter)
            }
        }




    }
    private fun To(){
        binding.textInputEditTextFrom.addTextChangedListener {

            var startPoint = binding.textInputEditTextFrom.text.toString()
            binding.butFind.isEnabled = (startPoint!="")

            if (!startPoint.isNullOrEmpty()) {
                if(userStatus == "User") {
                    viewModel.getCityTo(startPoint, false)
                }
                else if(userStatus == "Driver") {
                    viewModel.getCityTo(startPoint, true)
                }
                    viewModel.ApiGetTripsCityTo.observe(requireActivity()) {
                        val adapter: ArrayAdapter<String> = ArrayAdapter(
                            requireContext(),
                            R.layout.style_dropdown_item,
                            it
                        )
                        binding.textInputEditTextTo.isEnabled = true
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
