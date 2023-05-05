package com.mne4.fromandto.Fragment

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
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
import com.mne4.fromandto.databinding.ViewholderMyRequeestItemBinding
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class MyRequestFragment : Fragment() {
    lateinit var binding: FragmentMyRequestBinding
    private val viewModel: DataModel by activityViewModels()
    lateinit var listMyOrder: MutableList<MyOrder>
    private lateinit var adapter: RecyclerView.Adapter<MyOrdersAdapter.MyOrderViewHolder>
    lateinit var userStatus: String
    var count: Int = 0
    lateinit var userID: String
    lateinit var tripsArray: ArrayList<TripsFull>
    lateinit var fragmentActivity: FragmentActivity
    lateinit var bottomSheetDialog: BottomSheetDialog
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
        ObserveDeleteTrips()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listMyOrder = mutableListOf()
        bottomSheetDialog = BottomSheetDialog(view.context)
        binding.recyclearMyOrders.addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                var position = binding.recyclearMyOrders.findContainingViewHolder(view)!!.position
                view.findViewById<AppCompatButton>(R.id.butViewMore).setOnClickListener {

                    bottomSheetDialog.setContentView(R.layout.request_and_trips_bottom_sheet_dialog)
                    bottomSheetDialog.show()
                    var list = listMyOrder[position]
                    var status =  list.TripsFull.status
                    var txtStatus =   bottomSheetDialog.findViewById<TextView>(R.id.txtStatus)
                    if(status =="true"){
                        txtStatus?.text = "Активен"
                    }else{
                        txtStatus?.text = "Закрыт"
                    }
                    bottomSheetDialog.findViewById<TextView>(R.id.txtNameUser)?.text = list.User.surname
                    bottomSheetDialog.findViewById<TextView>(R.id.txtNameDriver)?.text = list.Driver.surname

                    val inputFormat: DateFormat =SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
                    val outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
                    val date: Date?
                    date = inputFormat.parse(list.TripsFull.start_time) as Date
                    val outputText: String = outputFormat.format(date)
                    bottomSheetDialog.findViewById<TextView>(R.id.txtDateStart)?.text = outputText

                    bottomSheetDialog.findViewById<TextView>(R.id.txtRatingUser)?.text = list.User.raiting.toString()
                    bottomSheetDialog.findViewById<TextView>(R.id.txtRatingDriver)?.text = list.Driver.raiting.toString()

                    bottomSheetDialog.findViewById<TextView>(R.id.txtPriceOne)?.text = list.TripsFull.price.toString()
                    bottomSheetDialog.findViewById<TextView>(R.id.txtSeats_amount)?.text = list.TripsFull.seats_amount.toString()

                    bottomSheetDialog.findViewById<TextView>(R.id.textComment)?.text = list.TripsFull.description


                    val geoCoder = Geocoder(requireContext(), Locale.getDefault())
                    var masStart = list.TripsFull.start_point_coord.split(" ")
                    val addressStart = geoCoder.getFromLocation(masStart[0].toDouble(), masStart[1].toDouble(), 2)
                    var nameAdressStart = addressStart?.get(0)?.getAddressLine(0)
                    bottomSheetDialog.findViewById<TextView>(R.id.txtAddressStart)?.text = nameAdressStart.toString()

                    var masEnd = list.TripsFull.end_point_coord.split(" ")
                    val addressEnd = geoCoder.getFromLocation(masEnd[0].toDouble(), masEnd[1].toDouble(), 2)
                    var nameAdressEnd= addressEnd?.get(0)?.getAddressLine(0)
                    bottomSheetDialog.findViewById<TextView>(R.id.txtAddressEnd)?.text = nameAdressEnd.toString()

                    if(userStatus == "User") {
                        bottomSheetDialog.findViewById<TextView>(R.id.txtPhoneUser)?.text =
                            if (!list.User.phone.isNullOrEmpty()) list.User.phone else "Пусто"

                        bottomSheetDialog.findViewById<TextView>(R.id.txtPhoneDriver)?.text =
                            if (!list.Driver.phone.isNullOrEmpty()) list.Driver.phone else "Пусто"
                    }

                    Picasso.get().load(list.User.image_url)
                        .placeholder(R.drawable.baseline_account_circle_24)
                        .error(R.drawable.baseline_account_circle_24)
                        .into(bottomSheetDialog.findViewById(R.id.imgUrlUser))

                    Picasso.get().load(list.Driver.image_url)
                        .placeholder(R.drawable.baseline_account_circle_24)
                        .error(R.drawable.baseline_account_circle_24)
                        .into(bottomSheetDialog.findViewById(R.id.imgUrlDriver))

                    bottomSheetDialog.findViewById<AppCompatButton>(R.id.txtDeleteTrips)?.setOnClickListener {
                        var dialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                        dialog.setTitle("Подтверждение удаления!")
                        dialog.setMessage("Подтвердите что вы хотите удалить эту поездку!")
                        dialog.setPositiveButton(
                            "Подтвердить",
                            DialogInterface.OnClickListener { dialog: DialogInterface?, i ->
                                viewModel.deleteTrips(list.TripsFull.id_trip)
                            })
                        dialog.setNegativeButton("Отменить",
                            DialogInterface.OnClickListener { dialog: DialogInterface?, i ->
                            dialog?.dismiss()
                        })

                        dialog.show()
                    }
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

    fun ObserveDeleteTrips(){
        viewModel.ApiDeleteTrips.observe(requireActivity()){
            if(it){
                Toast.makeText(requireContext(),"Успешно поездка удалена!",Toast.LENGTH_SHORT).show()
                bottomSheetDialog.dismiss()
                AnimationBut(true)
                InitMyRequest()


            }
        }
    }
    fun InitMyRequest(){
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
    fun AnimationBut(isLoad:Boolean){
        binding.progressBar2.isVisible = isLoad
        binding.recyclearMyOrders.isVisible = !isLoad
    }
    fun getCurrentUser(activ: FragmentActivity) {
        viewModel.getLocalDB(activ).getDao().getAllUser().asLiveData().observe(activ) {
            for (user in it) {
                if (user.isInAcc) {
                    userID = user.id_user
                    InitMyRequest()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun ObserveAddMyOrderAdapter(activ: FragmentActivity) {
        viewModel.ApiGetTwoUser.observe(activ) {

            if (listMyOrder.size != tripsArray.size) {
                var trip = tripsArray[count]
                var tripsMyOrder = MyOrder(
                    it[0],
                    it[1],
                    trip
                )
                listMyOrder.add(tripsMyOrder)
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
        adapter = MyOrdersAdapter(binding.root.context.applicationContext, listMyOrder)
        binding.recyclearMyOrders.adapter = adapter
        adapter.notifyDataSetChanged()
        AnimationBut(false)
        binding.txtInfoResultMyOrders.isVisible = false
        count = 0
    }
    fun ObserveTripsAdapter(activ:FragmentActivity) {
        viewModel.ApiGetMyOrdersTripsCurrentUser.observe(activ) {
            listMyOrder.clear()
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