package com.mne4.fromandto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mne4.fromandto.Data.DataModel
import com.mne4.fromandto.Fragment.HelpFragment
import com.mne4.fromandto.Fragment.ProfileFragment
import com.mne4.fromandto.Fragment.Search.SearchFragment
import com.mne4.fromandto.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val viewModel: DataModel by viewModels()
    private lateinit var userStatus:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userStatus = intent.getStringExtra(WelcomeActivity.ARG_USER_STATUS).toString()
        binding.bottomNavigationViewMenu.setBackground(null)
        binding.floatingActionButton.setOnClickListener{
            var intent = Intent(this, CreateRequestActivity::class.java)
            intent.putExtra(WelcomeActivity.ARG_USER_STATUS, userStatus)
            startActivity(intent)
        }
        binding.bottomNavigationViewMenu.setOnItemSelectedListener {
            CoroutineScope(Dispatchers.IO).launch {
                when (it.itemId) {
                    R.id.butSearchBottomNavigation -> {
                            fragmentInstance(
                                SearchFragment.newInstance()
                            );
                        runOnUiThread {
                            viewModel.UserStatus.value = userStatus
                        }
                    }
                    R.id.butHelpBottomNavigation -> {

                            fragmentInstance(
                                HelpFragment.newInstance()
                            );

                    }
                    R.id.butSettingBottomNavigation -> {

                    }
                    R.id.butProfileBottomNavigation -> {

                            fragmentInstance(
                                ProfileFragment.newInstance()
                            );
                    }
                }
            }
            true
        }


    }
    fun fragmentInstance(f: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace( R.id.bottomNavigationFrame, f)
            .commit()
    }



}