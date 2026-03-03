package com.example.adidata

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OccupationFormScreen(
    viewModel: OccupationFormViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        OccupationFormContent(
            uiState = uiState,
            onCompanyNameChanged = viewModel::onCompanyNameChanged,
            onCompanyAddressChanged = viewModel::onCompanyAddressChanged,
            onCityNameChanged = viewModel::onCityNameChanged,
            onPhoneNumberChanged = viewModel::onPhoneNumberChanged,
            onNpwpChanged = viewModel::onNpwpChanged,
            onSuggestionSelected = viewModel::onSuggestionSelected,
            onSuggestionDismissed = viewModel::onSuggestionDismissed,
            paddingValues = innerPadding,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OccupationFormContent(
    uiState: OccupationFormUiState,
    onCompanyNameChanged: (String) -> Unit,
    onCompanyAddressChanged: (String) -> Unit,
    onCityNameChanged: (String) -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
    onNpwpChanged: (String) -> Unit,
    onSuggestionSelected: (String) -> Unit,
    onSuggestionDismissed: () -> Unit,
    paddingValues: PaddingValues,
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Occupation Form")

        ExposedDropdownMenuBox(
            expanded = uiState.isSuggestionVisible,
            onExpandedChange = {},
        ) {
            OutlinedTextField(
                value = uiState.companyName,
                onValueChange = onCompanyNameChanged,
                label = { Text("Company Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.isSuggestionVisible)
                },
                isError = uiState.companyNameError != null,
                supportingText = {
                    uiState.companyNameError?.let { Text(it) }
                },
                singleLine = true,
            )

            ExposedDropdownMenu(
                expanded = uiState.isSuggestionVisible,
                onDismissRequest = onSuggestionDismissed,
            ) {
                uiState.suggestions.forEach { suggestion ->
                    DropdownMenuItem(
                        text = { Text(suggestion) },
                        onClick = { onSuggestionSelected(suggestion) },
                    )
                }
            }
        }

        OutlinedTextField(
            value = uiState.companyAddress,
            onValueChange = onCompanyAddressChanged,
            label = { Text("Company Address") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.companyAddressError != null,
            supportingText = {
                uiState.companyAddressError?.let { Text(it) }
            },
        )

        OutlinedTextField(
            value = uiState.cityName,
            onValueChange = onCityNameChanged,
            label = { Text("City Name") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.cityNameError != null,
            supportingText = {
                uiState.cityNameError?.let { Text(it) }
            },
            singleLine = true,
        )

        OutlinedTextField(
            value = uiState.phoneNumber,
            onValueChange = onPhoneNumberChanged,
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.phoneNumberError != null,
            supportingText = {
                uiState.phoneNumberError?.let { Text(it) }
            },
            singleLine = true,
        )

        OutlinedTextField(
            value = uiState.npwp,
            onValueChange = onNpwpChanged,
            label = { Text("NPWP (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.npwpError != null,
            supportingText = {
                uiState.npwpError?.let { Text(it) }
            },
            singleLine = true,
        )

        Button(
            onClick = {},
            enabled = uiState.isNextEnabled,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Next")
        }
    }
}
