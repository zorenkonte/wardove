package com.app.wardove.data.image

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val imagesDir: File
        get() = File(context.filesDir, IMAGES_SUBDIR).apply { mkdirs() }

    fun createTempImageFile(): File =
        File(imagesDir, "img_${System.currentTimeMillis()}.jpg")

    fun getUriForFile(file: File): Uri =
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

    suspend fun saveImageFromUri(uri: Uri): String = withContext(Dispatchers.IO) {
        val dest = createTempImageFile()
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(dest).use { output ->
                input.copyTo(output)
            }
        } ?: error("Cannot open input stream for $uri")
        dest.absolutePath
    }

    fun delete(path: String?) {
        if (path.isNullOrBlank()) return
        runCatching { File(path).takeIf { it.exists() }?.delete() }
    }

    companion object {
        private const val IMAGES_SUBDIR = "images"
    }
}
