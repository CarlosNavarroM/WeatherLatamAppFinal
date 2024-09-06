package com.desafiolatam.weatherlatam.extension

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import java.util.Calendar
import java.util.Locale

// Convertir un timestamp a una cadena con formato corto (hora:minuto)
fun Long.toShortDateString(): String {
    val calendar = Calendar.getInstance(Locale.getDefault())
    calendar.timeInMillis = this
    return String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
}

fun Double.toFahrenheit(): Double {
    return this * 9 / 5 - 459.67 // Devuelve un Double
}

fun Double.toCelsius(): Double {
    return this - 273.15 // Devuelve un Double
}

// Ajustar el ancho de un DialogFragment para que coincida con el ancho del padre
fun DialogFragment.setupWidthToMatchParent() {
    dialog?.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
}

// Mostrar una vista
fun View.show() {
    visibility = View.VISIBLE
}

// Ocultar una vista
fun View.hide() {
    visibility = View.GONE
}

// Convertir la velocidad de m/s a mph
fun Double.toMilesPerHour(): String {
    return String.format("%.1f", this * 2.23694) // Convertir metros por segundo a millas por hora
}
