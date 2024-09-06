package com.desafiolatam.weatherlatam.data

import com.desafiolatam.weatherlatam.data.local.Coord
import com.desafiolatam.weatherlatam.data.local.WeatherEntity
import com.desafiolatam.weatherlatam.data.local.WeatherWrapper
import com.desafiolatam.weatherlatam.model.WeatherDto

// Convierte de WeatherWrapper a WeatherDto
fun WeatherWrapper.toWeatherDto(): WeatherDto = WeatherDto(
    id = 0,
    currentTemp = main.temp,
    maxTemp = main.temp_max,
    minTemp = main.temp_min,
    pressure = main.pressure.toDouble(),
    humidity = main.humidity.toDouble(),
    windSpeed = wind.speed,
    sunrise = sys.sunrise,
    sunset = sys.sunset,
    cityName = "${name}, ${sys.country}",
    coord = Coord(coord.lat, coord.lon),
    weatherDescription = weather.firstOrNull()?.description ?: "No data" // Añadir la descripción aquí
)


// Convierte de WeatherWrapper a WeatherEntity (para almacenar en la base de datos)
fun WeatherWrapper.toWeatherEntity(): WeatherEntity = WeatherEntity(
    id = 0,
    currentTemp = main.temp,
    maxTemp = main.temp_max,
    minTemp = main.temp_min,
    pressure = main.pressure.toDouble(),
    humidity = main.humidity.toDouble(),
    windSpeed = wind.speed,
    sunrise = sys.sunrise,
    sunset = sys.sunset,
    cityName = "${name}, ${sys.country}",
    coordLat = coord.lat,
    coordLon = coord.lon,
    weatherDescription = weather.firstOrNull()?.description ?: "No data" // Añadir la descripción aquí
)


// Convierte de WeatherDto a WeatherEntity (para guardar en la base de datos)
fun WeatherDto.toEntity(): WeatherEntity = WeatherEntity(
    id = id,
    currentTemp = currentTemp,
    maxTemp = maxTemp,
    minTemp = minTemp,
    pressure = pressure,
    humidity = humidity,
    windSpeed = windSpeed,
    sunrise = sunrise,
    sunset = sunset,
    cityName = cityName,
    coordLat = coord.lat, // Añadir coordenadas aquí
    coordLon = coord.lon,
    weatherDescription = weatherDescription // Añadir la descripción aquí
)

// Convierte de WeatherEntity a WeatherDto (para usar en la UI)
fun entityToDto(entity: WeatherEntity): WeatherDto = WeatherDto(
    id = entity.id,
    currentTemp = entity.currentTemp,
    maxTemp = entity.maxTemp,
    minTemp = entity.minTemp,
    pressure = entity.pressure,
    humidity = entity.humidity,
    windSpeed = entity.windSpeed,
    sunrise = entity.sunrise,
    sunset = entity.sunset,
    cityName = entity.cityName,
    coord = Coord(entity.coordLat, entity.coordLon),
    weatherDescription = entity.weatherDescription // Añadir la descripción aquí
)


// Convierte una lista de WeatherEntity a una lista de WeatherDto
fun entityListToDtoList(list: List<WeatherEntity>): List<WeatherDto> = list.map(::entityToDto)
