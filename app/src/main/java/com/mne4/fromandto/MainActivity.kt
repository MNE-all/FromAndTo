package com.mne4.fromandto

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.se.omapi.Session
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.*
import com.yandex.mapkit.geometry.Direction
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), InertiaMoveListener, UserLocationObjectListener,
    com.yandex.mapkit.search.Session.SearchListener, CameraListener {
    lateinit var mapView: MapView
    lateinit var cameraListener: CameraListener

    lateinit var radioFrom:RadioButton
    lateinit var radioTo: RadioButton
    lateinit var txtFrom:TextView
    lateinit var txtTo:TextView
    lateinit var imgPin:ImageView

    var START_POSITION:Point=Point( 0.0,0.0 )
    var END_POSITION:Point=Point(0.0,0.0 )





    lateinit var locationMapKit:UserLocationLayer
    lateinit var searchEdit:TextInputEditText
    lateinit var searchManager:SearchManager
    lateinit var searchSession:com.yandex.mapkit.search.Session
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

        locationMapKit = userLocation
        locationMapKit.setObjectListener(this)
        SearchFactory.initialize(this)
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        mapView.map.addCameraListener(this)
        searchEdit = findViewById(R.id.EditSearch)
        searchEdit.setOnEditorActionListener{
            v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                sumbitQuery(searchEdit.text.toString())
            }
            false
        }

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


    private fun sumbitQuery(query:String){
        searchSession  = searchManager.submit(query,VisibleRegionUtils.toPolygon(mapView.map.visibleRegion), SearchOptions(), this)
    }
    override fun onObjectAdded(userLocation: UserLocationView) {
        locationMapKit.setAnchor(
            PointF((mapView.width() * 0.5).toFloat(), (mapView.height() * 0.5).toFloat()),
            PointF((mapView.width() * 0.5).toFloat(), (mapView.height() * 0.83).toFloat())
        )
        userLocation.accuracyCircle.fillColor = Color.BLUE and -0x66000001
    }

    override fun onObjectRemoved(p0: UserLocationView) {
        TODO("Not yet implemented")
    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
        TODO("Not yet implemented")
    }

    override fun onSearchResponse(response: Response) {
     val mapObjects:MapObjectCollection = mapView.map.mapObjects
        mapObjects.clear()
        for(searchResult in response.collection.children){
            val resultLocation = searchResult.obj!!.geometry[0].point!!
            if(response!=null){
                mapObjects.addPlacemark(resultLocation, ImageProvider.fromResource(this, R.drawable.ic_search))
            }

        }
    }

    override fun onSearchError(error:Error) {
       var errorMessage = "Неизвестная проблема"
        if(error is RemoteError){
            errorMessage = "Беспроводная ошибка"
        }else if(error is NetworkError){
            errorMessage = "Проблема с интернетом"
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT)
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdate: CameraUpdateReason,
        finish: Boolean
    ) {

        if(finish){
            sumbitQuery(searchEdit.text.toString())
        }
    }


}