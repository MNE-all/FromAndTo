package com.mne4.fromandto

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mne4.fromandto.Fragment.BarMenuFragment
import com.mne4.fromandto.databinding.ActivityMainBinding
import com.yandex.mapkit.search.Line

class MainActivity : AppCompatActivity() {
    lateinit var btnSearch: com.google.android.material.floatingactionbutton.FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        fragmentInstance(BarMenuFragment.newInstance(),R.id.framelayoutMenu);
        var menuFragment = LayoutInflater.from(this).inflate(R.layout.fragment_bar_menu,null)
        btnSearch = menuFragment.findViewById(R.id.createRequest)
        btnSearch.setOnClickListener{
            Toast.makeText(applicationContext, "Вы нажади искать!",Toast.LENGTH_SHORT).show()
        }
    }
    fun fragmentInstance(f: Fragment, idHolder: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(idHolder, f)
            .commit()
    }


}