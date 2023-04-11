package com.mne4.fromandto

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

class MainActivity : AppCompatActivity() {
    lateinit var mapView: MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("429ae64e-46c4-4b6a-aebe-e8ef49cbc0c5")
        MapKitFactory.initialize(this)

        setContentView(R.layout.activity_main)

        mapView = findViewById<MapView>(R.id.mapview)
        requestLocationPermission()

        var traffic = MapKitFactory.getInstance().createTrafficLayer(mapView.mapWindow)
        traffic.isTrafficVisible = true

        var userLocation = MapKitFactory.getInstance().createUserLocationLayer(mapView.mapWindow)
        userLocation.isVisible = true
        var position = userLocation.cameraPosition()?.target

        var locationManager = MapKitFactory.getInstance().createLocationManager()
        locationManager!!.requestSingleUpdate(object : LocationListener {
            override fun onLocationStatusUpdated(p0: LocationStatus) {
            }
            override fun onLocationUpdated(p0: Location) {
                var position = p0?.position ?: Point(60.023686, 30.228692)
                mapView.map.move(CameraPosition(position, 17.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 3f), null
                )
            }
        })

        if (position != null) {
            mapView.map.move(CameraPosition(position, 17.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 3f), null
            )
        }
        else {
            mapView.map.move(CameraPosition(Point(60.023686, 30.228692), 17.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 3f), null
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




}