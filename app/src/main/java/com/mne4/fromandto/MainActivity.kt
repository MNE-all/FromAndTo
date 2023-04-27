package com.mne4.fromandto

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mne4.fromandto.Fragment.HelpFragment
import com.mne4.fromandto.Fragment.ProfileFragment
import com.mne4.fromandto.Fragment.SearchFragment
import com.mne4.fromandto.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
       binding.bottomNavigationViewMenu.setBackground(null)
        binding.floatingActionButton.setOnClickListener{
            Toast.makeText(applicationContext,"Вы нажали на кнопку создание запроса!",Toast.LENGTH_SHORT).show()
        }
        binding.bottomNavigationViewMenu.setOnItemSelectedListener{
            when(it.itemId){
                R.id.butSearchBottomNavigation -> {
                    fragmentInstance(SearchFragment.newInstance(),R.id.bottomNavigationFrame);
                }
                R.id.butHelpBottomNavigation -> {
                    fragmentInstance(HelpFragment.newInstance(),R.id.bottomNavigationFrame);
                }
                R.id.butSettingBottomNavigation -> {
                    Toast.makeText(
                        applicationContext,
                        "Вы нажали на кнопку настройки!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                R.id.butProfileBottomNavigation -> {
                    fragmentInstance(ProfileFragment.newInstance(),R.id.bottomNavigationFrame);
                }
            }
            true
        }


    }
    fun fragmentInstance(f: Fragment, idHolder: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(idHolder, f)
            .commit()
    }


}