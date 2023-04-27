package com.mne4.fromandto

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
                    Toast.makeText(
                        applicationContext,
                        "Вы нажали на кнопку поиска!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                R.id.butHelpBottomNavigation -> {
                    Toast.makeText(
                        applicationContext,
                        "Вы нажали на кнопку помощи!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                R.id.butSettingBottomNavigation -> {
                    Toast.makeText(
                        applicationContext,
                        "Вы нажали на кнопку настройки!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                R.id.butProfileBottomNavigation -> {
                    Toast.makeText(
                        applicationContext,
                        "Вы нажали на кнопку профиля!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            true
        }
//        fragmentInstance(BarMenuFragment.newInstance(),R.id.framelayoutMenu);

    }
    fun fragmentInstance(f: Fragment, idHolder: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(idHolder, f)
            .commit()
    }


}