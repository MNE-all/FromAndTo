package com.mne4.fromandto

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.media.VolumeShaper.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.asLiveData
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.mne4.fromandto.Data.DataModel
import com.mne4.fromandto.Data.Retrofit2.Models.Trips
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.DrivingSession.DrivingRouteListener
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.Continuation


class CreateRequestActivity : AppCompatActivity(),
    Session.SearchListener, CameraListener, DrivingRouteListener, UserLocationObjectListener {
    val viewModel: DataModel by viewModels()
    lateinit var mapView: MapView
    lateinit var mapKit: MapKit
    lateinit var drivingRouter: DrivingRouter
    lateinit var mapObjects: MapObjectCollection

    lateinit var userStatus: String
    lateinit var radioFrom: RadioButton
    lateinit var radioTo: RadioButton
    lateinit var txtFrom: TextView
    lateinit var txtTo: TextView
    lateinit var imgPin: ImageView
    lateinit var imgMyLocation: ImageView
    lateinit var whenText: TextInputEditText

    lateinit var price: TextInputEditText
    var passengerCount: Int = 1

    var START_POSITION: String = ""
    var END_POSITION: String = ""
    var START_POSITION_COORD: Point = Point(0.0, 0.0)
    var END_POSITION_COORD: Point = Point(0.0, 0.0)
    var START_TIME: String = ""


    lateinit var locationMapKit: UserLocationLayer
    lateinit var searchEdit: EditText
    lateinit var searchManager: SearchManager
    lateinit var searchSession: Session


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_request)

        userStatus = intent.getStringExtra(WelcomeActivity.ARG_USER_STATUS)!!
        if (userStatus == "Driver") {
            findViewById<TextView>(R.id.textViewTitleCreate).text = "Создание поездки"
        }
        else if (userStatus == "User") {
            findViewById<TextView>(R.id.textViewTitleCreate).text = "Создание запроса"
        }

        // Инициализация элементов управления
        radioFrom = findViewById(R.id.radioFrom)
        radioTo = findViewById(R.id.radioTo)
        txtFrom = findViewById(R.id.txtFrom)
        txtTo = findViewById(R.id.txtTo)
        imgPin = findViewById(R.id.imgPin)
        imgMyLocation = findViewById(R.id.imageViewMyLocation)
        mapView = findViewById(R.id.mapview)
        searchEdit = findViewById(R.id.EditSearch)

        price = findViewById(R.id.editTextPrice)
        val textPassengerCount = findViewById<TextView>(R.id.textViewPassengerAmount)
        val btnAddPass = findViewById<CardView>(R.id.CardViewAddPassenger)
        val btnDeletePass = findViewById<CardView>(R.id.CardViewRemovePassenger)
        btnAddPass.setOnClickListener {
            passengerCount++
            textPassengerCount.text = passengerCount.toString()
        }
        btnDeletePass.setOnClickListener {
            submitRequest()
            if (passengerCount - 1 > 0) {
                passengerCount--
                textPassengerCount.text = passengerCount.toString()
            }
        }





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
            submitQuery(searchEdit.text.toString())
            false
        }


        when (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) {
            android.content.res.Configuration.UI_MODE_NIGHT_NO -> {
                mapView.map.isNightModeEnabled = false
            }
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> {
                mapView.map.isNightModeEnabled = true
            }
        }

        whenText = findViewById(R.id.TextInputEditTextWhen)
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
        val date = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Дата выезда")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        date.addOnPositiveButtonClickListener {
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
            val result = simpleDateFormat.format(it)
            START_TIME = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(it)
            whenText.setText(result)
        }



        // Слушатели для нажатия на "Куда"
        whenText.setOnClickListener {
            date.show(supportFragmentManager, "DATE_PICKER")
        }
        whenText.setOnFocusChangeListener { _, b ->
            if (b) {
                date.show(supportFragmentManager, "DATE_PICKER")
            }
        }


        // Добавление поездки/запроса на поездку
        val description = findViewById<TextInputEditText>(R.id.editTextDescription)
        val btnCreate = findViewById<Button>(R.id.btnCreate)
        btnCreate.setOnClickListener {
            if (!whenText.text.isNullOrEmpty() &&
                !txtFrom.text.isNullOrEmpty() && txtFrom.text == "Откуда" &&
                !txtTo.text.isNullOrEmpty() && txtFrom.text == "Куда" &&
                !price.text.isNullOrEmpty()) {
                val trip = Trips(
                    price.text.toString().toFloat(),
                    description.text.toString(),
                    START_TIME,
                    passengerCount,
                    START_POSITION,
                    "${START_POSITION_COORD.latitude} ${START_POSITION_COORD.longitude}",
                    END_POSITION,
                    "${END_POSITION_COORD.latitude} ${END_POSITION_COORD.longitude}"
                )

                viewModel.getLocalDB(this).getDao().getAllUser().asLiveData().observe(this) {
                    for (i in it) {
                        if (i.isInAcc){
                            if (userStatus == "Driver") {
                                viewModel.postCreateTrips(i.id_user, trip)
                            }
                            else if (userStatus == "User") {
                                viewModel.postCreateRequest(i.id_user, trip)
                            }
                            finish()
                        }
                    }
                }
            }
            else {
                Toast.makeText(this, "Не все нужные поля заполнены", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun moveToMyLocation(view: View) {
        try {
            var position = locationMapKit.cameraPosition()?.target
            if (position == null) {
                // Включение местоположения
                if (isGeoDisabled()) {
                    startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
                else {
                    Toast.makeText(applicationContext, "Ваше местоположение не обнаружено", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                mapView.map.move(
                    CameraPosition(position, 17.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 1f), null
                )
            }
        }
        catch (ex: Exception) {
            Log.d("moveToMyLocation","${ex.message}")
        }
    }
    fun isGeoDisabled(): Boolean {
        val mLocationManager: LocationManager =
            this.getSystemService(LOCATION_SERVICE) as LocationManager
        val mIsGPSEnabled: Boolean =
            mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val mIsNetworkEnabled: Boolean =
            mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return !mIsGPSEnabled && !mIsNetworkEnabled
    }

    fun radioClick(view: View) {
        if (view.tag == "radioTo") {
            radioFrom.isChecked = false
            radioTo.isChecked = true
            imgPin.setImageResource(R.drawable.baseline_location_off_24)
            if (txtTo.text != "Куда") {
                mapView.map.move(
                    CameraPosition(END_POSITION_COORD, 17.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 1f), null
                )
            }
        }
        else if (view.tag == "radioFrom") {
            radioFrom.isChecked = true
            radioTo.isChecked = false
            imgPin.setImageResource(R.drawable.baseline_location_on_24)
            if (txtFrom.text != "Откуда") {
                mapView.map.move(CameraPosition(START_POSITION_COORD, 17.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 1f), null
                )
            }
        }
    }


    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onSearchResponse(response: Response) {
        val mapObjects = mapView.map.mapObjects
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
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val geoCoder = Geocoder(applicationContext, Locale.getDefault())

                    val address = geoCoder.getFromLocation(cameraPosition.target.latitude, cameraPosition.target.longitude, 2)


                    var nameAdress = address?.get(0)?.getAddressLine(0)

                    var city = address?.get(1)?.locality ?: address?.get(1)?.adminArea

                    if (radioFrom.isChecked) {
                        txtFrom.text = nameAdress
                        START_POSITION = city.toString()
                        START_POSITION_COORD = cameraPosition.target

                    } else {
                        txtTo.text = nameAdress
                        END_POSITION = city.toString()
                        END_POSITION_COORD = cameraPosition.target
                    }


                    mapView.mapWindow.map.mapObjects.addTapListener { mapObject, point ->
                        mapView.map.move(CameraPosition(point, 17.0f, 0.0f, 0.0f),
                            Animation(Animation.Type.SMOOTH, 1f), null
                        )
                        false
                    }
                }
                catch (e: Exception) {
                    Log.d("onCameraPositionChanged", e.message.toString())
                }
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

    // Поиск на карте
    private fun submitQuery(query: String) {
        searchSession = searchManager.submit(
            query,
            VisibleRegionUtils.toPolygon(mapView.map.visibleRegion),
            SearchOptions(),
            this
        )
    }

    // Построение маршрута
    lateinit var drivingSession: DrivingSession
    private fun submitRequest() {
        val drivingOptions = DrivingOptions()
        val vehicleOptions = VehicleOptions()
        val requestPoints = ArrayList<RequestPoint>()
        mapObjects = mapView.map.mapObjects.addCollection()
        Log.d("MapKitDebug", "${mapObjects}")

        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        requestPoints.add(
            RequestPoint(
                START_POSITION_COORD,
                RequestPointType.WAYPOINT,
                null
            )
        )
        requestPoints.add(
            RequestPoint(
                END_POSITION_COORD,
                RequestPointType.WAYPOINT,
                null
            )
        )
        drivingSession =
            drivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, this)

    }

    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
        for (route in routes) {
            mapObjects.addPolyline(route.geometry)
        }
    }

    override fun onDrivingRoutesError(error: Error) {
        var errorMessage = getString(R.string.unknown_error_message)
        if (error is RemoteError) {
            errorMessage = getString(R.string.remote_error_message)
        } else if (error is NetworkError) {
            errorMessage = getString(R.string.network_error_message)
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onObjectAdded(p0: UserLocationView) {
    }

    override fun onObjectRemoved(p0: UserLocationView) {
    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
    }


}