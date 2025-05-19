package ru.mobileup.samples.core.resource

import android.content.Context
import android.content.res.AssetManager
import java.io.InputStream

class ResourceProviderImpl(val context: Context) : ResourceProvider {

    override val assetsManager: AssetManager = context.assets

    override fun getString(id: Int): String {
        return context.getString(id)
    }

    override fun useAssetStream(fileName: String, block: (InputStream) -> Unit) {
        context.assets.open(fileName).use(block)
    }
}