package com.desafiolatam.weatherlatam

import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.desafiolatam.weatherlatam.R
import com.desafiolatam.weatherlatam.view.MainActivity
import com.desafiolatam.weatherlatam.view.SettingsFragment
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsFragmentTest {

    @Before
    fun setUp() {
        // Lanza el fragmento en el contenedor de prueba
        FragmentScenario.launchInContainer(SettingsFragment::class.java)
    }

    @Test
    fun testInitialTemperatureUnitSelection() {
        // Verifica que el botón de Celsius esté seleccionado por defecto
        onView(withId(R.id.rb_metric))
            .check(matches(isChecked()))
    }

    @Test
    fun testTemperatureUnitChange() {
        // Cambia la selección a Fahrenheit
        onView(withId(R.id.rb_imperial)).perform(click())

        // Verifica que Fahrenheit esté seleccionado
        onView(withId(R.id.rb_imperial))
            .check(matches(isChecked()))
    }

    @Test
    fun testInitialWindSpeedUnitSelection() {
        // Verifica que la unidad de velocidad del viento en metros por segundo esté seleccionada por defecto
        onView(withId(R.id.rb_meters_per_second))
            .check(matches(isChecked()))
    }

    @Test
    fun testWindSpeedUnitChange() {
        // Cambia la selección a millas por hora
        onView(withId(R.id.rb_miles_per_hour)).perform(click())

        // Verifica que millas por hora esté seleccionada
        onView(withId(R.id.rb_miles_per_hour))
            .check(matches(isChecked()))
    }
}
