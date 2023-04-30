package com.mne4.fromandto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mne4.fromandto.Adapter.FindAdapter
import com.mne4.fromandto.Data.DataModel
import com.mne4.fromandto.Data.Retrofit2.Models.FindRequest
import com.mne4.fromandto.Fragment.Search.SearchFragment
import com.mne4.fromandto.databinding.ActivityFindBinding

class FindActivity : AppCompatActivity() {
    lateinit var binding: ActivityFindBinding
    lateinit var listFind: MutableList<FindRequest>
    private lateinit var adapter: RecyclerView.Adapter<FindAdapter.FindViewHolder>
    private val viewModel: DataModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listFind = mutableListOf()
        recuclear()

    }
    fun recuclear(){
        var outputDate1:String = intent.getStringExtra("outputDate1").toString()
        var outputDate2:String = intent!!.getStringExtra("outputDate2").toString()
        var txtFrom = intent.getStringExtra("txtFrom").toString()
        var txtTo = intent.getStringExtra("txtTo").toString()
        viewModel.getReadDateStartToDateEndToFrom(
            outputDate1,
            outputDate2,
            txtFrom,
            txtTo
        )
        viewModel.ApiGetTripsReadDateStartToDateEndToFrom.observe(this) {
            for (trips in it) {
                if (trips.driver_id != null) {
                    viewModel.getCurrentUser(trips.driver_id.toString())
                    viewModel.ApiGetCurrentUser.observe(this) {
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
            var layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
            binding.recyclerFind.layoutManager = layoutManager
            adapter = FindAdapter(this,listFind)
            binding.recyclerFind.adapter = adapter
        }

    }
}