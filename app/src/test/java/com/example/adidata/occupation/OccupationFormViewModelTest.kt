package com.example.adidata.occupation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OccupationFormViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    @Test
    fun `next button enabled when all fields valid and npwp empty`() = runTest(testDispatcher) {
        val viewModel = OccupationFormViewModel()

        viewModel.onCompanyNameChanged("PT Digital Solusi")
        viewModel.onCompanyAddressChanged("Jl. Merdeka No. 123")
        viewModel.onCityNameChanged("Jakarta")
        viewModel.onPhoneNumberChanged("08123456")
        viewModel.onNpwpChanged("")

        val state = viewModel.uiState.value
        assertTrue(state.isNextEnabled)
        assertEquals(null, state.npwpError)
    }

    @Test
    fun `validation failure shows errors and disables button`() = runTest(testDispatcher) {
        val viewModel = OccupationFormViewModel()

        viewModel.onCompanyNameChanged("")
        viewModel.onCompanyAddressChanged("Short")
        viewModel.onCityNameChanged("AB")
        viewModel.onPhoneNumberChanged("1234")
        viewModel.onNpwpChanged("123")

        val state = viewModel.uiState.value
        assertFalse(state.isNextEnabled)
        assertEquals("Company name is required", state.companyNameError)
        assertEquals("Company address must be at least 10 characters", state.companyAddressError)
        assertEquals("City name must be at least 3 characters", state.cityNameError)
        assertEquals("Phone number must be at least 8 digits", state.phoneNumberError)
        assertEquals("NPWP must be at least 15 digits", state.npwpError)
    }

    @Test
    fun `npwp optional when empty and invalid when under minimum length`() = runTest(testDispatcher) {
        val viewModel = OccupationFormViewModel()

        viewModel.onNpwpChanged("")
        assertEquals(null, viewModel.uiState.value.npwpError)

        viewModel.onNpwpChanged("12345678901234")
        assertEquals("NPWP must be at least 15 digits", viewModel.uiState.value.npwpError)

        viewModel.onNpwpChanged("123456789012345")
        assertEquals(null, viewModel.uiState.value.npwpError)
    }

    @Test
    fun `numeric fields keep digits only`() = runTest(testDispatcher) {
        val viewModel = OccupationFormViewModel()

        viewModel.onPhoneNumberChanged("08AB12-34")
        viewModel.onNpwpChanged("12.345.678.9-012.34")

        val state = viewModel.uiState.value
        assertEquals("081234", state.phoneNumber)
        assertEquals("12345678901234", state.npwp)
    }

    @Test
    fun `suggestions appear after debounce when query length more than three`() = runTest(testDispatcher) {
        val viewModel = OccupationFormViewModel()

        viewModel.onCompanyNameChanged("PT D")
        advanceTimeBy(499)
        assertFalse(viewModel.uiState.value.showSuggestions)

        advanceTimeBy(1)
        val state = viewModel.uiState.value
        assertTrue(state.showSuggestions)
        assertEquals(listOf("PT Digital Solusi"), state.suggestions)
    }

    @Test
    fun `suggestion selection fills company name and closes dropdown`() = runTest(testDispatcher) {
        val viewModel = OccupationFormViewModel()

        viewModel.onSuggestionSelected("PT Teknologi Nusantara")

        val state = viewModel.uiState.value
        assertEquals("PT Teknologi Nusantara", state.companyName)
        assertFalse(state.showSuggestions)
        assertTrue(state.suggestions.isEmpty())
    }

    @Test
    fun `button disabled when required fields missing despite valid npwp`() = runTest(testDispatcher) {
        val viewModel = OccupationFormViewModel()

        viewModel.onCompanyNameChanged("PT Bank Nasional")
        viewModel.onCompanyAddressChanged("Jl. Kebon Jeruk 45")
        viewModel.onCityNameChanged("Bandung")
        viewModel.onPhoneNumberChanged("")
        viewModel.onNpwpChanged("123456789012345")

        assertFalse(viewModel.uiState.value.isNextEnabled)
    }
}
