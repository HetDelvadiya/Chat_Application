package com.awcindia.chatapplication.model

import java.util.UUID

data class User(
    val userId : String = "" ,
    val userName : String = "" ,
    val userImage : String = "" ,
    val userPhoneNumber : String = ""
) {
}