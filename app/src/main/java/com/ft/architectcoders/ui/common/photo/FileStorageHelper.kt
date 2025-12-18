package com.ft.architectcoders.ui.common.photo

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object FileStorageHelper {
    private const val PROFILE_IMAGE_NAME = "profile_photo.jpg"
    private const val PROFILE_IMAGE_DIR = "profile_images"

    suspend fun saveProfileImage(
        context: Context,
        uri: Uri,
    ): String? =
        withContext(Dispatchers.IO) {
            try {
                val imageDir = File(context.filesDir, PROFILE_IMAGE_DIR)
                if (!imageDir.exists()) {
                    imageDir.mkdirs()
                }

                val imageFile = File(imageDir, PROFILE_IMAGE_NAME)

                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    FileOutputStream(imageFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                imageFile.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    fun getProfileImagePath(context: Context?): String? {
        val imageFile = File(context?.filesDir, "$PROFILE_IMAGE_DIR/$PROFILE_IMAGE_NAME")
        return if (imageFile.exists()) imageFile.absolutePath else null
    }

    suspend fun deleteProfileImage(context: Context) =
        withContext(Dispatchers.IO) {
            try {
                val imageFile = File(context.filesDir, "$PROFILE_IMAGE_DIR/$PROFILE_IMAGE_NAME")
                if (imageFile.exists()) {
                    imageFile.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
}
