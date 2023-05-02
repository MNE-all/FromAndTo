package com.mne4.fromandto.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mne4.fromandto.R
import com.mne4.fromandto.databinding.FragmentMyRequestBinding

class MyRequestFragment : Fragment() {
    lateinit var binding:FragmentMyRequestBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMyRequestBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapTabItem.setOnClickListener {

        }
        binding.dataTabItem.setOnClickListener {

        }

    }
    companion object {

        @JvmStatic
        fun newInstance() = MyRequestFragment()
    }
}