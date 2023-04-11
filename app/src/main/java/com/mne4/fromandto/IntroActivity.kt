package com.mne4.fromandto

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.mne4.fromandto.API.ViewModel
import com.mne4.fromandto.Models.GetUserRoom
import com.mne4.fromandto.Models.User
import com.mne4.fromandto.Observe.DataModel
import com.mne4.fromandto.databinding.ActivityIntroLoadScreenBinding
import java.time.LocalDate

class IntroActivity : AppCompatActivity() {
    lateinit var binding: ActivityIntroLoadScreenBinding
    private lateinit var viewModel: ViewModel
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroLoadScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*viewModel = ViewModel()

//        var password = viewModel.postSMS("79657683902")
//        Log.d("passwordSms","$password")
        var date = LocalDate.parse("2003-10-27")
        var users= User(
            "Николаев",
            "Вячеслав",
            "Мужской",
            "$date",
            "slavanik@gmail.com",
            "123456",
            "123456",
            "89645843532",
            true,
            5.0,
            null,
            null,
            null
        )
        viewModel.postNewUser(users)
        var user: GetUserRoom? = null
        viewModel.dataModel.ApiReturnUser.observe(this,{
           user = it

            Log.d("users","$user")
        })
        viewModel.getCurrentUser("9796c9ca-cf79-44c5-85af-e4e3daeea2f8")

        var user:User? = null
        viewModel.dataModel.ApiReturnCurrentUser.observe(this,{
            user = it
            Log.d("user","$user")
        })
*/

    }
}