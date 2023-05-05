package com.mne4.fromandto

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mne4.fromandto.Adapter.FindAdapter
import com.mne4.fromandto.Data.DataModel
import com.mne4.fromandto.Data.Retrofit2.Models.FindRequest
import com.mne4.fromandto.Data.Retrofit2.Models.TripsFull
import com.mne4.fromandto.Data.Retrofit2.Models.User
import com.mne4.fromandto.databinding.ActivityFindBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FindActivity : AppCompatActivity() {
    lateinit var binding: ActivityFindBinding
    lateinit var listFind: MutableList<FindRequest>
    var trips = mutableListOf<TripsFull>()
    var count:Int = 0
    var userStatus = ""
    var outputDate1: String = "null"
    var outputDate2: String = "null"
    var txtFrom: String = "null"
    var txtTo: String = "null"
    private lateinit var adapter: RecyclerView.Adapter<FindAdapter.FindViewHolder>
    private val viewModel: DataModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFindBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listFind = mutableListOf()
        count = 0
        var layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.recyclerFind.layoutManager = layoutManager
        userStatus = intent.getStringExtra(WelcomeActivity.ARG_USER_STATUS).toString()
        recuclear()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewModel.ApiGetTripsReadByStartPointEndPoint.observe(this) {
            RequestSeacrh(it)
        }
        viewModel.ApiGetTripsReadDateStartToDateEndToFrom.observe(this) {
            RequestSeacrh(it)
        }
        viewModel.ApiGetTripsReadByStart.observe(this) {
            RequestSeacrh(it)
        }
        viewModel.ApiGetTripsReadDateStartToDateEndFrom.observe(this) {
            RequestSeacrh(it)
        }
        viewModel.ApiGetCurrentUser.observe(this) {
            getRequestSearch(it)
        }
    }


    fun recuclear() {
        outputDate1= intent.getStringExtra("outputDate1").toString()
        outputDate2= intent.getStringExtra("outputDate2").toString()
        txtFrom = intent.getStringExtra("txtFrom").toString()
        txtTo = intent.getStringExtra("txtTo").toString()


        if(outputDate1 != "null" &&
            outputDate2 != "null" &&
            txtFrom != "" &&
            txtTo != "")
        {
            ReadDateStartToDateEndToFrom()
        }else if(outputDate1 != "null" &&
            outputDate2 != "null" && txtFrom != "")
        {
            ReadDateStartToDateEndFrom()
        }else if(txtFrom != "" &&
                txtTo != "")
        {
            ReadByStartPointEndPoint()
        }else if(txtFrom !=""){
            ReadByStart()
        }




    }
    fun ReadDateStartToDateEndToFrom(){
        viewModel.getReadDateStartToDateEndToFrom(
            outputDate1,
            outputDate2,
            txtFrom,
            txtTo
        )
    }
    fun ReadDateStartToDateEndFrom() {
        viewModel.getReadDateStartToDateEndFrom(
            outputDate1,
            outputDate2,
            txtFrom
        )
    }
    fun ReadByStartPointEndPoint() {
        viewModel.getReadByStartPointEndPoint(
            txtFrom,
            txtTo
        )

    }
    fun ReadByStart() {
        viewModel.getReadByStart(
            txtFrom
        )
    }

    fun RequestSeacrh(it: ArrayList<TripsFull>){
        listFind.clear()
        var notNull = false
        for (trip in it) {

            if (trip.driver_id != null && userStatus == "User") {
                notNull = true
                trips.add(trip)
                viewModel.getCurrentUser(trip.driver_id.toString())
            }
            else if (trip.client_id != null && userStatus == "Driver") {
                notNull = true
                trips.add(trip)
                viewModel.getCurrentUser(trip.client_id.toString())
            }
        }
        if (!notNull) {
            binding.progressBar.isVisible = false
            binding.txtInfoResult.isVisible = true
            binding.txtInfoResult.text = "Ничего не найдено!"
        }
    }
    fun getRequestSearch(user: User){
        if (listFind.size < trips.size) {
            val trip = trips[count]
            var tripsFind = FindRequest(
                trip.id_trip,
                "${user.surname}",
                "${user.image_url}",
                user.raiting,
                "${user.phone}",
                "${trip.start_time}",
                trip.price,
                "${trip.description}",
                "${trip.start_point}",
                "${trip.end_point}",
                trip.seats_amount,
                true
            )
            listFind.add(tripsFind)
            if (count + 1 < trips.size) {
                count += 1
            } else {
                adapter = FindAdapter(this, listFind)
                binding.recyclerFind.adapter = adapter
                binding.recyclerFind.addOnChildAttachStateChangeListener(object :
                    OnChildAttachStateChangeListener {
                    override fun onChildViewAttachedToWindow(view: View) {
                        var buttonMore = view.findViewById<Button>(R.id.btnFindMore)
                        buttonMore?.setOnClickListener {
                            var position =
                                binding.recyclerFind.findContainingViewHolder(view)!!.position
                            var currentTrip = listFind[position]

                            var bottomSheetDialog = BottomSheetDialog(view.context)
                            bottomSheetDialog.setContentView(R.layout.order_bottom_sheet_dialog)

                            bottomSheetDialog.findViewById<TextView>(R.id.txtAddressBegin)?.text = currentTrip.Start_Point
                            bottomSheetDialog.findViewById<TextView>(R.id.txtAddressEnd)?.text = currentTrip.End_Point
                            bottomSheetDialog.findViewById<TextView>(R.id.textDescription)?.text = currentTrip.Descreption
                            val price = "Цена: ${currentTrip.Price} руб."
                            bottomSheetDialog.findViewById<TextView>(R.id.tripPrice)?.text = price

                            bottomSheetDialog.findViewById<ImageView>(R.id.imgPhone)?.setOnClickListener {
                                val tel = currentTrip.Phone
                                val dial = "tel: $tel"
                                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(dial)))
                            }

                            val outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
                            val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)

                            val date: Date?
                            if (!currentTrip.Start_Time.isNullOrEmpty()) {
                                date = inputFormat.parse(currentTrip.Start_Time) as Date
                                val outputText: String = outputFormat.format(date)
                                bottomSheetDialog.findViewById<TextView>(R.id.txtSetDate)?.text = outputText
                            }
                            val btnResponse = bottomSheetDialog.findViewById<Button>(R.id.btnResponse)

                            btnResponse?.setOnClickListener {
                                //TODO Отклик на заказ
                            }
                            bottomSheetDialog.show()
                        }

                    }

                    override fun onChildViewDetachedFromWindow(view: View) {

                    }

                })
                binding.progressBar.isVisible = false
            }
        }
    }
}