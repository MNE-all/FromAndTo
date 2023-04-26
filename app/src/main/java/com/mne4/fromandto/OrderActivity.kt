package com.mne4.fromandto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mne4.fromandto.Adapter.OrderAdapter
import com.mne4.fromandto.Fragment.Domain.OrderDomain
import com.mne4.fromandto.databinding.ActivityOrderBinding

class OrderActivity : AppCompatActivity() {
    lateinit var binding: ActivityOrderBinding
    private lateinit var adapter: RecyclerView.Adapter<OrderAdapter.OrderViewHolder>
    private lateinit var recyclerViewOrderList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var orderList = mutableListOf<OrderDomain>()

        orderList.add(
            OrderDomain("Вячеслав","10:00","11:00",10000,
            "ул. Комендантский проспект, СПб","ул. Комендантский проспект, Москва",
            4.0)
        )
        orderList.add(
            OrderDomain("Николай","06:00","15:00",4000,
            "ул. Комендантский проспект, Урал","ул. Комендантский проспект, Мурманск",
            4.0)
        )
        orderList.add(
            OrderDomain("Дмитрий","00:00","01:00",3000,
            "ул. Комендантский проспект, Волга","ул. Комендантский проспект, Хабаровск",
            4.0)
        )
        orderList.add(
            OrderDomain("Данила","04:00","08:40",14000,
            "ул. Комендантский проспект, Новосибирск","ул. Комендантский проспект, Калиниград",
            4.0)
        )

        setOrderRecycler(orderList)

    }

    private fun setOrderRecycler(orderList: MutableList<OrderDomain>){
        var layoutManager:RecyclerView.LayoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        binding.recyclerOrder.layoutManager = layoutManager

        adapter = OrderAdapter(this,orderList)
        binding.recyclerOrder.adapter = adapter
    }

}