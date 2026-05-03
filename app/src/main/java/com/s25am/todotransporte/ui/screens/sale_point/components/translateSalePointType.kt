package com.s25am.todotransporte.ui.screens.sale_point.components

fun translateSalePointType(tipo: String?): String {
    return when (tipo) {
        "1" -> "Estanco"
        "2" -> "Kiosko"
        "3" -> "Papelería"
        "4" -> "Locutorio"
        "9" -> "Oficina Principal EMT"
        else -> "Punto de Venta"
    }
}