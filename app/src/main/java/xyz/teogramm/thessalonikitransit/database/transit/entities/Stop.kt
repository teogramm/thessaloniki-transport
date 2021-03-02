package xyz.teogramm.thessalonikitransit.database.transit.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Stop(
    @PrimaryKey var stopId: Int,
    var publicId: String,
    var nameEL: String,
    var nameEN: String,
    var heading: Int,
    var longitude: Double,
    var latitude: Double
)
