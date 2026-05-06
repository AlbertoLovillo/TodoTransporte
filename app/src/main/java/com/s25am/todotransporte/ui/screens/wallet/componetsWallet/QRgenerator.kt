package com.s25am.todotransporte.ui.screens.wallet.componetsWallet

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

fun generarQR(datos: String): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        // Generamos una matriz de 512x512
        val bitMatrix = writer.encode(datos, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                // Si el punto debe estar pintado, lo ponemos negro, si no, blanco
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}