package com.parkhub.app.ui.components.suche

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OsmMapView(
    modifier: Modifier = Modifier,
    latitude: Double = 49.0069,
    longitude: Double = 8.4037,
    markers: List<Pair<GeoPoint, String>> = emptyList()
) {
    AndroidView(
        modifier = modifier,
        factory = { context: Context ->
            Configuration.getInstance().userAgentValue = "ParkHub/1.0"
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(15.0)
                controller.setCenter(GeoPoint(latitude, longitude))
            }
        },
        update = { mapView ->
            mapView.controller.setCenter(GeoPoint(latitude, longitude))

            mapView.overlays.removeAll { it is Marker }

            markers.forEach { (point, label) ->
                val marker = Marker(mapView)
                marker.position = point
                marker.title = label
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                marker.setOnMarkerClickListener { clickedMarker, mv ->
                    val versatzPunkte = mv.projection.fromPixels(
                        0,
                        (mv.height * 0.65).toInt()
                    )
                    val mittelpunkt = mv.projection.fromPixels(
                        mv.width / 2,
                        mv.height / 2
                    )

                    val latVersatz = (mittelpunkt as GeoPoint).latitude -
                            (versatzPunkte as GeoPoint).latitude

                    val zielPunkt = GeoPoint(
                        clickedMarker.position.latitude + latVersatz,
                        clickedMarker.position.longitude
                    )

                    mv.controller.animateTo(zielPunkt)
                    clickedMarker.showInfoWindow()
                    true
                }

                mapView.overlays.add(marker)
            }

            mapView.invalidate()
        }
    )
}