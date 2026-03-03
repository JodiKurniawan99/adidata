package com.example.adidata.occupation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun OccupationFormRoute(
    viewModel: OccupationFormViewModel = viewModel(),
    onNextClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    OccupationFormScreen(
        uiState = uiState,
        onCompanyNameChanged = viewModel::onCompanyNameChanged,
        onCompanyAddressChanged = viewModel::onCompanyAddressChanged,
        onCityNameChanged = viewModel::onCityNameChanged,
        onPhoneNumberChanged = viewModel::onPhoneNumberChanged,
        onNpwpChanged = viewModel::onNpwpChanged,
        onSuggestionSelected = viewModel::onSuggestionSelected,
        onDismissSuggestions = viewModel::dismissSuggestions,
        onNextClick = onNextClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OccupationFormScreen(
    uiState: OccupationFormUiState,
    onCompanyNameChanged: (String) -> Unit,
    onCompanyAddressChanged: (String) -> Unit,
    onCityNameChanged: (String) -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
    onNpwpChanged: (String) -> Unit,
    onSuggestionSelected: (String) -> Unit,
    onDismissSuggestions: () -> Unit,
    onNextClick: () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Occupation Form") }) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = uiState.companyName,
                onValueChange = onCompanyNameChanged,
                label = { Text("Company Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.companyNameError != null,
                supportingText = { uiState.companyNameError?.let { Text(it) } },
            )
            DropdownMenu(
                expanded = uiState.showSuggestions,
                onDismissRequest = onDismissSuggestions,
                modifier = Modifier.fillMaxWidth(),
            ) {
                uiState.suggestions.forEach { suggestion ->
                    DropdownMenuItem(
                        text = { Text(suggestion) },
                        onClick = { onSuggestionSelected(suggestion) },
                    )
                }
            }

            OutlinedTextField(
                value = uiState.companyAddress,
                onValueChange = onCompanyAddressChanged,
                label = { Text("Company Address") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.companyAddressError != null,
                supportingText = { uiState.companyAddressError?.let { Text(it) } },
            )

            OutlinedTextField(
                value = uiState.cityName,
                onValueChange = onCityNameChanged,
                label = { Text("City Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.cityNameError != null,
                supportingText = { uiState.cityNameError?.let { Text(it) } },
            )

            OutlinedTextField(
                value = uiState.phoneNumber,
                onValueChange = onPhoneNumberChanged,
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = uiState.phoneNumberError != null,
                supportingText = { uiState.phoneNumberError?.let { Text(it) } },
            )

            OutlinedTextField(
                value = uiState.npwp,
                onValueChange = onNpwpChanged,
                label = { Text("NPWP (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = uiState.npwpError != null,
                supportingText = { uiState.npwpError?.let { Text(it) } },
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onNextClick,
                enabled = uiState.isNextEnabled,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Next")
            }
        }
    }
}
