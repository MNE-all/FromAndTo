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
import com.mne4.fromandto.databinding.FragmentHelpBinding
import java.text.SimpleDateFormat
import java.util.*

class HelpFragment : Fragment() {

    lateinit var binding: FragmentHelpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHelpBinding.inflate(inflater)
        return binding.root
    }


    companion object {

        @JvmStatic
        fun newInstance() = HelpFragment()
    }
}