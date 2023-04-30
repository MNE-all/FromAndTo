package com.mne4.fromandto.Fragment.Search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mne4.fromandto.Adapter.FindAdapter
import com.mne4.fromandto.Adapter.OrderAdapter
import com.mne4.fromandto.Data.DataModel
import com.mne4.fromandto.Data.Retrofit2.Models.FindRequest
import com.mne4.fromandto.Data.Retrofit2.Models.Trips
import com.mne4.fromandto.Data.Retrofit2.Models.TripsFull
import com.mne4.fromandto.R
import com.mne4.fromandto.databinding.FragmentFindBinding

private const val Array_TRIPS = "ArrayTrips"

class FindFragment : Fragment() {
    private lateinit var binding: FragmentFindBinding
    lateinit var listFind: MutableList<FindRequest>
    val viewModel: DataModel by activityViewModels()
    private lateinit var adapter: RecyclerView.Adapter<FindAdapter.FindViewHolder>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentFindBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.ApiGetTripsReadDateStartToDateEndToFrom.observe(requireActivity()) {

             for(trips in it){
                 if(trips.driver_id != null){
                     viewModel.getCurrentUser(trips.driver_id)
                     viewModel.ApiGetCurrentUser.observe(requireActivity()){
                         var tripsFind = FindRequest(
                            "${it.surname}",
                             "${it.image_url}",
                             it.raiting,
                             "${it.phone}",
                             "${trips.start_time}",
                             trips.price,
                             "${trips.description}",
                             "${trips.start_point}",
                             "${trips.end_point}",
                             trips.seats_amount,
                             true
                         )
                         listFind.add(tripsFind)
                     }
                 }
             }

        }
        var layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        binding.recyclerFindFragment.layoutManager = layoutManager
        adapter = FindAdapter(requireContext(),listFind)
        binding.recyclerFindFragment.adapter = adapter
    }
    companion object {
        @JvmStatic
        fun newInstance() = FindFragment()
    }
}