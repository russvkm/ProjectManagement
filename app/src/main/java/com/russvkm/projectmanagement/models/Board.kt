package com.russvkm.projectmanagement.models

import android.os.Parcel
import android.os.Parcelable

data class Board(
    val card_name:String="",
    val image: String ="",
    val cratedBy:String="",
    val assignTo:ArrayList<String> = ArrayList(),
    var documentId:String="",
    var taskList:ArrayList<Task> = ArrayList()
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Task.CREATOR)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(card_name)
        parcel.writeString(image)
        parcel.writeString(cratedBy)
        parcel.writeStringList(assignTo)
        parcel.writeString(documentId)
        parcel.writeTypedList(taskList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }

}