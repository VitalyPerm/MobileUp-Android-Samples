package ru.mobileup.samples.core.map.presentation.yandex

import com.yandex.mapkit.mapview.MapView

interface YandexMapOverlay {
    fun setup(mapView: MapView)
}