package com.mne4.fromandto.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mne4.fromandto.Adapter.FindAdapter
import com.mne4.fromandto.Adapter.MyOrdersAdapter
import com.mne4.fromandto.Data.DataModel
import com.mne4.fromandto.Data.Retrofit2.Models.MyOrder
import com.mne4.fromandto.Data.Retrofit2.Models.Trips
import com.mne4.fromandto.Data.Retrofit2.Models.TripsFull
import com.mne4.fromandto.Data.Room.MainDB
import com.mne4.fromandto.databinding.FragmentMyRequestBinding

class MyRequestFragment : Fragment() {
    lateinit var binding:FragmentMyRequestBinding
    private val viewModel: DataModel by activityViewModels()
    lateinit var listMyOrders: MutableList<MyOrder>
    private lateinit var adapter: RecyclerView.Adapter<MyOrdersAdapter.MyOrderViewHolder>
    lateinit var userStatus:String
    var count:Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMyRequestBinding.inflate(inflater)
        viewModel.UserStatus.observe(requireActivity()){
            userStatus = it
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listMyOrders = mutableListOf()
        var db = MainDB.getDB(requireContext()).getDao()
        count = 0
        var layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclearMyOrders.layoutManager = layoutManager
        var isInAcc:Boolean
        binding.RequestTabItem.setOnClickListener {
            isInAcc = true
            db.getAllUser().asLiveData().observe(requireActivity()) {
                for(user in it){
                    if(user.isInAcc && isInAcc){
                        isInAcc = false
                        if(userStatus == "User"){
                            viewModel.getMyOrdersTripsCurrentUser(user.id_user, false)
                        }else{
                            viewModel.getMyOrdersTripsCurrentUser(user.id_user, true)
                        }
                        viewModel.ApiGetMyOrdersTripsCurrentUser.observe(requireActivity()){
                            for(trip in it){
                                count+=1
                                var clientID:String
                                var driverID:String
                                clientID = if(trip.client_id != null) trip.client_id.toString() else MyRequestFragment.GUID_NULL
                                driverID = if(trip.driver_id != null) trip.driver_id.toString() else MyRequestFragment.GUID_NULL
                                viewModel.getTwoUser(clientID,driverID)
                                ObserveAdapter(trip)
                            }
                            binding.txtInfoResultMyOrders.isVisible = (it.size == 0)
                        }
                    }
                }

            }
        }
        binding.TripsTabItem.setOnClickListener {
            isInAcc = true
            db.getAllUser().asLiveData().observe(requireActivity()) {
                for(user in it){
                    if(user.isInAcc && isInAcc){
                        isInAcc = false
                        if(userStatus == "User"){
                            viewModel.getMyOrdersTripsCurrentUser(user.id_user, true)
                        }else{
                            viewModel.getMyOrdersTripsCurrentUser(user.id_user, false)
                        }
                        viewModel.ApiGetMyOrdersTripsCurrentUser.observe(requireActivity()){
                            for(trip in it){
                                count+=1
                                var clientID:String
                                var driverID:String
                                clientID = if(trip.client_id != null) trip.client_id.toString() else MyRequestFragment.GUID_NULL
                                driverID = if(trip.driver_id != null) trip.driver_id.toString() else MyRequestFragment.GUID_NULL
                                viewModel.getTwoUser(clientID,driverID)
                                ObserveAdapter(trip)
                            }
                            binding.txtInfoResultMyOrders.isVisible = (it.size == 0)
                        }
                    }
                }

            }
        }

    }

    fun ObserveAdapter(trip: TripsFull){
        listMyOrders.clear()
        viewModel.ApiGetTwoUser.observe(requireActivity()){
            var tripsMyOrder = MyOrder(
                "${it[0].surname}",
                "${it[0].image_url}",
                it[0].raiting,
                "${it[0].phone}",
                "${it[1].surname}",
                "${it[1].image_url}",
                it[1].raiting,
                "${it[1].phone}",
                "${trip.start_time}",
                trip.price,
                "${trip.description}",
                trip.seats_amount,
                "${trip.start_point}",
                "${trip.end_point}")

            listMyOrders.add(tripsMyOrder)
            count -= 1
            if (count == 0) {
                adapter = MyOrdersAdapter(requireContext(), listMyOrders)
                binding.recyclearMyOrders.adapter = adapter
            }

        }
    }
    companion object {
        const val GUID_NULL = "00000000-0000-0000-0000-000000000000"
        @JvmStatic
        fun newInstance() = MyRequestFragment()
    }
}