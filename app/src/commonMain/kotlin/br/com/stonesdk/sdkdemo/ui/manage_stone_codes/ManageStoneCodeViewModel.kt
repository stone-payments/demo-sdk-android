package br.com.stonesdk.sdkdemo.ui.manage_stone_codes

import androidx.lifecycle.ViewModel
import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.activation.provider.ActivationProvider
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.merchant.domain.model.Merchant
import co.stone.posmobile.sdk.merchant.provider.MerchantProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ManageStoneCodeViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<ManageStoneCodeState> =
        MutableStateFlow(ManageStoneCodeState.Loading("Loading ..."))
    val uiState: StateFlow<ManageStoneCodeState> = _uiState.asStateFlow()

    private val merchantProvider = MerchantProvider.create()
    private val activationProvider = ActivationProvider.create()


    @OptIn(ExperimentalUuidApi::class)
    fun getMerchants() {
        _uiState.update { ManageStoneCodeState.Loading("Loading ...") }
        merchantProvider.getAllMerchants(object : StoneResultCallback<List<Merchant>> {
            override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                _uiState.value =
                    ManageStoneCodeState.Error(stoneStatus?.code ?: "", stoneStatus?.message ?: "")
            }

            override fun onSuccess(result: List<Merchant>) {
                if (result.isEmpty()) {
                    _uiState.value = ManageStoneCodeState.Error("", "No merchants found")
                    return
                }

                _uiState.value = ManageStoneCodeState.Finish(result.map {
                    MerchantData(
                        uuid = Uuid.random().toString(),
                        affiliationCode = it.affiliationCode,
                        legalName = it.legalName,
                        displayName = it.displayName
                    )
                })
            }
        })

    }

    fun desactivateMerchant(affiliationCode: String) {
        activationProvider
            .deactivate(affiliationCode, object : StoneResultCallback<Boolean> {
                override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                    _uiState.value = ManageStoneCodeState.Error(
                        stoneStatus?.code ?: "",
                        stoneStatus?.message ?: ""
                    )
                }

                override fun onSuccess(result: Boolean) {
                    getMerchants()
                }
            })
    }

    fun updateMerchant(affiliationCode: String) {
        activationProvider.update(affiliationCode, object : StoneResultCallback<Unit> {
            override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                _uiState.value = ManageStoneCodeState.Error(
                    stoneStatus?.code ?: "",
                    stoneStatus?.message ?: ""
                )
            }

            override fun onSuccess(result: Unit) {
                getMerchants()
            }
        })
    }

    sealed interface ManageStoneCodeState {
        data class Loading(val message: String) : ManageStoneCodeState
        data class Error(val code: String, val message: String) : ManageStoneCodeState
        data class Finish(val merchants: List<MerchantData>) : ManageStoneCodeState
    }


}