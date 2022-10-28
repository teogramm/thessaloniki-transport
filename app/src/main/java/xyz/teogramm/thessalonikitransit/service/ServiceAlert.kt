package xyz.teogramm.thessalonikitransit.service

import android.os.Parcel
import android.os.Parcelable

/**
 * Class used for sending alerts to the Alerts service
 * TODO: Restructure so we send one intent with multiple routeId - lineNumber pairs
 */
data class ServiceAlert(
    val stopId: Int,
    val routeId: Int,
    val stopName: String?,
    val lineNumber: String?,
    val notificationTime: Int
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(stopId)
        parcel.writeInt(routeId)
        parcel.writeString(stopName)
        parcel.writeString(lineNumber)
        parcel.writeInt(notificationTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ServiceAlert> {
        override fun createFromParcel(parcel: Parcel): ServiceAlert {
            return ServiceAlert(parcel)
        }

        override fun newArray(size: Int): Array<ServiceAlert?> {
            return arrayOfNulls(size)
        }
    }
}