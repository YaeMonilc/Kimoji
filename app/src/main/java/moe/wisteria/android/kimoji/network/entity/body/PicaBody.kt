package moe.wisteria.android.kimoji.network.entity.body

import moe.wisteria.android.kimoji.entity.Sort

data class SignInBody(
    val email: String,
    val password: String
)

data class RegisterBody(
    val email: String,
    val name: String,
    val password: String,
    val gender: String,
    val birthday: String,
    val question1: String,
    val answer1: String,
    val question2: String = question1,
    val answer2: String = answer1,
    val question3: String = question1,
    val answer3: String = answer1,
)

data class SearchBody(
    val keyword: String,
    val sort: String = Sort.NEW.toString()
)