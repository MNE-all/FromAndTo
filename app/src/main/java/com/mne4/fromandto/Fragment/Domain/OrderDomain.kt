package com.mne4.fromandto.Fragment.Domain

class OrderDomain {
    private var FIO: String = ""
    private var TimeBegin: String = ""
    private var TimeEnd: String = ""
    private var Price: Int = 0
    private var TripBegin: String = ""
    private var TripEnd: String = ""
    private var Mark: Double = 4.0

    constructor(
        FIO: String,
        TimeBegin: String,
        TimeEnd: String,
        Price: Int,
        TripBegin: String,
        TripEnd: String,
        Mark: Double
    ) {
        this.FIO = FIO
        this.TimeBegin = TimeBegin
        this.TimeEnd = TimeEnd
        this.Price = Price
        this.TripBegin = TripBegin
        this.TripEnd = TripEnd
        this.Mark = Mark
    }

    fun getFIO()= FIO
    fun getTimeBegin()= TimeBegin
    fun getTimeEnd()= TimeEnd
    fun getPrice()= Price
    fun getTripBegin()= TripBegin
    fun getTripEnd()= TripEnd
    fun getMark()= Mark

    fun setFIO(FIO: String){
        this.FIO = FIO
    }
    fun setTimeBegin(FIO: String){
        this.TimeBegin = TimeBegin
    }
    fun setTimeEnd(FIO: String){
        this.TimeEnd = TimeEnd
    }
    fun setPrice(FIO: String){
        this.Price = Price
    }
    fun setTripBegin(FIO: String){
        this.TripBegin = TripBegin
    }
    fun setTripEnd(FIO: String){
        this.TripEnd = TripEnd
    }
    fun setMark(FIO: String){
        this.Mark = Mark
    }

}