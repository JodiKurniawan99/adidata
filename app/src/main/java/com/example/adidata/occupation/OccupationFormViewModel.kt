package com.example.adidata.occupation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@OptIn(FlowPreview::class)
class OccupationFormViewModel : ViewModel() {

    private val allCompanies = listOf(
        "PT Bank Nasional",
        "PT Digital Solusi",
        "PT Teknologi Nusantara",
    )

    private val _uiState = MutableStateFlow(OccupationFormUiState())
    val uiState: StateFlow<OccupationFormUiState> = _uiState.asStateFlow()

    private val companyNameQuery = MutableStateFlow("")

    init {
        companyNameQuery
            .debounce(500)
            .distinctUntilChanged()
            .onEach { query ->
                val shouldShow = query.length > 3
                val suggestions = if (shouldShow) {
                    allCompanies.filter { it.contains(query, ignoreCase = true) }
                } else {
                    emptyList()
                }
                _uiState.update {
                    it.copy(
                        suggestions = suggestions,
                        showSuggestions = shouldShow && suggestions.isNotEmpty(),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onCompanyNameChanged(value: String) {
        _uiState.update {
            it.copy(
                companyName = value,
                companyNameError = validateCompanyName(value),
            )
        }
        companyNameQuery.value = value
        updateButtonState()
    }

    fun onCompanyAddressChanged(value: String) {
        _uiState.update {
            it.copy(
                companyAddress = value,
                companyAddressError = validateCompanyAddress(value),
            )
        }
        updateButtonState()
    }

    fun onCityNameChanged(value: String) {
        _uiState.update {
            it.copy(
                cityName = value,
                cityNameError = validateCityName(value),
            )
        }
        updateButtonState()
    }

    fun onPhoneNumberChanged(value: String) {
        val numericValue = value.filter { it.isDigit() }
        _uiState.update {
            it.copy(
                phoneNumber = numericValue,
                phoneNumberError = validatePhoneNumber(numericValue),
            )
        }
        updateButtonState()
    }

    fun onNpwpChanged(value: String) {
        val numericValue = value.filter { it.isDigit() }
        _uiState.update {
            it.copy(
                npwp = numericValue,
                npwpError = validateNpwp(numericValue),
            )
        }
        updateButtonState()
    }

    fun onSuggestionSelected(value: String) {
        _uiState.update {
            it.copy(
                companyName = value,
                companyNameError = validateCompanyName(value),
                suggestions = emptyList(),
                showSuggestions = false,
            )
        }
        companyNameQuery.value = value
        updateButtonState()
    }

    fun dismissSuggestions() {
        _uiState.update { it.copy(showSuggestions = false) }
    }

    private fun updateButtonState() {
        val state = _uiState.value
        val requiredFieldsFilled = state.companyName.isNotBlank() &&
            state.companyAddress.isNotBlank() &&
            state.cityName.isNotBlank() &&
            state.phoneNumber.isNotBlank()

        val hasError = listOf(
            validateCompanyName(state.companyName),
            validateCompanyAddress(state.companyAddress),
            validateCityName(state.cityName),
            validatePhoneNumber(state.phoneNumber),
            validateNpwp(state.npwp),
        ).any { it != null }

        _uiState.update {
            it.copy(
                isNextEnabled = requiredFieldsFilled && !hasError,
                companyNameError = validateCompanyName(state.companyName),
                companyAddressError = validateCompanyAddress(state.companyAddress),
                cityNameError = validateCityName(state.cityName),
                phoneNumberError = validatePhoneNumber(state.phoneNumber),
                npwpError = validateNpwp(state.npwp),
            )
        }
    }

    private fun validateCompanyName(value: String): String? =
        if (value.isBlank()) "Company name is required" else null

    private fun validateCompanyAddress(value: String): String? = when {
        value.isBlank() -> "Company address is required"
        value.length < 10 -> "Company address must be at least 10 characters"
        else -> null
    }

    private fun validateCityName(value: String): String? = when {
        value.isBlank() -> "City name is required"
        value.length < 3 -> "City name must be at least 3 characters"
        else -> null
    }

    private fun validatePhoneNumber(value: String): String? = when {
        value.isBlank() -> "Phone number is required"
        value.length < 8 -> "Phone number must be at least 8 digits"
        else -> null
    }

    private fun validateNpwp(value: String): String? = when {
        value.isBlank() -> null
        value.length < 15 -> "NPWP must be at least 15 digits"
        else -> null
    }
}
