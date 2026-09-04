package com.example.medication_demo.data

import android.content.Context
import android.net.Uri
import io.github.jan.supabase.storage.storage
import java.util.UUID

class MedicineImageRepository(
    private val context: Context
) {

    suspend fun uploadMedicineImage(
        userId: String,
        medicineId: Int,
        imageUri: String
    ): String? {

        return try {

            val uri =
                Uri.parse(
                    imageUri
                )

            val bytes =
                when (uri.scheme) {

                    "file" -> {
                        val path =
                            uri.path
                                ?: return null

                        java.io.File(path)
                            .readBytes()
                    }

                    else -> {
                        context.contentResolver
                            .openInputStream(uri)
                            ?.use {
                                it.readBytes()
                            }
                            ?: return null
                    }
                }

            val fileName = "${UUID.randomUUID()}.jpg"

            val filePath = "$userId/$medicineId/$fileName"

            val bucket =
                SupabaseClientProvider
                    .client
                    .storage
                    .from(
                        "medicine-images"
                    )

            bucket.upload(
                path = filePath,
                data = bytes
            ) {
                upsert = false
            }

            bucket.publicUrl(
                filePath
            )

        } catch (e: Exception) {

            android.util.Log.e(
                "MedicineImageRepository",
                "Failed to upload medicine image",
                e
            )

            null
        }
    }

    suspend fun deleteMedicineImage(
        imageUrl: String?
    ) {
        if (imageUrl.isNullOrBlank()) {
            return
        }

        try {

            val marker =
                "/storage/v1/object/public/medicine-images/"

            if (!imageUrl.contains(marker)) {
                return
            }

            val filePath =
                imageUrl.substringAfter(marker)

            val bucket =
                SupabaseClientProvider
                    .client
                    .storage
                    .from("medicine-images")

            bucket.delete(
                filePath
            )

        } catch (e: Exception) {

            android.util.Log.e(
                "MedicineImageRepository",
                "Failed to delete medicine image",
                e
            )
        }
    }
}