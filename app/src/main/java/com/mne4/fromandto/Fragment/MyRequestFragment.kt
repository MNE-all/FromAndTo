package com.mne4.fromandto.Fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mne4.fromandto.Adapter.MyOrdersAdapter
import com.mne4.fromandto.Data.DataModel
import com.mne4.fromandto.Data.Retrofit2.Models.MyOrder
import com.mne4.fromandto.Data.Retrofit2.Models.TripsFull
import com.mne4.fromandto.MainActivity
import com.mne4.fromandto.R
import com.mne4.fromandto.databinding.FragmentMyRequestBinding
import com.mne4.fromandto.databinding.RequestAndTripsBottomSheetDialogBinding
import com.mne4.fromandto.databinding.ViewholderMyRequeestItemBinding
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MyRequestFragment : Fragment() {
    lateinit var binding: FragmentMyRequestBinding
    private val viewModel: DataModel by activityViewModels()
    lateinit var listMyOrders: MutableList<MyOrder>
    private lateinit var adapter: RecyclerView.Adapter<MyOrdersAdapter.MyOrderViewHolder>
    lateinit var userStatus: String
    var count: Int = 0
    lateinit var userID: String
    lateinit var tripsArray: ArrayList<TripsFull>
    lateinit var fragmentActivity: FragmentActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMyRequestBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.let { activ ->
            fragmentActivity = activ as MainActivity
        }
        viewModel.UserStatus.observe(fragmentActivity) {
            userStatus = it
            var layoutManager: RecyclerView.LayoutManager =
                LinearLayoutManager(
                    binding.root.context.applicationContext,
                    RecyclerView.VERTICAL,
                    false
                )
            binding.recyclearMyOrders.layoutManager = layoutManager
        }
        getCurrentUser(fragmentActivity)
        ObserveTripsAdapter(fragmentActivity)
        ObserveAddMyOrderAdapter(fragmentActivity)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listMyOrders = mutableListOf()

        binding.recyclearMyOrders.addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                var bindingDialogItem = ViewholderMyRequeestItemBinding.bind(view)
                bindingDialogItem.butViewMore.setOnClickListener {
                    var bottomSheetDialog = BottomSheetDialog(requireContext())
                    bottomSheetDialog.setContentView(R.layout.request_and_trips_bottom_sheet_dialog)
                    bottomSheetDialog.show()
                    bottomSheetDialog.findViewById<TextView>(R.id.txtNameDriver)?.text = "Слава"
                    bottomSheetDialog.findViewById<TextView>(R.id.txtNameUser)?.text = "Петя"

                    var start_time =bindingDialogItem.txtTimeE.text.toString()
                    val inputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
                    val outputFormat: DateFormat = SimpleDateFormat("d MMM yyyy", Locale.ROOT)
                    val date: Date?
                    date = inputFormat.parse(start_time) as Date
                    val outputText: String = outputFormat.format(date)
                    bottomSheetDialog.findViewById<TextView>(R.id.txtDateStart)?.text = outputText

                }
            }

            override fun onChildViewDetachedFromWindow(view: View) {
            }

        })



        binding.RequestTabItem.setOnClickListener {
            AnimationBut(true)
            binding.RequestTabItem.setBackgroundResource(R.drawable.custom_button_style2)
            binding.TripsTabItem.setBackgroundResource(R.drawable.custom_button_style_noactive)
            viewModel.getMyOrdersTripsCurrentUser(userID, false)
        }
        binding.TripsTabItem.setOnClickListener {
            AnimationBut(true)
            binding.TripsTabItem.setBackgroundResource(R.drawable.custom_button_style2)
            binding.RequestTabItem.setBackgroundResource(R.drawable.custom_button_style_noactive)
            viewModel.getMyOrdersTripsCurrentUser(userID, true)

        }
    }

    fun AnimationBut(isLoad:Boolean){
        binding.progressBar2.isVisible = isLoad
        binding.recyclearMyOrders.isVisible = !isLoad
    }
    fun getCurrentUser(activ: FragmentActivity) {
        viewModel.getLocalDB(activ).getDao().getAllUser().asLiveData().observe(activ) {
            for (user in it) {
                if (user.isInAcc) {
                    userID = user.id_user
                    if (userStatus == "User") {
                        binding.RequestTabItem.setBackgroundResource(R.drawable.custom_button_style2)
                        binding.TripsTabItem.setBackgroundResource(R.drawable.custom_button_style_noactive)
                        viewModel.getMyOrdersTripsCurrentUser(userID, false)
                    } else {
                        binding.TripsTabItem.setBackgroundResource(R.drawable.custom_button_style2)
                        binding.RequestTabItem.setBackgroundResource(R.drawable.custom_button_style_noactive)
                        viewModel.getMyOrdersTripsCurrentUser(userID, true)
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun ObserveAddMyOrderAdapter(activ: FragmentActivity) {
        listMyOrders.clear()
        viewModel.ApiGetTwoUser.observe(activ) {

            if (listMyOrders.size != tripsArray.size) {
                var trip = tripsArray[count]
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
                if (count + 1 < tripsArray.size) {
                    count += 1
                }
                else {
                    UpdateAdapter()
                    return@observe
                }
            }
        }
    }
    fun UpdateAdapter(){
        adapter = MyOrdersAdapter(binding.root.context.applicationContext, listMyOrders)
        binding.recyclearMyOrders.adapter = adapter
        adapter.notifyDataSetChanged()
        AnimationBut(false)
        binding.txtInfoResultMyOrders.isVisible = false
        count = 0
    }
    fun ObserveTripsAdapter(activ:FragmentActivity) {
        viewModel.ApiGetMyOrdersTripsCurrentUser.observe(activ) {
            listMyOrders.clear()
            tripsArray = it
            for (trip in it) {
                var clientID: String
                var driverID: String
                clientID =
                    if (trip.client_id != null) trip.client_id.toString() else MyRequestFragment.GUID_NULL
                driverID =
                    if (trip.driver_id != null) trip.driver_id.toString() else MyRequestFragment.GUID_NULL

                viewModel.getTwoUser(clientID, driverID)
            }
            if(it.size == 0) {
                binding.progressBar2.isVisible = false
                binding.txtInfoResultMyOrders.isVisible = true
            }
        }

    }
    companion object {
        const val GUID_NULL = "00000000-0000-0000-0000-000000000000"
        @JvmStatic
        fun newInstance() = MyRequestFragment()
    }
}