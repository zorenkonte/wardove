package com.app.wardove.data.backup

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.app.wardove.data.local.WardoveDatabase
import com.app.wardove.data.local.dao.ClothingDao
import com.app.wardove.data.local.dao.LaundryDao
import com.app.wardove.data.local.dao.WearLogDao
import com.app.wardove.data.image.ImageStorage
import com.app.wardove.widget.StatsWidget
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Exports the entire local database (items, wear logs, laundry cycles, images) to a single
 * zip the user saves via the system file picker, and restores it back. Storage is local-only
 * and wiped on uninstall (see CLAUDE.md) — this is the only way a user can preserve their data
 * across a reinstall or device change.
 *
 * Zip layout:
 *   data.json   — versioned JSON payload (see [BackupPayload])
 *   images/&#42;  — copies of the files referenced by each item's imagePath
 */
@Singleton
class BackupRepository @Inject constructor(
    private val database: WardoveDatabase,
    private val clothingDao: ClothingDao,
    private val wearLogDao: WearLogDao,
    private val laundryDao: LaundryDao,
    private val imageStorage: ImageStorage,
    @ApplicationContext private val context: Context
) {
    suspend fun exportTo(uri: Uri): ImportResult = withContext(Dispatchers.IO) {
        try {
            val items = clothingDao.getAll()
            val payload = BackupPayload(
                schemaVersion = BackupPayload.CURRENT_SCHEMA_VERSION,
                exportedAt = System.currentTimeMillis(),
                items = items,
                wearLogs = wearLogDao.getAll(),
                laundryCycles = laundryDao.getAllCycles(),
                laundryCycleItems = laundryDao.getAllCycleItems()
            )

            context.contentResolver.openOutputStream(uri)?.use { out ->
                ZipOutputStream(out).use { zip ->
                    // data.json — image paths rewritten to the relative name written below.
                    zip.putNextEntry(ZipEntry(ENTRY_DATA))
                    zip.write(payload.toJson().toString().toByteArray())
                    zip.closeEntry()

                    // images/<basename> for every item that has one on disk.
                    for (item in items) {
                        val file = File(item.imagePath)
                        if (!file.exists()) continue
                        zip.putNextEntry(ZipEntry("$ENTRY_IMAGES_DIR/${file.name}"))
                        file.inputStream().use { it.copyTo(zip) }
                        zip.closeEntry()
                    }
                }
            } ?: return@withContext ImportResult.Error("Could not open destination file")

            ImportResult.Success(
                itemCount = payload.items.size,
                wearLogCount = payload.wearLogs.size,
                laundryCycleCount = payload.laundryCycles.size
            )
        } catch (e: Exception) {
            Timber.e(e, "Backup export failed")
            ImportResult.Error(e.message ?: "Export failed")
        }
    }

    /**
     * Replaces all local data with the contents of the given backup zip. Runs inside a single
     * Room transaction so a failure partway through leaves the existing data untouched.
     */
    suspend fun importFrom(uri: Uri): ImportResult = withContext(Dispatchers.IO) {
        try {
            var payload: BackupPayload? = null
            val stagedImages = mutableMapOf<String, File>() // relative name -> staged temp file

            context.contentResolver.openInputStream(uri)?.use { input ->
                ZipInputStream(input).use { zip ->
                    var entry = zip.nextEntry
                    while (entry != null) {
                        when {
                            entry.name == ENTRY_DATA -> {
                                val json = zip.readBytes().toString(Charsets.UTF_8)
                                payload = BackupPayload.fromJson(JSONObject(json))
                            }
                            entry.name.startsWith("$ENTRY_IMAGES_DIR/") -> {
                                val name = entry.name.substringAfter("$ENTRY_IMAGES_DIR/")
                                val staged = imageStorage.createTempImageFile()
                                staged.outputStream().use { out -> zip.copyTo(out) }
                                stagedImages[name] = staged
                            }
                        }
                        zip.closeEntry()
                        entry = zip.nextEntry
                    }
                }
            } ?: return@withContext ImportResult.Error("Could not open backup file")

            val data = payload ?: return@withContext ImportResult.Error("Backup file is missing data.json")

            // Rewrite each item's imagePath from the zip's relative name to the staged file's
            // real path before writing rows — the staged files already live in imagesDir.
            val rewrittenItems = data.items.map { item ->
                val relativeName = File(item.imagePath).name
                val staged = stagedImages[relativeName]
                if (staged != null) item.copy(imagePath = staged.absolutePath) else item
            }

            database.withTransaction {
                wearLogDao.deleteAll()
                laundryDao.deleteAllCycleItems()
                laundryDao.deleteAllCycles()
                clothingDao.deleteAll()

                clothingDao.insertAll(rewrittenItems)
                wearLogDao.insertAll(data.wearLogs)
                laundryDao.insertCyclesAll(data.laundryCycles)
                if (data.laundryCycleItems.isNotEmpty()) {
                    laundryDao.insertCycleItems(data.laundryCycleItems)
                }
            }

            // Any staged image not referenced by a surviving item is orphaned — clean it up.
            val keptPaths = rewrittenItems.map { it.imagePath }.toSet()
            stagedImages.values
                .filter { it.absolutePath !in keptPaths }
                .forEach { it.delete() }

            StatsWidget().updateAll(context)

            ImportResult.Success(
                itemCount = data.items.size,
                wearLogCount = data.wearLogs.size,
                laundryCycleCount = data.laundryCycles.size
            )
        } catch (e: Exception) {
            Timber.e(e, "Backup import failed")
            ImportResult.Error(e.message ?: "Import failed")
        }
    }

    companion object {
        private const val ENTRY_DATA = "data.json"
        private const val ENTRY_IMAGES_DIR = "images"
    }
}
