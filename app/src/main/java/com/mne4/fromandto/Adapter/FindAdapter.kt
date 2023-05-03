package com.mne4.fromandto.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mne4.fromandto.Data.Retrofit2.Models.FindRequest
import com.mne4.fromandto.R
import com.mne4.fromandto.databinding.ActivityFindBinding
import com.mne4.fromandto.databinding.ViewholderOrderBinding
import com.squareup.picasso.Picasso

class FindAdapter(): RecyclerView.Adapter<FindAdapter.FindViewHolder>() {

    lateinit var context: Context
    lateinit var findList:MutableList<FindRequest>
    constructor(context: Context,FindRequest:MutableList<FindRequest>):this(){
        this.context = context
        this.findList = FindRequest
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindViewHolder {
        var orderItems:View = LayoutInflater.from(context).inflate(R.layout.viewholder_order,parent,false)
        return FindViewHolder(orderItems)
    }

    override fun getItemCount():Int{
        return findList.size
    }

    override fun onBindViewHolder(holder: FindViewHolder, position: Int) {
        holder.surname.text = findList.get(position).Surname
        holder.rating.text = findList.get(position).Rating.toString()
        Picasso.get().load(findList.get(position).Image_url.toString())
            .placeholder(R.drawable.baseline_account_circle_24)
            .error(R.drawable.baseline_account_circle_24).into(holder.image_url)
        holder.start_time.text = findList.get(position).Start_Time
        holder.price.text = findList.get(position).Price.toString()
        holder.start_point.text = findList.get(position).Start_Point
        holder.end_point.text = findList.get(position).End_Point
    }

    class FindViewHolder: RecyclerView.ViewHolder{
        lateinit var surname:TextView
        lateinit var rating:TextView
        lateinit var image_url: ImageView
        lateinit var start_time: TextView
        lateinit var price: TextView
        lateinit var start_point: TextView
        lateinit var end_point: TextView
        constructor(itemView: View):super(itemView){
            var binding = ViewholderOrderBinding.bind(itemView)
            surname =binding.txtName
            rating =binding.txtRating
            image_url =binding.imgUrl
            start_time =binding.txtTimeE
            price =binding.txtPriceOne
            start_point =binding.txtStartPoint
            end_point =binding.txtEndPoint
        }
    }
}