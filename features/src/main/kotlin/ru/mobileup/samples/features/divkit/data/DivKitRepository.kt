package ru.mobileup.samples.features.divkit.data

import com.yandex.div2.DivData

interface DivKitRepository {
    suspend fun getMenuData(): DivData
    suspend fun getView(jsonName: String): DivData
}