package xyz.teogramm.thessalonikitransit.database.transit.entities

import androidx.room.*

@Entity(
    foreignKeys = [
        ForeignKey(entity = MasterLine::class, parentColumns = ["masterLineId"], childColumns = ["masterLineId"])
    ],
    indices = [
        // Used when searching for all lines of a masterline
        Index(value = ["masterLineId"])
    ]
)
class Line(
    @PrimaryKey val lineId: Int,
    val number: String,
    val nameEL: String,
    val nameEN: String,
    // Each line belongs to one masterline
    val masterLineId: Int
)


/**
 * Contains information about a line and its routes.
 */
data class LineWithRoutes(
    @Embedded val line: Line,
    @Relation(
        parentColumn = "lineId",
        entityColumn = "lineId"
    )
    val routes: List<RouteWithLastStop>
)