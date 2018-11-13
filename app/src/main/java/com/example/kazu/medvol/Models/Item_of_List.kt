package com.example.kazu.medvol.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Item_of_List (val name:String, val id: String, val device: String, val creationTime: String,
                    val calibration_factor: Float, val item_key: String, val tare: Float, val update: String,
                    val raw_vale: Float, val true_value: Float):Parcelable{
    constructor():this("","","", "", 0f, "",1f, "no", 0f,0f)
}