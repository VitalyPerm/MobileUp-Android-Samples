package ru.mobileup.samples.features.chat.data.cached_file

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.mobileup.samples.features.chat.data.FileHelper.Companion.CACHED_FILES_DIR
import ru.mobileup.samples.features.chat.domain.cache.CachedFile

@Entity(tableName = "cached_files")
data class CachedFileEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "relative_path") val relativePath: String,
    @ColumnInfo(name = "uploaded") val uploaded: Boolean,
    @ColumnInfo(name = "downloaded") val downloaded: Boolean,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "role") val role: String
)

fun CachedFile.toEntity(): CachedFileEntity {
    val startRelativePath = absolutePath.indexOf(CACHED_FILES_DIR)
    return CachedFileEntity(
        id = id,
        relativePath = absolutePath.drop(startRelativePath - 1),
        uploaded = uploaded,
        downloaded = downloaded,
        date = date,
        role = role.name
    )
}

fun CachedFileEntity.toCachedFile(
    cacheDirPath: String
): CachedFile {
    return CachedFile(
        id = id,
        absolutePath = cacheDirPath + relativePath,
        uploaded = uploaded,
        downloaded = downloaded,
        date = date,
        role = role.toCachedFileRole()!!
    )
}

fun String.toCachedFileRole() = when (this) {
    "CHAT_ATTACHMENT" -> CachedFile.Role.CHAT_ATTACHMENT
    else -> null
}