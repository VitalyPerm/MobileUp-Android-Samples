package ru.mobileup.samples.core.map.utils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import ru.mobileup.samples.core.location.GeoCoordinate
import ru.mobileup.samples.core.map.domain.MapCommand

suspend fun CameraPositionState.executeCommand(command: MapCommand) {
    when (command) {
        is MapCommand.MoveTo -> moveTo(
            coordinate = command.coordinate,
            zoom = command.zoom,
            animate = command.animate
        )
        is MapCommand.MoveToBoundingBox -> moveToBoundingBox(
            coordinates = command.coordinates,
            animate = command.animate,
        )
        MapCommand.ZoomIn -> changeZoomByStep(1f, true)
        MapCommand.ZoomOut -> changeZoomByStep(-1f, true)
    }
}

suspend fun CameraPositionState.changeZoomByStep(value: Float, animate: Boolean) {
    val newPosition = CameraUpdateFactory.newCameraPosition(
        fromLatLngZoom(
            position.target,
            position.zoom + value
        )
    )
    if (animate) animate(newPosition) else move(newPosition)
}

suspend fun CameraPositionState.moveTo(
    coordinate: GeoCoordinate,
    animate: Boolean,
    zoom: Float?
) {
    val newPosition = CameraUpdateFactory.newCameraPosition(
        fromLatLngZoom(
            coordinate.toLatLng(),
            zoom ?: position.zoom
        )
    )
    if (animate) animate(newPosition) else move(newPosition)
}

suspend fun CameraPositionState.moveToBoundingBox(
    coordinates: List<GeoCoordinate>,
    animate: Boolean,
    azimuth: Float? = null,
    tilt: Float? = null
) {
    if (coordinates.isEmpty()) return
    val bounds = coordinates.fold(LatLngBounds.Builder()) { builder, latLng ->
        builder.include(latLng.toLatLng())
    }.build()

    val rectanglePaddingPx = 100
    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, rectanglePaddingPx)

    // move camera to new bounds
    if (animate) animate(cameraUpdate) else move(cameraUpdate)

    // apply tilt, azimuth
    val targetCameraPosition = CameraPosition.Builder()
        .target(position.target)
        .zoom(position.zoom)
        .bearing(azimuth ?: position.bearing)
        .tilt(tilt ?: position.tilt)
        .build()

    animate(CameraUpdateFactory.newCameraPosition(targetCameraPosition))
}

fun GeoCoordinate.toLatLng() = LatLng(lat, lng)
