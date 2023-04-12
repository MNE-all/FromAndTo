package com.mne4.fromandto.Observe

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mne4.fromandto.Models.GetUserRoom
import com.mne4.fromandto.Models.Trips
import com.mne4.fromandto.Models.User

open class DataModelUsers: ViewModel() {

    val ApiGetUserAll: MutableLiveData<ArrayList<User>> by lazy {
        MutableLiveData<ArrayList<User>>()
    }
    val ApiGetCurrentUser: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }

    val ApiGetAuthentication: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val ApiPostNewUser: MutableLiveData<GetUserRoom> by lazy {
        MutableLiveData<GetUserRoom>()
    }
    val ApiPutEditUser: MutableLiveData<GetUserRoom> by lazy {
        MutableLiveData<GetUserRoom>()
    }




}