package com.ute.studentprofile_lab3

import java.io.Serializable

data class Student(
    val id: String,
    var name: String,
    var className: String,
    var email: String,
    var gpa: Double
) : Serializable
