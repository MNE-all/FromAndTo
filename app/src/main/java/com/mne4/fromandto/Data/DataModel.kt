package com.mne4.fromandto.Data

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.mne4.fromandto.Data.Retrofit2.API.ImageAPI
import com.mne4.fromandto.Data.Retrofit2.API.SmsApi
import com.mne4.fromandto.Data.Retrofit2.API.TripsApi
import com.mne4.fromandto.Data.Retrofit2.API.UsersApi
import com.mne4.fromandto.Data.Retrofit2.Models.*
import com.mne4.fromandto.Data.Room.MainDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import android.util.*
import retrofit2.converter.gson.GsonConverterFactory


@OptIn(ExperimentalMultiplatform::class)
class   DataModel: ViewModel() {
    fun getLocalDB(context: Context): MainDB {
        return MainDB.getDB(context)
    }
    val onboardPosition: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val UserStatus: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val ApiImgUrl: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val ApiGetTripsAll: MutableLiveData<ArrayList<TripsFull>> by lazy {
        MutableLiveData<ArrayList<TripsFull>>()
    }
    val ApiGetCurrenTrips: MutableLiveData<TripsFull> by lazy {
        MutableLiveData<TripsFull>()
    }

    val ApiGetMyOrdersTripsCurrentUser: MutableLiveData<ArrayList<TripsFull>> by lazy {
        MutableLiveData<ArrayList<TripsFull>>()
    }
    val ApiGetTripsReadDateStartToDateEndToFrom: MutableLiveData<ArrayList<TripsFull>> by lazy {
        MutableLiveData<ArrayList<TripsFull>>()
    }
    val ApiGetTripsReadDateStartToDateEndFrom: MutableLiveData<ArrayList<TripsFull>> by lazy {
        MutableLiveData<ArrayList<TripsFull>>()
    }
    val ApiGetTripsReadByStart: MutableLiveData<ArrayList<TripsFull>> by lazy {
        MutableLiveData<ArrayList<TripsFull>>()
    }
    val ApiGetTripsReadByStartPointEndPoint: MutableLiveData<ArrayList<TripsFull>> by lazy {
        MutableLiveData<ArrayList<TripsFull>>()
    }
    val ApiGetTripsCityFrom: MutableLiveData<ArrayList<String>> by lazy {
        MutableLiveData<ArrayList<String>>()
    }
    val ApiGetTripsCityTo: MutableLiveData<ArrayList<String>> by lazy {
        MutableLiveData<ArrayList<String>>()
    }
    val ApiPutTripsResponse: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val ApiDeleteTrips: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val ApiGetUserAll: MutableLiveData<ArrayList<User>> by lazy {
        MutableLiveData<ArrayList<User>>()
    }
    val ApiGetCurrentUser: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }
    val ApiGetTwoUser: MutableLiveData<ArrayList<UserFull>> by lazy {
        MutableLiveData<ArrayList<UserFull>>()
    }

    val ApiPostAuthentication: MutableLiveData<UserFull?> by lazy {
        MutableLiveData<UserFull?>()
    }

    val ApiPostAuthenticationAuto: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val ApiPostIsPhoneUnique: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }


    val ApiPostNewUser: MutableLiveData<com.mne4.fromandto.Data.Room.Models.User> by lazy {
        MutableLiveData<com.mne4.fromandto.Data.Room.Models.User>()
    }
    val ApiPutEditUser: MutableLiveData<com.mne4.fromandto.Data.Room.Models.User> by lazy {
        MutableLiveData<com.mne4.fromandto.Data.Room.Models.User>()
    }
    val ApiDeleteUser: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    private lateinit var users: com.mne4.fromandto.Data.Room.Models.User
    private var usersFull: UserFull? = null
    private lateinit var usersApi: UsersApi
    private lateinit var tripsApi: TripsApi
    private lateinit var registr_sms: SmsApi
    private var imageAPI: ImageAPI
    private val api_sms_key = "812064B6-84ED-BE83-505C-E729039CB70A"

    init{
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

        var retrofit_img = Retrofit.Builder()
            .baseUrl("http://upload-soft.photolab.me/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        imageAPI = retrofit_img.create(ImageAPI::class.java)

        usersApi = retrofit.create(UsersApi::class.java)
        tripsApi = retrofit.create(TripsApi::class.java)

        registr_sms = retrofit_sms.create(SmsApi::class.java)
    }

    fun makeImgUrl(file: MultipartBody.Part){
        CoroutineScope(Dispatchers.IO).launch {
            imageAPI.postImg(file).enqueue(object :Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    ApiImgUrl.value = response.body()?.string()
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                }
            })
        }
    }
    fun getUserAll()
    {
        CoroutineScope(Dispatchers.Main).launch {
           var users = usersApi.getAll()
            ApiGetUserAll.value = users
        }
    }

    fun getCurrentUser(guid:String)
    {
        CoroutineScope(Dispatchers.Main).launch {
           var user = usersApi.getCurrentUser(guid)
            ApiGetCurrentUser.value = user
        }
    }
    fun getTwoUser(userOne:String,userTwo:String)
    {
        CoroutineScope(Dispatchers.Main).launch {
         var user =  usersApi.getTwoUser(userOne, userTwo)
            ApiGetTwoUser.value = user

        }
    }
    fun postIsPhoneUnique(phone:String)
    {
        CoroutineScope(Dispatchers.Main).launch {
           usersApi.postIsPhoneUnique(phone).enqueue(object :Callback<Boolean>{
               override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                   ApiPostIsPhoneUnique.value = response.body()
               }
               override fun onFailure(call: Call<Boolean>, t: Throwable) {
               }
           })

        }
    }
    fun postAuthenticationAuto(guid:String, hashPassword:String){
        CoroutineScope(Dispatchers.Main).launch {
            usersApi.postAuthenticationAuto(guid, hashPassword).enqueue(object :Callback<Boolean>{
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    ApiPostAuthenticationAuto.value = response.body()
                }
                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                }
            })
        }
    }

    fun postAuthentication(phone:String, password:String){
        CoroutineScope(Dispatchers.Main).launch {
            usersApi.postAuthentication(phone, password).enqueue(object : retrofit2.Callback<ResponseBody>{
               override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                   var list = Gson().fromJson("""${response.body()?.string()}""", UserFull::class.java)
                   if(list!=null) {
                       usersFull = UserFull(
                           list.id_user,
                           list.surname,
                           list.name,
                           list.gender,
                           list.birthday,
                           list.email,
                           list.password,
                           list.phone,
                           list.isDriver,
                           list.raiting,
                           list.image_url,
                           list.passport,
                           list.license
                       )
                   }
                       ApiPostAuthentication.value = usersFull

                   Log.d("Post","Response")
               }
               override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                   Log.d("Post","Failture")
               }
           })


        }
    }

    fun postNewUser(user: User)
    {
        CoroutineScope(Dispatchers.IO).launch {
            usersApi.postNewUser(user).enqueue(object : retrofit2.Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    var list = Gson().fromJson("""${response.body()?.string()}""", com.mne4.fromandto.Data.Room.Models.User::class.java)
                    users = com.mne4.fromandto.Data.Room.Models.User(
                        list.id_user,
                        list.password
                    )
                    ApiPostNewUser.value = users
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
                    Log.d("Put","${response.body()}")
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("Put","Failture")
                }
            })
        }
    }
    fun putEditUserSecure(guid:String, hashPassword: String, user: User)
    {
        CoroutineScope(Dispatchers.IO).launch {
            usersApi.putEditUserSecure(guid, hashPassword, user).enqueue(object : retrofit2.Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    var list = Gson().fromJson("""${response.body()?.string()}""", com.mne4.fromandto.Data.Room.Models.User::class.java)
                    users = com.mne4.fromandto.Data.Room.Models.User(
                        list.id_user,
                        list.password
                    )
                    ApiPutEditUser.value = users
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
            usersApi.deleteUser(guid,password).enqueue(object : Callback<Boolean>{
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    ApiDeleteUser.value = response.body()
                    Log.d("Delete","Response")
                }
                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Log.d("Delete","Failture")
                }
            })
        }
    }

    fun getTripsAll()
    {

        CoroutineScope(Dispatchers.Main).launch {
            var list =  tripsApi.getAll()
            ApiGetTripsAll.value = list
        }
    }

    fun getCurrenTrips(guid:String)
    {
        CoroutineScope(Dispatchers.Main).launch {
            var trips = tripsApi.getCurrentTrips(guid)
            ApiGetCurrenTrips.value = trips
        }
    }
    fun getMyOrdersTripsCurrentUser(guid:String, isDriver: Boolean)
    {
        CoroutineScope(Dispatchers.Main).launch {
            var trips = tripsApi.getMyOrdersTripsCurrentUser(guid,isDriver)
            ApiGetMyOrdersTripsCurrentUser.value = trips
        }
    }

    fun getReadDateStartToDateEndToFrom(date_start:String,date_end:String, start_point:String, end_point: String){
        CoroutineScope(Dispatchers.Main).launch {
            var list =  tripsApi.getReadDateStartToDateEndToFrom(date_start,date_end,start_point,end_point)
                ApiGetTripsReadDateStartToDateEndToFrom.value = list
        }
    }
    fun getReadDateStartToDateEndFrom(date_start: String, date_end: String, start_point: String){
        CoroutineScope(Dispatchers.Main).launch {
            var list = tripsApi.getReadByDateStartDateEndFrom(date_start,date_end,start_point)
                ApiGetTripsReadDateStartToDateEndFrom.value = list
        }
    }
    fun getReadByStart(start_point: String){
        CoroutineScope(Dispatchers.Main).launch {
            var list = tripsApi.getReadByStart(start_point)
            ApiGetTripsReadByStart.value = list
        }
    }
    fun getReadByStartPointEndPoint(start_point:String, end_point: String){
        CoroutineScope(Dispatchers.Main).launch {
            var list = tripsApi.getReadByStartPointEndPoint(start_point,end_point)
            ApiGetTripsReadByStartPointEndPoint.value = list
        }
    }
    fun getCityFrom(isDriver:Boolean){
        CoroutineScope(Dispatchers.Main).launch {
            var list =  tripsApi.getCitysFrom(isDriver)
            ApiGetTripsCityFrom.value = list
        }
    }
    fun getCityTo(start_point:String,isDriver:Boolean){
        CoroutineScope(Dispatchers.Main).launch {
            var list =  tripsApi.getCitysTo(start_point, isDriver)
            ApiGetTripsCityTo.value = list
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
    fun putTripsRespond(guidTrips:String,  guidUser:String, isDriver:Boolean)
    {
        CoroutineScope(Dispatchers.IO).launch {
            tripsApi.putTripsRespond(guidTrips,guidUser, isDriver).enqueue(object : retrofit2.Callback<Boolean>{
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    ApiPutTripsResponse.value = response.body()
                    Log.d("Put","Response")
                }
                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Log.d("Put","Failture")
                }
            })
        }
    }

    fun deleteTrips(guid: String){
        CoroutineScope(Dispatchers.IO).launch {
            tripsApi.deleteTrips(guid).enqueue(object : retrofit2.Callback<Boolean>{
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    ApiDeleteTrips.value = response.body()
                    Log.d("Delete","Response")
                }
                override fun onFailure(call: Call<Boolean>, t: Throwable) {
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