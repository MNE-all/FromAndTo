package com.mne4.fromandto.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mne4.fromandto.Data.Retrofit2.Models.FindRequest
import com.mne4.fromandto.R

class FindAdapter(): RecyclerView.Adapter<FindAdapter.OrderViewHolder>() {

    lateinit var context: Context
    lateinit var findList:MutableList<FindRequest>
    constructor(context: Context,FindRequest:MutableList<FindRequest>):this(){
        this.context = context
        this.findList = FindRequest
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        var orderItems:View = LayoutInflater.from(context).inflate(R.layout.viewholder_order,parent,false)
        return OrderViewHolder(orderItems)
    }

    override fun getItemCount():Int{
        return findList.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {

    }

    class OrderViewHolder: RecyclerView.ViewHolder{

        constructor(itemView: View):super(itemView){

        }
    }
}