package ru.mobileup.samples.features.chat.data.cached_file

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedFileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedFile(cachedFileDb: CachedFileEntity)

    @Query("DELETE FROM cached_files")
    suspend fun clearAll()

    @Query("DELETE FROM cached_files WHERE id IN (:ids)")
    suspend fun clearCachedFilesByIds(ids: List<String>)

    @Query("SELECT * FROM cached_files")
    suspend fun getAllCachedFiles(): List<CachedFileEntity>

    @Query("SELECT * FROM cached_files WHERE id = :id")
    suspend fun getCachedFileById(id: String): CachedFileEntity?

    @Query("UPDATE cached_files SET uploaded = :uploaded, id = :newId WHERE id = :oldId")
    suspend fun updateUploadingStatusAndId(oldId: String, newId: String, uploaded: Boolean)

    @Query("UPDATE cached_files SET downloaded = :downloaded WHERE id = :id")
    suspend fun updateDownloadingStatusById(id: String, downloaded: Boolean)

    @Query("DELETE FROM cached_files WHERE id = :id")
    suspend fun clearCachedFileById(id: String)
}