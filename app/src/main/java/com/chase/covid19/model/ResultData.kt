package com.chase.covid19.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName as SN

data class ResultData(
    @SN("gender")
    var gender: String?,

    @SN("birth")
    var birth: String?,

    @SN("latlon")
    var latlon: String?,

    @SN("latlonUser")
    var latlonUser: String?,

    @SN("mobile")
    var mobile: String?,

    @SN("name")
    var name: String?,

    @SN("weight")
    var weight: Double?,

    @SN("height")
    var height: Double?,

    @SN("origin")
    var origin: String?,

    @SN("answers")
    var questions: HashMap<String, String>?
) : Parcelable {
    constructor(source: Parcel) : this(source.readString(),
                                       source.readString(),
                                       source.readString(),
                                       source.readString(),
                                       source.readString(),
                                       source.readString(),
                                       source.readDouble(),
                                       source.readDouble(),
                                       source.readString(),
                                       source.readSerializable() as? HashMap<String, String>)

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(gender)
        writeString(birth)
        writeString(latlon)
        writeString(latlonUser)
        writeString(mobile)
        writeString(name)
        writeDouble(weight!!)
        writeDouble(height!!)
        writeString(origin)
        writeSerializable(questions)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ResultData> = object : Parcelable.Creator<ResultData> {
            override fun createFromParcel(source: Parcel): ResultData = ResultData(source)
            override fun newArray(size: Int): Array<ResultData?> = arrayOfNulls(size)
        }
    }
}