package com.mne4.fromandto.API

import android.os.Bundle
import android.util.Log
import com.mne4.fromandto.Models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.internal.userAgent
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMultiplatform::class)
class ViewModel{

    private lateinit var usersApi: UsersApi
    private lateinit var tripsApi: TripsApi

    constructor(){
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        var retrofit = Retrofit.Builder()
            .baseUrl("http://fromandtoapi.somee.com").client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        usersApi = retrofit.create(UsersApi::class.java)
        tripsApi = retrofit.create(TripsApi::class.java)
    }
    fun GetUserAll()
    {
        CoroutineScope(Dispatchers.IO).launch {
            usersApi.GetAll()
        }
    }

    fun GetCurrentUser(guid:String)
    {
        CoroutineScope(Dispatchers.IO).launch {
            usersApi.GetCurrentUser(guid)
        }
    }
    fun PostNewUser(user:User)
    {
        CoroutineScope(Dispatchers.IO).launch {
            usersApi.PostNewUser(user)
        }
    }

}