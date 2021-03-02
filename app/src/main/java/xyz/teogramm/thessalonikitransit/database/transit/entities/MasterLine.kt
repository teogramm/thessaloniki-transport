package xyz.teogramm.thessalonikitransit.database.transit.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class MasterLine(
    @PrimaryKey val masterLineId: Int,
    val number: String,
    val nameEL: String,
    val nameEN: String
)

data class MasterLineWithLines(
    @Embedded val masterLine: MasterLine,
    @Relation(
        parentColumn = "masterLineId",
        entityColumn = "masterLineId"
    )
    val lines: List<Line>
)
