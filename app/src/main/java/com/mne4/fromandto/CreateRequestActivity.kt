package com.mne4.fromandto

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabItem
import com.mne4.fromandto.Fragment.DataRequestFragment
import com.mne4.fromandto.Fragment.MapFragment

class CreateRequestActivity : AppCompatActivity(){
    lateinit var mapTabItem: TabItem
    lateinit var dataTabItem: TabItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_request)
        mapTabItem = findViewById(R.id.mapTabItem)
        dataTabItem = findViewById(R.id.dataTabItem)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.creteRequestFragment, MapFragment.newInstance())
            .commit()
        mapTabItem.setOnClickListener {
            fragmentInstance(MapFragment.newInstance(), R.id.creteRequestFragment)
        }
        dataTabItem.setOnClickListener {
            fragmentInstance(DataRequestFragment.newInstance(), R.id.creteRequestFragment)
        }

    }
    fun fragmentInstance(f: Fragment, idHolder: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(idHolder, f)
            .commit()
    }

}
