package com.mne4.fromandto.API

import android.util.Log
import android.view.View
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.gson.Gson
import com.mne4.fromandto.Models.GetUserRoom
import com.mne4.fromandto.Models.Trips
import com.mne4.fromandto.Models.User
import com.mne4.fromandto.Observe.DataModel
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
import retrofit2.http.Path

@OptIn(ExperimentalMultiplatform::class)
class ViewModel{

    private lateinit var users: GetUserRoom
    private lateinit var usersApi: UsersApi
    private lateinit var tripsApi: TripsApi
    private lateinit var registr_sms: SmsApi
    val dataModel = DataModel()
    private val api_sms_key = "92ECBB50-F17D-BC2B-0205-63A4B6210D31"

    constructor(){
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        var retrofit = Retrofit.Builder()
            .baseUrl("http://fromandtoapi.somee.com").client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var retrofit_sms =  Retrofit.Builder()
            .baseUrl("https://sms.ru").client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        usersApi = retrofit.create(UsersApi::class.java)
        tripsApi = retrofit.create(TripsApi::class.java)

        registr_sms = retrofit_sms.create(SmsApi::class.java)
    }
    fun getUserAll(): ArrayList<User>
    {
        var list: ArrayList<User> = arrayListOf()
        CoroutineScope(Dispatchers.IO).launch {
           list =  usersApi.getAll()

        }
        return list
    }

    fun getCurrentUser(guid:String)
    {
        CoroutineScope(Dispatchers.Main).launch {
           var user = usersApi.getCurrentUser(guid)
            dataModel.ApiReturnCurrentUser.value = user
        }
    }



    fun postNewUser(user:User)
    {
        CoroutineScope(Dispatchers.IO).launch {
            usersApi.postNewUser(user).enqueue(object : retrofit2.Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    var list = Gson().fromJson("""${response.body()?.string()}""", GetUserRoom::class.java)
                    users = GetUserRoom(
                        list.id_user,
                        list.surname,
                        list.name,
                       list.birthday,
                        list.gender,
                        list.phone
                    )
                    dataModel.ApiReturnUser.value = users
                    Log.d("Post","Response")
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("Post","Failture")
                }
            })
        }
    }

    fun putEditUser(guid:String, hashPassword: String, user: User)
    {
        CoroutineScope(Dispatchers.IO).launch {
            usersApi.putEditUser(guid, hashPassword, user).enqueue(object : retrofit2.Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("Put","Response")
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("Put","Failture")
                }
            })
        }
    }

    fun deleteUser(guid: String, password: String){
        CoroutineScope(Dispatchers.IO).launch {
            usersApi.deleteUser(guid,password).enqueue(object : retrofit2.Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("Delete","Response")
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("Delete","Failture")
                }
            })
        }
    }

    fun getTripsAll(): ArrayList<Trips>
    {
        var list: ArrayList<Trips> = arrayListOf()
        CoroutineScope(Dispatchers.IO).launch {
            list =  tripsApi.getAll()
        }
        return list
    }

    fun getCurrenTrips(guid:String):Trips?
    {
        var trips: Trips? = null
        CoroutineScope(Dispatchers.IO).launch {
            trips = tripsApi.getCurrentTrips(guid)
        }
        return trips
    }

    fun postCreateTrips(guid:String, trips: Trips)
    {
        CoroutineScope(Dispatchers.IO).launch {
            tripsApi.postCreateTrips(guid, trips).enqueue(object : retrofit2.Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("Post","Response Trips")
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("Post","Failture")
                }
            })
        }
    }
    fun postCreateRequest(guid:String, trips: Trips)
    {
        CoroutineScope(Dispatchers.IO).launch {
            tripsApi.postCreateRequest(guid, trips).enqueue(object : retrofit2.Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("Post","Response Request")
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("Post","Failture")
                }
            })
        }
    }

    fun putEditTrips(guid:String, trips: Trips)
    {
        CoroutineScope(Dispatchers.IO).launch {
            tripsApi.putEditTrips(guid, trips).enqueue(object : retrofit2.Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("Put","Response")
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("Put","Failture")
                }
            })
        }
    }

    fun deleteTrips(guid: String){
        CoroutineScope(Dispatchers.IO).launch {
            tripsApi.deleteTrips(guid).enqueue(object : retrofit2.Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("Delete","Response")
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("Delete","Failture")
                }
            })
        }
    }

    fun postSMS(phone: String): Int
    {
        var password = (1000..10000).random()
        CoroutineScope(Dispatchers.IO).launch {
            registr_sms.postSMS(api_sms_key, phone,"Пожалуйста введите пароль: ${password}",1).enqueue(object : retrofit2.Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("Post","SMS send ")
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("Post","SMS Failture")
                }
            })
        }
        return password
    }

}