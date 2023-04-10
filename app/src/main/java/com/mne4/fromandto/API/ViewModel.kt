package com.mne4.fromandto.API

import android.util.Log
import com.google.gson.Gson
import com.mne4.fromandto.Models.GetUserRoom
import com.mne4.fromandto.Models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
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
    fun GetUserAll(): ArrayList<User>
    {
        var list: ArrayList<User> = arrayListOf()
        CoroutineScope(Dispatchers.IO).launch {
           list =  usersApi.GetAll()
        }
        return list
    }

    fun GetCurrentUser(guid:String):User?
    {
        var user: User? = null
        CoroutineScope(Dispatchers.IO).launch {
           user = usersApi.GetCurrentUser(guid)
        }
        return user
    }
    fun PostNewUser(user:User):GetUserRoom?
    {
        var getUserRoom: GetUserRoom? = null
        CoroutineScope(Dispatchers.IO).launch {
            usersApi.PostNewUser(user).enqueue(object : retrofit2.Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    var list = Gson().fromJson("""${response.body()!!.string()}""", GetUserRoom::class.java)
                    getUserRoom = GetUserRoom(
                       list.id_user,
                       list.surname,
                       list.name,
                       list.birthday,
                       list.gender,
                       list.phone
                    )
                    Log.d("Post","Response")
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("Post","Failture")
                }
            })
        }
        return getUserRoom
    }

    fun PutEditUser(guid:String, user: User)
    {
        CoroutineScope(Dispatchers.IO).launch {
            usersApi.PutEditUser(guid, user).enqueue(object : retrofit2.Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("Put","Response")
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("Put","Failture")
                }
            })
        }
    }
}