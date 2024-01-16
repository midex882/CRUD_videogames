package com.example.crud_videojuegos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Game(
    var id : String? = null,
    var title : String? = null,
    var rating : Int? = null,
    var platform : String? = null,
    var date : Int? = null,
    var cover : String? = null
) : Parcelable {}