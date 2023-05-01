package com.mne4.fromandto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.BoringLayout
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mne4.fromandto.Adapter.FindAdapter
import com.mne4.fromandto.Data.DataModel
import com.mne4.fromandto.Data.Retrofit2.Models.FindRequest
import com.mne4.fromandto.Data.Retrofit2.Models.TripsFull
import com.mne4.fromandto.Data.Retrofit2.Models.User
import com.mne4.fromandto.Fragment.Search.SearchFragment
import com.mne4.fromandto.databinding.ActivityFindBinding

class FindActivity : AppCompatActivity() {
    lateinit var binding: ActivityFindBinding
    lateinit var listFind: MutableList<FindRequest>
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
            Log.d("All","ReadDateStartToDateEndToFrom()")
            ReadDateStartToDateEndToFrom()
        }else if(outputDate1 != "null" &&
            outputDate2 != "null")
        {
            Log.d("All","ReadDateStartToDateEnd()")
            ReadDateStartToDateEnd()
        }else if(txtFrom != "" &&
        txtTo != "")
        {
            Log.d("All","ReadByStartPointEndPoint()")
            ReadByStartPointEndPoint()
        }




    }
    fun ReadDateStartToDateEndToFrom(){
        viewModel.getReadDateStartToDateEndToFrom(
            outputDate1,
            outputDate2,
            txtFrom,
            txtTo
        )
        viewModel.ApiGetTripsReadDateStartToDateEndToFrom.observe(this) {
            RequestSeacrh(it)
        }
    }
    fun ReadDateStartToDateEnd() {
        viewModel.getReadDateStartToDateEnd(
            outputDate1,
            outputDate2
        )
        viewModel.ApiGetTripsReadDateStartToDateEnd.observe(this) {
            RequestSeacrh(it)
        }
    }
    fun ReadByStartPointEndPoint() {
        viewModel.getReadByStartPointEndPoint(
            txtFrom,
            txtTo
        )
        viewModel.ApiGetTripsReadByStartPointEndPoint.observe(this) {
            RequestSeacrh(it)
        }
    }

    fun RequestSeacrh(it: ArrayList<TripsFull>){
        var notNull = false
        for (trips in it) {
            if (trips.driver_id != null && userStatus == "User") {
                notNull = true
                count += 1
                viewModel.getCurrentUser(trips.driver_id.toString())
                viewModel.ApiGetCurrentUser.observe(this) {
                    getRequestSearch(it, trips)
                }
            } else if (trips.client_id != null && userStatus == "Driver") {
                notNull = true
                count += 1
                viewModel.getCurrentUser(trips.client_id.toString())
                viewModel.ApiGetCurrentUser.observe(this) {
                    getRequestSearch(it, trips)
                }
            }
        }
        if (!notNull) {
            binding.progressBar.isVisible = false
            binding.txtInfoResult.isVisible = true
            binding.txtInfoResult.text = "Ничего не найдено!"
        }
    }
    fun getRequestSearch(user: User, trips:TripsFull){
        var tripsFind = FindRequest(
            "${user.surname}",
            "${user.image_url}",
            user.raiting,
            "${user.phone}",
            "${trips.start_time}",
            trips.price,
            "${trips.description}",
            "${trips.start_point}",
            "${trips.end_point}",
            trips.seats_amount,
            true
        )
        listFind.add(tripsFind)
        count -= 1
        if (count == 0) {
            adapter = FindAdapter(this, listFind)
            binding.recyclerFind.adapter = adapter
            binding.progressBar.isVisible = false
        }
    }
}