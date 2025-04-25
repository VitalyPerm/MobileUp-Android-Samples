package ru.mobileup.samples.features.chat.data.cached_file

import android.net.Uri
import ru.mobileup.samples.features.chat.data.FileHelper
import ru.mobileup.samples.features.chat.domain.cache.CachedFile
import java.time.LocalDateTime

class RoomCachedFileStorage(
    private val cachedFileDao: CachedFileDao,
    private val fileHelper: FileHelper
) : CachedFileStorage {

    override suspend fun copyFileToCache(
        id: String,
        uri: Uri,
        time: LocalDateTime,
        role: CachedFile.Role
    ): CachedFile {
        val cachedFile = fileHelper.copyFileToCache(id, uri, time.toString(), role)

        cachedFileDao.insertCachedFile(cachedFile.toEntity())

        return cachedFile
    }

    override suspend fun clearAll() {
        fileHelper.clearAllCachedFiles()
        cachedFileDao.clearAll()
    }

    override suspend fun checkAndClearIfNeedCachedFiles(olderThan: LocalDateTime) {
        val allCachedFiles = cachedFileDao.getAllCachedFiles()
        val ids = arrayListOf<String>()
        allCachedFiles.forEach {
            // Проверяем существует ли файл в папке cached_files
            val isFileExists = fileHelper.isFileExists(it.relativePath)
            if (isFileExists) {
                val createAt = LocalDateTime.parse(it.date)
                if (createAt < olderThan || !it.downloaded || !it.uploaded) {
                    ids.add(it.id)
                    fileHelper.deleteFile(it.relativePath)
                }
            } else {
                // Если файла нет в cached_files запись удаляем из бд
                ids.add(it.id)
            }
        }
        cachedFileDao.clearCachedFilesByIds(ids)
    }

    override suspend fun getCachedFile(
        id: String
    ): CachedFile? {
        val cachedFileInfo = cachedFileDao.getCachedFileById(id)
        return cachedFileInfo?.toCachedFile(fileHelper.cacheDirPath)
    }

    override suspend fun updateUploadingStatusAndId(
        oldId: String,
        newId: String,
        uploaded: Boolean
    ) {
        cachedFileDao.updateUploadingStatusAndId(oldId, newId, uploaded)
    }

    override suspend fun updateDownloadingStatusById(id: String, downloaded: Boolean) {
        cachedFileDao.updateDownloadingStatusById(id, downloaded)
    }

    override suspend fun insertCachedFile(
        id: String,
        time: LocalDateTime,
        filename: String,
        downloaded: Boolean,
        uploaded: Boolean,
        role: CachedFile.Role
    ): CachedFile {
        val relativePath = "/${FileHelper.CACHED_FILES_DIR}/$filename"
        val file = CachedFileEntity(
            id = id,
            relativePath = relativePath,
            uploaded = uploaded,
            downloaded = downloaded,
            date = time.toString(),
            role = role.name
        )
        cachedFileDao.insertCachedFile(file)
        return file.toCachedFile(fileHelper.cacheDirPath)
    }

    override suspend fun clearCachedFileById(id: String) {
        val cachedFileInfo = cachedFileDao.getCachedFileById(id)
        cachedFileInfo?.relativePath?.let {
            fileHelper.deleteFile(it)
        }
        cachedFileDao.clearCachedFileById(id)
    }
}