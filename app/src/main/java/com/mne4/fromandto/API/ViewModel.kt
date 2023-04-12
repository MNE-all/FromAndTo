package com.mne4.fromandto.API

import android.util.Log
import com.google.gson.Gson
import com.mne4.fromandto.Models.GetUserRoom
import com.mne4.fromandto.Models.Trips
import com.mne4.fromandto.Models.User
import com.mne4.fromandto.Observe.DataModelTrips
import com.mne4.fromandto.Observe.DataModelUsers
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

    private lateinit var users: GetUserRoom
    private lateinit var usersApi: UsersApi
    private lateinit var tripsApi: TripsApi
    private lateinit var registr_sms: SmsApi
    val dataModelUsers = DataModelUsers()
    val dataModelTrips = DataModelTrips()
    private val api_sms_key = "812064B6-84ED-BE83-505C-E729039CB70A"

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
    fun getUserAll()
    {
        CoroutineScope(Dispatchers.Main).launch {
           var users = usersApi.getAll()
            dataModelUsers.ApiGetUserAll.value = users
        }
    }

    fun getCurrentUser(guid:String)
    {
        CoroutineScope(Dispatchers.Main).launch {
           var user = usersApi.getCurrentUser(guid)
            dataModelUsers.ApiGetCurrentUser.value = user
        }
    }

    fun getAuthentication(phone:String, password:String){
        CoroutineScope(Dispatchers.Main).launch {
           var truth = usersApi.getAuthentication(phone, password)
            dataModelUsers.ApiGetAuthentication.value = truth
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
                        list.password,
                        list.surname,
                        list.name,
                       list.birthday,
                        list.gender,
                        list.phone
                    )
                    dataModelUsers.ApiPostNewUser.value = users
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
                    var list = Gson().fromJson("""${response.body()?.string()}""", GetUserRoom::class.java)
                    users = GetUserRoom(
                        list.id_user,
                        list.password,
                        list.surname,
                        list.name,
                        list.birthday,
                        list.gender,
                        list.phone
                    )
                    dataModelUsers.ApiPutEditUser.value = users
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

    fun getTripsAll()
    {

        CoroutineScope(Dispatchers.Main).launch {
            var list =  tripsApi.getAll()
            dataModelTrips.ApiGetTripsAll.value = list
        }
    }

    fun getCurrenTrips(guid:String)
    {
        CoroutineScope(Dispatchers.Main).launch {
            var trips = tripsApi.getCurrentTrips(guid)
            dataModelTrips.ApiGetCurrenTrips.value = trips
        }
    }

    fun getTripsByDate(date:String, start_point:String, end_point: String){

        CoroutineScope(Dispatchers.Main).launch {
            var list =  tripsApi.getTrips(date,start_point,end_point)
            dataModelTrips.ApiGetTripsByDate.value = list
        }
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