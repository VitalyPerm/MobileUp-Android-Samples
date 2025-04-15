package ru.mobileup.samples.core.map.domain

import ru.mobileup.samples.core.location.GeoCoordinate

sealed interface MapCommand {
    data class MoveTo(
        val coordinate: GeoCoordinate,
        val zoom: Float? = null, // null means don't change
        val animate: Boolean = true
    ) : MapCommand

    data object ZoomIn : MapCommand

    data object ZoomOut : MapCommand

    data class MoveToBoundingBox(
        val coordinates: List<GeoCoordinate>,
        val animate: Boolean = false
    ) : MapCommand
}