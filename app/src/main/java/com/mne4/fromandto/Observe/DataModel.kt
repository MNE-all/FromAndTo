package com.mne4.fromandto.Observe

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mne4.fromandto.Models.GetUserRoom
import com.mne4.fromandto.Models.User

open class DataModel: ViewModel() {
    val ApiReturnUser: MutableLiveData<GetUserRoom> by lazy {
        MutableLiveData<GetUserRoom>()
    }
    val ApiReturnCurrentUser: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }
    val ApiReturnUsers: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
}