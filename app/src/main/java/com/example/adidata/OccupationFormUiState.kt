package com.example.adidata

data class OccupationFormUiState(
    val companyName: String = "",
    val companyAddress: String = "",
    val cityName: String = "",
    val phoneNumber: String = "",
    val npwp: String = "",
    val companyNameError: String? = null,
    val companyAddressError: String? = null,
    val cityNameError: String? = null,
    val phoneNumberError: String? = null,
    val npwpError: String? = null,
    val suggestions: List<String> = emptyList(),
    val isSuggestionVisible: Boolean = false,
    val isNextEnabled: Boolean = false,
)
