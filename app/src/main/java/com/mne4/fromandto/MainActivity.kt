package com.mne4.fromandto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mne4.fromandto.Fragment.BarMenuFragment
import com.mne4.fromandto.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var btnSearch: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        fragmentInstance(BarMenuFragment.newInstance(),R.id.framelayoutMenu);
        btnSearch = findViewById(R.id.btnSearch)
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