package com.mne4.fromandto.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mne4.fromandto.Adapter.MyOrdersAdapter
import com.mne4.fromandto.Data.DataModel
import com.mne4.fromandto.Data.Retrofit2.Models.MyOrder
import com.mne4.fromandto.Data.Retrofit2.Models.TripsFull
import com.mne4.fromandto.Data.Retrofit2.Models.User
import com.mne4.fromandto.Data.Retrofit2.Models.UserFull
import com.mne4.fromandto.Data.Room.DAO.DaoUser
import com.mne4.fromandto.Data.Room.MainDB
import com.mne4.fromandto.databinding.FragmentMyRequestBinding
import okhttp3.internal.notify
import okhttp3.internal.notifyAll

class MyRequestFragment : Fragment() {
    lateinit var binding: FragmentMyRequestBinding
    private val viewModel: DataModel by activityViewModels()
    lateinit var listMyOrders: MutableList<MyOrder>
    private lateinit var adapter: RecyclerView.Adapter<MyOrdersAdapter.MyOrderViewHolder>
    lateinit var userStatus: String
    var count: Int = 0
    lateinit var userID: String
    lateinit var tripsArray: ArrayList<TripsFull>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMyRequestBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.let {activ->
            viewModel.UserStatus.observe(activ) {
                userStatus = it
                var layoutManager: RecyclerView.LayoutManager =
                    LinearLayoutManager(
                        binding.root.context.applicationContext,
                        RecyclerView.VERTICAL,
                        false
                    )
                binding.recyclearMyOrders.layoutManager = layoutManager
                getCurrentUser(activ)
                ObserveTripsAdapter(activ)
                ObserveAddMyOrderAdapter(activ)
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listMyOrders = mutableListOf()



        binding.RequestTabItem.setOnClickListener {
            binding.progressBar2.isVisible = true
            if (userStatus == "User") {
                viewModel.getMyOrdersTripsCurrentUser(userID, false)
            } else {
                viewModel.getMyOrdersTripsCurrentUser(userID, true)
            }
        }
        binding.TripsTabItem.setOnClickListener {
            binding.progressBar2.isVisible = true
            if (userStatus == "User") {
                viewModel.getMyOrdersTripsCurrentUser(userID, true)
            } else {
                viewModel.getMyOrdersTripsCurrentUser(userID, false)
            }
        }
    }

    fun getCurrentUser(activ: FragmentActivity) {
        viewModel.getLocalDB(activ).getDao().getAllUser().asLiveData().observe(activ) {
            for (user in it) {
                if (user.isInAcc) {
                    userID = user.id_user
                    if (userStatus == "User") {
                        viewModel.getMyOrdersTripsCurrentUser(userID, false)
                    } else {
                        viewModel.getMyOrdersTripsCurrentUser(userID, true)
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun ObserveAddMyOrderAdapter(activ:FragmentActivity) {
        viewModel.ApiGetTwoUser.observe(activ) {
            try {
                var trip = tripsArray[tripsArray.size - count]
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
                    "${trip.end_point}"
                )

                listMyOrders.add(tripsMyOrder)
            }catch (ex:java.lang.IndexOutOfBoundsException){
                count=1
            }
            count -= 1
            if (count == 0) {
                adapter = MyOrdersAdapter(binding.root.context.applicationContext, listMyOrders)
                binding.recyclearMyOrders.adapter = adapter
                adapter.notifyDataSetChanged()
                binding.progressBar2.isVisible = false
                return@observe
            }

        }
    }
    fun ObserveTripsAdapter(activ:FragmentActivity) {
        viewModel.ApiGetMyOrdersTripsCurrentUser.observe(activ) {
            count=0
            for (trip in it) {
                count += 1
                var clientID: String
                var driverID: String
                clientID =
                    if (trip.client_id != null) trip.client_id.toString() else MyRequestFragment.GUID_NULL
                driverID =
                    if (trip.driver_id != null) trip.driver_id.toString() else MyRequestFragment.GUID_NULL
                viewModel.getTwoUser(clientID, driverID)
            }
            listMyOrders.clear()
            tripsArray = it
            binding.txtInfoResultMyOrders.isVisible = (it.size == 0)
        }

    }
    companion object {
        const val GUID_NULL = "00000000-0000-0000-0000-000000000000"
        @JvmStatic
        fun newInstance() = MyRequestFragment()
    }
}