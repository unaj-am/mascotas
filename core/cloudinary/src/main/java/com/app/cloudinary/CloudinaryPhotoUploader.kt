package com.app.cloudinary

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CloudinaryPhotoUploader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val client = OkHttpClient()

    suspend fun uploadPhoto(uri: Uri): String =
        withContext(Dispatchers.IO) {

            val contentResolver = context.contentResolver

            val mimeType = contentResolver.getType(uri)
                ?: throw IOException(
                    "No se pudo identificar el formato de la fotografía"
                )

            if (mimeType !in listOf(
                    "image/jpeg",
                    "image/png",
                    "image/webp"
                )
            ) {
                throw IOException(
                    "Formato no permitido. Usá JPG, PNG o WebP"
                )
            }

            val imageBytes = contentResolver
                .openInputStream(uri)
                ?.use { it.readBytes() }
                ?: throw IOException(
                    "No se pudo leer la fotografía"
                )

            val maxSizeBytes = 5 * 1024 * 1024

            if (imageBytes.size > maxSizeBytes) {
                throw IOException(
                    "La fotografía no puede superar los 5 MB"
                )
            }

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    "profile_photo",
                    imageBytes.toRequestBody(
                        mimeType.toMediaType()
                    )
                )
                .addFormDataPart(
                    "upload_preset",
                    "mascotas_profile_unsigned"
                )
                .build()

            val request = Request.Builder()
                .url(
                    "https://api.cloudinary.com/v1_1/" +
                            "r7ums0dr/image/upload"
                )
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->

                val responseText = response.body?.string()
                    ?: throw IOException(
                        "Cloudinary devolvió una respuesta vacía"
                    )

                if (!response.isSuccessful) {
                    throw IOException(
                        "Error de Cloudinary: ${response.code}"
                    )
                }

                JSONObject(responseText)
                    .getString("secure_url")
            }
        }
}