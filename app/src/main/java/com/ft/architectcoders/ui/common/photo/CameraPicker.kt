package com.ft.architectcoders.ui.common.photo

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun rememberCameraPicker(onImageCaptured: (Uri) -> Unit): () -> Unit {
    val context = LocalContext.current
    var pendingPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var hasPermission by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingPhotoUri?.let { uri ->
                onImageCaptured(uri)
            }
        }
        pendingPhotoUri = null
    }

    // Launcher para solicitar el permiso de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            // Si el permiso fue otorgado, tomamos la foto
            pendingPhotoUri?.let { uri ->
                cameraLauncher.launch(uri)
            }
        } else {
            pendingPhotoUri = null
        }
    }

    return {
        val photoUri = createImageUri(context)
        photoUri?.let { uri ->
            pendingPhotoUri = uri
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}

private fun createImageUri(context: Context): Uri? {
    return try {
        val imageFile = File(context.cacheDir, "camera_photo_${System.currentTimeMillis()}.jpg")
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}