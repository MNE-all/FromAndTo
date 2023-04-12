package com.mne4.fromandto

import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.core.app.ActivityCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.*
import com.yandex.mapkit.geometry.Direction
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Error
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), InertiaMoveListener {
    lateinit var mapView: MapView
    lateinit var cameraListener: CameraListener

    lateinit var radioFrom:RadioButton
    lateinit var radioTo: RadioButton
    lateinit var txtFrom:TextView
    lateinit var txtTo:TextView
    lateinit var imgPin:ImageView

    var START_POSITION:Point=Point(
        0.0,0.0
    )
    var END_POSITION:Point=Point(
        0.0,0.0
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("429ae64e-46c4-4b6a-aebe-e8ef49cbc0c5")
        MapKitFactory.initialize(this)

        setContentView(R.layout.activity_main)

        radioFrom = findViewById(R.id.radioFrom)
        radioTo = findViewById(R.id.radioTo)
        txtFrom = findViewById(R.id.txtFrom)
        txtTo = findViewById(R.id.txtTo)
        imgPin = findViewById(R.id.imgPin)

        mapView = findViewById<MapView>(R.id.mapview)
        requestLocationPermission()

        var traffic = MapKitFactory.getInstance().createTrafficLayer(mapView.mapWindow)
        traffic.isTrafficVisible = false

        var userLocation = MapKitFactory.getInstance().createUserLocationLayer(mapView.mapWindow)

        userLocation.isVisible = false
        var position = userLocation.cameraPosition()?.target ?: Point(
            60.023686,
            30.228692
        )
        mapView.map.move(CameraPosition(position, 17.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 3f), null
        )


        var locationManager = MapKitFactory.getInstance().createLocationManager()
        locationManager!!.requestSingleUpdate(object : LocationListener {
            override fun onLocationStatusUpdated(p0: LocationStatus) {
            }
            override fun onLocationUpdated(p0: Location) {
                var position = p0?.position ?: Point(60.023686, 30.228692)
            }
        })

    }


    fun radioFrom(view: View){
        radioTo.isChecked = false
        radioFrom.isChecked = true
        var imgId = resources.getIdentifier("baseline_location_on_24","drawable",this.packageName)
        imgPin.setImageResource(imgId)
        if(txtTo.text != "Откуда"){
            PositionCamera(START_POSITION)
        }


    }
    fun radioTo(view: View){
        radioFrom.isChecked = false
        radioTo.isChecked = true
        var imgId = resources.getIdentifier("baseline_location_off_24","drawable",this.packageName)
        imgPin.setImageResource(imgId)
        if(txtTo.text != "Куда"){
            PositionCamera(END_POSITION)
        }


    }
    fun PositionCamera(position: Point){
        mapView.map.move(CameraPosition(position, 17.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f), null
        )
    }

            /*var db = MainDB.getDB(this)

        db.getDao().getAllUser().asLiveData().observe(this){
            binding.edMessage.text = ""
            it.forEach{
                val text = "Id:${it.id} id_user:${it.id_user}\n" +
                        "Surname: ${it.surname} Name: ${it.name}\n" +
                        "birthday: ${it.birthday} phone: ${it.phone}\n" +
                        "isInAcc: ${it.isInAcc} gender: ${it.gender}\n"
                binding.edMessage.append(text)
            }
        }
        binding.button.setOnClickListener{
            var user = User(
                null,
                "3453-34534-34534-535",
                "Николаев",
                "Вячеслав",
                "27:10:2003",
                "Мужской",
                "89645843532",
                true
            )
            CoroutineScope(Dispatchers.IO).launch {
                db.getDao().insertUser(user)
            }
        }
*/




    fun onAddClick(view: View) {
        var position = mapView.map.cameraPosition.target
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address = geoCoder.getFromLocation(position.latitude,position.longitude,2)

        var nameAdress =address?.get(0)?.getAddressLine(0)

        if(radioFrom.isChecked){
            txtFrom.text = nameAdress
            START_POSITION = position
        }else{
            txtTo.text = nameAdress
            END_POSITION = position
        }

    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }
    override fun onStart() {
        super.onStart()
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), 0
            )
            return

        }
    }


    @UiThread
    override fun onStart(p0: Map, p1: CameraPosition) {
        Log.d("pos","Start!!!!!!!!!!!!!!!!!!!!!!!!")
    }

    @UiThread
    override fun onCancel(p0: Map, p1: CameraPosition) {
        Log.d("pos","Cansel!!!!!!!!!!!!!!!!!!!!!!!!")
    }
    @UiThread
    override fun onFinish(p0: Map, p1: CameraPosition) {
        Log.d("pos","Finish!!!!!!!!!!!!!!!!!!!!!!!!")
    }


}