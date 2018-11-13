package com.example.kazu.medvol.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class DataPiece (val mass: Float, val rate: Float, val timestamp: Int, val date: String): Parcelable{
    constructor():this(0f,0f,0,"" )
}