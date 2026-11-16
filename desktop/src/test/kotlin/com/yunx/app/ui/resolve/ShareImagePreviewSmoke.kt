package com.yunx.app.ui.resolve

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.yunx.app.data.network.HttpClients
import com.yunx.app.data.network.model.ShareFile
import com.yunx.app.data.preview.ShareImagePreview
import com.sun.net.httpserver.HttpServer
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.net.InetSocketAddress
import javax.imageio.ImageIO

/** Interactive smoke test using only generated pictures and a loopback server. */
object ShareImagePreviewSmoke {
    @JvmStatic fun main(args: Array<String>) {
        HttpClients.setProxy(null, 0)
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        for (n in 1..2) {
            val image = BufferedImage(640, 360, BufferedImage.TYPE_INT_RGB)
            image.createGraphics().apply {
                color = if (n == 1) Color(39, 85, 143) else Color(26, 115, 84)
                fillRect(0, 0, 640, 360)
                color = Color.WHITE
                font = font.deriveFont(48f)
                drawString("Preview $n", 180, 190)
                dispose()
            }
            val bytes = ByteArrayOutputStream().also { ImageIO.write(image, "png", it) }.toByteArray()
            server.createContext("/$n.png") { exchange ->
                exchange.responseHeaders.add("Content-Type", "image/png")
                exchange.sendResponseHeaders(200, bytes.size.toLong())
                exchange.responseBody.use { it.write(bytes) }
            }
        }
        server.start()
        val files = (1..2).map { ShareFile("$it", "预览图 ($it).png", 5000, false, "0", "") }
        val loader: suspend (ShareFile) -> Result<ShareImagePreview> = { file ->
            Result.success(ShareImagePreview("http://127.0.0.1:${server.address.port}/${file.fid}.png", emptyMap()))
        }
        application {
            Window(onCloseRequest = { server.stop(0); exitApplication() }, title = "图片预览本地测试") {
                MaterialTheme {
                    var index by remember { mutableStateOf(-1) }
                    Box(Modifier.fillMaxSize()) {
                        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("点击缩略图或文件行，测试放大与左右切图")
                            files.forEachIndexed { i, file ->
                                ShareFileRow(file, onClick = { index = i }, previewable = true,
                                    leadingContent = { ShareImageThumbnail(file, "smoke", loader) })
                            }
                        }
                        if (index >= 0) ShareImageGallery(files, index, { index = it }, { index = -1 }, loader, {})
                    }
                }
            }
        }
    }
}
