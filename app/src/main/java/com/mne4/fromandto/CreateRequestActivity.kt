package com.mne4.fromandto

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.*
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import java.util.*

class CreateRequestActivity : AppCompatActivity(), UserLocationObjectListener,
    Session.SearchListener, CameraListener {
    lateinit var mapView: MapView
    lateinit var cameraListener: CameraListener
    lateinit var mapKit: MapKit

    lateinit var radioFrom: RadioButton
    lateinit var radioTo: RadioButton
    lateinit var txtFrom: TextView
    lateinit var txtTo: TextView
    lateinit var imgPin: ImageView
    lateinit var imgMyLocation: ImageView

    var START_POSITION: Point = Point(0.0, 0.0)
    var END_POSITION: Point = Point(0.0, 0.0)

    lateinit var locationMapKit: UserLocationLayer
    lateinit var searchEdit: EditText
    lateinit var searchManager: SearchManager
    lateinit var searchSession: Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_request)

        // Инициализация элементов управления
        radioFrom = findViewById(R.id.radioFrom)
        radioTo = findViewById(R.id.radioTo)
        txtFrom = findViewById(R.id.txtFrom)
        txtTo = findViewById(R.id.txtTo)
        imgPin = findViewById(R.id.imgPin)
        imgMyLocation = findViewById(R.id.imageViewMyLocation)
        mapView = findViewById(R.id.mapview)
        searchEdit = findViewById(R.id.EditSearch)


        // Перемещение карты на колледж
        mapView.map.move(CameraPosition(Point(60.023686, 30.228692), 17.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f), null
        )

        mapKit = MapKitFactory.getInstance()

        locationMapKit = mapKit.createUserLocationLayer(mapView.mapWindow)
        locationMapKit.isVisible = true
        locationMapKit.setObjectListener(this)


        var traffic = mapKit.createTrafficLayer(mapView.mapWindow)
        traffic.isTrafficVisible = false

        requestLocationPermission()

        SearchFactory.initialize(this)
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        mapView.map.addCameraListener(this)

        searchEdit.setOnEditorActionListener { textView, i, keyEvent ->
            submitQuery(searchEdit!!.text.toString())
            false
        }
        mapView.mapWindow.map.mapObjects.addTapListener { mapObject, point ->
            mapView.map.move(CameraPosition(point, 17.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1f), null
            )
            false
        }
    }

    fun moveToMyLocation(view: View) {
        try {
            var position = locationMapKit.cameraPosition()!!.target
            mapView.map.move(CameraPosition(position, 17.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1f), null
            )
        }
        catch (ex: Exception) {
            Log.d("moveToMyLocation","${ex.message}")
        }
    }

    fun radioFrom(view: View) {
        radioTo.isChecked = false
        radioFrom.isChecked = true
        var imgId = resources.getIdentifier("baseline_location_on_24", "drawable", this.packageName)
        imgPin.setImageResource(imgId)
        if (txtTo.text != "Откуда") {
            mapView.map.move(CameraPosition(START_POSITION, 17.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1f), null
            )
        }


    }

    fun radioTo(view: View) {
        radioFrom.isChecked = false
        radioTo.isChecked = true
        var imgId = resources.getIdentifier("baseline_location_off_24", "drawable", this.packageName)
        imgPin.setImageResource(imgId)
        if (txtTo.text != "Куда") {
            mapView.map.move(CameraPosition(END_POSITION, 17.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1f), null
            )
        }
    }


    override fun onStop() {
        mapView!!.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView!!.onStart()
    }

    override fun onSearchResponse(response: Response) {
        val mapObjects = mapView!!.map.mapObjects
        mapObjects.clear()
        for (searchResult in response.collection.children) {
            val resultLocation = searchResult.obj!!.geometry[0].point
            if (resultLocation != null) {
                mapObjects.addPlacemark(
                    resultLocation,
                    ImageProvider.fromResource(this, R.drawable.search_result)
                )
                mapView.map.move(CameraPosition(resultLocation, 17.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 1f), null
                )

            }
        }
        mapView.mapWindow.map.mapObjects.addTapListener { mapObject, point ->
            mapView.map.move(CameraPosition(point, 17.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1f), null
            )
            false
        }
    }

    override fun onSearchError(error: Error) {
        var errorMessage = "getString(R.string.unknown_error_message)"
        if (error is RemoteError) {
            errorMessage = "getString(R.string.remote_error_message)"
        } else if (error is NetworkError) {
            errorMessage = "getString(R.string.network_error_message)"
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if(finished) {
            val geoCoder = Geocoder(applicationContext, Locale.getDefault())
            val address = geoCoder.getFromLocation(cameraPosition.target.latitude, cameraPosition.target.longitude, 2)

            var nameAdress = address?.get(0)?.getAddressLine(0)
            if (radioFrom.isChecked) {
                txtFrom.text = nameAdress
                START_POSITION = cameraPosition.target
            } else {
                txtTo.text = nameAdress
                END_POSITION = cameraPosition.target
            }
        }
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

    private fun submitQuery(query: String) {
        searchSession = searchManager.submit(
            query,
            VisibleRegionUtils.toPolygon(mapView.map.visibleRegion),
            SearchOptions(),
            this
        )
    }
    override fun onObjectAdded(userLocation: UserLocationView) {
//        locationMapKit.setAnchor(
//            PointF((mapView.width() * 0.5).toFloat(), (mapView.height() * 0.5).toFloat()),
//            PointF((mapView.width() * 0.5).toFloat(), (mapView.height() * 0.83).toFloat())
//        )
//        userLocation.arrow.setIcon(ImageProvider.fromResource(this,R.drawable.baseline_directions_car_24))
//        var picIcon = userLocation.pin.useCompositeIcon()
//        picIcon.setIcon("icon", ImageProvider.fromResource(this, R.drawable.ic_search),IconStyle()
//            .setAnchor(PointF(  0f,0f)).setRotationType(RotationType.NO_ROTATION).setZIndex(0f).setScale(1f))
//
//        picIcon.setIcon("pin", ImageProvider.fromResource(this, R.drawable.baseline_notifications_active_24),
//        IconStyle().setAnchor(PointF(0.5f,0.5f)).setRotationType(RotationType.ROTATE).setZIndex(0f).setScale(0.5f))
//        userLocation.accuracyCircle.fillColor = Color.BLUE and -0x66000001
//
//        mapView.map.move(CameraPosition(Point(0.5,0.5), 17.0f, 0.0f, 0.0f),
//            Animation(Animation.Type.SMOOTH, 1f), null
//        )
    }

    override fun onObjectRemoved(p0: UserLocationView) {

    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {

    }


}