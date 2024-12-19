package ru.mobileup.samples.core

import android.app.Application
import org.koin.core.Koin

interface KoinProvider {
    val koin: Koin
}

val Application.koin get() = (this as KoinProvider).koin