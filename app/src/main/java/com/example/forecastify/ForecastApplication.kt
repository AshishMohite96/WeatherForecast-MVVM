package com.example.forecastify

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.example.forecastify.data.WeatherAPI
import com.example.forecastify.data.db.WeatherDB
import com.example.forecastify.data.network.ConnectivityInterceptor
import com.example.forecastify.data.network.ConnectivityInterceptorImpl
import com.example.forecastify.data.network.WeatherNetworkDataSource
import com.example.forecastify.data.network.WeatherNetworkDataSourceImpl
import com.example.forecastify.data.provider.*
import com.example.forecastify.data.repository.ForecastRepository
import com.example.forecastify.data.repository.ForecastRepositoryImpl
import com.example.forecastify.ui.weather.current.CurrentWeatherViewModelFactory
import com.example.forecastify.ui.weather.future.detail.FutureDetailViewModelFactory
import com.example.forecastify.ui.weather.future.list.FutureListWeatherViewModelFactory
import com.google.android.gms.location.LocationServices
import com.jakewharton.threetenabp.AndroidThreeTen
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.support.androidSupportModule
import org.kodein.di.generic.*
import org.threeten.bp.LocalDate

class ForecastApplication: Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidSupportModule(this@ForecastApplication))

        bind() from singleton { WeatherDB(instance()) }
        bind() from singleton { instance<WeatherDB>().currentWeatherDAO() }
        bind() from singleton { instance<WeatherDB>().weatherLocationDAO() }
        bind() from singleton { instance<WeatherDB>().futureWeatherDAO() }
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { WeatherAPI(instance()) }
        bind<WeatherNetworkDataSource>() with singleton { WeatherNetworkDataSourceImpl(instance()) }
        bind() from provider { LocationServices.getFusedLocationProviderClient(instance<Context>()) }
        bind<LocationProvider>() with singleton { LocationProviderImpl(instance(), instance()) }
        bind<ThemeProvider>() with singleton { ThemeProviderImpl(instance()) }
        bind<ForecastRepository>() with singleton { ForecastRepositoryImpl(instance(), instance(), instance(), instance(), instance()) }
        bind<UnitProvider>() with singleton { UnitProviderImpl(instance()) }
        bind() from provider { CurrentWeatherViewModelFactory(instance(), instance()) }
        bind() from provider { FutureListWeatherViewModelFactory(instance(), instance()) }
        bind() from factory { detailDate: LocalDate -> FutureDetailViewModelFactory(detailDate, instance(), instance()) }
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        val themeProvider :ThemeProvider by instance()
        val theme = themeProvider.getThemeFromPreferences()
        AppCompatDelegate.setDefaultNightMode(theme)
    }
}