package com.mne4.fromandto.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mne4.fromandto.R
import com.mne4.fromandto.databinding.FragmentMapBinding
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.location.Geocoder
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import com.mne4.fromandto.CreateRequestActivity
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

class MapFragment : Fragment(), UserLocationObjectListener,
Session.SearchListener, CameraListener {
    lateinit var mapView: MapView
    lateinit var cameraListener: CameraListener
    lateinit var mapKit: MapKit


    var START_POSITION: Point = Point(0.0, 0.0)
    var END_POSITION: Point = Point(0.0, 0.0)

    lateinit var locationMapKit: UserLocationLayer
    lateinit var searchEdit: EditText
    lateinit var searchManager: SearchManager
    lateinit var searchSession: Session
    lateinit var binding: FragmentMapBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMapBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchEdit = binding.EditSearch
        mapView = binding.mapview

        // Перемещение карты на колледж
        mapView.map.move(CameraPosition(Point(60.023686, 30.228692), 17.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f), null
        )

        mapKit = MapKitFactory.getInstance()

        locationMapKit = mapKit.createUserLocationLayer(mapView.mapWindow)
        locationMapKit.isVisible = true
            //locationMapKit.setObjectListener(this)


        var traffic = mapKit.createTrafficLayer(mapView.mapWindow)
        traffic.isTrafficVisible = false

        requestLocationPermission()

        SearchFactory.initialize(activity?.applicationContext)
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        mapView.map.addCameraListener(this)

        searchEdit.setOnEditorActionListener { textView, i, keyEvent ->
            submitQuery(searchEdit.text.toString())
            false
        }

        binding.radioFromLayout.setOnClickListener{
            radioFromLayout()
        }
        binding.radioToLayout.setOnClickListener{
            radioToLayout()
        }

        binding.imageViewMyLocation.setOnClickListener{
            moveToMyLocation()
        }


    }
    fun moveToMyLocation() {
        try {
            var position = locationMapKit.cameraPosition()?.target
            if (position == null) {
                Toast.makeText(activity?.applicationContext, "Ваше местоположение не обнаружено", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext().applicationContext, CreateRequestActivity::class.java))
            }
            mapView.map.move(CameraPosition(position!!, 17.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1f), null
            )

        }
        catch (ex: Exception) {
            Log.d("moveToMyLocation","${ex.message}")
        }
    }

    fun radioFromLayout() {
        binding.radioTo.isChecked = false
        binding.radioFrom.isChecked = true
        var imgId = resources.getIdentifier("baseline_location_on_24", "drawable", activity?.packageName)
        binding.imgPin.setImageResource(imgId)
        if (binding.txtTo.text != "Откуда") {
            mapView.map.move(CameraPosition(START_POSITION, 17.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1f), null
            )
        }
    }

    fun radioToLayout() {
        binding.radioFrom.isChecked = false
        binding.radioTo.isChecked = true
        var imgId = resources.getIdentifier("baseline_location_off_24", "drawable", activity?.packageName)
        binding.imgPin.setImageResource(imgId)
        if ( binding.txtTo.text != "Куда") {
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
                    ImageProvider.fromResource(activity?.applicationContext, R.drawable.search_result)
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
        Toast.makeText(activity?.applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if(finished) {
            val geoCoder = Geocoder(requireContext(), Locale.getDefault())
            val address = geoCoder.getFromLocation(cameraPosition.target.latitude, cameraPosition.target.longitude, 2)

            var nameAdress = address?.get(0)?.getAddressLine(0)
            if ( binding.radioFrom.isChecked) {
                binding.txtFrom.text = nameAdress
                START_POSITION = cameraPosition.target
            } else {
                binding.txtTo.text = nameAdress
                END_POSITION = cameraPosition.target
            }

            mapView.mapWindow.map.mapObjects.addTapListener { mapObject, point ->
                mapView.map.move(CameraPosition(point, 17.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 1f), null
                )
                false
            }
        }
    }


    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireActivity().applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireActivity().applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
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
        locationMapKit.setAnchor(
            PointF((mapView.width() * 0.5).toFloat(), (mapView.height() * 0.5).toFloat()),
            PointF((mapView.width() * 0.5).toFloat(), (mapView.height() * 0.83).toFloat())
        )
        var picIcon = userLocation.pin.useCompositeIcon()
        picIcon.setIcon("icon", ImageProvider.fromResource(requireContext(), R.drawable.search_result),IconStyle()
            .setAnchor(PointF(  0f,0f)).setRotationType(RotationType.NO_ROTATION).setZIndex(0f).setScale(1f))

        picIcon.setIcon("pin", ImageProvider.fromResource(requireContext(), R.drawable.baseline_notifications_active_24),
        IconStyle().setAnchor(PointF(0.5f,0.5f)).setRotationType(RotationType.ROTATE).setZIndex(0f).setScale(0.5f))
        userLocation.accuracyCircle.fillColor = Color.BLUE and -0x66000001

        mapView.map.move(CameraPosition(Point(0.5,0.5), 17.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f), null
        )
    }

    override fun onObjectRemoved(p0: UserLocationView) {

    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {

    }

    companion object {
        @JvmStatic
        fun newInstance() = MapFragment()
    }
}