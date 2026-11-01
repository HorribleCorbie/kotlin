package com.project.kotlin3

import android.R
import android.app.Activity
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import androidx.core.content.ContextCompat
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class FileManager(val context: Activity, val type: FileExtension) {
    val pathFiles = "bluetoothChat"
    var nameFile: String = type.toString() + "File." + type.toString()
    var pageHeight: Int = 1120
    var pagewidth: Int = 792
    fun createFile(message: String) {

        val directory = File(context.getExternalFilesDir(null), pathFiles)
        var success = true
        if (!directory.exists())
            success = directory.mkdir()

        if (success) {
            val sd = File(directory, nameFile)
            try {
                if (type == FileExtension.pdf)
                    generatePDF(message, sd)
                else
                    sd.writeText(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun openFile(): List<String>? {
        val directory = File(context.getExternalFilesDir(null), pathFiles)

        if (!directory.exists())
            directory.mkdir()

        try {
            val file = File(directory, nameFile)
            if (file.exists()) {
                var message : List<String>?
                PDFBoxResourceLoader.init(context)
                if (type == FileExtension.pdf) {
                    message = textFromPDF(file)
                } else message = file.readLines()
                return message
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun deleteFile(): String {
        val directory = File(context.getExternalFilesDir(null), pathFiles)

        if (!directory.exists())
            directory.mkdir()

        try {
            if (File(directory, nameFile).exists()) {
                val file = File(directory, nameFile)
                file.delete()
                return "Файл удален"
            } else
                return "Файла не существует"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "Случилась ошибка"
    }

    fun textFromPDF(file: File): List<String> {
        val doc = PDDocument.load(file)
        val textStripper = PDFTextStripper()
        val text = textStripper.getText(doc).split("\n").map { it.trim() }.filter { it.isNotEmpty() }
        doc.close()
        return text
    }

    fun generatePDF(message: String, file: File) {
        val pdfDocument = PdfDocument()

        val paint = Paint()
        val title = Paint()

        val lines = message.split("\n")
        var currentPageNumber = 1

        var mypageInfo = PageInfo.Builder(pagewidth, pageHeight, currentPageNumber).create()

        var myPage = pdfDocument.startPage(mypageInfo)

        var canvas = myPage.getCanvas()

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))

        title.setTextSize(15f)

        title.setColor(ContextCompat.getColor(context, R.color.black))

        var yPosition = 50f
        val xPosition = 50f


        for (l in lines) {

            if (yPosition >= 1100f) {
                yPosition = 50f
                currentPageNumber++
                pdfDocument.finishPage(myPage)

                mypageInfo = PageInfo.Builder(pagewidth, pageHeight, currentPageNumber).create()
                myPage = pdfDocument.startPage(mypageInfo)
                canvas = myPage.getCanvas()
            }

            canvas.drawText(l, xPosition, yPosition, title)
            yPosition += paint.descent() - paint.ascent() + 6f
        }

        pdfDocument.finishPage(myPage)

        try {
            pdfDocument.writeTo(FileOutputStream(file))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        pdfDocument.close()
    }
}