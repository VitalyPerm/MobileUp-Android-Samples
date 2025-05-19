package ru.mobileup.samples.core.resource

import android.content.res.AssetManager
import androidx.annotation.StringRes
import kotlinx.io.IOException
import java.io.InputStream
import kotlin.jvm.Throws

interface ResourceProvider {

    val assetsManager: AssetManager

    fun getString(@StringRes id: Int): String

    @Throws(IOException::class)
    fun useAssetStream(fileName: String, block: (InputStream) -> Unit)
}
