package br.com.stonesdk.sdkdemo.ui.splashscreen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stone.sdk.android.error.StoneStatus
import br.com.stonesdk.sdkdemo.utils.AppInfo
import co.stone.posmobile.lib.commons.platform.PlatformContext
import co.stone.posmobile.sdk.activation.provider.ActivationProvider
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.merchant.domain.model.Merchant
import co.stone.posmobile.sdk.stoneStart.domain.model.Organization
import co.stone.posmobile.sdk.stoneStart.provider.StoneStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ValidationViewModel() : ViewModel() {

    private val _uiState =
        MutableStateFlow<SplashScreenState>(SplashScreenState.Loading("Carregando ..."))
    val uiState: StateFlow<SplashScreenState> = _uiState.asStateFlow()


    fun activate(stoneCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.emit(SplashScreenState.Loading("Ativando ..."))
            val provider = ActivationProvider.create()
            provider.activate(stoneCode, object : StoneResultCallback<Any> {
                override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                    println(">>> stoneStatus: $stoneStatus, throwable: $throwable")
                    viewModelScope.launch {
                        _uiState.emit(
                            SplashScreenState.Error(
                                stoneStatus?.code ?: "",
                                stoneStatus?.message ?: ""
                            )
                        )
                    }
                }

                override fun onSuccess(result: Any) {
                    viewModelScope.launch {
                        _uiState.emit(SplashScreenState.Activated)
                    }
                }
            })
        }
    }


    fun initializeSDK(context: PlatformContext, info: AppInfo) {
        viewModelScope.launch {
            //TODO Ver a inicialização do sdk não esta legal
//            if (false) {
//                MerchantProvider.create()
//                    .getAllMerchants(object : StoneResultCallback<List<Merchant>> {
//                        override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
//                            viewModelScope.launch {
//                                println(">>> stoneStatus: $stoneStatus, throwable: $throwable")
//                                _uiState.emit(
//                                    SplashScreenState.Error(
//                                        stoneStatus?.code ?: "",
//                                        stoneStatus?.message ?: ""
//                                    )
//                                )
//                            }
//                        }
//
//                        override fun onSuccess(result: List<Merchant>) {
//                            if (result.isEmpty()) {
//                                viewModelScope.launch {
//                                    _uiState.emit(SplashScreenState.NotActivated)
//                                }
//                            } else {
//                                viewModelScope.launch {
//                                    _uiState.emit(SplashScreenState.Activated)
//                                }
//                            }
//                        }
//
//                    })
//            } else {
                val callback = object : StoneResultCallback<List<Merchant>> {
                    override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                        viewModelScope.launch {
                            println(">>> stoneStatus: $stoneStatus, throwable: $throwable")
                            _uiState.emit(
                                SplashScreenState.Error(
                                    stoneStatus?.code ?: "",
                                    stoneStatus?.message ?: ""
                                )
                            )
                        }
                    }

                    override fun onSuccess(result: List<Merchant>) {
                        if (result.isEmpty()) {
                            viewModelScope.launch {
                                _uiState.emit(SplashScreenState.NotActivated)
                            }
                        } else {
                            viewModelScope.launch {
                                _uiState.emit(SplashScreenState.Activated)
                            }
                        }
                    }

                }

                StoneStart.init(
                    context,
                    Organization.Stone,
                    info.appName,
                    info.appVersion,
                    info.packageName,
                    callback,
                    StoneStart.StoneEnvironment.STAGING
                )

            }
//        }
    }
}


sealed interface SplashScreenState {
    data class Loading(val message: String) : SplashScreenState
    data object Activated : SplashScreenState
    data object NotActivated : SplashScreenState
    data class Error(val code: String, val message: String) : SplashScreenState
}
