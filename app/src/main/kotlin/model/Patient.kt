package model

data class Patient(
    val resourceType: String,
    val active: Boolean,
    val name: List<Name>,
    val gender: String,
    val birthDate: String
)