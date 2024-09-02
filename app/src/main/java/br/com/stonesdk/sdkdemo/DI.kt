package br.com.stonesdk.sdkdemo

import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ActivationProviderWrapper
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ManageStoneCodeViewModel
import br.com.stonesdk.sdkdemo.activities.validation.AppInitializer
import br.com.stonesdk.sdkdemo.activities.validation.ValidationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import stone.application.SessionApplication
import stone.utils.Stone

val demoApplicationModule = module {

    factory<SessionApplication> {
        Stone.sessionApplication
    }

    factory<ActivationProviderWrapper> {
        ActivationProviderWrapper(get())
    }
    factory<AppInitializer> {
        AppInitializer(get())
    }

    viewModel {
        ManageStoneCodeViewModel(get(), get())
    }
    viewModel {
        ValidationViewModel(get(), get())
    }
}