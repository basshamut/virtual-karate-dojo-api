package com.virtual.karate.dojo.api.utils

import java.io.ByteArrayOutputStream
import java.util.*
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter


object Utils {
    fun createInvoicePDF(invoiceData: Map<String, Any>): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val document = Document()
        val writer = PdfWriter.getInstance(document, outputStream)
        document.open()

        document.add(Paragraph("Mushin Dojo"))
        document.add(Paragraph("C/VISTALEGRE 91-93 08940 CORNELLA DE LLOBREGAT (TRAM PADRÓ) Barcelona"))
        document.add(Paragraph("Facturar a: ${invoiceData["billTo"]}"))
        document.add(Paragraph("N° de factura: ${invoiceData["invoiceNumber"]}"))
        document.add(Paragraph("Fecha: ${invoiceData["date"]}"))
        document.add(Paragraph("Total: ${invoiceData["total"]}"))

        document.close()
        return outputStream.toByteArray()
    }

    fun formatDate(date: Date): String {
        val day = String.format("%02d", date.date)
        val month = String.format("%02d", date.month + 1)
        val year = date.year + 1900
        return "$day/$month/$year"
    }
}