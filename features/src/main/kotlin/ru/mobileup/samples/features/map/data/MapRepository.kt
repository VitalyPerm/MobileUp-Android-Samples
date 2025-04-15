package ru.mobileup.samples.features.map.data

import me.aartikov.replica.single.Replica
import ru.mobileup.samples.core.location.GeoCoordinate

interface MapRepository {
    val placesReplica: Replica<List<GeoCoordinate>>
}