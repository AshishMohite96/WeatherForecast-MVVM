package com.example.forecastify.data.db.unitlocalised.future

import org.threeten.bp.LocalDate

interface UnitSpecificFutureEntry {
    val date: LocalDate
    val avgtemp: Double
    val conditionText: String
    val conditionIconUrl: String
}