package com.app.cloudinary.uploader

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import com.app.core.domain.service.PhotoUploader
import okhttp3.RequestBody
import okio.BufferedSink

@Singleton
class CloudinaryPhotoUploader @Inject constructor(
    @ApplicationContext private val context: Context
) : PhotoUploader {
    private val client = OkHttpClient()

    override suspend fun uploadPhoto(uri: String): String =
        withContext(Dispatchers.IO) {
            val photoUri = Uri.parse(uri)

            val contentResolver = context.contentResolver

            val mimeType = contentResolver.getType(photoUri)
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

            val maxSizeBytes = 5L * 1024L * 1024L
            val assetLength = contentResolver
                .openAssetFileDescriptor(photoUri, "r")
                ?.use { it.length }
                ?: -1L

            if (assetLength > maxSizeBytes) {
                throw IOException(
                    "La fotografía no puede superar los 5 MB"
                )
            }

            val photoBody = object : RequestBody() {
                override fun contentType() = mimeType.toMediaType()

                override fun contentLength(): Long = assetLength

                override fun writeTo(sink: BufferedSink) {
                    val input = contentResolver.openInputStream(photoUri)
                        ?: throw IOException(
                            "No se pudo leer la fotografía"
                        )

                    input.use { stream ->
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        var totalBytes = 0L

                        while (true) {
                            val bytesRead = stream.read(buffer)
                            if (bytesRead == -1) break

                            totalBytes += bytesRead
                            if (totalBytes > maxSizeBytes) {
                                throw IOException(
                                    "La fotografía no puede superar los 5 MB"
                                )
                            }

                            sink.write(buffer, 0, bytesRead)
                        }
                    }
                }
            }

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    "profile_photo",
                    photoBody
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