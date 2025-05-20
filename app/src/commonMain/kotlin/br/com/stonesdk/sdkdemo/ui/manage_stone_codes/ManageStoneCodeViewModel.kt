package br.com.stonesdk.sdkdemo.ui.manage_stone_codes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stone.sdk.android.error.StoneStatus
import br.com.stonesdk.sdkdemo.wrappers.ActivationProviderWrapper
import br.com.stonesdk.sdkdemo.wrappers.MerchantProviderWrapper
import br.com.stonesdk.sdkdemo.wrappers.QueryAllMerchantsStatus
import co.stone.posmobile.sdk.callback.StoneResultCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ManageStoneCodeViewModel(
    private val activationProvider: ActivationProviderWrapper,
    private val merchantProvider: MerchantProviderWrapper
) : ViewModel() {
    private val _uiState: MutableStateFlow<ManageStoneCodeState> =
        MutableStateFlow(ManageStoneCodeState.Loading("Loading ..."))
    val uiState: StateFlow<ManageStoneCodeState> = _uiState.asStateFlow()

    @OptIn(ExperimentalUuidApi::class)
    fun getMerchants() {
        viewModelScope.launch {
            merchantProvider.getAllMerchants().collect { status ->
                when (status) {
                    QueryAllMerchantsStatus.Loading -> {
                        _uiState.update { ManageStoneCodeState.Loading("Loading ...") }
                    }

                    is QueryAllMerchantsStatus.Success -> {

                        val merchants = status.merchants

                        if (merchants.isEmpty()) {
                            _uiState.value = ManageStoneCodeState.Error("", "No merchants found")
                        } else {
                            val foundMerchants = merchants.map {
                                MerchantData(
                                    uuid = Uuid.random().toString(),
                                    affiliationCode = it.affiliationCode,
                                    legalName = it.legalName,
                                    displayName = it.displayName
                                )
                            }

                            _uiState.value = ManageStoneCodeState.Finish(foundMerchants)
                        }
                    }

                    is QueryAllMerchantsStatus.Error -> {

                        _uiState.value =
                            ManageStoneCodeState.Error("", status.errorMessage)
                    }
                }
            }
        }
    }

    fun deactivateMerchant(affiliationCode: String) {
        viewModelScope.launch {
            val deactivated = activationProvider.deactivate(affiliationCode)
            if (deactivated) {
                getMerchants()
            } else {
                _uiState.value = ManageStoneCodeState.Error(
                    "", "falha ao desativar merchant"
                )
            }
        }
    }

    fun updateMerchant(affiliationCode: String) {
        viewModelScope.launch {
            val merchantUpdated = activationProvider.update(affiliationCode)
            if (merchantUpdated) {
                getMerchants()
            } else {
                _uiState.value = ManageStoneCodeState.Error(
                    "", "falha ao atualizar merchant"
                )
            }
        }

    }

    sealed interface ManageStoneCodeState {
        data class Loading(val message: String) : ManageStoneCodeState
        data class Error(val code: String, val message: String) : ManageStoneCodeState
        data class Finish(val merchants: List<MerchantData>) : ManageStoneCodeState
    }


}