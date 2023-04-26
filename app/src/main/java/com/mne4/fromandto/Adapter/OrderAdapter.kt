package com.mne4.fromandto.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mne4.fromandto.Data.Retrofit2.Models.Order
import com.mne4.fromandto.R

class OrderAdapter(): RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    lateinit var context: Context
    lateinit var orders:MutableList<Order>
    constructor(context: Context,order:MutableList<Order>):this(){
        this.context = context
        this.orders = order
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        var orderItems:View = LayoutInflater.from(context).inflate(R.layout.viewholder_order,parent,false)
        return OrderViewHolder(orderItems)
    }

    override fun getItemCount():Int{
        return orders.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.orderName.text = orders.get(position).FIO
        holder.orderTimeBegin.text = orders.get(position).TimeBegin
        holder.orderTimeEnd.text = orders.get(position).TimeEnd
        holder.orderPrice.text = orders.get(position).Price.toString()
        holder.orderTripBegin.text = orders.get(position).TripBegin
        holder.orderTripEnd.text = orders.get(position).TripEnd
        holder.orderMark.text = orders.get(position).Mark.toString()
    }

    class OrderViewHolder: RecyclerView.ViewHolder{
        lateinit var orderName:TextView
        lateinit var orderTimeBegin:TextView
        lateinit var orderTimeEnd:TextView
        lateinit var orderPrice:TextView
        lateinit var orderTripBegin:TextView
        lateinit var orderTripEnd:TextView
        lateinit var orderMark:TextView
        constructor(itemView: View):super(itemView){
            orderName = itemView.findViewById(R.id.txtName)
            orderTimeBegin = itemView.findViewById(R.id.txtName)
            orderTimeEnd = itemView.findViewById(R.id.txtName)
            orderPrice = itemView.findViewById(R.id.txtName)
            orderTripBegin = itemView.findViewById(R.id.txtName)
            orderTripEnd = itemView.findViewById(R.id.txtName)
            orderMark = itemView.findViewById(R.id.txtName)
        }
    }
}