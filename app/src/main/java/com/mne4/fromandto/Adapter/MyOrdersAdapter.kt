package com.mne4.fromandto.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.mne4.fromandto.Data.Retrofit2.Models.FindRequest
import com.mne4.fromandto.Data.Retrofit2.Models.MyOrder
import com.mne4.fromandto.R
import com.mne4.fromandto.databinding.FragmentMyRequestBinding
import com.mne4.fromandto.databinding.RequestAndTripsBottomSheetDialogBinding
import com.mne4.fromandto.databinding.ViewholderMyRequeestItemBinding
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MyOrdersAdapter(): RecyclerView.Adapter<MyOrdersAdapter.MyOrderViewHolder>() {

    lateinit var context: Context
    lateinit var findList:MutableList<MyOrder>
    constructor(context: Context,myOrdersRequest:MutableList<MyOrder>):this(){
        this.context = context
        this.findList = myOrdersRequest
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrderViewHolder {
        var orderItems:View = LayoutInflater.from(context).inflate(R.layout.viewholder_my_requeest_item,parent,false)
        var myRequestItems:View = LayoutInflater.from(context).inflate(R.layout.request_and_trips_bottom_sheet_dialog,parent,false)
        return MyOrderViewHolder(orderItems,myRequestItems)
    }

    override fun getItemCount():Int{
        return findList.size
    }

    override fun onBindViewHolder(holder: MyOrderViewHolder, position: Int) {
        holder.surnameUser.text = findList.get(position).SurnameUser
        holder.ratingUser.text = findList.get(position).RatingUser.toString()
        Picasso.get().load(findList.get(position).Image_urlUser.toString())
            .placeholder(R.drawable.baseline_account_circle_24)
            .error(R.drawable.baseline_account_circle_24).into(holder.image_urlUser)
        holder.surnameDriver.text = findList.get(position).SurnameDriver
        holder.ratingDriver.text = findList.get(position).RatingDriver.toString()
        Picasso.get().load(findList.get(position).Image_urlDriver.toString())
            .placeholder(R.drawable.baseline_account_circle_24)
            .error(R.drawable.baseline_account_circle_24).into(holder.image_urlDriver)

        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
        val outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
        val date: Date?
        if (!findList.get(position).Start_Time.isEmpty()) {
            date = inputFormat.parse(findList.get(position).Start_Time) as Date
            val outputText: String = outputFormat.format(date)
            holder.start_time.text = outputText
        }

        holder.price.text = findList.get(position).Price.toString()
        holder.start_point.text = findList.get(position).Start_Point
        holder.end_point.text = findList.get(position).End_Point
        holder.seats_amount.text = findList.get(position).Seats_amount.toString()
    }

    class MyOrderViewHolder: RecyclerView.ViewHolder{
        lateinit var surnameUser:TextView
        lateinit var ratingUser:TextView
        lateinit var image_urlUser: ImageView
        lateinit var surnameDriver:TextView
        lateinit var ratingDriver:TextView
        lateinit var image_urlDriver: ImageView
        lateinit var start_time: TextView
        lateinit var price: TextView
        lateinit var start_point: TextView
        lateinit var end_point: TextView
        lateinit var seats_amount: TextView

        constructor(itemView: View,myRequestItems:View):super(itemView){
            val binding = ViewholderMyRequeestItemBinding.bind(itemView)

            surnameUser = binding.txtNameUser
            ratingUser = binding.txtRatingUser
            image_urlUser = binding.imgUrlUser
            surnameDriver =binding.txtNameDriver
            ratingDriver=binding.txtRatingDriver
            image_urlDriver =binding.imgUrlDriver
            start_time = binding.txtTimeE
            price = binding.txtPriceOne
            start_point =binding.txtStartPoint
            end_point =binding.txtEndPoint
            seats_amount =binding.txtSeatsAmount
        }
    }
}