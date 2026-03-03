package com.example.adidata

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@OptIn(ExperimentalCoroutinesApi::class)
class OccupationFormViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var viewModel: OccupationFormViewModel

    @Before
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        viewModel = OccupationFormViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `next button disabled when required fields are empty`() {
        val state = viewModel.uiState.value

        assertFalse(state.isNextEnabled)
        assertEquals("Company name is required", state.companyNameError)
        assertEquals("Company address is required", state.companyAddressError)
        assertEquals("City name is required", state.cityNameError)
        assertEquals("Phone number is required", state.phoneNumberError)
    }

    @Test
    fun `validation errors shown for invalid values`() {
        viewModel.onCompanyNameChanged("PT")
        viewModel.onCompanyAddressChanged("Short")
        viewModel.onCityNameChanged("AB")
        viewModel.onPhoneNumberChanged("12345")
        viewModel.onNpwpChanged("123456")

        val state = viewModel.uiState.value

        assertEquals("Address must be at least 10 characters", state.companyAddressError)
        assertEquals("City name must be at least 3 characters", state.cityNameError)
        assertEquals("Phone number must be at least 8 digits", state.phoneNumberError)
        assertEquals("NPWP must be at least 15 digits", state.npwpError)
        assertFalse(state.isNextEnabled)
    }

    @Test
    fun `next button enabled when all fields valid and npwp empty`() {
        viewModel.onCompanyNameChanged("PT Digital Solusi")
        viewModel.onCompanyAddressChanged("Jl. Sudirman No. 10")
        viewModel.onCityNameChanged("Jakarta")
        viewModel.onPhoneNumberChanged("08123456789")
        viewModel.onNpwpChanged("")

        val state = viewModel.uiState.value

        assertTrue(state.companyNameError == null)
        assertTrue(state.companyAddressError == null)
        assertTrue(state.cityNameError == null)
        assertTrue(state.phoneNumberError == null)
        assertTrue(state.npwpError == null)
        assertTrue(state.isNextEnabled)
    }

    @Test
    fun `phone and npwp only accept numeric`() {
        viewModel.onPhoneNumberChanged("08a12b34")
        viewModel.onNpwpChanged("12-34a56")

        val state = viewModel.uiState.value

        assertEquals("081234", state.phoneNumber)
        assertEquals("123456", state.npwp)
    }

    @Test
    fun `suggestions appear after debounce when company name longer than 3 characters`() = runTest {
        viewModel.onCompanyNameChanged("PT D")

        advanceTimeBy(499)
        assertFalse(viewModel.uiState.value.isSuggestionVisible)

        advanceTimeBy(1)
        val state = viewModel.uiState.value
        assertTrue(state.isSuggestionVisible)
        assertTrue(state.suggestions.contains("PT Digital Solusi"))
    }

    @Test
    fun `selecting suggestion fills company name and closes dropdown`() = runTest {
        viewModel.onCompanyNameChanged("PT T")
        advanceTimeBy(500)

        viewModel.onSuggestionSelected("PT Teknologi Nusantara")

        val state = viewModel.uiState.value
        assertEquals("PT Teknologi Nusantara", state.companyName)
        assertFalse(state.isSuggestionVisible)
        assertTrue(state.suggestions.isEmpty())
    }

    @Test
    fun `mockito verification for suggestion dismiss callback`() {
        val callback = mock(Runnable::class.java)

        viewModel.onSuggestionDismissed()
        callback.run()

        verify(callback).run()
    }
}
