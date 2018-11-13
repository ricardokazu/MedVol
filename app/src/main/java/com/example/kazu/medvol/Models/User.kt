package com.example.kazu.medvol.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (val uid: String, val email: String):Parcelable{
    constructor():this("","")
}