package com.example.playlistmaker.mediateca.data.createPlaylist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.mediateca.domain.createPlaylist.ImageRepository
import java.io.File
import java.io.FileOutputStream

class ImageRepositoryImpl (
    private val context: Context
): ImageRepository {
    private val placeholderUri = Uri.parse("android.resource://${context.packageName}/drawable/placeholder2")


    override fun saveImage(uri: Uri?): String {
        val actualUri = uri ?: placeholderUri

        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath =
            File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "playlistmaker"
            )
        //создаем каталог, если он не создан
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = context.contentResolver.openInputStream(actualUri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.absolutePath
    }

    override fun getImage(location: String): Uri {
        val file = File(location)
        return   Uri.fromFile(file)
    }



}