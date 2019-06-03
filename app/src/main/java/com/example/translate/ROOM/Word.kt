package com.example.translate.ROOM

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = "word")
data class Word(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var english: String,
    var vietnamese: String,
    var language : Boolean
): Parcelable {
    constructor(): this(null, "","", true)
}
