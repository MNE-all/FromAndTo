package com.mne4.fromandto.Data.Retrofit2.Models

import android.os.Parcel
import android.os.Parcelable
@Parcelize
data class FindRequest(
    val Surname: String?,
    val Image_url: String?,
    val Rating: Double?,
    val Phone:String?,
    val Start_Time: String?,
    val Price: Float,
    val Descreption: String?,
    val Start_Point: String?,
    val End_Point: String?,
    val Seats_amount:Int,
    val Status: Boolean
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readFloat(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Surname)
        parcel.writeString(Image_url)
        parcel.writeValue(Rating)
        parcel.writeString(Phone)
        parcel.writeString(Start_Time)
        parcel.writeFloat(Price)
        parcel.writeString(Descreption)
        parcel.writeString(Start_Point)
        parcel.writeString(End_Point)
        parcel.writeInt(Seats_amount)
        parcel.writeByte(if (Status) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FindRequest> {
        override fun createFromParcel(parcel: Parcel): FindRequest {
            return FindRequest(parcel)
        }

        override fun newArray(size: Int): Array<FindRequest?> {
            return arrayOfNulls(size)
        }
    }
}

annotation class Parcelize
