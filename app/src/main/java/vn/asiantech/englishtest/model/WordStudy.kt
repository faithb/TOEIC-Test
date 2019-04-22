package vn.asiantech.englishtest.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WordStudy(
    val imageWord: String = "",
    val word: String = "",
    val spelling: String = "",
    val definition: String = "",
    val meaning: String = "",
    val example: String = "",
    val translation: String = "",
    val audio : String = ""
) : Parcelable
