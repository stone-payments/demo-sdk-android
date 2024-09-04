package br.com.stonesdk.sdkdemo

import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ActivationProviderWrapper
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ManageStoneCodeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import stone.application.SessionApplication
import stone.providers.ActiveApplicationProvider
import stone.utils.Stone

val demoApplicationModule = module {

    factory<SessionApplication> {
        Stone.sessionApplication
    }

    factory<ActivationProviderWrapper> {
        ActivationProviderWrapper(get())
    }

    viewModel {
        ManageStoneCodeViewModel(get(), get())
    }
}