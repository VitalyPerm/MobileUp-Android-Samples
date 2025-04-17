package ru.mobileup.samples.core.map.domain

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import ru.mobileup.samples.core.location.GeoCoordinate
import ru.mobileup.samples.core.map.utils.toLatLng

data class GoogleMapClusterItem(
    val itemPosition: GeoCoordinate,
    val itemTitle: String? = null,
    val itemSnippet: String? = null,
    val itemZIndex: Float? = null
) : ClusterItem {
    override fun getPosition(): LatLng = itemPosition.toLatLng()
    override fun getSnippet(): String? = itemSnippet
    override fun getTitle(): String? = itemTitle
    override fun getZIndex(): Float? = itemZIndex
}

fun GeoCoordinate.toClusterItem() = GoogleMapClusterItem(
    itemPosition = this
)
