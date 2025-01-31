package com.example.app // 이 부분 없으면 패키지 이름 불일치로 앱 실행 안됨. 실행 했더니 god 폴더 같은 경로에 생성

import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import android.provider.MediaStore
import android.content.ContentValues
import android.os.Environment
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Paths

class MainActivity: FlutterActivity() {
    // 이 아래 코드 전부 새로 로직 구현
    private val CHANNEL = "com.example.app/media_store"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "saveVideo") {
                val videoPath = call.argument<String>("videoPath")
                val filePath = saveVideoToGallery(videoPath)
                if (filePath != null) {
                    result.success(filePath)
                } else {
                    result.error("UNAVAILABLE", "Failed to save the video.", null)
                }
            } else {
                result.notImplemented()
            }
        }
    }

    private fun saveVideoToGallery(videoPath: String?): String? {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "NewVideo_${System.currentTimeMillis()}")
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/MyApp")
        }

        val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)

        uri?.let {
            contentResolver.openOutputStream(it).use { outputStream ->
                Files.copy(Paths.get(videoPath!!), outputStream)
            }
            return uri.toString()
        }
        return null
    }
}

