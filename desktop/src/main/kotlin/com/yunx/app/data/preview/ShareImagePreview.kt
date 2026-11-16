package com.yunx.app.data.preview

import com.yunx.app.data.network.model.ShareFile

data class ShareImagePreview(val url: String, val headers: Map<String, String>)

object ShareImagePolicy {
    private val extensions = setOf("jpg", "jpeg", "png", "webp", "gif", "bmp")
    const val MAX_PREVIEW_BYTES = 32L * 1024 * 1024
    private const val MAX_THUMBNAIL_BYTES = 8L * 1024 * 1024

    fun isImage(file: ShareFile): Boolean = !file.isdir &&
        file.fname.substringAfterLast('.', "").lowercase() in extensions &&
        file.fsize in 0..MAX_PREVIEW_BYTES

    fun autoThumbnail(file: ShareFile): Boolean = isImage(file) &&
        file.fsize in 1..MAX_THUMBNAIL_BYTES
}
