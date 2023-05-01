package com.mne4.fromandto

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.GnssAntennaInfo.Listener
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreateRequestActivity : AppCompatActivity(), UserLocationObjectListener,
    Session.SearchListener, CameraListener {
    val viewModel: DataModel by viewModels()
    lateinit var mapView: MapView
    lateinit var cameraListener: CameraListener
    lateinit var mapKit: MapKit

    lateinit var radioFrom: RadioButton
    lateinit var radioTo: RadioButton
    lateinit var txtFrom: TextView
    lateinit var txtTo: TextView
    lateinit var imgPin: ImageView
    lateinit var imgMyLocation: ImageView

    lateinit var price: TextInputEditText
    var passengerCount: Int = 1

    var START_POSITION: Point = Point(0.0, 0.0)
    var END_POSITION: Point = Point(0.0, 0.0)
    var START_TIME: String = ""


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

        price = findViewById(R.id.editTextPrice)
        val textPassengerCount = findViewById<TextView>(R.id.textViewPassengerAmount)
        val btnAddPass = findViewById<CardView>(R.id.CardViewAddPassenger)
        val btnDeletePass = findViewById<CardView>(R.id.CardViewRemovePassenger)
        btnAddPass.setOnClickListener {
            passengerCount++
            textPassengerCount.text = passengerCount.toString()
        }
        btnDeletePass.setOnClickListener {
            if (passengerCount - 1 > 0) {
                passengerCount--
                textPassengerCount.text = passengerCount.toString()
            }
        }



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



        val whenText = findViewById<TextInputEditText>(R.id.TextInputEditTextWhen)
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
        var userStatus = ""
        viewModel.UserStatus.observe(this) {
            userStatus = it
        }

        val description = findViewById<TextInputEditText>(R.id.editTextDescription)
        val btnCreate = findViewById<Button>(R.id.btnCreate)
        btnCreate.setOnClickListener {
            if (!whenText.text.isNullOrEmpty() &&
                !txtFrom.text.isNullOrEmpty() &&
                !txtTo.text.isNullOrEmpty() &&
                !price.text.isNullOrEmpty()) {
                val trip = Trips(
                    price.text.toString().toFloat(),
                    description.text.toString(),
                    START_TIME,
                    passengerCount,
                    "${START_POSITION.latitude} ${START_POSITION.longitude}",
                    "${END_POSITION.latitude} ${END_POSITION.longitude}",
                    true
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
                Toast.makeText(this, "Не все нужные поля заполнены", Toast.LENGTH_SHORT)
            }
        }
    }

    fun moveToMyLocation(view: View) {
        try {
            var position = locationMapKit.cameraPosition()?.target
            if (position == null) {
                Toast.makeText(applicationContext, "Ваше местоположение не обнаружено", Toast.LENGTH_SHORT).show()
                finish()
                var intent = Intent(applicationContext, CreateRequestActivity::class.java)
            }
            mapView.map.move(CameraPosition(position!!, 17.0f, 0.0f, 0.0f),
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
            CoroutineScope(Dispatchers.IO).launch {
                try {
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