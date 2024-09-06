package com.desafiolatam.weatherlatam

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.desafiolatam.weatherlatam.view.DetailsFragment
import com.desafiolatam.weatherlatam.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*

@RunWith(AndroidJUnit4::class)
class DetailsFragmentTest {

    @Before
    fun setUp() {
        // Inicializa el fragmento en un contenedor
        launchFragmentInContainer<DetailsFragment>()
    }

    @Test
    fun testTemperatureIsDisplayed() {
        // Verifica que la temperatura actual está visible
        onView(withId(R.id.current_temp))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testWindSpeedIsDisplayed() {
        // Verifica que la velocidad del viento está visible
        onView(withId(R.id.wind_speed))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testPressureAndHumidityAreDisplayed() {
        // Verifica que la presión y la humedad están visibles
        onView(withId(R.id.pressure))
            .check(matches(isDisplayed()))

        onView(withId(R.id.humidity))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testSunriseAndSunsetAreDisplayed() {
        // Verifica que los horarios de amanecer y atardecer están visibles
        onView(withId(R.id.sunrise))
            .check(matches(isDisplayed()))

        onView(withId(R.id.sunset))
            .check(matches(isDisplayed()))
    }
}
