package com.mne4.fromandto.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mne4.fromandto.Adapter.FindAdapter
import com.mne4.fromandto.Data.DataModel
import com.mne4.fromandto.Data.Retrofit2.Models.FindRequest
import com.mne4.fromandto.Data.Room.MainDB
import com.mne4.fromandto.R
import com.mne4.fromandto.databinding.FragmentMyRequestBinding

class MyRequestFragment : Fragment() {
    lateinit var binding:FragmentMyRequestBinding
    private val viewModel: DataModel by activityViewModels()
    lateinit var listMyOrders: MutableList<FindRequest>
    private lateinit var adapter: RecyclerView.Adapter<FindAdapter.FindViewHolder>
    lateinit var userStatus:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMyRequestBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var db = MainDB.getDB(requireContext()).getDao()
        var layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclearMyOrders.layoutManager = layoutManager
        binding.RequestTabItem.setOnClickListener {
            db.getAllUser().asLiveData().observe(requireActivity()) {
                for(user in it){
                    if(user.isInAcc){
                        if(userStatus == "User"){
                            viewModel.getMyOrdersTripsCurrentUser(user.id_user, false)
                        }else{
                            viewModel.getMyOrdersTripsCurrentUser(user.id_user, true)
                        }
                        viewModel.ApiGetMyOrdersTripsCurrentUser.observe(requireActivity()){
//                            var tripsFind = FindRequest(
//                                "${user.surname}",
//                                "${user.image_url}",
//                                user.raiting,
//                                "${user.phone}",
//                                "${trips.start_time}",
//                                trips.price,
//                                "${trips.description}",
//                                "${trips.start_point}",
//                                "${trips.end_point}",
//                                trips.seats_amount,
//                                true
//                            )
//                            adapter = FindAdapter(requireContext(), it.toMutableList())
                           // binding.recyclearMyOrders.adapter = adapter
                        }
                    }
                }

            }


        }
        binding.TripsTabItem.setOnClickListener {
            db.getAllUser().asLiveData().observe(requireActivity()) {
                for(user in it){
                    if(user.isInAcc){
                        if(userStatus == "User"){
                            viewModel.getMyOrdersTripsCurrentUser(user.id_user, true)
                        }else{
                            viewModel.getMyOrdersTripsCurrentUser(user.id_user, false)
                        }
                        viewModel.ApiGetMyOrdersTripsCurrentUser.observe(requireActivity()){
                           // adapter = FindAdapter(requireContext(), it)
                            //binding.recyclearMyOrders.adapter = adapter
                        }
                    }
                }

            }
        }

    }
    companion object {

        @JvmStatic
        fun newInstance() = MyRequestFragment()
    }
}