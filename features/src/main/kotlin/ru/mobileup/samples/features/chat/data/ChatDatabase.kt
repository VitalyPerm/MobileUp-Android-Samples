package ru.mobileup.samples.features.chat.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.mobileup.samples.features.chat.data.cached_file.CachedFileDao
import ru.mobileup.samples.features.chat.data.cached_file.CachedFileEntity

@Database(
    entities = [CachedFileEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ChatDatabase : RoomDatabase() {

    companion object {
        private const val DATABASE_NAME = "chat-database"

        fun create(context: Context) = Room
            .databaseBuilder(context, ChatDatabase::class.java, DATABASE_NAME)
            .build()
    }

    abstract fun getCachedFileDao(): CachedFileDao
}