package com.yunx.app.data.preview

import com.yunx.app.data.network.model.ShareFile
import org.junit.Assert.*
import org.junit.Test

class ShareImagePolicyTest {
    private fun file(name: String, size: Long = 45_000, dir: Boolean = false) =
        ShareFile("image", name, size, dir, "0", "")

    @Test fun `supported images include upper case and unicode filenames`() {
        for (name in listOf("预览图 (1).jpg", "PHOTO.JPEG", "scan.png", "a.webp", "a.gif", "a.bmp")) {
            assertTrue(name, ShareImagePolicy.isImage(file(name)))
        }
    }
    @Test fun `folders archives and unsupported formats use the normal file action`() {
        assertFalse(ShareImagePolicy.isImage(file("folder.jpg", dir = true)))
        assertFalse(ShareImagePolicy.isImage(file("photo.jpg.zip")))
        assertFalse(ShareImagePolicy.isImage(file("image.heic")))
    }
    @Test fun `large images require an explicit click and oversized files use download`() {
        assertTrue(ShareImagePolicy.autoThumbnail(file("photo.jpg")))
        assertFalse(ShareImagePolicy.autoThumbnail(file("photo.jpg", 9 * 1024 * 1024)))
        assertTrue(ShareImagePolicy.isImage(file("photo.jpg", 9 * 1024 * 1024)))
        assertFalse(ShareImagePolicy.isImage(file("photo.jpg", 33 * 1024 * 1024)))
        assertFalse(ShareImagePolicy.autoThumbnail(file("photo.jpg", 0)))
    }
}
