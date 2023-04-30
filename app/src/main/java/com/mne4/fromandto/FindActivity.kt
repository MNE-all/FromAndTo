package com.mne4.fromandto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mne4.fromandto.Adapter.FindAdapter
import com.mne4.fromandto.Data.DataModel
import com.mne4.fromandto.Data.Retrofit2.Models.Example
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
            val list = intent.getParcelableArrayListExtra<FindRequest>("arrayTripsFull") as MutableList<FindRequest>
            var layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this,
                RecyclerView.VERTICAL,false)
            binding.recyclerFind.layoutManager = layoutManager
            adapter = FindAdapter(this,list)
            binding.recyclerFind.adapter = adapter
    }
}