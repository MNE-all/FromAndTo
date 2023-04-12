package com.mne4.fromandto.Observe

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mne4.fromandto.Models.GetUserRoom
import com.mne4.fromandto.Models.Trips
import com.mne4.fromandto.Models.User

open class DataModelTrips: ViewModel() {

    val ApiGetTripsAll: MutableLiveData<ArrayList<Trips>> by lazy {
        MutableLiveData<ArrayList<Trips>>()
    }
    val ApiGetCurrenTrips: MutableLiveData<Trips> by lazy {
        MutableLiveData<Trips>()
    }

    val ApiGetTripsByDate: MutableLiveData<ArrayList<Trips>> by lazy {
        MutableLiveData<ArrayList<Trips>>()
    }





}