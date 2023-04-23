package com.mne4.fromandto
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mne4.fromandto.Fragment.DataRequestFragment
import com.mne4.fromandto.Fragment.MapFragment
class CreateRequestActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_request)

        findViewById<com.google.android.material.tabs.TabItem>(R.id.mapTabItem).setOnClickListener {
            fragmentInstance(MapFragment.newInstance(), R.id.creteRequestFragment)
        }
        findViewById<com.google.android.material.tabs.TabItem>(R.id.dataTabItem).setOnClickListener {
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
