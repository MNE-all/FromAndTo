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
        return MyOrderViewHolder(orderItems)
    }

    override fun getItemCount():Int{
        return findList.size
    }

    override fun onBindViewHolder(holder: MyOrderViewHolder, position: Int) {
        holder.surnameUser.text = findList.get(position).User.surname
        holder.phoneUser.text = findList.get(position).User.phone
        holder.ratingUser.text = findList.get(position).User.raiting.toString()
        Picasso.get().load(findList.get(position).User.image_url.toString())
            .placeholder(R.drawable.baseline_account_circle_24)
            .error(R.drawable.baseline_account_circle_24).into(holder.image_urlUser)
        holder.surnameDriver.text = findList.get(position).Driver.surname
        holder.phoneDriver.text = findList.get(position).Driver.phone
        holder.ratingDriver.text = findList.get(position).Driver.raiting.toString()
        Picasso.get().load(findList.get(position).Driver.image_url.toString())
            .placeholder(R.drawable.baseline_account_circle_24)
            .error(R.drawable.baseline_account_circle_24).into(holder.image_urlDriver)

        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
        val outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
        val date: Date?
        if (!findList.get(position).TripsFull.start_time.isEmpty()) {
            date = inputFormat.parse(findList.get(position).TripsFull.start_time) as Date
            val outputText: String = outputFormat.format(date)
            holder.start_time.text = outputText
        }

        holder.price.text = findList.get(position).TripsFull.price.toString()
        holder.id_trip.text = findList.get(position).TripsFull.id_trip
        holder.start_point.text = findList.get(position).TripsFull.start_point
        holder.end_point.text = findList.get(position).TripsFull.end_point
        holder.seats_amount.text = findList.get(position).TripsFull.seats_amount.toString()
        holder.start_coord.text = findList.get(position).TripsFull.start_point_coord
        holder.end_coord.text = findList.get(position).TripsFull.end_point_coord
        holder.start_time.text = findList.get(position).TripsFull.status
    }

    class MyOrderViewHolder: RecyclerView.ViewHolder{
        lateinit var surnameUser:TextView
        lateinit var ratingUser:TextView
        lateinit var image_urlUser: ImageView
        lateinit var phoneUser: TextView
        lateinit var surnameDriver:TextView
        lateinit var ratingDriver:TextView
        lateinit var image_urlDriver: ImageView
        lateinit var phoneDriver: TextView
        lateinit var id_trip: TextView
        lateinit var start_time: TextView
        lateinit var price: TextView
        lateinit var start_point: TextView
        lateinit var end_point: TextView
        lateinit var seats_amount: TextView
        lateinit var start_coord: TextView
        lateinit var end_coord: TextView
        lateinit var status: TextView

        constructor(itemView: View):super(itemView){
            val binding = ViewholderMyRequeestItemBinding.bind(itemView)

            surnameUser = binding.txtNameUser
            ratingUser = binding.txtRatingUser
            image_urlUser = binding.imgUrlUser
            phoneUser = binding.txtUserPhone
            surnameDriver =binding.txtNameDriver
            ratingDriver=binding.txtRatingDriver
            image_urlDriver =binding.imgUrlDriver
            phoneDriver = binding.txtDriverPhone
            id_trip = binding.idTrip
            start_time = binding.txtTimeE
            price = binding.txtPriceOne
            start_point =binding.txtStartPoint
            end_point =binding.txtEndPoint
            seats_amount =binding.txtSeatsAmount
            start_coord =binding.txtCoordStart
            end_coord =binding.txtCoordEnd
            status =binding.txtStatusTrips
        }
    }
}