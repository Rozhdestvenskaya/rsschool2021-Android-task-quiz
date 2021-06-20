package com.rsschool.quiz

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Question(

    @SerializedName("Question")
    val question: String,

    @SerializedName("Correct")
    val correct: Int,

    @SerializedName("Options")
    val options: List<String>

) : Serializable