package xyz.teogramm.thessalonikitransport.database.transit.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Calendar(
    @PrimaryKey val calendarId: Int,
    val nameEL: String,
    val nameEN: String
)
