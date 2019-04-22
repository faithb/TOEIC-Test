package vn.asiantech.englishtest.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ToeicIntro(
    var introTitle: String = "",
    var introContent: String = ""
) : Parcelable
