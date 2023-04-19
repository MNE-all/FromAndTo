package com.mne4.fromandto.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mne4.fromandto.R
import com.mne4.fromandto.databinding.FragmentDataRequestBinding

class DataRequestFragment : Fragment() {
    lateinit var binding: FragmentDataRequestBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
        ): View? {
        binding = FragmentDataRequestBinding.inflate(inflater)
        return  binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = DataRequestFragment()
    }
}