package com.mne4.fromandto

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mne4.fromandto.Data.DataModel
import com.mne4.fromandto.Fragment.HelpFragment
import com.mne4.fromandto.Fragment.MyRequestFragment
import com.mne4.fromandto.Fragment.ProfileFragment
import com.mne4.fromandto.Fragment.Search.SearchFragment
import com.mne4.fromandto.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: DataModel by viewModels()
    private lateinit var userStatus:String
    var page = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userStatus = intent.getStringExtra(WelcomeActivity.ARG_USER_STATUS).toString()
        binding.bottomNavigationViewMenu.background = null
        binding.floatingActionButton.setOnClickListener{
            val intent = Intent(this, CreateRequestActivity::class.java)
            intent.putExtra(WelcomeActivity.ARG_USER_STATUS, userStatus)
            startActivity(intent)
        }
        page = R.id.butSearchBottomNavigation

        binding.bottomNavigationViewMenu.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.butSearchBottomNavigation -> {
                        if (R.id.butSearchBottomNavigation != page) {
                            fragmentInstance(
                                SearchFragment.newInstance()
                            )
                            viewModel.UserStatus.value = userStatus
                            page = R.id.butSearchBottomNavigation
                        }
                    }
                    R.id.butHelpBottomNavigation -> {
                        if (R.id.butHelpBottomNavigation != page) {
                            fragmentInstance(
                                HelpFragment.newInstance()
                            )
                            page = R.id.butHelpBottomNavigation
                        }
                    }
                    R.id.butMyOrdersBottomNavigation -> {
                        if (R.id.butMyOrdersBottomNavigation != page) {
                            fragmentInstance(
                                MyRequestFragment.newInstance()
                            )
                            viewModel.UserStatus.value = userStatus
                            page = R.id.butMyOrdersBottomNavigation
                        }
                    }
                    R.id.butProfileBottomNavigation -> {
                        if (R.id.butProfileBottomNavigation != page) {
                            fragmentInstance(
                                ProfileFragment.newInstance()
                            )
                            page = R.id.butProfileBottomNavigation
                        }
                    }
                }
            true
        }
        binding.bottomNavigationViewMenu.selectedItemId = R.id.butSearchBottomNavigation
    }
    private fun fragmentInstance(f: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.bottomNavigationFrame, f)
            .commit()
    }



}