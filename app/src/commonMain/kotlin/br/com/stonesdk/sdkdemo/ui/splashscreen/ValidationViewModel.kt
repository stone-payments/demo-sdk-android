package br.com.stonesdk.sdkdemo.ui.splashscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stone.sdk.android.error.StoneStatus
import br.com.stonesdk.sdkdemo.wrappers.ActivationProviderWrapper
import br.com.stonesdk.sdkdemo.utils.AppInfo
import co.stone.posmobile.lib.commons.platform.PlatformContext
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.merchant.domain.model.Merchant
import co.stone.posmobile.sdk.stoneStart.domain.model.Organization
import co.stone.posmobile.sdk.stoneStart.provider.StoneStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ValidationViewModel : ViewModel() {
    private val activationProvider: ActivationProviderWrapper = ActivationProviderWrapper()

    private val _uiState: MutableStateFlow<ValidationUiModel> =
        MutableStateFlow(ValidationUiModel())
    val uiState = _uiState.asStateFlow()

    fun initializeSDK(context: PlatformContext, info: AppInfo) {
        viewModelScope.launch {
            if (StoneStart.isInitialized) {
                _uiState.update {
                    it.copy(state = SplashScreenState.Idle)
                }
                return@launch
            }

            StoneStart.init(
                context = context,
                organization = Organization.Stone,
                appName = info.appName,
                appVersion = info.appVersion,
                packageName = info.packageName,
                callback =
                    object : StoneResultCallback<List<Merchant>> {
                        override fun onSuccess(result: List<Merchant>) {
                            _uiState.update {
                                it.copy(state = SplashScreenState.Idle)
                            }
                        }

                        override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                            _uiState.update {
                                it.copy(
                                    state =
                                        SplashScreenState.Error(
                                            code = stoneStatus?.code ?: "",
                                            message = stoneStatus?.message ?: "",
                                        ),
                                )
                            }
                        }
                    },
                environment = StoneStart.StoneEnvironment.CERTIFICATION,
            )
        }
    }

    fun activate(stoneCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(state = SplashScreenState.Loading) }
            val activationResult = activationProvider.activate(stoneCode)
            if (activationResult) {
                _uiState.update { it.copy(state = SplashScreenState.Activated) }
            } else {
                _uiState.update { it.copy(state = SplashScreenState.Error("", "")) }
            }
        }
    }

    fun checkNeedToActivate() {
        viewModelScope.launch {
            if (activationProvider.getActivatedAffiliationCodes().isEmpty()) {
                _uiState.update { it.copy(state = SplashScreenState.NotActivated) }
            } else {
                _uiState.update { it.copy(state = SplashScreenState.Activated) }
            }
        }
    }
}

data class ValidationUiModel(
    val state: SplashScreenState? = null,
)

sealed interface SplashScreenState {
    data object Idle : SplashScreenState

    data object Loading : SplashScreenState

    data object Activated : SplashScreenState

    data object NotActivated : SplashScreenState

    data class Error(val code: String, val message: String) : SplashScreenState
}
