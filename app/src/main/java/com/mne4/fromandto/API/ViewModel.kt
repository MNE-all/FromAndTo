package com.mne4.fromandto.API

import android.util.Log
import com.google.gson.Gson
import com.mne4.fromandto.Models.GetUserRoom
import com.mne4.fromandto.Models.Trips
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
    fun GetTripsAll(): ArrayList<Trips>
    {
        var list: ArrayList<Trips> = arrayListOf()
        CoroutineScope(Dispatchers.IO).launch {
            list =  tripsApi.GetAll()
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

    fun GetCurrenTrips(guid:String):Trips?
    {
        var trips: Trips? = null
        CoroutineScope(Dispatchers.IO).launch {
            trips = tripsApi.GetCurrentTrips(guid)
        }
        return trips
    }
    fun PostCreateTrips(guid:String, trips: Trips)
    {
        CoroutineScope(Dispatchers.IO).launch {
            tripsApi.PostCreateTrips(guid, trips).enqueue(object : retrofit2.Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("Post","Response Trips")
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("Post","Failture")
                }
            })
        }
    }
    fun PostCreateRequest(guid:String, trips: Trips)
    {
        CoroutineScope(Dispatchers.IO).launch {
            tripsApi.PostCreateRequest(guid, trips).enqueue(object : retrofit2.Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("Post","Response Request")
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("Post","Failture")
                }
            })
        }
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
    fun PutEditTrips(guid:String, trips: Trips)
    {
        CoroutineScope(Dispatchers.IO).launch {
            tripsApi.PutEditTrips(guid, trips).enqueue(object : retrofit2.Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("Put","Response")
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("Put","Failture")
                }
            })
        }
    }
    fun DeleteUser(guid: String){
        CoroutineScope(Dispatchers.IO).launch {
            usersApi.DeleteUser(guid).enqueue(object : retrofit2.Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("Delete","Response")
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("Delete","Failture")
                }
            })
        }
    }

    fun DeleteTrips(guid: String){
        CoroutineScope(Dispatchers.IO).launch {
            tripsApi.DeleteTrips(guid).enqueue(object : retrofit2.Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("Delete","Response")
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("Delete","Failture")
                }
            })
        }
    }
}