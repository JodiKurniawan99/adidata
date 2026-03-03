package com.example.adidata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OccupationFormViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OccupationFormUiState())
    val uiState: StateFlow<OccupationFormUiState> = _uiState.asStateFlow()

    private val companySuggestions = listOf(
        "PT Bank Nasional",
        "PT Digital Solusi",
        "PT Teknologi Nusantara",
    )

    private var suggestionJob: Job? = null

    init {
        _uiState.update { updateValidationState(it) }
    }

    fun onCompanyNameChanged(value: String) {
        _uiState.update { current ->
            val updated = current.copy(companyName = value)
            updateValidationState(updated)
        }
        triggerCompanySuggestions(value)
    }

    fun onCompanyAddressChanged(value: String) {
        _uiState.update { current ->
            val updated = current.copy(companyAddress = value)
            updateValidationState(updated)
        }
    }

    fun onCityNameChanged(value: String) {
        _uiState.update { current ->
            val updated = current.copy(cityName = value)
            updateValidationState(updated)
        }
    }

    fun onPhoneNumberChanged(value: String) {
        val numericValue = value.filter { it.isDigit() }
        _uiState.update { current ->
            val updated = current.copy(phoneNumber = numericValue)
            updateValidationState(updated)
        }
    }

    fun onNpwpChanged(value: String) {
        val numericValue = value.filter { it.isDigit() }
        _uiState.update { current ->
            val updated = current.copy(npwp = numericValue)
            updateValidationState(updated)
        }
    }

    fun onSuggestionSelected(value: String) {
        suggestionJob?.cancel()
        _uiState.update { current ->
            val updated = current.copy(
                companyName = value,
                suggestions = emptyList(),
                isSuggestionVisible = false,
            )
            updateValidationState(updated)
        }
    }

    fun onSuggestionDismissed() {
        _uiState.update {
            it.copy(isSuggestionVisible = false)
        }
    }

    private fun triggerCompanySuggestions(keyword: String) {
        suggestionJob?.cancel()
        suggestionJob = viewModelScope.launch {
            delay(500)
            if (keyword.length <= 3) {
                _uiState.update {
                    it.copy(suggestions = emptyList(), isSuggestionVisible = false)
                }
                return@launch
            }

            val filteredSuggestions = companySuggestions.filter {
                it.contains(keyword, ignoreCase = true)
            }
            _uiState.update {
                it.copy(
                    suggestions = filteredSuggestions,
                    isSuggestionVisible = filteredSuggestions.isNotEmpty(),
                )
            }
        }
    }

    private fun updateValidationState(state: OccupationFormUiState): OccupationFormUiState {
        val companyNameError = validateCompanyName(state.companyName)
        val companyAddressError = validateAddress(state.companyAddress)
        val cityNameError = validateCityName(state.cityName)
        val phoneNumberError = validatePhoneNumber(state.phoneNumber)
        val npwpError = validateNpwp(state.npwp)

        val hasMandatoryFields =
            state.companyName.isNotBlank() &&
                state.companyAddress.isNotBlank() &&
                state.cityName.isNotBlank() &&
                state.phoneNumber.isNotBlank()

        val noErrors =
            companyNameError == null &&
                companyAddressError == null &&
                cityNameError == null &&
                phoneNumberError == null &&
                npwpError == null

        return state.copy(
            companyNameError = companyNameError,
            companyAddressError = companyAddressError,
            cityNameError = cityNameError,
            phoneNumberError = phoneNumberError,
            npwpError = npwpError,
            isNextEnabled = hasMandatoryFields && noErrors,
        )
    }

    private fun validateCompanyName(value: String): String? {
        return if (value.isBlank()) "Company name is required" else null
    }

    private fun validateAddress(value: String): String? {
        return when {
            value.isBlank() -> "Company address is required"
            value.length < 10 -> "Address must be at least 10 characters"
            else -> null
        }
    }

    private fun validateCityName(value: String): String? {
        return when {
            value.isBlank() -> "City name is required"
            value.length < 3 -> "City name must be at least 3 characters"
            else -> null
        }
    }

    private fun validatePhoneNumber(value: String): String? {
        return when {
            value.isBlank() -> "Phone number is required"
            value.length < 8 -> "Phone number must be at least 8 digits"
            else -> null
        }
    }

    private fun validateNpwp(value: String): String? {
        return when {
            value.isBlank() -> null
            value.length < 15 -> "NPWP must be at least 15 digits"
            else -> null
        }
    }
}
