package ru.mobileup.samples.core.map.presentation

import androidx.compose.runtime.saveable.listSaver
import ru.mobileup.samples.core.location.DEFAULT_MAP_ZOOM
import ru.mobileup.samples.core.location.GeoCameraPosition
import ru.mobileup.samples.core.location.GeoCoordinate

val GeoCameraPositionSaver = listSaver<GeoCameraPosition, Any>(
    save = { position ->
        listOf(
            position.geoCoordinate.lat,
            position.geoCoordinate.lng,
            position.zoom,
            position.azimuth,
            position.tilt
        )
    },
    restore = { values ->
        GeoCameraPosition(
            geoCoordinate = GeoCoordinate(
                lat = values.getSafe(0, GeoCoordinate.Companion.KREMLIN.lat),
                lng = values.getSafe(1, GeoCoordinate.Companion.KREMLIN.lng)
            ),
            zoom = values.getSafe(2, DEFAULT_MAP_ZOOM),
            azimuth = values.getSafe(3, 0f),
            tilt = values.getSafe(4, 0f),
        )
    }
)

private inline fun <reified T> List<Any>.getSafe(index: Int, default: T): T {
    return try {
        getOrNull(index) as? T ?: default
    } catch (_: ClassCastException) {
        default
    }
}