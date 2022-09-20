package xyz.teogramm.thessalonikitransit.fragments.routeDetails.stopMap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import xyz.teogramm.thessalonikitransit.R
import xyz.teogramm.thessalonikitransit.database.transit.entities.Stop
import xyz.teogramm.thessalonikitransit.viewModels.RouteViewModel
import xyz.teogramm.thessalonikitransit.viewModels.StopViewModel

class StopMapFragment: Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private val routeViewModel: RouteViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_route_details_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapView = view.findViewById<MapView>(R.id.routeMapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

    }

    override fun onMapReady(p0: GoogleMap) {
        p0.setOnInfoWindowClickListener(this)
        routeViewModel.stops.observe(viewLifecycleOwner){ stops ->
            // See https://stackoverflow.com/questions/14828217/android-map-v2-zoom-to-show-all-the-markers
            val builder = LatLngBounds.builder()
            stops.forEach {stop->
                val position = LatLng(stop.latitude, stop.longitude)
                val stopName = stop.nameEN
                val marker = p0.addMarker(MarkerOptions().position(position).title(stopName))
                // Keep the stop as the marker's tag
                marker?.tag = stop

                builder.include(position)
            }
            val bounds = builder.build()
            val padding = 0
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            p0.moveCamera(cameraUpdate)
        }
        routeViewModel.getRoutePoints().observe(viewLifecycleOwner){points->
            // Map each point to a LatLng object
            val latLngs = points.map { point -> LatLng(point.first,point.second) }
            // Create a new polyline options object with all the points
            val lineOptions = PolylineOptions().addAll(latLngs)
            p0.addPolyline(lineOptions)
        }
    }

    override fun onInfoWindowClick(p0: Marker) {
        val stopViewModel: StopViewModel by activityViewModels()
        stopViewModel.setStop(p0.tag as Stop)
        findNavController().navigate(R.id.action_routeDetailsFragment_to_stopDetailsFragment)
    }
}