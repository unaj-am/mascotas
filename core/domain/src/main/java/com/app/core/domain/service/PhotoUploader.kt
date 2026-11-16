package com.app.core.domain.service

/**
 * Uploads a local photo and returns its durable remote URL.
 *
 * The URI is represented as a string to keep Android framework types out of domain.
 */
interface PhotoUploader {
    suspend fun uploadPhoto(uri: String): String
}
