package com.yunx.app.ui.resolve

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.yunx.app.data.network.model.ShareFile
import com.yunx.app.data.preview.ShareImagePreview
import com.yunx.app.ui.BackHandler
import com.yunx.app.ui.components.ImageViewerOverlay
import com.yunx.app.ui.components.RemoteImage

@Composable
internal fun ShareImageThumbnail(
    file: ShareFile,
    scopeKey: String,
    load: suspend (ShareFile) -> Result<ShareImagePreview>
) {
    var finished by remember(file.fid, scopeKey) { mutableStateOf(false) }
    val source by produceState<ShareImagePreview?>(null, file.fid, scopeKey) {
        value = load(file).getOrNull()
        finished = true
    }
    Box(Modifier.size(64.dp), contentAlignment = Alignment.Center) {
        val image = source
        if (image != null) {
            RemoteImage(image.url, file.fname, Modifier.fillMaxSize(),
                shape = MaterialTheme.shapes.small, headers = image.headers,
                fallback = Icons.Outlined.InsertDriveFile)
        } else if (!finished) {
            CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
        } else {
            Icon(Icons.Outlined.InsertDriveFile, contentDescription = "点击打开图片预览")
        }
    }
}

@Composable
internal fun ShareImageGallery(
    files: List<ShareFile>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
    load: suspend (ShareFile) -> Result<ShareImagePreview>,
    onDownload: (ShareFile) -> Unit
) {
    val file = files.getOrNull(selectedIndex) ?: return
    var source by remember(file.fid) { mutableStateOf<ShareImagePreview?>(null) }
    var failed by remember(file.fid) { mutableStateOf(false) }
    var attempt by remember(file.fid) { mutableStateOf(0) }
    val focus = remember { FocusRequester() }
    LaunchedEffect(file.fid, attempt) {
        failed = false
        source = load(file).getOrNull()
        failed = source == null
    }
    LaunchedEffect(Unit) { focus.requestFocus() }
    BackHandler { onDismiss() }
    Box(
        Modifier.fillMaxSize().background(Color(0xFF121212))
            .pointerInput(Unit) { detectTapGestures { } }
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) false else when (event.key) {
                    Key.Escape -> { onDismiss(); true }
                    Key.DirectionLeft -> { if (selectedIndex > 0) onSelect(selectedIndex - 1); true }
                    Key.DirectionRight -> { if (selectedIndex < files.lastIndex) onSelect(selectedIndex + 1); true }
                    else -> false
                }
            }.focusRequester(focus).focusable()
    ) {
        val image = source
        if (image != null) {
            key(file.fid) {
                ImageViewerOverlay(image.url, onDismiss, headers = image.headers, showControls = false)
            }
        } else {
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                if (failed) {
                    Text("暂时无法预览此图片", color = Color.White)
                    TextButton(onClick = { attempt++ }) { Text("重试") }
                    TextButton(onClick = { onDownload(file) }) { Text("下载图片") }
                } else {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("正在加载图片…", color = Color.White)
                }
            }
        }
        Row(
            Modifier.align(Alignment.TopCenter).fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.65f)).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${selectedIndex + 1} / ${files.size}  ·  ${file.fname}",
                color = Color.White, modifier = Modifier.weight(1f), maxLines = 1)
            TextButton(onClick = { onDownload(file) }) { Text("下载图片", color = Color.White) }
            IconButton(onClick = onDismiss) { Icon(Icons.Outlined.Close, "关闭图片预览", tint = Color.White) }
        }
        if (selectedIndex > 0) {
            IconButton(onClick = { onSelect(selectedIndex - 1) }, modifier = Modifier.align(Alignment.CenterStart).padding(8.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "上一张", tint = Color.White)
            }
        }
        if (selectedIndex < files.lastIndex) {
            IconButton(onClick = { onSelect(selectedIndex + 1) }, modifier = Modifier.align(Alignment.CenterEnd).padding(8.dp)) {
                Icon(Icons.Outlined.ChevronRight, "下一张", tint = Color.White)
            }
        }
        Text("← → 切图 · 滚轮缩放 · 拖拽移动 · 双击复位 · Esc 关闭",
            color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp))
    }
}
