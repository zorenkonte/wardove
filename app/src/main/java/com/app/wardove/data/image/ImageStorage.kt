package com.app.wardove.data.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
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

    /** Copies the picked image into [imagesDir] and downscales it (see [optimize]). */
    suspend fun saveImageFromUri(uri: Uri): String = withContext(Dispatchers.IO) {
        val dest = createTempImageFile()
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(dest).use { output ->
                input.copyTo(output)
            }
        } ?: error("Cannot open input stream for $uri")
        optimize(dest)
        dest.absolutePath
    }

    /**
     * Re-encodes a freshly captured/picked photo so a 12 MP camera shot doesn't sit in
     * the sandbox (and every backup zip) at 4–8 MB each: the longest edge is capped at
     * [MAX_EDGE_PX] and the result is written as JPEG at [JPEG_QUALITY]. EXIF rotation
     * is baked into the pixels first, since re-encoding drops the orientation tag.
     * Best-effort — on any failure the original file is left untouched.
     */
    suspend fun optimize(path: String) = withContext(Dispatchers.IO) { optimize(File(path)) }

    private fun optimize(file: File) {
        if (!file.exists() || file.length() == 0L) return
        try {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(file.absolutePath, bounds)
            val width = bounds.outWidth
            val height = bounds.outHeight
            if (width <= 0 || height <= 0) return

            val orientation = ExifInterface(file).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            val needsRotation = orientation != ExifInterface.ORIENTATION_NORMAL &&
                orientation != ExifInterface.ORIENTATION_UNDEFINED
            val needsResize = maxOf(width, height) > MAX_EDGE_PX
            // Already small, upright and reasonably compressed → nothing to gain.
            if (!needsRotation && !needsResize && file.length() <= MAX_BYTES_UNTOUCHED) return

            var sample = 1
            while (maxOf(width, height) / (sample * 2) >= MAX_EDGE_PX) sample *= 2
            val decoded = BitmapFactory.decodeFile(
                file.absolutePath,
                BitmapFactory.Options().apply { inSampleSize = sample }
            ) ?: return

            val scaled = scaleToMaxEdge(decoded, MAX_EDGE_PX)
            val upright = applyOrientation(scaled, orientation)

            val tmp = File(file.parentFile, file.name + ".tmp")
            FileOutputStream(tmp).use { out ->
                upright.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
            }
            if (upright !== scaled) upright.recycle()
            if (scaled !== decoded) scaled.recycle()
            decoded.recycle()

            if (!tmp.renameTo(file)) {
                tmp.copyTo(file, overwrite = true)
                tmp.delete()
            }
        } catch (e: Exception) {
            Timber.w(e, "Image optimization skipped for ${file.name}")
        } catch (e: OutOfMemoryError) {
            Timber.w(e, "Image optimization skipped (OOM) for ${file.name}")
        }
    }

    private fun scaleToMaxEdge(src: Bitmap, maxEdge: Int): Bitmap {
        val longest = maxOf(src.width, src.height)
        if (longest <= maxEdge) return src
        val ratio = maxEdge.toFloat() / longest
        val w = (src.width * ratio).toInt().coerceAtLeast(1)
        val h = (src.height * ratio).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(src, w, h, true)
    }

    private fun applyOrientation(src: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> { matrix.postRotate(90f); matrix.preScale(-1f, 1f) }
            ExifInterface.ORIENTATION_TRANSVERSE -> { matrix.postRotate(270f); matrix.preScale(-1f, 1f) }
            else -> return src
        }
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }

    fun delete(path: String?) {
        if (path.isNullOrBlank()) return
        runCatching { File(path).takeIf { it.exists() }?.delete() }
    }

    companion object {
        private const val IMAGES_SUBDIR = "images"
        /** Longest edge after downscale — plenty for a full-width detail header on any phone. */
        private const val MAX_EDGE_PX = 1600
        private const val JPEG_QUALITY = 85
        private const val MAX_BYTES_UNTOUCHED = 600 * 1024L
    }
}
