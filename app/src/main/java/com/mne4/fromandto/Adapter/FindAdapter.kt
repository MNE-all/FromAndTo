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
import com.mne4.fromandto.databinding.OrderItemBinding
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FindAdapter(): RecyclerView.Adapter<FindAdapter.FindViewHolder>() {

    lateinit var context: Context
    lateinit var findList:MutableList<FindRequest>
    constructor(context: Context,FindRequest:MutableList<FindRequest>):this(){
        this.context = context
        this.findList = FindRequest
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindViewHolder {
        var orderItems:View = LayoutInflater.from(context).inflate(R.layout.order_item,parent,false)
        return FindViewHolder(orderItems)
    }

    override fun getItemCount():Int{
        return findList.size
    }

    override fun onBindViewHolder(holder: FindViewHolder, position: Int) {

        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
        val outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
        val date: Date?
        if (findList[position].Start_Time.isNotEmpty()) {
            date = inputFormat.parse(findList[position].Start_Time) as Date
            val outputText: String = outputFormat.format(date)
            holder.start_time.text = outputText
        }
        holder.surname.text = findList[position].Name
        holder.rating.text = findList[position].Rating.toString()
        Picasso.get().load(findList[position].Image_url.toString())
            .placeholder(R.drawable.baseline_account_circle_24)
            .error(R.drawable.baseline_account_circle_24).into(holder.image_url)
        holder.price.text = findList[position].Price.toString()
        holder.start_point.text = findList[position].Start_Point
        holder.start_point.isSelected = true
        holder.end_point.text = findList[position].End_Point
    }

    class FindViewHolder: RecyclerView.ViewHolder{
        var surname:TextView
        var rating:TextView
        var image_url: ImageView
        var start_time: TextView
        var price: TextView
        var start_point: TextView
        var end_point: TextView
        constructor(itemView: View):super(itemView){
            var binding = OrderItemBinding.bind(itemView)
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