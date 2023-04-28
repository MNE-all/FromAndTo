package com.mne4.fromandto.Fragment

import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.mne4.fromandto.Data.DataModel
import com.mne4.fromandto.R


private const val ARG_TITLE = "title"
private const val ARG_DESCRIPTION = "description"
private const val ARG_POSITION = "position"
private const val ARG_IMAGE = "image"
class OnboardFragment : Fragment() {

    val viewModel: DataModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding, container, false)
        val textViewOnboardTitle = view.findViewById<TextView>(R.id.textViewOnboardTitle)
        val textViewDescription = view.findViewById<TextView>(R.id.textViewDescription)
        val imageViewOnboard = view.findViewById<ImageView>(R.id.imageViewOnboard)
        arguments?.let {
            textViewOnboardTitle.text = it.getString(ARG_TITLE)
            textViewDescription.text =it.getString(ARG_DESCRIPTION)
            imageViewOnboard.setImageDrawable(view.context.getDrawable(it.getInt(ARG_IMAGE)))
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        viewModel.onboardPosition.value = arguments?.getInt(ARG_POSITION)
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String, description: String, position: Int, image: Int) =
            OnboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_DESCRIPTION, description)
                    putInt(ARG_POSITION, position)
                    putInt(ARG_IMAGE, image)
                }
            }
    }
}